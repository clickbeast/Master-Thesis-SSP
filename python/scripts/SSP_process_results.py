import csv
import json

#params

files = []
with open('path_to_file/person.json') as f:
    files = json.load(f)

outputFolder = ""


csvLine = {
    "jobs": 0,
    "tools": 0,
    "magazine_size": 0,
    "sequence": [],
    "switches": 0,
}

class Processor:
    def __init__(self) -> None:
        super().__init__()


    def read_result(self, path):


         with open(path) as result_file:
            jobs = int(result_file.readline())
            tools = int(result_file.readline())
            magazine_size = int(result_file.readline())
            sequence = result_file.readline().split(" ")
            switches =  int(result_file.readline())

            return {"jobs": jobs, "tools": tools, }


    def process(self):
        with open('allResults.csv', 'w', newline='') as csvfile:
            csvwriter = csv.writer(csvfile, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
            csvwriter.writerow([])





processor = Processor()
processor.process()