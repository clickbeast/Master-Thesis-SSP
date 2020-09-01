
import csv
import json
import copy
import os
import pandas as pd
import yaml
from ssp_dashboard.instance import Instance, InstanceGroup, LiveInstance, InstanceCollector, InstanceCollection, Lookup
import os.path as path
from tqdm import tqdm
#
# DEFAULTS
#


root_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP"
rfp_multi_config = "data/config/configs.yaml"
rfp_config_path = "data/config/config.yaml"
rdp_instances = "data/instances"
rdp_results = "data/results"

fp_result_config = os.path.join(root_path, "data/config/result_config.yaml")

columnsRAW = [
    'instance',
    'run_type',
    'run',
    'n_jobs',
    'n_tools',
    'magazine_size',
    'variation',
    'n_switches',
    'run_time',
]

default_config = {
    "root_path": "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP",
    "files": "all_files.json",
    "bundle_variations": False,
    "bundle_runs": False,
    "use_run_type": False,
    "run_type": '',
    "use_filter": '',
    "filter": {
        "author": "cat",
        "n_jobs": 10,
        "n_tools": 10,
        "magazine_size": 4,
        "variation": 1,
        "run": 0
    }
}


# ILS = iterated Local Search
# DQGA = Dynamic Q Learning based Genetic Algorithm
# HGS = Hybrid Genetic Search
# LS = Local Search


