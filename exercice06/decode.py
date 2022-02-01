#!/usr/bin/env python3

from cbor2 import load, CBORTag
import uuid
import json

"""
Integer id  = 500
String body = 501
String title = 502
Category category = 503

category :
    UUID id (it seems there is a tag for UUID: 37)
    String name = 504
"""

with open("notes.cbor", "rb") as input_file:
    data = load(input_file)

print(data)

notes = []
for note in data:
    json_note = {}
    for element in note:
        if isinstance(element, CBORTag):
            if element.tag == 503:
                category = {}
                for ce in element.value:
                    if isinstance(ce, CBORTag) and ce.tag == 504:
                        category["title"] = ce.value
                    if isinstance(ce, uuid.UUID):
                        category["id"] = str(ce)
                    json_note["category"] = category
                continue
            if element.tag == 500:
                key = "id"
            if element.tag == 501:
                key = "body"
            if element.tag == 502:
                key = "title"
            json_note[key] = element.value
    notes.append(json_note)

json_str = json.dumps(notes, separators=(',', ':'))
print(json_str)
with open("notes.json", "w") as output_file:
    output_file.write(json_str)

