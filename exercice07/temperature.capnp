@0xae47d9b20d9e659d;

interface Town {
	currentTemperature @0 () -> (temperature :CelsiusTemp);
	updateTemperature @2 (temperature :CelsiusTemp) -> ();
	name @1 () -> (name :Text);
	location @3 () -> (location :Coordinates);
	struct Coordinates {
  	latitude @0 :Float32;
  	longitude @1 :Float32;
	}
}

interface WeatherMap {
	findTown @0 (name :Text) -> (town :Town); # Investigate: Error handling?
	listTowns @1 () -> (towns :List(Town));
}

interface CelsiusTemp {
	getValue @0 () -> (value :Float32);
	toFahrenheit @1 () -> (temperature :FahrenheitTemp);
}

interface FahrenheitTemp {
	getValue @0 () -> (value :Float32);
	toCelsius @1 () -> (temperature :CelsiusTemp);
}
