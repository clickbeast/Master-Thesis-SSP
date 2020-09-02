import dash
import json
import dash_html_components as html
import dash_core_components as dcc
from dash.dependencies import Input, Output
from typing import List
from ssp_dashboard.instance import Instance, InstanceGroup, LiveInstance, transpose_matrix
import os
import ssp_matrix
import ssp_vector_sequence
import ssp_json
import dash_daq as daq

from ssp_dashboard.view.view_data import *
from ssp_dashboard.view.view_graph import *
from ssp_dashboard.view.live_view import *


class State:
    default_file_descriptor = {
        "original": "datA-1",
        "root_folder": "catanzaro",
        "instance": "cat_10_10_4_1",
        "author": "cat",
        "n_jobs": 10,
        "n_tools": 10,
        "magazine_size": 4,
        "variation": 1
    }
    project_root_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/"
    all_files_path = os.path.join(project_root_path, "data/instances/all_files.json")
    all_files = []

    #
    # DATA SOURCES CONFIG - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
    #

    instance_group: InstanceGroup = None
    instance: Instance = None
    instances: List[Instance]
    instance_live: LiveInstance = None
    run_types = []

    def __init__(self) -> None:
        super().__init__()
        self.read_all_files()
        self.instance_live = LiveInstance()
        self.setInstanceFromState()

    def setInstanceFromState(self):
        self.instance = getInstanceFromState()

        if self.instance.read_error:
            self.instance = None

        self.instances = []
        self.instances.append(self.instance)

    def read_all_files(self):
        with open(self.all_files_path) as f:
            d = json.load(f)
            self.all_files.extend(d["files"])


#
# PARAMETERS - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#

state = State()
external_stylesheets = ['/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis'
                        '-SSP/python/ssp_dashboard/style.css']

#
# DASH CONFIG - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#

app = dash.Dash(__name__)

app.config['suppress_callback_exceptions'] = True

app.layout = html.Div([
    dcc.Tabs(id="tabs", value='tab-1', children=[
        dcc.Tab(label='Single Instance', value='tab-1'),
        dcc.Tab(label='Live View', value='tab-3'),
        dcc.Tab(label='Multiple Instances', value='tab-2'),
    ]),
    html.Div(id='tabs-content')
])


#
# DASH COMPONENTS - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#

@app.callback(Output('tabs-content', 'children'),
              [Input('tabs', 'value')])
def render_root(tab):
    if tab == 'tab-2':
        return html.Div([
            render_multi_instance()
        ])
    elif tab == 'tab-1':
        return html.Div([
            render_instance()
        ])

    elif tab == 'tab-3':
        return html.Div([
            render_live_data_view()
        ])


#
# MULTI INSTANCE VIEW - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#


def render_live_data_view():
    return html.Div([html.Div(id="live-data-view", children=render_live_data()), dcc.Interval(
        id='interval-component',
        interval=1 * 300,  # in milliseconds
        n_intervals=0
    )])


def render_live_data():
    return html.Div([
        render_live_data_elements(state)
    ])


@app.callback(Output('live-data-view', 'children'),
              [Input('interval-component', 'n_intervals')])
def update_metrics(n):
    state.instance_live.reload()
    return render_live_data()


#
# LIVE INSTANCE VIEW - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#

def render_multi_instance():
    return html.Div([
    ])


#
# INSTANCE VIEW - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#

def render_instance():
    return html.Div([
        render_instance_config()
        ,
        dcc.Tabs(id="tabs-instance-view", value='tab-1', children=[
            dcc.Tab(label='Charts', value='tab-1'),
            dcc.Tab(label='Data', value='tab-2'),
        ]),
        html.Div(id='tabs-instance-view-content')

    ])


def render_instance_config():
    return html.Div([
        html.Button('Default', id='button-default'), html.Br(), render_instance_selection(), html.Br(),
        render_run_type_selection(), html.Br()
    ])


@app.callback(
    [Output('tabs', 'value')], [Input('button-default', 'n_clicks')])
def set_default(n_clicks):
    state.setInstanceFromState()
    return ['tab-1']


def construct_instance_dropdown_options():
    options = []
    for desc in state.all_files:
        option = {
            'label': desc["instance"],
            'value': desc["instance"]
        }
        options.append(option)
    return options


