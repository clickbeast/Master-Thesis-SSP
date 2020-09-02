import os
import json
import sys
from inspect import getsourcefile
import yaml
import datetime
from colorama import Fore, Back, Style
from analytics.ssp_process_results_v3 import Processor

os.system("echo \"hello world\"")

#
# DEFAULTS
#

root_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP"
r_multi_config_path = "data/config/configs.yaml"
r_config_path = "data/config/config.yaml"
r_command_path = "data/config/commands"
rdp_instances = "data/instances"
rdp_builds = "build/libs/Master-Thesis-SSP"

parallel = True
background = True
multi = True
runs = True
jobs = 32

config_path = os.path.join(root_path, r_config_path)
multi_config_path = os.path.join(root_path, r_multi_config_path)
command_path = os.path.join(root_path, r_command_path)


class Runner():

    def __init__(self) -> None:
        super().__init__()
        self.config = {}
        self.files = []
        self.fp_commands = command_path
        self.uniqueFilePath()

    def read_config_file(self):
        with open(config_path, 'r') as file:
            try:
                self.config = yaml.safe_load(file)
            except yaml.YAMLError as exc:
                print(exc)

    def read_files(self, path):
        with open(path, 'r') as f:
            # parsing JSON string:
            self.files = json.load(f)["files"]

    def run(self):
        self.read_config_file()
        fp_files = os.path.join(root_path, rdp_instances, self.config["files"])
        self.read_files(fp_files)

        runs = self.config["runs"] if self.config["use_runs"] else 1
        self.create_commands_files(use_multi=self.config["multi"],
                                   runs=runs,
                                   use_filter=self.config["use_filter"],
                                   filter=self.config["filter"])

        if self.config["parallel"]:
            self.run_parallel(self.config["jobs"], self.config["background"])
        else:
            self.run_sequential()

        if self.config["open_results"]:
            self.open_results()

        # remove the commands file
        os.remove(self.fp_commands)

    def open_results(self):
        print("opening results")

        default_config = {
            "root_path": self.config["root_path"],
            "files": self.config["files"],
            "bundle_variations": True,
            "bundle_runs": True,
            "use_run_type": True,
            "run_type": self.collect_run_types(),
            "use_filter": False,
            "title": '',
            "filter": {
                "author": "cat",
                "n_jobs": 10,
                "n_tools": 10,
                "magazine_size": 4,
                "variation": 1,
                "run": 0
            }
        }

        p = Processor()
        p.config = default_config
        p.process(use_config_file=False)

        return None

    def uniqueFilePath(self):
        self.fp_commands = self.fp_commands + str(datetime.datetime.now().timestamp()).replace('.', '-') + ".txt"

    def run_parallel(self, jobs=jobs, background=background):
        if background:
            command = "nohup parallel --jobs {}  --progress < {}".format(jobs, self.fp_commands)
        else:
            command = "parallel --jobs {}  --progress < {}".format(jobs, self.fp_commands)

        os.system(command)

    def run_sequential(self):
        with open(self.fp_commands) as file:
            for command in file:
                os.system(command)

    def create_jar_name(self, use_multi):
        name = ""
        if use_multi:
            for p in self.config["params"]:
                if "run_type" in p:
                    name += "-"
                    name += p["run_type"]
        else:
            name += "-"
            name += self.config["params"][0]["run_type"]

        #
        # SAFE for BUILD CONFIG
        #

        out = {
            "run_type": name[1:]
        }

        with open(os.path.join(root_path, "data/config/build_config.json"), mode='w') as file:
            json.dump(out, file)

        return name

    def create_commands_files(self,
                              use_multi=multi,
                              runs=runs,
                              use_filter=False,
                              filter=None):

        os.chdir(root_path)
        jar_file = rdp_builds + self.create_jar_name(use_multi) + ".jar"
        os.system("gradle jar")

        multi = len(self.config["params"]) if use_multi else 0
        count = 0

        with open(self.fp_commands, mode='w') as command_file:
            for file in self.files:
                if self.instance_allowed(file, self.sanitize_filter(filter)) or use_filter is False:
                    for multi_index in range(multi):
                        for run in range(runs):
                            self.config["params"][multi_index]["root_folder"] = os.path.join(
                                root_path,
                                "data/instances",
                                file["root_folder"])

                            self.config["params"][multi_index]["instance"] = file["instance"]
                            self.config["params"][multi_index]["run"] = run

                            command = "java -jar" + " " + jar_file + " " + \
                                      self.create_command_param(multi_index)

                            print(command)
                            command_file.write(command)
                            command_file.write("\n")
                            count += 1
        print("Preparing to run " + Fore.GREEN + str(count) + Fore.RESET + " instances")

    @staticmethod
    def sanitize_filter(filter):
        for k, interval in filter.items():
            if isinstance(interval, int):
                filter[k] = [interval, interval]
            pass

        return filter

    @staticmethod
    def instance_allowed(descriptor, filter):
        for k, interval in filter.items():
            if isinstance(interval, str):
                if descriptor.get(k) != interval:
                    return False
            elif k in descriptor:
                if descriptor.get(k) < interval[0] or descriptor.get(k) > interval[1]:
                    return False
        return True

    def create_command_param(self, multi_index):

        if multi_index is 0:
            command = ""
            for k, v in self.config["params"][0].items():
                command += "--" + str(k) + "=" + str(v) + " "
            return command

        else:
            command = ""

            if "run_type" in self.config["params"][multi_index]:
                for k, v in self.config["params"][0].items():
                    # key present
                    if k in self.config["params"][multi_index]:
                        v = self.config["params"][multi_index][k]
                        command += "--" + str(k) + "=" + str(v) + " "

                    # use the value from the first params
                    else:
                        command += "--" + str(k) + "=" + str(v) + " "
            else:
                raise ValueError("run type not present")

            return command

    def collect_run_types(self):

        rt = []

        for p in self.config["params"]:
            if 'run_type' in p:
                rt.append(p['run_type'])
        return rt


runner = Runner()
runner.run()
