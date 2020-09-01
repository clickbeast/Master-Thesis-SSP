import csv
import json

# params
config_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/config/config.json"


class Processor:
    results_folder = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis" \
                     "-SSP/data/results"
    result_filename_root = "results_"

    files_path_base = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis" \
                      "-SSP/data/instances"
    files_path = files_path_base + "/" + "catanzaro_files.json"
    files = {}

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


    def extract_sequence(self, line):
        line = line.replace('[','').replace(']','').replace(' ', '').strip().split(",")

        return [int(element) for element in line]

    def read_solution(self, path):

        matrix = []
        with open(path) as file:

            self.solution["n_jobs"] = int(file.readline())
            self.solution["n_tools"] = int(file.readline())
            self.solution["magazine_size"] = int(file.readline())
            self.solution["switches"] = int(file.readline())
            self.solution["tool_hops"] = int(file.readline())
            self.solution["tool_add_distance"] = int(file.readline())
            self.solution["tool_remove_distance"] = int(file.readline())
            self.solution["run_time"] = int(file.readline())
            self.solution["sequence"] = self.extract_sequence(file.readline())
            self.solution["tool_hops_sequence"] = self.extract_sequence(file.readline())
            self.solution["tool_add_distance_sequence"] = self.extract_sequence(file.readline())
            self.solution["tool_remove_distance_sequence"] = self.extract_sequence(file.readline())

            while True:
                line = file.readline()

                if line is None or line is '':
                    break

                matrix_line = line.strip().split(" ")

                matrix_line = [int(element) for element in matrix_line]
                matrix.append(matrix_line)


    def process_solutions(self):

        # out = {}
        #
        # out["files_folder_path"] = ""
        # out["files_name"] = ""
        # out["title"] = self.title
        # out["params"] = self.params
        # out["filter"] = self.filter
        # out["solution"] = self.solution
        # out["result"] = self.result
        # out["csv_line"] = self.csvLine
        #
        #
        #
        # d = json.dumps(out, indent=4)
        #
        # with open("/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/config/config.json", "w") as f:
        #     f.write(d)



        # create new csv file
        self.write_csv_header()

        for file in self.files["files"]:
            self.file_descriptor = file
            self.params["root_folder"] = self.files["root_path"] + file["root_folder"]
            self.params["instance"] = file["instance"]
            # todo: extract from params itself
            #self.params["run_type"] = file["run_type"]
            instance_path = self.params["root_folder"] + "/" + self.params[
                "instance"] + "/" + "solution_" + self.params["run_type"] + ".txt"
            self.read_solution(instance_path)
            if self.solution_allowed():
                self.write_CSV_line()


    def solution_allowed(self):
        for k, range in self.filter.items():
            if k in self.solution:
                if self.solution.get(k) < range[0] or self.solution.get(k) > range[1]:
                    return False
            elif k in self.file_descriptor:
                if self.file_descriptor.get(k) < range[0] or self.file_descriptor.get(k) > range[1]:
                    return False
        return True

    def write_csv_header(self):

        print(self.get_results_file_out_path())
        with open(self.get_results_file_out_path(), 'w', newline='') as csvfile:
            csvwriter = csv.writer(csvfile, delimiter=',',quoting=csv.QUOTE_MINIMAL)
            csvwriter.writerow(self.csv_line.keys())

    def write_CSV_line(self):
        print(self.get_results_file_out_path())

        with open(self.get_results_file_out_path(), 'a', newline='') as csvfile:
            csvwriter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_MINIMAL)
            csvwriter.writerow(self.construct_CSV_list())

    def construct_CSV_list(self):
        out = []
        dicts = [self.file_descriptor,self.params, self.solution]
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

    def get_results_file_out_path(self):
        path = self.results_folder + "/" + self.result_filename_root + self.title + "_" + self.params["run_type"] + ".csv"
        return path

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


processor = Processor(config_path)
processor.process_solutions()