class Processor():

    def __init__(self) -> None:
        super().__init__()
        self.config = default_config
        self.instances: [Instance] = []
        self.files = []

    def read_references(self):
        df = pd.DataFrame({
            'idx': ['ILS', 'DQGA', 'HGS-SSP'],
            'dfs': []
        })

    def read_files(self, path):
        with open(path, 'r') as f:
            # parsing JSON string:
            self.files = json.load(f)["files"]

    def read_config_file(self):
        with open(fp_result_config, 'r') as file:
            try:
                self.config = yaml.safe_load(file)
            except yaml.YAMLError as exc:
                print(exc)

    def collect_raw_df(self):
        dfa = pd.DataFrame(columns=columnsRAW)

        # Collected instances , RAW FORMAT

        instances: [Instance] = []

        lookUp = Lookup()

        for desc in tqdm(self.files):
            found_instances: [Instance] = []

            if self.config["use_run_type"]:
                found_instances.extend(lookUp.get_instances_desc(desc, self.config["run_type"], solution_only=True))
            else:
                found_instances.extend(lookUp.get_instances_desc(desc, solution_only=True))

            if self.config["use_filter"]:
                for instance in found_instances:
                    if self.instance_allowed(instance, self.config["filter"]):
                        instances.append(instance)
            else:
                instances.extend(found_instances)

        for instance in instances:
            if instance.read_error:
                print("Skipping: {} , read error".format(instance.descriptor["instance"]))
                continue
            data = {
                'instance': instance.descriptor["instance"],
                'author': instance.descriptor["author"],
                'n_jobs': int(instance.descriptor["n_jobs"]),
                'n_tools': instance.descriptor["n_tools"],
                'magazine_size': instance.descriptor["magazine_size"],
                'variation': instance.descriptor["variation"],
                'run': instance.run,
                'run_type': instance.run_type,
                'n_switches': instance.solution.n_switches,
                'run_time': instance.solution.time_running,
            }
            # print(data)
            dfa = dfa.append(data, ignore_index=True)

        # set time column to seconds
        dfa.run_time = dfa.run_time.div(1000)

        return dfa

    def instance_allowed(self, instance: Instance, filter):

        # author
        if 'run_type' in filter:
            if instance.descriptor["run_type"] != filter["run_type"]:
                return False

        # author
        if 'author' in filter:
            if instance.descriptor["author"] != filter["author"]:
                return False

        # jobs
        if 'n_jobs' in filter:
            if not self.between_interval(instance.run, filter["n_jobs"]):
                return False

        # tools
        if 'n_tools' in filter:

            if not self.between_interval(instance.run, filter["n_tools"]):
                return False

        # magazine
        if 'magazine_size' in filter:

            if not self.between_interval(instance.run, filter["magazine_size"]):
                return False

        # variation
        if 'variation' in filter:

            if not self.between_interval(instance.run, filter["variation"]):
                return False

        # run
        if 'run' in filter:
            if not self.between_interval(instance.run, filter["run"]):
                return False

        return True

    def between_interval(self, ref, interval):
        if isinstance(interval, int):
            interval = [interval, interval]
        if ref < interval[0] or ref > interval[1]:
            return False

        return True

    def process(self, use_config_file=True):
        if use_config_file:
            self.read_config_file()

        # 0) Get the files needed
        self.read_files(os.path.join(root_path, "data/instances", self.config["files"]))

        dfr = pd.read_csv(os.path.join(root_path, "data/reference/reference.csv"))

        dfa = self.collect_raw_df()

        dfa.to_csv(os.path.join(root_path,"data/results/temp.csv"), mode='w', header=True, index=False)
        self.process_runs_variations_merged(dfa)


    def process_runs_variations_merged(self, df):

        df = pd.read_csv(path.join(root_path, "data/results/temp.csv"))

        a = {
            'n_switches': 'mean',
            'run_time': 'mean'
        }

        variationsGrouped = df.groupby(["author", "n_jobs", "n_tools", "magazine_size", "run", "run_type"],
                                       as_index=False).agg(a)
        variationsGrouped

        # %%

        ab = {
            'n_switches': ['min', 'mean'],
            'run_time': ['min', 'mean'],
        }

        runsGrouped = variationsGrouped.groupby(["author", "n_jobs", "n_tools", "magazine_size", "run_type"],
                                                as_index=False).agg(ab)
        runsGrouped

        # %%

        # rename and unpack

        # %%

        reference = pd.read_csv(path.join(root_path, "data/reference/reference.csv"))
        reference

        # %%

        reference = reference.set_index(['author', 'n', 'm', 'C'])
        reference

        # %%

        multi: pd.DataFrame = runsGrouped.set_index(['author', 'n_jobs', 'n_tools', 'magazine_size'])

        multi

        # %%

        merged = pd.merge(multi, reference, left_index=True, right_on=['author', 'n', 'm', 'C'])
        merged

        # %%

        merged = merged.rename(columns={
            ('run_type', ''): 'run_type',
            ('n_switches', 'min'): 'Best_LS',
            ('n_switches', 'mean'): 'Avg_LS',
            ('run_time', 'min'): 'T_LS',
            ('run_time', 'mean'): 'T_LS_AVG',

        })
        merged

        # %%

        merged.drop(merged.columns[[4]], axis=1, inplace=True)
        # print(merged.to_latex(index=True))
        merged

        # %%

        # keep min value and add the percentage difference
        # col = ['n',
        #        'm',
        #        'C',
        #        'i',
        #        'Best_ILS',
        #        'Avg_ILS',
        #        'T_ILS',
        #        'Best_DQGA',
        #        'Avg_DQGA',
        #        'T_DQGA',
        #        'Best_HGS',
        #        'Avg_HGS',
        #        'T_HGS'
        #        ]
        #
        min_sw_col = ['Best_ILS', 'Avg_ILS', 'Best_DQGA', 'Avg_DQGA', 'Best_HGS', 'Avg_HGS']
        min_t_col = ['T_ILS', 'T_DQGA', 'T_HGS']
        #
        merged['Best_C'] = merged[min_sw_col].min(axis=1)
        merged['T_C'] = merged[min_t_col].min(axis=1)

        merged

        # %%

        #merged.drop(min_sw_col, axis=1, inplace=True)
        #merged.drop(min_t_col, axis=1, inplace=True)
        merged

        # %%

        merged['Best_DIF'] = (1 - (merged['Best_LS'] / merged['Best_C'])) * 100
        merged['T_DIF'] = (1 - (merged['T_LS'] / merged['T_C'])) * 100
        merged

        merged = merged.round(2)

        out_path = os.path.join(root_path, "data/results/", self.get_filename())

        merged.to_csv(out_path, mode='w', header=True, index=True)


        self.open(out_path)



    def open(self, path):
        command = "open" + " " + path
        os.system(command)

    def get_filename(self):

        if isinstance(self.config["run_type"], list):
            rt = str(self.config["run_type"]).replace('[','').replace(']','').replace(',','+').replace("'","").replace(" ","")
        else:
            rt = self.config["run_type"]

        path = "results_" + self.config["title"] + "_" + rt + ".csv"
        print(path)
        return path


if __name__  == "__main__":
    p = Processor()
    p.process()