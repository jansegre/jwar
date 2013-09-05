all: maps

.PHONY: maps
maps:
	topojson --id-property id -p -o webapp/maps/risk/world.json maps/risk/countries.shp maps/risk/labels.shp
