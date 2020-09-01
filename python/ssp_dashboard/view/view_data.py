import numpy as np
import copy
from ssp_dashboard.view.matrix_data_v2 import *


def get_job_tool_matrix_data(instance):
    matrix = instance.problem["matrix"]

    if instance is not None:

        jobs = list(range(instance.solution.n_jobs))
        tools = list(range(instance.solution.n_tools))

        sequence_matrix = []

        for tool_id in tools:
            out_tools = []
            for jobId in jobs:
                out_tools.append(matrix[jobId][tool_id])

            sequence_matrix.append(out_tools)

        # - - - -

        center = dict(
            data=sequence_matrix,
            layout=dict(
                title="matrix",
                binary=True,
                binaryShape="circle",
                color="black",
                color2="translucent",
                shape="circle",
                stripe=False,
                highlight=False,
                background="purple",
                background2="translucent"
            )
        )

        # bottom=get_bottom_data(instance)

        matrix_data = copy.deepcopy(matrixDataV2)
        matrix_data["center"] = center
        matrix_data["left"]["data"] = tools
        matrix_data["top"]["data"] = jobs
        #matrix_data["right"]["data"][0]["data"] = tools
        #matrix_data["right"]["data"][1]["data"] = tools
        matrix_data["bottom"]["data"][0]["data"] = instance.solution.switches
        matrix_data["center"]["layout"]["binaryShape"] = "square"

        return matrix_data

    return {}


def get_job_tool_matrix_data_sequenced(instance):
    matrix = instance.problem["matrix"]

    if instance is not None:

        jobs = list(range(instance.solution.n_jobs))
        tools = list(range(instance.solution.n_tools))

        sequence_matrix = []

        for tool_id in tools:
            row = []
            for jobId in instance.solution.sequence:
                row.append({"value": matrix[jobId][tool_id], "highlight": False})

            sequence_matrix.append(row)

        # - - -

        center = dict(
            data=sequence_matrix,
            layout=dict(
                title="matrix",
                binary=True,
                binaryShape="circle",
                color="black",
                color2="translucent",
                shape="circle",
                stripe=False,
                highlight=False,
                background="purple",
                background2="translucent"
            )
        )


        matrix_data = copy.deepcopy(matrixDataV2)
        matrix_data["center"] = center
        matrix_data["left"]["data"] = tools

        top = []
        for jobId in instance.solution.sequence:
            v = {
                "value": jobId,
                "highlight": False,
            }
            top.append(v)

        matrix_data["top"]["data"] = top
        #matrix_data["right"]["data"][0]["data"] = tools
        #matrix_data["right"]["data"][1]["data"] = tools
        matrix_data["bottom"]["data"][0]["data"] = instance.solution.switches

        return matrix_data

    return {}


def get_solution_matrix_data(result, instance, prev_result=None):
    if result is not None:
        jobs = result.sequence
        tools = list(range(result.n_tools))

        sequence_matrix = []

        # transpose view
        for tool_id in tools:
            row = []
            for job_id in result.sequence:

                value = result.job_tool_matrix[job_id][tool_id]

                if instance.problem["matrix"][job_id][tool_id] == 1:
                    row.append(value)
                else:
                    if value == 1:
                        # ktns tool
                        value = dict(
                            value=value,
                            highlight=True,
                            layout=dict(background="#24c70b")
                        )

                        row.append(value)

                    else:
                        row.append(value)
            sequence_matrix.append(row)





        # - - - -

        center = dict(
            data=sequence_matrix,
            layout=dict(
                title="matrix",
                binary=True,
                binaryShape="circle",
                color="black",
                color2="translucent",
                shape="circle",
                stripe=False,
                highlight=False,
                background="purple",
                background2="translucent"
            )
        )

        if len(result.sequence) < result.n_jobs:
            center["layout"]["binaryShape"] = "square"



        if prev_result is not None:
            top = []
            for i, jobId in enumerate(result.sequence):
                if jobId != prev_result.sequence[i]:
                    v = {
                        "value": jobId,
                        "highlight": True,
                    }

                else:
                    v = jobId

                top.append(v)
        else:
            top = jobs

        matrix_data = copy.deepcopy(matrixDataV2)
        matrix_data["center"] = center
        matrix_data["left"]["data"] = tools
        matrix_data["top"]["data"] = top
        #matrix_data["right"]["data"][0]["data"] = tools
        #matrix_data["right"]["data"][1]["data"] = tools
        matrix_data["bottom"]["data"][0]["data"] = result.switches

        return matrix_data

    return {}


