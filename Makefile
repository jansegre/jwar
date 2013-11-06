PRECISION=9e-5

all: maps

.PHONY: maps
maps:
	topojson --simplify $(PRECISION) --id-property ID -p -o webapp/maps/risk/world.json maps/risk/countries.shp maps/risk/labels.shp
