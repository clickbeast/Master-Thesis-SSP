import dash
import json
import pandas as pd
import dash_html_components as html
import dash_core_components as dcc
from dash.dependencies import Input, Output
from os import walk
import plotly.express as px

defaultFile = {
    "original": "datA-1",
    "root_folder": "catanzaro",
    "instance": "cat_10_10_4_1",
    "author": "cat",
    "n_jobs": 10,
    "n_tools": 10,
    "magazine_size": 4,
    "variation": 1
}

class Instance:
    config_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/config/config.json"

    descriptor = {}
    instance_folder_path = ""

    run_types = []

    solution_template = {}
    result_template = {}
    log_template = {}

    log_path = ""
    results_path = ""
    solution_path = ""

    log = None
    results = []
    solution = {}

    def __init__(self, descriptor) -> None:
        super().__init__()
        self.read_config_file()
        self.descriptor = descriptor
        self.instance_folder_path = instances_root_folder_path + "/" + self.descriptor["root_folder"] + "/" + \
                                    self.descriptor["instance"]
        self.read_run_types()

    def read_run_types(self):

        f = []
        for (dirpath, dirnames, filenames) in walk(self.instance_folder_path):
            f.extend(filenames)
            break
        self.filter_filenames_to_run_types(f)

    def filter_filenames_to_run_types(self, files):
        for name in files:
            if name.endswith(".csv"):
                self.run_types.append(name.strip("log_").strip(".csv"))

    def select_run_type(self, type):
        self.set_file_paths(type)
        self.read_log()

    def get_run_types(self):

        return self.run_types

    def set_file_paths(self, type):
        self.results_path = self.instance_folder_path + "/" + "result_" + type + ".txt"
        self.log_path = self.instance_folder_path + "/" + "log_" + type + ".csv"
        self.solution_path = self.instance_folder_path + "/" + "solution_" + type + ".txt"

    # todo
    def read_solution(self):
        pass

    # todo
    def read_results(self):
        pass

    # todo
    def read_log(self):
        self.log = pd.read_csv(self.log_path)

    def read_config_file(self):
        with open(self.config_path) as f:
            d = json.load(f)
            self.solution_template = d["solution"]
            self.result_template = d["result"]

    def get_log(self):
        return self.log

    def get_descriptor(self):
        return self.descriptor



all_files_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP" \
                 "/data/instances/all_files.json"

all_files = []


def read_all_files():
    with open(all_files_path) as f:
        d = json.load(f)
        all_files.extend(d["files"])


external_stylesheets = ['https://codepen.io/chriddyp/pen/bWLwgP.css']

app = dash.Dash(__name__, external_stylesheets=external_stylesheets)



app.config['suppress_callback_exceptions'] = True

instances_root_folder_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances"

# get the read out instance
instance = Instance(defaultFile)

app.layout = html.Div([
    dcc.Tabs(id="tabs", value='tab-1', children=[
        dcc.Tab(label='Multiple Instances', value='tab-1'),
        dcc.Tab(label='Single Instance', value='tab-2'),
    ]),
    html.Div(id='tabs-content')
])


@app.callback(Output('tabs-content', 'children'),
              [Input('tabs', 'value')])
def render_content(tab):
    if tab == 'tab-1':
        return html.Div([
            "hello"
        ])
    elif tab == 'tab-2':
        return html.Div(
            [get_single_instance_tab()]
        )


def get_single_instance_tab():
    return html.Div([get_instance_selection(),get_run_type_selection(),get_instance_graph()])


def construct_instance_dropdown_options():
    options = []
    for desc in all_files:
        option = {
            'label': desc["instance"],
            'value': desc["instance"]
        }
        options.append(option)
    return options


def get_instance_selection():
    # read the instances that are available
    read_all_files()

    options = construct_instance_dropdown_options()

    instance = dcc.Dropdown(
        id="instance-dropdown",
        options=options,
        disabled=False,
        multi=True,
    )
    return html.Div(instance)


def get_run_type_selection():
    run_type = dcc.Dropdown(
        id="run-type-dropdown",
        disabled=True,
        multi=True,
    )
    return html.Div(run_type)


def get_instance_graph():
    switches = dcc.Graph(id="switches-graph")

    reject_accept_improve = dcc.Graph(id="reject_accept_improve-graph")
    sa_temp = dcc.Graph(id="sa_temp-graph")

    return html.Div([switches,reject_accept_improve, sa_temp])


def construct_run_type_dropdown_options(run_types):
    options = []
    for type in run_types:
        option = {
            'label': type,
            'value': type
        }
        options.append(option)
    return options

@app.callback(
    [Output('run-type-dropdown', 'disabled'),Output('run-type-dropdown', 'options')],
    [Input('instance-dropdown', 'value')])
def update_run_type_dropdown(instance_name):

    desc = [d for d in all_files if d["instance"] in instance_name]

    instance = Instance(desc[0])


    disabled=False
    options = construct_run_type_dropdown_options(instance.get_run_types())
    return [disabled, options]



@app.callback(
    [Output('switches-graph', 'figure'),
     Output('reject_accept_improve-graph', 'figure'),
     Output('sa_temp-graph', 'figure')],
    [Input('run-type-dropdown', 'value')])



def update_graph(run_type):

    added_instances = []

    for rt in run_type:
        add = Instance(instance.get_descriptor())
        add.select_run_type(rt)
        added_instances.append(add)



    graphs = []

    for ain in added_instances:
        graphs.append(dict(
            x=list(ain.get_log().T_RUN),
            y=list(ain.get_log().SW),
            name=ain.get_descriptor()["instance"],
        ))

    if len(run_type) >= 1:
        instance.select_run_type(run_type[0])
        #instance2name = instance.get_descriptor()["instance"]

        #instance.get_descriptor()["instance"]
        figure = dict(
            data=graphs,
            layout=dict(
                title="#Switches",
                showlegend=True,
                legend=dict(
                    x=0,
                    y=1.0
                ),
                margin=dict(l=40, r=0, t=40, b=30)
            )
        )


        reject_accept_improve = dict(
            data=[
                dict(
                    legendgroup="g1",
                    x=list(instance.get_log().T_RUN),
                    y=list(instance.get_log().REJECT),
                    name="REJECT",
                    marker=dict(
                        color='rgb(55, 83, 109)'
                    )
                ),
                dict(
                    legendgroup="g1",
                    x=list(instance.get_log().T_RUN),
                    y=list(instance.get_log().ACCEPT),
                    name="ACCEPT",
                ),
                dict(
                    legendgroup="g1",
                    x=list(instance.get_log().T_RUN),
                    y=list(instance.get_log().IMPROVE),
                    name="IMPROVED",
                )
            ],
            layout=dict(
                title="#Rejected vs #Accepted vs #Improved",
                showlegend=True,
                legend=dict(
                    x=0,
                    y=1.0
                ),
                margin=dict(l=40, r=0, t=40, b=30)
            )
        )


        sa_temp = dict(
            data=[
                dict(
                    x=list(instance.get_log().T_RUN),
                    y=list(instance.get_log().TEMP),
                    name=instance.get_descriptor()["instance"],
                )
            ],
            layout=dict(
                title="Temperature SA",
                showlegend=True,
                legend=dict(
                    x=0,
                    y=1.0
                ),
                margin=dict(l=40, r=0, t=40, b=30)
            )
        )



        return [figure, reject_accept_improve, sa_temp]



    return [{}, {},{}]


def readFiles():
    return None


if __name__ == '__main__':
    app.run_server(debug=True)
