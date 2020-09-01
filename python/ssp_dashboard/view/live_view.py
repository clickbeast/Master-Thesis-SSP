import dash
import json
import dash_html_components as html
import dash_core_components as dcc
from dash.dependencies import Input, Output
from typing import List, Set, Tuple, Dict
from ssp_dashboard.instance import Instance, InstanceGroup, Result
import os
import ssp_matrix
import ssp_vector_sequence
import copy
from ssp_dashboard.view.matrix_data_v2 import *
from ssp_dashboard.view.view_data import *


def render_live_data_elements(state):
    return html.Div([
        html.H3(children="Solution: Sequence Matrix"),
        render_live_solution_sequence_matrix(state.instance_live),
        html.H3(children="Solution: Magazine State"),
    ])


def render_live_solution_sequence_matrix(instance):
    if instance is not None:
        if instance.read_error is False:
            return html.Div([ssp_matrix.Matrix(id="solution-sequence-matrix",
                                               data=get_solution_matrix_data(instance.solution, instance))])
    return ""
