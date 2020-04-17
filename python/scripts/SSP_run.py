import os
import json
from inspect import getsourcefile

os.system("echo \"hello world\"")


class Runner:
    config_path = "data/config/config.json"
    base = "build/libs/Master-Thesis-SSP"
    files_path_base = "data/instances"
    files_path = files_path_base + "/" + "catanzaro_files.json"
    files = {}
    file_descriptor = None
    files_folder_path = ""
    files_name = ""
    title = ""
    params = {}
    filter = {}
    solution = {}
    result = {}
    csv_line = {}

    def __init__(self, root_project_path) -> None:
        super().__init__()

        self.config_path = os.path.join(root_project_path, self.config_path)
        self.base = os.path.join(root_project_path, self.base)
        self.files_path_base = os.path.join(root_project_path, self.files_path_base)
        self.files_path = os.path.join(root_project_path, self.files_path)

        self.read_config_file()
        self.read_files()

    def read_files(self):
        with open(self.files_path, 'r') as f:
            # parsing JSON string:
            self.files = json.load(f)

    def run(self):

        # self.params["run_type"] = self.files["run_type"]
        jar_file = self.base + "-" + self.params["run_type"] + ".jar"

        for file in self.files["files"]:
            self.params["root_folder"] = self.files["root_path"] + file["root_folder"]
            self.params["instance"] = file["instance"]
            command = "java -jar" + " " + jar_file + " " + \
                      self.create_command_param()

            print(command)
            os.system(command)

    def create_command_param(self):

        command = ""
        for k, v in self.params.items():
            command += "--" + k + "=" + v + " "
        return command

    def read_config_file(self):
        with open(self.config_path) as f:
            d = json.load(f)
            self.files_path = d["files_folder_path"] + "/" + d["files_name"]
            self.files_folder_path = d["files_folder_path"]
            self.files_name = d["files_name"]
            self.title = d["title"]
            self.params = d["params"]
            self.filter = d["filter"]
            self.solution = d["solution"]
            self.result = d["result"]
            self.csv_line = d["csv_line"]


def create_project_root_path():
    path = os.path.dirname(__file__)
    while os.path.split(path)[1] != "Master-Thesis-SSP":
        path = os.path.dirname(path)

    return path


runner = Runner(create_project_root_path())
runner.run()