def render_instance_selection():
    options = construct_instance_dropdown_options()

    instance = dcc.Dropdown(
        id="instance-dropdown",
        options=options,
        disabled=False,
        multi=True,
    )
    return html.Div(instance)


def render_run_type_selection():
    run_type = dcc.Dropdown(
        id="run-type-dropdown",
        disabled=True,
        multi=True,
    )
    return html.Div(run_type)


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
    [Output('run-type-dropdown', 'disabled'), Output('run-type-dropdown', 'options')],
    [Input('instance-dropdown', 'value')])
def update_run_type_dropdown(instance_name):
    desc = [d for d in state.all_files if d["instance"] in instance_name]
    if len(desc) > 0:
        state.instance_group = InstanceGroup(desc[0])
        disabled = False
        options = construct_run_type_dropdown_options(state.instance_group.run_types)

        return [disabled, options]

    return [True, []]


@app.callback(Output('tabs-instance-view-content', 'children'),
              [Input('tabs-instance-view', 'value')])
def update_instance(tab):
    if tab == 'tab-1':
        return html.Div([
            render_charts()
        ])
    elif tab == 'tab-2':
        return html.Div([
            render_data()
        ])


#
# INSTANCE VIEW : Charts - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#


def render_charts():
    switches = dcc.Graph(id="switches-graph")
    cost_reject_accept_improve = dcc.Graph(id="cost_reject_accept_improve_graph")
    reject_accept_improve = dcc.Graph(id="reject_accept_improve-graph")
    sa_temp = dcc.Graph(id="sa_temp-graph")
    bar = dcc.Graph(id="bar_graph")

    if state.instance is not None:
        switches = dcc.Graph(id="switches-graph",
                             figure=get_figure_switches(state.instances))
        cost_reject_accept_improve = dcc.Graph(id="cost_reject_accept_improve_graph",
                                               figure=get_figure_cost_reject_accept_improve(state.instance))
        reject_accept_improve = dcc.Graph(id="reject_accept_improve-graph",
                                          figure=get_figure_reject_accept_improve(state.instance))
        sa_temp = dcc.Graph(id="sa_temp-graph",
                            figure=get_figure_sa_temp(state.instance))

        bar = dcc.Graph(id="bar_graph",
                        figure=get_figure_bar_graph(state.instances))

    return html.Div(className="graphContainer",
                    children=[
                        cost_reject_accept_improve,
                        html.Div(children=[switches]),
                        reject_accept_improve,
                        sa_temp,
                        bar
                    ])


@app.callback(
    [Output('cost_reject_accept_improve_graph', 'figure'),
     Output('switches-graph', 'figure'),
     Output('reject_accept_improve-graph', 'figure'),
     Output('bar_graph', 'figure'),
     Output('sa_temp-graph', 'figure')],
    [Input('run-type-dropdown', 'value')])
def update_graph(run_types):
    added_instances = []

    for rt in run_types:
        added_instances.append(state.instance_group.get_instance(rt))

    if len(run_types) >= 1:
        state.instance = added_instances[0]
        state.instances = added_instances
        instance = state.instance
        state.run_types = run_types

        return [get_figure_cost_reject_accept_improve(instance), get_figure_switches(added_instances),
                get_figure_sa_temp(instance)
            , get_figure_reject_accept_improve(instance), get_figure_bar_graph(added_instances)]

    return [{}, {}, {}, {}, {}]


#
# INSTANCE VIEW : Data - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#


def render_result_matrix_data(instance):
    if instance is not None:
        marks = {i: '{}'.format(i) for i in range(len(instance.results))}

        return html.Div([
            dcc.Slider(
                id='slider-matrix',
                min=0,
                marks=marks,
                max=len(instance.results) - 1,
                value=0,
                step=1,
                updatemode='drag'
            )
            ,
            ssp_matrix.Matrix(id="result-sequence-matrix",
                              data=get_solution_matrix_data(instance.solution, instance))
        ])

    return ""


@app.callback([Output('result-sequence-matrix', 'data'), Output('gra-bar', 'value'),
               Output('results-switches', 'children')],
              [Input('slider-matrix', 'value')])
