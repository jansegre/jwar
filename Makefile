all: maps

.PHONY: maps
maps:
	topojson --id-property ID -p -o webapp/maps/risk/world.json maps/risk/countries.shp maps/risk/labels.shp
