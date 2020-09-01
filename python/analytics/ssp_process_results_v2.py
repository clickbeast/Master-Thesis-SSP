import csv
import json
import copy
import os
import pandas as pd
from ssp_dashboard.instance import Instance, InstanceGroup, LiveInstance, InstanceCollector, InstanceCollection

# params
project_root_path = "/"
config_path = project_root_path + "/" + "data/config/config.json"
results_folder = project_root_path + "/" + "data/results"
files_path = project_root_path + "/" + "data/instances/mixed_files.json"

csv_line =  {
    "instance": "",
    "n_jobs" : 0,
    "n_tools": 0,
    "magazine_size": 0,
    "variations": 0,
    "n_switches": 0,
    "b_n_switches": 0,
    "time_running": 0,
    "b_time_running": 0,
    "run_type": "",
    "sequence": [],
    "switches": [],
}



csv_line_2 =  {
    "instance": "",
    "n_jobs": 0,
    "n_tools": 0,
    "magazine_size": 0,
    "variations": 0,
    "n_switches": 0,
    "b_n_switches": 0,
    "time_running": 0,
    "b_time_running": 0,
    "run_type": "",
    "sequence": [],
    "switches": [],
}

class References():
    names = ["catanzaro"]


class Processor:

    def __init__(self, config_path) -> None:
        super().__init__()

        self.title = "ALL"
        self.run_type = "ALL"
        self.filter = {}

        self.files_name = None
        self.params = None
        self.csv_line = None
        self.config_path = config_path

        self.read_config_file()
        self.files = self.read_files(files_path)

    @staticmethod
    def read_files(fp):
        with open(fp) as f:
            j = json.load(f)
            return j["files"]

    def read_config_file(self):
        with open(self.config_path) as f:
            d = json.load(f)
            self.files_name = d["files_name"]
            self.title = d["title"]
            self.params = d["params"]
            self.filter = d["filter"]
            self.csv_line = d["csv_line"]

    def process_solutions_from_config(self):
        rt = self.params["run_type"]
        self.read_config_file()
        self.process_solutions(filter, rt, self.title)


    def process_variations_from_config(self):
        rt = self.params["run_type"]
        self.read_config_file()
        self.process_solutions_variations(filter, rt, self.title)

    def process_solutions(self, filter, run_type, title):

        descriptors = self.filter_solutions(filter)

        instanceGroups = []
        instances = []
        for desc in descriptors:
            instanceGroups.append(InstanceGroup(desc))

        for g in instanceGroups:

            if run_type is None:
                for instance in g.get_instances():
                    instances.append(instance)
            else:
                instances.append(g.get_instance(run_type))
        #

        self.title = title
        if run_type is None:
            self.run_type = "*"
        else:
            self.run_type = run_type

        self.write_csv_header()

        for instance in instances:
            solution = instance.solution
            data = {
                "instance": instance.descriptor["instance"],
                "n_jobs": solution.n_jobs,
                "n_tools": solution.n_tools,
                "magazine_size": solution.magazine_size,
                "n_switches": solution.n_switches,
                "time_running": solution.time_running,
                "run_type": instance.run_type,
                "sequence": instance.solution.sequence,
                "switches": instance.solution.switches,
                "nToolsAdd": instance.solution.n_tools_add
            }
            l = self.construct_CSV_list_variations_sumarized(data)
            self.write_CSV_line_row(l)

    def process_solutions_variations(self, filter, run_type, title):

        descriptors = self.filter_solutions(filter)

        instanceGroups = []
        instances = []
        for desc in descriptors:
            instanceGroups.append(InstanceGroup(desc))

        for g in instanceGroups:

            if run_type is None:
                for instance in g.get_instances():
                    instances.append(instance)
            else:
                instances.append(g.get_instance(run_type))

        self.title = title
        if run_type is None:
            self.run_type = "*"
        else:
            self.run_type = run_type

        self.write_csv_header()

        filter = {
            "author": "cat",
            "n_jobs": [8, 8],
            "n_tools": [0, 20],
            "magazine_size": [0, 10],
            "variation": [1, 30]
        }

        key = ()

        ic = InstanceCollector()

        for collection in ic.bundleVariations(instances):
            data = {
                "instance": str(collection.key),
                "n_jobs": collection.first_instance().solution.n_jobs,
                "n_tools": collection.first_instance().solution.n_tools,
                "magazine_size": collection.first_instance().solution.magazine_size,
                "variations": len(collection.instances),
                "n_switches": collection.average_switches_solutions(),
                "time_running": collection.average_run_time_solutions(),
                "run_type": collection.first_instance().run_type,
                "sequence": []
            }
            l = self.construct_CSV_list_variations_sumarized(data)
            self.write_CSV_line_row(l)


    def filter_solutions(self, filter):
        out = []
        for descriptor in self.files:
            if self.solution_allowed(descriptor, filter):
                out.append(descriptor)

        return out

    def solution_allowed(self, descriptor, filter):
        for k, interval in filter.items():
            if k in descriptor:
                if descriptor.get(k) < interval[0] or descriptor.get(k) > interval[1]:
                    return False
        return True

    #
    # CSV Writing - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
    #

    def construct_CSV_list_variations_sumarized(self, sum):
        out = []
        dicts = [sum]
        for k, v in self.csv_line.items():
            out.append(self.match_key_with_dict(k, dicts))
        return out

    def match_key_with_dict(self, key, dicts):
        for d in dicts:
            if key in d:
                value = d.get(key)
                if value is not "":
                    return value

        return None

    def write_csv_header(self):
        print(self.get_results_file_out_path())
        with open(self.get_results_file_out_path(), 'w', newline='') as csvfile:
            csvwriter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_MINIMAL)
            csvwriter.writerow(self.csv_line.keys())

    def write_CSV_line_row(self, row):
        print(self.get_results_file_out_path())

        with open(self.get_results_file_out_path(), 'a', newline='') as csvfile:
            csvwriter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_MINIMAL)
            csvwriter.writerow(row)

    def get_results_file_out_path(self):
        path = results_folder + "/" + "results" + "_" + self.title + "_" + self.run_type + ".csv"
        return path

    def open_last(self):
        command = "open" + " " + self.get_results_file_out_path()
        os.system(command)


filter = {
    "n_jobs": [0, 100],
    "n_tools": [0, 200],
    "magazine_size": [0, 100],
    "variation": [1, 30]
}



processor = Processor(config_path)
processor.process_solutions_variations(filter, "9,7-SD-ORDERED", "MIXED")
#processor.process_solutions_variations(filter, "BF-A", "DEBUG")
#processor.process_variations_from_config()
processor.open_last()
