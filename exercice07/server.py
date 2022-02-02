#!/usr/bin/env python3

import capnp
import temperature_capnp


class TownImpl(temperature_capnp.Town.Server):
    def currentTemperature(self, _context, **kwargs):
        _context.results.temperature.value = 15.6


class WeatherMapImpl(temperature_capnp.WeatherMap.Server):
    "Implementation of the WeatherMap Cap'n Proto interface."

    def findTown(self, _context, **kwargs):
        _context.results.town = TownImpl()

    """
    def listTowns(self, _context, **kwargs):
        print("called listTowns")
        _context.results.towns.init('towns', 1)
        _context.results.towns[0] = TownImpl()
    """


server = capnp.TwoPartyServer('*:60000', bootstrap=WeatherMapImpl())
server.run_forever()
