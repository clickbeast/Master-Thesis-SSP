import os
import json
import re
import copy
import numpy  as np
from json import JSONEncoder


template = {
    "N_JOBS": 10,
    "N_TOOLS": 10,
    "MAGAZINE_SIZE": 0,
    "matrix_m": "N_JOBS",
    "matrix_n": "N_TOOLS",
    "matrix": [
        [1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0],
        [0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0],
        [0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    ],

}


class MarkedList:
    _list = None

    def __init__(self, l):
        self._list = l


class CustomJSONEncoder(JSONEncoder):
    def default(self, o):
        if isinstance(o, MarkedList):
            return "##<{}>##".format(o._list)


class Converter:
    out_folder = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP" \
                 "/data/instances/catanzaro"
    root_folder = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP" \
                  "/data/raw_instances/Catanzaro"
    files = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/catanzaro_files.json"

    rn = ["dat"]
    extension = ""
    variation_map = dict()

    file_descriptor = {
        "original": "",
        "root_folder": "catanzaro",
        "instance": "",
        "author": "cat",
        "n_jobs": 10,
        "n_tools": 10,
        "magazine_size": 10,
        "variation": 100
    }

    def __init__(self) -> None:


        super().__init__()
        self.create_out_folder()

    def create_out_folder(self):
        try:
            os.mkdir(self.out_folder)
        except OSError:
            print("Creation of the directory %s failed" % self.out_folder)

    def convert(self):
        self.root_folder = self.root_folder + "/" + "Tabela"
        root_folder_i = self.root_folder
        tabelas = [1,2,3,4]
        self.extension = ""
        r1l = ["A","B","C","D"]
        r2 = 10
        for i in range(len(tabelas)):
            root_folder_i = self.root_folder + str(tabelas[i])
            for ra in r1l:
                for rb in range(1, r2 + 1):
                    original = self.rn[0] + str(ra) + "-" + str(rb) + self.extension
                    input_file_path = root_folder_i + "/" + self.rn[0] + str(ra) + str(rb) + self.extension
                    self.file_descriptor["original"] = original
                    #print(self.file_descriptor)
                    self.extract(input_file_path, tabelas[i])



    def extract(self, input_file,tabela):
        matrix = []

        with open(input_file) as fr:

            n_jobs = int(fr.readline())
            n_tools = int(fr.readline())
            magazine_size = int(fr.readline())

            while True:
                line = fr.readline()

                if line is None or line is '':
                    break

                matrix_line = line.strip().split(" ")

                matrix_line = [int(element) for element in matrix_line]
                matrix.append(matrix_line)

            matrix_transpose = self.transpose_matrix(matrix)
            matrix_tranpose_marked = self.mark_matrix(matrix_transpose)

            self.file_descriptor["n_jobs"] = n_jobs
            self.file_descriptor["n_tools"] = n_tools
            self.file_descriptor["magazine_size"] = magazine_size
            self.file_descriptor["variation"] = self.get_variation(n_tools, n_jobs, magazine_size)
            self.file_descriptor["instance"] = self.file_descriptor["author"] + "_" + str(n_jobs) + "_" + str(
                n_tools) + "_" + str(magazine_size) + "_" + str(self.file_descriptor["variation"])

            self.addFile()
            self.add_problem(matrix_tranpose_marked, n_tools, n_jobs, magazine_size)

    def get_variation(self, n_tools, n_jobs, magazine_size):
        key = str(n_tools) + "_" + str(n_jobs) + "_" + str(magazine_size)
        self.variation_map[key] = self.variation_map.get(key, 0) + 1
        return self.variation_map[key]


    def addFile(self):
        with open(self.files, 'r') as f:
            # parsing JSON string:
            j = json.load(f)
        s = j["files"]
        s.append(self.file_descriptor)

        with open(self.files,'w') as write_file:
            json.dump(j, write_file , indent=4)


    def add_problem(self, matrix, n_tools, n_jobs, magazine_size):

        output_directory = self.out_folder.strip(" ") + "/" + self.file_descriptor["instance"]
        output_file_path = output_directory + "/" + self.file_descriptor["instance"] + ".json"

        result = copy.deepcopy(template)

        result["MAGAZINE_SIZE"] = magazine_size
        result["N_JOBS"] = n_jobs
        result["N_TOOLS"] = n_tools
        result["matrix"] = matrix

        d = json.dumps(result, indent=4, cls=CustomJSONEncoder)
        d = d.replace('"##<', "").replace('>##"', "")

        try:
            os.mkdir(output_directory)
        except OSError:
            print("Creation of the directory %s failed" % output_directory)

        with open(output_file_path, "w") as write_file:
            write_file.write(d)



    def mark_matrix(self, matrix):
        out = []
        for i in range(len(matrix)):
            out.append(MarkedList(matrix[i]))
        return out

    def transpose_matrix(self, matrix):
        a = np.array(matrix)
        a = a.transpose()

        return a.tolist()

    def get_trailing_number(self, s):
        m = re.search(r'\d+$', s)
        return int(m.group()) if m else None


converter = Converter()
converter.convert()
