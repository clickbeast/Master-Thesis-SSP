import json
from os import walk
import os
import pandas as pd
import numpy as np
import re

root_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP"
instances_path = "data/instances/"
all_files_path = "data/instances/all_files.json"
saved_state = "data/config/state.txt"

camel_pat = re.compile(r'([A-Z])')
under_pat = re.compile(r'_([a-z])')

run = 0

columns = ['SW','B_SW','THOP','B_THOP','TAD','B_TAD','TRD','B_TRD','ACCEPT','REJECT','IMPROVE','STEP','T_RUN','T_REM','TEMP','SEQ','TYPE','TYPEID']



#### source: https://stackoverflow.com/questions/17156078/converting-identifier-naming-between-camelcase-and-underscores-during-json-seria
def camel_to_underscore(name):
    return camel_pat.sub(lambda x: '_' + x.group(1).lower(), name)


def underscore_to_camel(name):
    return under_pat.sub(lambda x: x.group(1).upper(), name)


def convert_json(d, convert):
    new_d = {}
    for k, v in d.items():
        new_d[convert(k)] = convert_json(v, convert) if isinstance(v, dict) else v
    return new_d


def getInstance(instance, run_type, run):
    # descriptor lookup
    descriptor = getDescriptor(instance)
    instance_folder_path = os.path.join(root_path, instances_path, descriptor["root_folder"], descriptor["instance"])
    return Instance(instance_folder_path, descriptor, run_type)


def getDescriptor(instance):
    with open(os.path.join(root_path, all_files_path), mode='r') as file:
        all_files = json.load(file)

    for desc in all_files["files"]:
        if desc["instance"] == instance:
            return desc

    return None


def getInstanceFromState():
    with open(os.path.join(root_path, saved_state), mode='r') as file:
        author = file.readline().strip()
        instance = file.readline().strip()
        run_type = file.readline().strip()
        run = file.readline().strip()

        return getInstance(instance, run_type, run)


####
class InstanceGroup:

    def __init__(self, descriptor) -> None:
        super().__init__()
        self.run_types = []
        self.descriptor = descriptor
        self.instance_folder_path = os.path.join(root_path, instances_path, self.descriptor["root_folder"],
                                                 self.descriptor["instance"])
        self.read_run_types()

    def read_run_types(self):
        f = []
        for (dirpath, dirnames, filenames) in walk(self.instance_folder_path):
            f.extend(filenames)
            break

        self.filter_filenames_to_run_types(f)

    def filter_filenames_to_run_types(self, files):
        for name in files:
            if name.endswith(".csv"):
                self.run_types.append(self.remove_prefix(name, "log_").strip(".csv"))

    def get_instance(self, run_type):
        return Instance(self.instance_folder_path, self.descriptor, run_type)

    def remove_prefix(self, text, prefix):
        if text.startswith(prefix):
            return text[len(prefix):]
        return text

    def get_instances(self):

        result = []
        for run_type in self.run_types:
            result.append(self.get_instance(run_type))

        return result


