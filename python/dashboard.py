import dash
import pathlib
from dash.dependencies import Input, Output
import pandas as pd
import dash_core_components as dcc
import dash_html_components as html
import plotly.express as px
import plotly.graph_objects as go


BASE = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP"
LOGFILE = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data" \
          "/instances/catanzaro/cat_10_10_4_1/log_ran_swap-2job_full_sd_sw_v1_none.csv"


# app = dash.Dash(__name__)
app = dash.Dash(
    __name__, meta_tags=[{"name": "viewport", "content": "width=device-width"}]
)
server = app.server


#DATA
df = pd.read_csv(LOGFILE)
fig = px.line_3d(df, x = 'T_RUN', y = 'TEMP', z='ACCEPT', title='SSP')
#fig.update_xaxes(autorange='reversed')
fig1 = px.line(df, x = 'T_RUN', y = 'SW')
#fig1.update_xaxes(autorange="reversed")
fig2 = px.line(df, x = 'T_RUN', y = 'IMPROVE')
#fig2.update_xaxes(autorange="reversed")
fig3 = px.line(df, x = 'T_RUN', y = 'TEMP')
#fig3.update_xaxes(autorange="reversed")
fig4 = px.line(df, x = 'T_RUN', y = 'ACCEPT')
#fig4.update_xaxes(autorange="reversed")$


fig = go.Figure()
fig.add_trace(go.Scatter(x=T_RUN, y=random_y1,
                         mode='lines+markers',
                         name='lines+markers'))

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
