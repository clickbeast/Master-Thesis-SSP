import os
import json

os.system("echo \"hello world\"")

config_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/config/config.json"


class Runner:
    base = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/build" \
           "/libs/Master-Thesis-SSP"

    files_path_base = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances"
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

    def __init__(self, path) -> None:
        super().__init__()
        self.config_path = path
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


runner = Runner(config_path)
runner.run()
