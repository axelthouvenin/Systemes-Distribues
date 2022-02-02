#!/usr/bin/env python3

import capnp
import temperature_capnp

client = capnp.TwoPartyClient('localhost:60000')
weathermap = client.bootstrap().cast_as(temperature_capnp.WeatherMap)

request = weathermap.findTown()
result = request.wait()

town = result.town.cast_as(temperature_capnp.Town)

request = town.currentTemperature()
result = request.wait()
print(result.temperature.value)

