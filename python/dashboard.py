import dash
import pathlib
import dash_core_components as dcc
import dash_html_components as html
import pandas as pd
import plotly.graph_objs as go
from dash.dependencies import Input, Output
from plotly import tools

from demo_utils import demo_callbacks, demo_explanation

# get relative data folder
DATA_PATH = pathlib.Path(__file__).parent.joinpath("data").resolve()

LOGFILE = "examples/run_log.csv"

# app = dash.Dash(__name__)
app = dash.Dash(
    __name__, meta_tags=[{"name": "viewport", "content": "width=device-width"}]
)

server = app.server
demo_mode = True



def div_graph(name):
    # Generates an html Div containing graph and control options for smoothing and display, given the name
    return html.Div(
        className="row",
        children=[
            html.Div(
                className="two columns",
                style={"padding-bottom": "5%"},
                children=[
                    html.Div(
                        [
                            html.Div(
                                className="graph-checkbox-smoothing",
                                children=["Smoothing:"],
                            ),
                            dcc.Checklist(
                                options=[
                                    {"label": " Training", "value": "train"},
                                    {"label": " Validation", "value": "val"},
                                ],
                                value=[],
                                id=f"checklist-smoothing-options-{name}",
                                className="checklist-smoothing",
                            ),
                        ],
                        style={"margin-top": "10px"},
                    ),
                    html.Div(
                        [
                            dcc.Slider(
                                min=0,
                                max=1,
                                step=0.05,
                                marks={i / 5: str(i / 5) for i in range(0, 6)},
                                value=0.6,
                                updatemode="drag",
                                id=f"slider-smoothing-{name}",
                            )
                        ],
                        style={"margin-bottom": "40px"},
                        className="slider-smoothing",
                    ),
                    html.Div(
                        [
                            html.P(
                                "Plot Display Mode:",
                                style={"font-weight": "bold", "margin-bottom": "0px"},
                                className="plot-display-text",
                            ),
                            html.Div(
                                [
                                    dcc.RadioItems(
                                        options=[
                                            {
                                                "label": " Overlapping",
                                                "value": "overlap",
                                            },
                                            {
                                                "label": " Separate (Vertical)",
                                                "value": "separate_vertical",
                                            },
                                            {
                                                "label": " Separate (Horizontal)",
                                                "value": "separate_horizontal",
                                            },
                                        ],
                                        value="overlap",
                                        id=f"radio-display-mode-{name}",
                                        labelStyle={"verticalAlign": "middle"},
                                        className="plot-display-radio-items",
                                    )
                                ],
                                className="radio-item-div",
                            ),
                            html.Div(id=f"div-current-{name}-value"),
                        ],
                        className="entropy-div",
                    ),
                ],
            ),
            html.Div(id=f"div-{name}-graph", className="ten columns"),
        ],
    )



app.layout = html.Div(
    style={"height": "100%"},
    children=[
        # Banner display
        html.Div(
            [
                html.H2(
                    "Live Model Training Viewer",
                    id="title",
                    className="eight columns",
                    style={"margin-left": "3%"},
                ),
                html.Button(
                    id="learn-more-button",
                    className="two columns",
                    children=["Learn More"],
                ),
                html.Img(
                    src=app.get_asset_url("dash-logo.png"),
                    className="two columns",
                    id="plotly-logo",
                ),
            ],
            className="banner row",
        ),
        html.Div(html.Div(id="demo-explanation", children=[])),
        html.Div(
            className="container",
            style={"padding": "35px 25px"},
            children=[
                dcc.Store(id="storage-simulated-run", storage_type="memory"),
                # Increment the simulation step count at a fixed time interval
                dcc.Interval(
                    id="interval-simulated-step",
                    interval=125,  # Updates every 100 milliseconds, i.e. every step takes 25 ms
                    n_intervals=0,
                ),
                html.Div(
                    className="row",
                    style={"margin": "8px 0px"},
                    children=[
                        html.Div(
                            className="twelve columns",
                            children=[
                                html.Div(
                                    className="eight columns",
                                    children=[
                                        html.Div(
                                            dcc.Dropdown(
                                                id="dropdown-demo-dataset",
                                                options=[
                                                    {
                                                        "label": "CIFAR 10",
                                                        "value": "cifar",
                                                    },
                                                    {
                                                        "label": "MNIST",
                                                        "value": "mnist",
                                                    },
                                                    {
                                                        "label": "Fashion MNIST",
                                                        "value": "fashion",
                                                    },
                                                ],
                                                value="mnist",
                                                placeholder="Select a demo dataset",
                                                searchable=False,
                                            ),
                                            className="six columns dropdown-box-first",
                                        ),
                                        html.Div(
                                            dcc.Dropdown(
                                                id="dropdown-simulation-model",
                                                options=[
                                                    {
                                                        "label": "1-Layer Neural Net",
                                                        "value": "softmax",
                                                    },
                                                    {
                                                        "label": "Simple Conv Net",
                                                        "value": "cnn",
                                                    },
                                                ],
                                                value="cnn",
                                                placeholder="Select Model to Simulate",
                                                searchable=False,
                                            ),
                                            className="six columns dropdown-box-second",
                                        ),
                                        html.Div(
                                            dcc.Dropdown(
                                                id="dropdown-interval-control",
                                                options=[
                                                    {
                                                        "label": "No Updates",
                                                        "value": "no",
                                                    },
                                                    {
                                                        "label": "Slow Updates",
                                                        "value": "slow",
                                                    },
                                                    {
                                                        "label": "Regular Updates",
                                                        "value": "regular",
                                                    },
                                                    {
                                                        "label": "Fast Updates",
                                                        "value": "fast",
                                                    },
                                                ],
                                                value="regular",
                                                className="twelve columns dropdown-box-third",
                                                clearable=False,
                                                searchable=False,
                                            )
                                        ),
                                    ],
                                ),
                                html.Div(
                                    className="four columns",
                                    id="div-interval-control",
                                    children=[
                                        html.Div(
                                            id="div-total-step-count",
                                            className="twelve columns",
                                        ),
                                        html.Div(
                                            id="div-step-display",
                                            className="twelve columns",
                                        ),
                                    ],
                                ),
                            ],
                        )
                    ],
                ),
                dcc.Interval(id="interval-log-update", n_intervals=0),
                dcc.Store(id="run-log-storage", storage_type="memory"),
            ],
        ),
        html.Div(className="container", children=[div_graph("accuracy")]),
        html.Div(
            className="container",
            style={"margin-bottom": "30px"},
            children=[div_graph("cross-entropy")],
        ),
    ],
)