import csv
import json

# params

files = []
with open('path_to_file/person.json') as f:
    files = json.load(f)

outputFolder = ""


class Processor:
    results_folder = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis" \
                     "-SSP/data/results"
    result_filename_root = "results_"

    files_path_base = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis" \
                      "-SSP/data/instances"
    files_path = files_path_base + "/" + "yanasse_files.json"
    files = {}

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

    file_descriptor = {
        "original": "",
        "root_folder": "mecler",
        "instance": "",
        "author": "mec",
        "n_jobs": 10,
        "n_tools": 10,
        "magazine_size": 10,
        "variation": 100
    }

    result = {
        "n_jobs": 0,
        "n_tools": 0,
        "magazine_size": 0,
        "switches": 0,
        "tool_hops": 0,
        "tool_add_distance": 0,
        "tool_remove_distance": 0,
        "run_time": 0,
        "sequence": [],
        "tool_hops_sequence": [],
        "tool_add_distance_sequence": [],
        "tool_remove_distance_sequence": [],
        "matrix": [[]],
    }

    solution = {
        "n_jobs": 0,
        "n_tools": 0,
        "magazine_size": 0,
        "switches": 0,
        "run_time": 0,
        "sequence": [],
        "matrix": [[]]
    }

    csvLine = {
        "instance": "",
        "n_jobs": 0,
        "n_tools": 0,
        "magazine_size": 0,
        "switches": 0,
        "run_time": 0,
        "sequence": [],
    }

    def __init__(self) -> None:
        super().__init__()

    def read_solution(self, path):

        matrix = []
        with open(path) as file:

            self.solution["n_jobs"] = int(file.readline())
            self.solution["n_tools"] = int(file.readline())
            self.solution["magazine_size"] = int(file.readline())
            self.solution["switches"] = int(file.readline())
            self.solution["run_time"] = int(file.readline())
            self.solution["sequence"] = file.readline().split(" ")

            while True:
                line = file.readline()

                if line is None or line is '':
                    break

                matrix_line = line.strip().split(" ")

                matrix_line = [int(element) for element in matrix_line]
                matrix.append(matrix_line)

    def process_solutions(self):

        # create new csv file
        self.write_csv_header()

        for file in self.files["files"]:
            self.params["root_folder"] = self.files["root_path"] + file["root_folder"]
            self.params["instance"] = file["instance"]
            #todo: extract from params itself
            self.params["run_type"] = file["run_type"]
            instance_path = files["root_path"] + self.params["root_folder"] + "/" + self.params[
                "instance"] + "/" + "result_" + self.params["run_type"] + ".txt"
            self.read_solution(instance_path)
            self.write_CSV_line()

    def write_csv_header(self):
        with open(self.get_results_file_out_path(), 'w', newline='') as csvfile:
            csvwriter = csv.writer(csvfile, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
            csvwriter.writerow()

    def write_CSV_line(self):
        with open(self.get_results_file_out_path(), 'w', newline='') as csvfile:
            csvwriter = csv.writer(csvfile, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
            csvwriter.writerow(self.construct_CSV_list())

    def construct_CSV_list(self):
        out = []
        for k, v in self.csvLine.items():
            out.append(self.solution[k] or self.result[k] or self.file_descriptor[k] or self.params[k])
        return out

    def get_results_file_out_path(self):
        path = self.results_folder + "/" + self.result_filename_root + self.params["run_type"]
        return path

processor = Processor()
processor.process_solutions()