class Instance:

    def __init__(self, instance_folder_path, descriptor, run_type, run=run, solution_only=False) -> None:
        super().__init__()
        self.results: [Result] = []
        self.solution: Result
        self.log = None
        self.descriptor = {}
        self.run_type = ""
        self.run = run
        self.problem = []
        self.parameters = {}
        self.read_error = False
        self.solution_only = solution_only


        self.instance_folder_path = instance_folder_path
        self.results_path = ""
        self.solution_path = ""
        self.log_path = ""
        self.instance_path = ""
        self.parameter_path = ""
        self.run_type = run_type
        self.descriptor = descriptor
        self.set_file_paths(run_type)

        self.loadData()

    def loadData(self):
        if self.solution_only:
            parser = Parser()
            s = parser.read_solution_json(self.solution_path)
            if s is None:
                self.read_error = True
            self.solution = s
        else:
            parser = Parser()
            r = parser.read_results_json(self.results_path)
            s = parser.read_solution_json(self.solution_path)
            l = parser.read_log(self.log_path)
            pr = parser.read_instance(self.instance_path)
            pa = parser.read_paramaters(self.parameter_path)

            if (r or s or l or pr or pa) is None:
                self.read_error = True

            self.results = r
            self.solution = s
            self.log = l
            self.problem = pr
            self.parameters = pa



    def set_file_paths(self, type):
        self.instance_path = self.instance_folder_path + "/" + self.descriptor["instance"] + ".json"
        self.results_path = self.instance_folder_path + "/" + "result_" + type + "_#" + str(self.run) + ".txt"
        self.log_path = self.instance_folder_path + "/" + "log_" + type + "_#" + str(self.run) + ".csv"
        self.solution_path = self.instance_folder_path + "/" + "solution_" + type + "_#" + str(self.run) + ".txt"
        self.parameter_path = self.instance_folder_path + "/" + "parameter_" + type + "_#" + str(self.run) + ".txt"

        # fallback old: file exits?
        try:
            f = open(self.log_path)
        # Do something with the file
        except IOError:
            print("Old file type: falling back to old path structure")
            self.results_path = self.instance_folder_path + "/" + "result_" + type + ".txt"
            self.log_path = self.instance_folder_path + "/" + "log_" + type + ".csv"
            self.solution_path = self.instance_folder_path + "/" + "solution_" + type + ".txt"
            self.parameter_path = self.instance_folder_path + "/" + "parameter_" + type + ".txt"


class LiveInstance:

    def __init__(self) -> None:
        super().__init__()
        self.solution = []
        self.log = None
        self.descriptor = {}
        self.problem = []

        self.live_path = os.path.join(root_path, "data/instances/live.txt")
        self.instance_path = ""
        self.read_error = False
        self.parse()

    def parse(self):
        parser = Parser()

        self.solution = parser.read_live_solution_json(self.live_path)
        self.read_error = False

        if self.solution is None:
            self.read_error = True
            return


        self.instance_path = parser.get_live_instance_problem_path(self.solution)
        self.problem = parser.read_instance(self.instance_path)

    def reload(self):
        self.parse()


def transpose_matrix(matrix):
    a = np.array(matrix)
    a = a.transpose()
    return a.tolist()


class Lookup():

    def get_instances_param(self, author, n_jobs, n_tools, magazine_size, run_type=None, run=None):
        instance = str(author) + "_" + str(n_jobs) + "_" + str(n_tools) + "_" + str(magazine_size)
        return self.get_instances_name(instance, run_type, run)

    def get_instances_name(self, instance, run_type=None, run=None):
        descriptor = getDescriptor(instance)
        return self.get_instances_desc(descriptor, run_type, run)

    def get_instances_desc(self, desc, run_type=None, run=None, solution_only=False):

        instances = []

        instance_folder_path = self.get_instance_folder_path(desc)

        runs = self.get_run_types_and_runs_for_desc(desc)
        run_types = []

        if run_type is None:
            run_types = runs.keys()
        else:
            if isinstance(run_type, list):
                run_types.extend(run_type)
            else:
                run_types.append(run_type)

        for rt in run_types:
            if run is None:
                for r in runs.get(rt):
                    instances.append(Instance(instance_folder_path, desc, rt, r, solution_only=solution_only))
            else:
                instances.append(Instance(instance_folder_path, desc, rt, run, solution_only=solution_only))

        return instances

    def get_instance_folder_path(self, desc):
        return os.path.join(root_path, instances_path, desc["root_folder"], desc["instance"])

    def get_run_types_for_desc(self, desc):
        rt = []
        for k, v in self.get_run_types_and_runs_for_desc(desc):
            rt.append(k)
        return rt

    def get_runs_for_run_type_desc(self, desc, run_type):
        return self.get_run_types_and_runs_for_desc(desc)[run_type]

    def get_run_types_and_runs_for_desc(self, desc):
        f = []
        for (dirpath, dirnames, filenames) in walk(self.get_instance_folder_path(desc)):
            f.extend(filenames)
            break

        return self.filter_filenames_to_run_types(f)

    def filter_filenames_to_run_types(self, files):
        rt = {}

        for name in files:
            if name.endswith(".csv"):
                combo: str = self.remove_prefix(name, "log_").strip(".csv")

                s = combo.split('#', 1)
                run_type = s[0].rstrip('_')

                if len(s) > 1:
                    run = int(s[1])
                else:
                    run = 0

                if run_type not in rt:
                    rt[run_type] = list()
                rt[run_type].append(run)

        return rt

    def remove_prefix(self, text, prefix):
        if text.startswith(prefix):
            return text[len(prefix):]
        return text


