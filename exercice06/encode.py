#!/usr/bin/env python3

from cbor2 import dumps, CBORTag
import requests
import binascii
import uuid

"""
Integer id  = 500
String body = 501
String title = 502
Category category = 503

category :
    UUID id (it seems there is a tag for UUID: 37)
    String name = 504
"""

json = requests.get("http://localhost:8080/notes").json()
print(f"Got from server: {json}")

tagified = []
for note in json:
    category = [
        uuid.UUID(note["category"]["id"]),  # Will be a tag 37
        CBORTag(504, note["category"]["name"])
        ]
    new_note = [
        CBORTag(500, note["id"]),
        CBORTag(501, note["body"]),
        CBORTag(503, category)
        # TODO: add others
        ]
    tagified.append(new_note)

print(f"Translated to tags: {tagified}")

cbor_data = dumps(tagified)
print(f"CBOR in hex: {binascii.hexlify(cbor_data)}")

with open("notes.cbor", "wb") as output_file:
    output_file.write(cbor_data)
