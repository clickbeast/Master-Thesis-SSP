import dash
import pathlib
from dash.dependencies import Input, Output
import pandas as pd
import dash_core_components as dcc
import dash_html_components as html
import plotly.express as px

BASE = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP"
LOGFILE = BASE + "/data/log.csv"


# app = dash.Dash(__name__)
app = dash.Dash(
    __name__, meta_tags=[{"name": "viewport", "content": "width=device-width"}]
)
server = app.server


#DATA
df = pd.read_csv(LOGFILE)
fig = px.line_3d(df, x = 'Time', y = 'Temperature', z='Accepted', title='SSP')
fig.update_xaxes(autorange='reversed')
fig1 = px.line(df, x = 'Time', y = 'Cost')
fig1.update_xaxes(autorange="reversed")
fig2 = px.line(df, x = 'Time', y = 'Min Cost')
fig2.update_xaxes(autorange="reversed")
fig3 = px.line(df, x = 'Time', y = 'Temperature')
fig3.update_xaxes(autorange="reversed")
fig4 = px.line(df, x = 'Time', y = 'Accepted')
fig4.update_xaxes(autorange="reversed")

app.layout = html.Div(
    style={"height": "100%"},
    children=[
        # Banner display
        html.Div(
            [
                html.H2(
                    "Tool Switching and Job Sequencing Problem",
                    id="title",
                    className="eight columns",
                    style={"margin-left": "3%"},
                ),
            ],
            className="banner row",
        ),
        dcc.Graph(
            id='example-graph-1',
            figure=fig1
        ),
        dcc.Graph(
            id='example-graph-2',
            figure=fig2
        ),
        dcc.Graph(
            id='example-graph-3',
            figure=fig3
        ),dcc.Graph(
            id='example-graph-5',
            figure=fig4
        ),
        dcc.Graph(
            id='example-graph-4',
            figure=fig
        )

    ]
)

#run the server
app.run_server(debug=True)