class Result:

    def __init__(self, json=False, **kwargs) -> None:
        super().__init__()

        self.instance = "",

        self.job_tool_matrix = []

        self.n_jobs = int()
        self.n_tools = int()
        self.magazine_size = int()

        self.id = int()
        self.time_running = int()
        self.time_remaining = int()
        self.type = str()
        self.cost = int()
        self.n_switches = int()
        self.n_tool_hops = int()
        self.n_ktns_tool_hops = int()

        self.sequence = []
        self.jobPositions = []
        self.switches = []
        self.tool_distance = []
        self.n_tools_add = []
        self.n_tools_delete = []
        self.n_tools_keep = []

        self.tool_hops = []
        self.ktns_tool_hops = []

        self.parameters = {}

        if not json:
            for key, value in kwargs.items():
                setattr(self, key, value)
        else:
            data = convert_json(kwargs, camel_to_underscore)

            for key, value in data.items():
                if hasattr(self, key):
                    setattr(self, key, value)

            for key, value in data["result"].items():
                if hasattr(self, key):
                    setattr(self, key, value)

        self.tools = range(0, self.n_tools)

    def get_sequence_matrix(self):
        matrix = []
        for seq in self.sequence:
            matrix.append(self.job_tool_matrix[seq])
        return matrix


class Parser:
    def read_solution(self, path):

        with open(path, mode='r') as file:
            solution, line = self.read_result(file)
            solution["id"] = 0

        return Result(**solution)

    def read_solution_json(self, path):
        with open(path, mode='r') as file:
            try:
                obj = json.loads(file.readline())
            except ValueError as e:
                print("Solution  read error")
                out = None
            else:
                out = Result(json=True, **obj)

        return out

    def read_result(self, file):
        solution = {}
        matrix = []

        solution["n_jobs"] = int(file.readline())
        solution["n_tools"] = int(file.readline())
        solution["magazine_size"] = int(file.readline())
        solution["n_switches"] = int(file.readline())
        solution["n_tool_hops"] = int(file.readline())
        solution["tool_add_distance"] = int(file.readline())
        solution["tool_remove_distance"] = int(file.readline())
        solution["time_running"] = int(file.readline())
        solution["sequence"] = self.extract_sequence(file.readline())
        solution["tool_hops"] = self.extract_sequence(file.readline())
        solution["tool_add_distance_sequence"] = self.extract_sequence(file.readline())
        solution["tool_remove_distance_sequence"] = self.extract_sequence(file.readline())

        while True:
            line = file.readline()

            if line is None or line is '' or line.startswith('#'):
                break

            matrix_line = line.strip().split(" ")

            matrix_line = [int(element) for element in matrix_line]
            matrix.append(matrix_line)

        solution["job_tool_matrix"] = matrix

        return solution, line

    def read_paramaters(self, path):

        try:
            with open(path, mode='r') as file:
                return json.loads(file.readline())
        except:
            print("File not accessible")

            return None

    def read_results(self, path):
        results = []

        with open(path, mode='r') as file:
            line = file.readline()
            id = int(self.remove_prefix(line, "#").strip())
            result, line = self.read_result(file)
            result["id"] = id
            results.append(Result(**result))

            while line.startswith("#"):
                id = int(self.remove_prefix(line, "#").strip())
                result, line = self.read_result(file)
                result["id"] = id
                results.append(Result(**result))

            return results

    def read_results_json(self, path):
        results = []
        with open(path, mode='r') as file:
            for line in file:
                try:
                    obj = json.loads(line)
                except ValueError as e:
                    print("Result read error")
                else:
                    results.append(Result(json=True, **obj))

        return results

    def read_log(self, path):

        try:
            pd.read_csv(path)
        except:
            print("CSV NOT READ")
            return None

        return pd.read_csv(path)

    def read_instance(self, path):
        with open(path, mode='r') as f:
            # parsing JSON string:
            return json.load(f)

    def extract_sequence(self, line):
        line = line.replace('[', '').replace(']', '').replace(' ', '').strip().split(",")

        return [int(element) for element in line]

    def remove_prefix(self, text, prefix):
        if text.startswith(prefix):
            return text[len(prefix):]
        return text

    def read_live_instance_problem_path(self, path):
        with open(path, mode='r') as file:
            instance = str(file.readline().strip())
            if instance is '':
                return ''

            auth_map = {
                "cram": "crama",
                "cat": "catanzaro",
                "mec": "mecler",
                "yan": "yanasse"
            }

            author = auth_map[instance.split('_')[0]]

            p = os.path.join(root_path, "data/instances", author, instance, instance) + ".json"

        return p

    def get_live_instance_problem_path(self, result: Result):
        instance = result.instance



        if instance is '':
            return ''

        auth_map = {
            "cram": "crama",
            "cat": "catanzaro",
            "mec": "mecler",
            "yan": "yanasse"
        }

        author = auth_map[instance.split('_')[0]]

        p = os.path.join(root_path, "data/instances", author, instance, instance) + ".json"

        return p

    def read_live_solution_json(self, live_path):
        with open(live_path, mode='r') as file:
            try:
                obj = json.loads(file.readline())
            except ValueError as e:
                print("Live Result read error")
                out = None
            else:
                out = Result(json=True, **obj)

        return out

    def read_live_solution(self, live_path):
        with open(live_path, mode='r') as file:
            instance = file.readline()
            solution, line = self.read_result(file)
            solution["id"] = 0

        return Result(**solution)


