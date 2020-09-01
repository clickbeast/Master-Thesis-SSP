from scripts.SSP_collect_files import FileCollector
import os, glob
import shutil
import datetime
import fire

root_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP"
instances_path = "data/instances/"
archive_instance_path = "data/instances_archive"
all_files_path = "data/instances/all_files.json"


class CleanFiles():
    pretext = ["result", "log", "solution", "parameter"]

    def __init__(self) -> None:
        super().__init__()
        fc = FileCollector(os.path.join(root_path, all_files_path))
        self.files = fc.files
        print("Number of instances present: {}".format(len(self.files)))

    def cleanFiles(self):

        for desc in self.files:
            print(desc["instance"])
            folder_path = root_path + "/" + instances_path + "/" + desc["root_folder"] + "/" + desc["instance"]
            destination_folder = folder_path + "/" + "archive"
            os.mkdir(destination_folder)
            for pre in self.pretext:
                for file_path in glob.glob(folder_path + "/" + pre + "*"):
                    head, tail = os.path.split(file_path)
                    shutil.move(file_path, destination_folder + "/" + tail)
                    print("File: {} , Moved to: {}".format(file_path, destination_folder + "/" + tail))

    def archive_results(self):

        for desc in self.files:
            instance_path = os.path.join(desc["root_folder"], desc["instance"])
            source_folder_path = os.path.join(root_path, instances_path, instance_path)
            destination_folder_path = self.create_destination_folder_path(instance_path)

            try:
                os.makedirs(destination_folder_path)
            except OSError as e:
                print("Error: %s - %s." % (e.filename, e.strerror))
            else:
                print("made dir: {}".format(destination_folder_path))

                for pre in self.pretext:
                    for file_path in glob.glob(source_folder_path + "/" + pre + "*"):
                        head, tail = os.path.split(file_path)
                        shutil.move(file_path, destination_folder_path + "/" + tail)
                        print("File: {} , Moved to: {}".format(file_path, destination_folder_path + "/" + tail))

    def create_destination_folder_path(self, instance_path):
        date = datetime.datetime.now().date()
        name = "archive_" + str(date)
        destination_path = os.path.join(root_path, archive_instance_path, instance_path, name)
        return destination_path


    def remove_archive_folder(self):
        for desc in self.files:
            instance_path = os.path.join(desc["root_folder"], desc["instance"])
            source_folder_path = os.path.join(root_path, instances_path, instance_path)
            archive_path = os.path.join(source_folder_path, "archive")

            print("removing: {}".format(archive_path))
            try:
                shutil.rmtree(archive_path)
            except OSError as e:
                print("Error: %s - %s." % (e.filename, e.strerror))
            else:
                print("removal done")




#cf = CleanFiles()


if __name__ == '__main__':
    fire.Fire(CleanFiles)



# cf.cleanFiles()
