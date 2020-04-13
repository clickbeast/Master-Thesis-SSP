import os

os.system("echo \"hello world\"")

files = {
    "base": "SSP",
    "run_type": "ran_swap-2job_full_sd_sw_v1_none",
    "files":
        [
            {
                "orginal": "orginal",
                "root_folder": "catanzaro",
                "author": "cat",
                "jobToolVersions": ["A", "B", "C", "D"],
                "magazineVersions": [1, 2, 3, 4],
                "variations": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
            },
            {

                "root_folder": "crama",
                "author": "crama",
                "jobToolVersions": ["A", "B", "C", "D"],
                "magazineVersions": [1, 2, 3, 4],
                "variations": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

            }, {

            "root_folder": "mecler",
            "author": "cat",
            "jobToolVersions": ["A", "B", "C", "D"],
            "magazineVersions": [1, 2, 3, 4],
            "variations": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        },

        ]
}


class Runner:
    base = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/build" \
           "/libs/Master-Thesis-SSP"

    def __init__(self) -> None:
        super().__init__()

    def run(self):

        i = 0
        jar_file = self.base + "-" + files["version"] + ".jar"
        run_type = files["run_type"]

        for file in files["files"]:
            for jobToolVersion in file["jobToolVersions"]:
                for magazineVersion in file["magazineVersions"]:
                    for variation in file["variations"]:
                        root_folder = file["root_folder"]
                        instance = file["author"] + "_" + jobToolVersion + str(magazineVersion) + "_" + str(variation)
                        command = "java -jar" + jar_file + " " + root_folder + " " + instance + " " + run_type

                        os.system("command")
                        i+=1


runner = Runner()
runner.run()