class InstanceCollector:

    def __init__(self) -> None:
        super().__init__()

    def bundleVariations(self, instances: [Instance]):
        collection = {}

        for instance in instances:
            key = self.generate_key(instance)
            collection[key] = collection.get(key, [])
            collection[key].append(instance)

        instance_collections = []
        for key, item in collection.items():
            instance_collections.append(InstanceCollection(item, key))

        return instance_collections

    def generate_key(self, instance: Instance):
        key = (instance.descriptor["author"], instance.solution.n_jobs, instance.solution.n_tools,
               instance.solution.magazine_size)

        return key


class InstanceCollection:

    def __init__(self, instances, key) -> None:
        super().__init__()
        self.instances = instances
        self.key = key

    def average_switches_solutions(self):
        return self.average(self.getSolutions(), "n_switches")

    def average_run_time_solutions(self):
        return self.average(self.getSolutions(), "time_running") / 1000

    def average(self, results: [Result], attribute):
        average = round(self.sum(results, attribute) / len(results), 2)

        return average

    def getSolutions(self):
        out = []
        for instance in self.instances:
            out.append(instance.solution)
        return out

    def getResults(self, index):
        out = []
        for instance in self.instances:
            out.append(instance.results[index])
        return out

    def sum(self, results: [Result], attribute):
        total = 0
        for result in results:
            total += result.__getattribute__(attribute)
        return total

    def first_instance(self):
        return self.instances[0]
