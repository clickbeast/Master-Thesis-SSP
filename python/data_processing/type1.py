from os import listdir
import json
import re
import copy
from json_tricks import dump, dumps, load, loads, strip_comments

from os.path import isfile, join

path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances"
resultPath = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/json"
fileNames = [f for f in listdir(path) if isfile(join(path, f))]

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
    "BEST_VALUE":  0,
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
    with open(cpath) as fr:
        while True:
            line = setUntilFirstMatrixLine(fr)
            if line is None:
                break
            result = extractProblem(fr, line)
            addProblem(result[0], result[1], f)



def setUntilFirstMatrixLine(fr):
    while fr:
        line = fr.readline()
        if line.startswith("0") | line.startswith("1"):
            return line

    return None


def extractProblem(file, line):
    matrix = []
    while line.startswith("0") | line.startswith("1"):
        matrixLine = line.strip().split(" ")
        matrix.append(matrixLine)
        line = file.readline()

    # extract best value
    while not line.startswith("best known value"):
        line = file.readline()
    bestValue = get_trailing_number(line)

    return (matrix, bestValue)


def get_trailing_number(s):
    m = re.search(r'\d+$', s)
    return int(m.group()) if m else None


def addProblem(matrix, bestValue, name):
    result = copy.deepcopy(template)
    magazine = copy.deepcopy(template["magazines"][0])

    result["BEST_VALUE"] = bestValue
    result["N_JOBS"] = len(matrix)
    result["N_TOOLS"] = len(matrix[0])
    result["matrix"] = matrix

    result["magazines"] = magazine
    with open(name + ".json", "w") as write_file:
        json.dump(result, write_file, indent=4)





def parseArrayToSingleLine(file):
    return None

for fileName in fileNames:
    extract(path, fileName)
