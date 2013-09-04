all:

.PHONY: maps
maps:
	topojson --id-property name -p -o webapp/maps/risk/world.json maps/risk/countries.shp
