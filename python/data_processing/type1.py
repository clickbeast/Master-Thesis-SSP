from os import listdir
import os
import json
import re
import copy
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


input_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/raw_instances"
output_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data" \
              "/instances "
fileNames = [f for f in listdir(input_path) if isfile(join(input_path, f))]

temp_tool = {
    "id": 0,
    "loadingTime": -1,
    "priority": -1,
    "size": -1,
    "maintenanceLevel": 0,
    "parallel": []
}

temp_magazine = {
    "id": 0,
    "magazineSize": 10,
    "slotSize": []
}

temp_job = {
    "id": 0,
    "time": 0,
    "priority": 0,
    "similarity": []
}

temp_machine = {
    "id": 0,
    "magazines": [0]
}

# define all the different params of the problem
problemParams = {
    "uniform": True,
    "dynamic": False,
    "jobSplitting": False,
    "jobPriority": False,
    "toolPriority": False,
    "toolSize": False,
    "slotSize": False,
    "multipleMachines": False,
    "multipleMagazines": False,
    "sharedMagazine": False,
    "toolMaintenance": False,
    "toolLoadingCost": False,
    "parallelToolLoading": False,
    "jobTime": False,
    "parallelExecutionAndLoading": False
}

template = {
    "N_TOOLS": 10,
    "N_JOBS": 10,
    "SEED": 7,
    "BEST_VALUE": 0,
    "allowJobSplit": False,
    "jobSplitNeeded": False,

    "problemParams": problemParams,

    "objective": [{
        "min_switches": True,
        "min_time_span": False,
        "min_machine_stops": False,
        "priority": []
    }],

    "machines": [{
        "id": 0,
        "magazines": [0]
    }],

    "magazines": [temp_magazine],

    "tools": [temp_tool],
    "jobs": [temp_job],
    "matrix": [
        [1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0],
        [0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0],
        [0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    ],

}


def extract(p, f):
    cpath = p + "/" + f
    count = 0


    with open(cpath) as fr:
        while True:
            line = fr.readline()
            print("s")
            if line.startswith("c="):
                magazine_size = get_trailing_number(line)
                print("yas")
                break
        print("yas")
        while fr:
            line = setUntilFirstMatrixLine(fr)
            print(line)
            if line is None:
                break
            result = extractProblem(fr, line)
            name = f + "_" + str(count)
            addProblem(result[0], result[1], name, magazine_size)
            count += 1


def setUntilFirstMatrixLine(fr):
    for line in fr:
        print("bonjour")
        if line.startswith("0") | line.startswith("1"):
            return line

    return None


def extractProblem(file, line):
    matrix = []
    while line.startswith("0") | line.startswith("1"):
        matrixLine = line.strip().split(" ")
        matrixLine = [int(element) for element in matrixLine]
        matrix.append(MarkedList(matrixLine))
        line = file.readline()

    # extract best value
    while not line.startswith("best known value"):
        line = file.readline()
    bestValue = get_trailing_number(line)

    return (matrix, bestValue)


def get_trailing_number(s):
    m = re.search(r'\d+$', s)
    return int(m.group()) if m else None


def addProblem(matrix, bestValue, name, magazine_size):
    result = copy.deepcopy(template)
    magazine = copy.deepcopy(template["magazines"][0])
    magazine["magazineSize"] = magazine_size

    result["BEST_VALUE"] = bestValue
    result["N_JOBS"] = len(matrix)
    result["N_TOOLS"] = len(matrix[0]._list)
    result["matrix"] = matrix

    result["magazines"] = magazine

    d = json.dumps(result, indent=4, cls=CustomJSONEncoder)
    d = d.replace('"##<', "").replace('>##"', "")

    name = name.replace("dat","DAT_")

    target_path = output_path.strip(" ") + "/" + name
    print("bonsour")
    try:
        os.mkdir(target_path)
    except OSError:
        print ("Creation of the directory %s failed" % target_path)

    with open(target_path + "/" + name + ".json", "w") as write_file:
        write_file.write(d)




for n in fileNames:
    extract(input_path, n)
