import dash
import json
import ssp_test
import dash_html_components as html
import dash_core_components as dcc
from dash.dependencies import Input, Output
from ssp_dashboard.instance import Instance
import os

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


all_files_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/all_files.json"

all_files = []



def create_project_root_path():
    path = os.path.dirname(__file__)
    print(path)
    while os.path.split(path)[1] != "Master-Thesis-SSP":
        path = os.path.dirname(path)

    return path


def read_all_files():
    with open(all_files_path) as f:
        d = json.load(f)
        all_files.extend(d["files"])



external_stylesheets = ['https://codepen.io/chriddyp/pen/bWLwgP.css']
app = dash.Dash(__name__, external_stylesheets=external_stylesheets)
app.config['suppress_callback_exceptions'] = True
instances_root_folder_path = "/data/instances"

# get the read out instance
instance = Instance(instances_root_folder_path, defaultFile, )

app.layout = html.Div([
    dcc.Tabs(id="tabs", value='tab-1', children=[
        dcc.Tab(label='Multiple Instances', value='tab-1'),
        dcc.Tab(label='Single Instance', value='tab-2'),
    ]),
    html.Div(id='tabs-content')
])


j = {
    "bonsoir": "bonjour"
}

@app.callback(Output('tabs-content', 'children'),
              [Input('tabs', 'value')])
def render_content(tab):
    if tab == 'tab-1':
        return html.Div([
            ssp_test.SspTest(
                id='input',
                value='my-value',
                label='my-label',
                json=j
            )
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
    instance = Instance(desc[0],,
    disabled=False
    options = construct_run_type_dropdown_options(instance.get_run_types())
    return [disabled, options]




def readFiles():
    return None


if __name__ == '__main__':
    app.run_server(debug=True)
