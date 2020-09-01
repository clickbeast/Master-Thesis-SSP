import json
import os

files_folder_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances"
all_files = "crama_files.json"


class FileCollector:

    def __init__(self, files_path) -> None:
        super().__init__()
        self.files = self.read_files(files_path)

    @staticmethod
    def read_files(files_path):
        with open(files_path) as f:
            j = json.load(f)
            print(len(j["files"]))
            return j["files"]

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
    @staticmethod
    def safe_files(files, path):
        with open(path, mode='w') as f:
            out = {}
            out["base"]= "SSP"
            out["run_type"]= "ran_swap-2job_full_sd_sw_v1_none"
            out["root_path"]= "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/"

            out["files"] = files

            json.dump(out, f, indent=4)




out = files_folder_path + "/" + "collect.json"

prefilter = [60,90,30]


filter = {
    "n_jobs": [70, 70],
    "n_tools": [105, 105],
    "magazine_size": [55, 55],
    "variation": [1, 30]
}

fc = FileCollector(os.path.join(files_folder_path, all_files))
#fc.safe_files(fc.filter_solutions(filter), out)
