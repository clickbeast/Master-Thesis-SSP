import os
import json

os.system("echo \"hello world\"")


class Runner:
    base = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/build" \
           "/libs/Master-Thesis-SSP"

    files_path_base = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances"
    files_path = files_path_base + "/" + "yanasse_files.json"
    files = {}
    file_descriptor = None

    params = {
        "root_folder": "",
        "instance": "",
        "run_type": "ran_swap-2job_full_sd_sw_v1_none",
        "run_time": "700",
        "seed": "7",
        "start_temp": "100",
        "end_temp": "0.000097",
        "decay_rate": "0.99900",
    }

    def __init__(self) -> None:
        super().__init__()
        self.read_files()

    def read_files(self):
        with open(self.files_path, 'r') as f:
            # parsing JSON string:
            self.files = json.load(f)

    def run(self):

        #self.params["run_type"] = self.files["run_type"]
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


runner = Runner()
runner.run()
