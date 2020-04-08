from os import listdir
import os
import json
import re
import copy
import numpy  as np
from json import JSONEncoder
from os.path import isfile, join


class MarkedList:
    _list = None

    def __init__(self, l):
        self._list = l


class CustomJSONEncoder(JSONEncoder):
    def default(self, o):
        if isinstance(o, MarkedList):
            return "##<{}>##".format(o._list)


base_input_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP" \
                  "/data/raw_instances/Mecler/Tabela4"

base_output_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data" \
                   "/instances/mecler"


def generateFileNames():
    input_base = "F300"
    output_base = "Mecler_C4_"

    files = []

    for a in range(1, 6):
        file = {
            "input_file_path": base_input_path + "/" + input_base + str(a) + ".txt",
            "output_path": base_output_path,
            "output_name": output_base + str(a)
        }

        files.append(file)
    return files


template = {
    "N_JOBS": 10,
    "N_TOOLS": 10,
    "MAGAZINE_SIZE": 0,
    "matrix_m": "N_JOBS",
    "matrix_n": "N_TOOLS",
    "SEED": 7,
    "matrix": [
        [1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0],
        [0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0],
        [0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    ],

}


def extract(file):
    matrix = []

    with open(file["input_file_path"]) as fr:
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

        matrix_transpose = transposeMatrix(matrix)
        matrix_tranpose_marked = markMatrix(matrix_transpose)

        addProblem(file, matrix_tranpose_marked, n_tools, n_jobs, magazine_size)


def markMatrix(matrix):
    out = []
    for i in range(len(matrix)):
        out.append(MarkedList(matrix[i]))
    return out


def transposeMatrix(matrix):
    a = np.array(matrix)
    a = a.transpose()

    return a.tolist()


def get_trailing_number(s):
    m = re.search(r'\d+$', s)
    return int(m.group()) if m else None


def addProblem(file, matrix, n_tools, n_jobs, magazine_size):
    output_directory = file["output_path"].strip(" ") + "/" + file["output_name"]

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

    with open(output_directory + "/" + file["output_name"] + ".json", "w") as write_file:
        write_file.write(d)

    print("done")


file_names = generateFileNames()

for file in file_names:
    extract(file)