def update_result_matrix_data(value):

    prev_r = None

    if 0 < value < len(state.instance.results):
        prev_r = state.instance.results[value-1]

    data = get_solution_matrix_data(state.instance.results[value], state.instance, prev_result=prev_r)
    switches = "Switches: " + str(state.instance.results[value].n_switches)
    value = max(0, min(10, (state.instance.results[value].n_switches / state.instance.solution.n_switches) * 10))

    return [data, value, switches]


def render_result_magazine_data(instance):
    if instance is not None:
        marks = {i: '{}'.format(i) for i in range(len(instance.results))}

        return html.Div([
            dcc.Slider(
                id='slider-vector',
                min=0,
                marks=marks,
                max=len(instance.results) - 1,
                value=0,
                step=1,
                updatemode='drag'
            ),
            ssp_vector_sequence.VectorSequence(id="result-magazine-state",
                                               data=get_solution_vector_data(instance.solution))
        ])

    return ""


@app.callback(Output('result-magazine-state', 'data'),
              [Input('slider-vector', 'value')])
def update_result_magazine_data(value):
    data = get_solution_vector_data(state.instance.results[value])
    return data

def render_solution_matrix_data(instance):
    if instance is not None:
        return html.Div([ssp_matrix.Matrix(id="solution-sequence-matrix",
                                           data=get_solution_matrix_data(instance.solution, instance))])
    return ""


def render_solution_vector(instance):
    if instance is not None:
        return html.Div([ssp_vector_sequence.VectorSequence(id="solution-magazine-state",
                                                            data=get_solution_vector_data(instance.solution))])
    return ""


def render_job_tool_matrix(instance):
    if instance is not None:
        return html.Div([ssp_matrix.Matrix(id="job-tool-matrix",
                                           data=get_job_tool_matrix_data(instance))])
    return ""


def render_job_tool_matrix_sequenced(instance):
    if instance is not None:
        return html.Div([ssp_matrix.Matrix(id="job-tool-matrix-sequenced",
                                           data=get_job_tool_matrix_data_sequenced(instance))])
    return ""


def render_parameters(instance):
    if instance is not None:
        data = dict(
            data=instance.parameters
        )

        return html.Div([ssp_json.JsonView(id="parameters", data=data)])
    return ""


def render_data():
    return html.Div([html.Div([

        html.Div(className="group", children=[
            encapsulate([html.H3(children="JobTool Matrix"),
                         render_job_tool_matrix(state.instance)]),

            encapsulate([html.H3(children="JobTool Matrix: Sequenced"),
                         render_job_tool_matrix_sequenced(state.instance)]),
        ]),

        encapsulate([html.H3(children="Solution: Sequence Matrix"),
                     html.H3(children="Switches: " + str(state.instance.solution.n_switches),
                             style={'color': '#007AFF'}),
                     render_solution_matrix_data(state.instance)]),

        encapsulate([html.H3(children="Solution: Magazine State"),
                     render_solution_vector(state.instance)]),

        encapsulate([html.H3(children="Results: Sequence Matrix")
                        ,
                     html.H3(id="results-switches", children="Switches: " + str(state.instance.solution.n_switches),
                             style={'color': '#007AFF'}),
                     render_result_matrix_data(state.instance)]),

        encapsulate([html.H3(children="Results: Magazine State"),
                     render_result_magazine_data(state.instance)]),

        encapsulate([html.H3(children="Parameters"),
                     render_parameters(state.instance)]),

    ])])


def encapsulate(children):
    return html.Div(className="container", children=[html.Div(className="item", children=children)])


def readFiles():
    return None


#
# Control View - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#

def render_control():
    return html.Div(className="", children=[
        dcc.Checklist(
            options=[
                {'label': 'Show KTNS Tools', 'value': 'ktns'},
                {'label': 'Show Tool Loadings', 'value': 'tl'},
                {'label': 'Show Job tools', 'value': 'tl'}
            ],
            value=['ktns', 'tl'],
            labelStyle={'display': 'inline-block'}
        ),
        dcc.Checklist(
            options=[
                {'label': 'Show Differences', 'value': 'dif'},
                {'label': 'Show Value Differences', 'value': 'vdif'},
            ],
            value=['dif', 'tl'],
            labelStyle={'display': 'inline-block'}
        ),
    ])


#
# INSTANCE VIEW : Live View - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - #
#


if __name__ == '__main__':
    app.run_server(debug=False)
