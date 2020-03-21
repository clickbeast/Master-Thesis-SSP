import json
from json import JSONEncoder
import re

class MarkedList:
    _list = None
    def __init__(self, l):
        self._list = l

z = {
    "rows_parsed": [
        MarkedList([
            "a",
            "b",
            "c",
            "d"
        ]),
        MarkedList([
            "e",
            "f",
            "g",
            "i"
        ]),
    ]
}

class CustomJSONEncoder(JSONEncoder):
    def default(self, o):
        if isinstance(o, MarkedList):
            return "##<{}>##".format(o._list)

b = json.dumps(z, indent=2, separators=(',', ':'), cls=CustomJSONEncoder)
#b = b.replace('"##<', "").replace('>##"', "")

print(b)