def get_magazine_state_data(result):
    magazines = []
    m_base = []
    for seq in result.sequence:
        mi = []
        i = 0
        for on in result.job_tool_matrix[seq]:
            if on == 1:
                mi.append(i)
            i += 1

        m_base.append(mi)

    # sort
    for i, magazine in enumerate(m_base):

        if i > 0:
            locked = []
            unlocked = []
            final = [None] * result.magazine_size

            for j, tool in enumerate(magazine):
                value = tool
                lock = False

                prev = i - 1
                for pj, prev_tool_o in enumerate(magazines[prev]):
                    prev_tool = prev_tool_o["value"]
                    if prev_tool == tool:
                        index = pj
                        value = {"value": value}
                        lock = True
                        locked.append({"index": index, "value": value})
                        break

                if not lock:
                    value = {"value": value, "highlight": True, "background": "darkGray"}
                    unlocked.append({"index": j, "value": value})

            fill = result.magazine_size - (len(locked) + len(unlocked))
            for li in locked:
                index = li["index"]
                final[index] = li["value"]

            k = 0
            for lu in unlocked:
                while final[k] is not None:
                    k += 1

                final[k] = lu["value"]
                k += 1

            a = 0
            if fill > 0:
                for fi in range(fill):
                    while final[a] is not None:
                        a += 1
                    if a <= len(final) - 1:
                        final[a] = {"value": -1}
                    a += 1

            magazines.append(final)
        else:

            mo = []
            for tm in magazine:
                mo.append({"value": tm, "highlight": True, "shape": "circle"})

            fill = result.magazine_size - (len(mo))
            if fill > 0:
                for fi in range(fill):
                    mo.append({"value": -1})

            magazines.append(mo)

    return magazines


def get_solution_vector_data(result):
    if result is not None:
        m = get_magazine_state_data(result)

        magazine = dict(
            title="Magazine",
            subtitle="SIZE=" + str(result.magazine_size),
            data=m,
            group=True,
            background="black",
            groupBackground="lightGray",
            shape="circle"
        )

        sequence_pos = dict(
            title="Sequence Pos",
            subtitle="",
            data=list(range(result.n_jobs)),
            group=False,
            background="translucent",
            shape="circle"
        )

        sequence = dict(
            title="Job",
            subtitle="",
            data=result.sequence,
            group=False,
            background="lightGray",
            shape="circle"
        )

        switches = dict(
            title="Switches",
            subtitle="",
            data=result.switches,
            group=False,
            background="translucent",
            shape="square",
            total=result.n_switches,
        )

        hops = dict(
            title="Tool Hops",
            subtitle="",
            data=result.tool_hops,
            group=False,
            background="translucent",
            shape="square",
            total=result.n_tool_hops
        )

        ktnsHops = dict(
            title="KTNS Hops",
            subtitle="",
            data=result.ktns_tool_hops,
            group=False,
            background="translucent",
            shape="square",
            total=result.n_ktns_tool_hops
        )

        data = {
            "data": [
                magazine,
                sequence_pos,
                sequence,
                switches,
                hops,
                ktnsHops,
            ]
        }

        return data

    return {}


def transpose_matrix(matrix):
    a = np.array(matrix)
    a = a.transpose()
    return a.tolist()


def normal(result, instance):
    sequence_matrix = []

    for seq in result.sequence:
        el = []
        toolid = 0

        for i in result.job_tool_matrix[seq]:
            if instance.problem["matrix"][seq][toolid] == 1:
                el.append(i)
            else:
                if i == 1:
                    el.append({"value": i, "highlight": True})
                else:
                    el.append(i)
            toolid += 1
        sequence_matrix.append(el)

    return sequence_matrix
