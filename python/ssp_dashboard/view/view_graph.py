import numpy as np
import dash_html_components as html
import dash_core_components as dcc
import plotly.graph_objs as go
from dash.dependencies import Input, Output
from ssp_dashboard.instance import *


def get_figure_switches(instances):
    graphs = []
    instance = instances[0]

    if instance.log is None:
        print("ALLLOOOO")
        return dict()

    for instance in instances:
        graphs.append(dict(
            x=list(instance.log.T_RUN / 1000),
            y=list(instance.log.loc[instance.log.TYPE == "improved"].SW),
            name=instance.run_type + "_SW",
        ))

        graphs.append(go.Scatter(
            x=list(instance.log.T_RUN / 1000),
            y=list(instance.log.loc[instance.log.TYPE == "improved"].SW),
            mode='lines+markers',
            name=instance.run_type + "_SW",
        ))

        graphs.append(go.Scatter(
            x=list(instance.log.T_RUN / 1000),
            y=list(instance.log.loc[instance.log.TYPE == "improved"].COST),
            mode='lines+markers',
            name=instance.run_type + "_COST"))

    figure = dict(
        data=graphs,
        layout=dict(
            title="#Switches",
            showlegend=True,
            legend=dict(
                x=0,
                y=1.0
            ),
            margin=dict(l=40, r=0, t=40, b=30),
            height=630,
        ),
    )
    return figure


def get_figure_cost_reject_accept_improve(instance):
    if instance.read_error:
        return dict()

    graphs = []

    graphs.append(go.Scatter(
        y=list(instance.log.loc[instance.log.TYPE == "rejected"].SW),
        x=list(instance.log.loc[instance.log.TYPE == "rejected"].T_RUN / 1000),
        mode='lines+markers',

        name=instance.descriptor["instance"] + "REJECTED",
    ))

    graphs.append(go.Scatter(
        y=list(instance.log.loc[instance.log.TYPE == "accepted"].SW),
        x=list(instance.log.loc[instance.log.TYPE == "accepted"].T_RUN / 1000),
        mode='lines+markers',

        name=instance.descriptor["instance"] + "ACCEPTED",
    ))

    graphs.append(go.Scatter(
        y=list(instance.log.loc[instance.log.TYPE == "improved"].SW),
        x=list(instance.log.loc[instance.log.TYPE == "improved"].T_RUN / 1000),
        mode='lines+markers',

        name=instance.descriptor["instance"] + "IMPROVED",
    ))

    graphs.append(go.Scatter(
        y=list(instance.log.loc[instance.log.TYPE == "rejected"].COST),
        x=list(instance.log.loc[instance.log.TYPE == "rejected"].T_RUN / 1000),
        mode='lines+markers',

        name=instance.descriptor["instance"] + "REJECTED" + "_COST",
    ))

    graphs.append(go.Scatter(
        y=list(instance.log.loc[instance.log.TYPE == "accepted"].COST),
        x=list(instance.log.loc[instance.log.TYPE == "accepted"].T_RUN / 1000),
        mode='lines+markers',
        name=instance.descriptor["instance"] + "ACCEPTED" + "_COST",
    ))

    graphs.append(go.Scatter(
        y=list(instance.log.loc[instance.log.TYPE == "improved"].COST),
        x=list(instance.log.loc[instance.log.TYPE == "improved"].T_RUN / 1000),
        mode='lines+markers',
        name=instance.descriptor["instance"] + "IMPROVED" + "_COST",
    ))

    figure = dict(
        data=
        graphs
        ,
        layout=dict(
            title="COST",
            showlegend=True,
            legend=dict(
                x=0,
                y=1.0
            ),
            margin=dict(l=40, r=0, t=40, b=30),
            height=600
        )
    )

    return figure


def get_figure_sa_temp(instance):
    if instance.read_error:
        return dict()

    figure = dict(
        data=[
            dict(
                x=list(instance.log.T_RUN),
                y=list(instance.log.TEMP),
                name=instance.descriptor["instance"],
            )
        ],
        layout=dict(
            title="Temperature SA",
            showlegend=True,
            legend=dict(
                x=0,
                y=1.0
            ),
            margin=dict(l=40, r=0, t=40, b=30),
        )
    )
    return figure


def get_figure_reject_accept_improve(instance):
    if instance.read_error:
        return dict()

    figure = dict(
        data=[
            dict(
                legendgroup="g1",
                x=list(instance.log.T_RUN),
                y=list(instance.log.REJECT),
                name="REJECT",
                marker=dict(
                    color='rgb(55, 83, 109)'
                )
            ),
            dict(
                legendgroup="g1",
                x=list(instance.log.T_RUN),
                y=list(instance.log.ACCEPT),
                name="ACCEPT",
            ),
            dict(
                legendgroup="g1",
                x=list(instance.log.T_RUN),
                y=list(instance.log.IMPROVE),
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
    return figure


def get_figure_bar_graph(instances):
    # based on the results

    grab = ["time_running", "n_switches"]

    traces = []

    for instance in instances:
        y = []

        solution: Result = instance.solution

        for g in grab:
            if g is not "time_running":
                y.append(solution.__getattribute__(g))
            else:
                y.append(solution.__getattribute__(g) / 1000)

        trace = go.Bar(
            x=grab,
            y=y,
            name=instance.run_type
        )

        traces.append(trace)

    figure = dict(
        data=traces,
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
    return figure
