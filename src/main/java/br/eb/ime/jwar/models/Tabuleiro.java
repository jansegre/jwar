/*
 * This file is part of JWar.
 *
 * JWar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.
 *
 */

package br.eb.ime.jwar.models;

import java.util.*;

public class Tabuleiro {

    private final Set<Continente> continentes;
    private final Map<String, Pais> paisMap;
    private final List<Jogador> jogadores;

    public Tabuleiro(Set<Continente> continentes, List<Jogador> jogadores) {
        this.continentes = continentes;

        this.jogadores = jogadores;
        for (Jogador jogador : jogadores)
            jogador.setTabuleiro(this);

        this.paisMap = new HashMap<>();
        for (Pais pais : getPaises())
            this.paisMap.put(pais.getCodigo().toUpperCase(), pais);
    }

    public Pais getPaisBySlug(String slug) {
        return paisMap.get(slug.toUpperCase());
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public Iterable<Continente> getContinentes() {
        return continentes;
    }

    public Iterable<Pais> getPaises() {
        return new Iterable<Pais>() {
            @Override
            public Iterator<Pais> iterator() {
                return new Iterator<Pais>() {
                    Iterator<Continente> continenteIterator = continentes.iterator();
                    Iterator<Pais> paisIterator = continenteIterator.next().getPaises().iterator();

                    @Override
                    public boolean hasNext() {
                        return continenteIterator.hasNext() || paisIterator.hasNext();
                    }

                    @Override
                    public Pais next() {
                        if (!paisIterator.hasNext())
                            paisIterator = continenteIterator.next().getPaises().iterator();
                        return paisIterator.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static Set<Continente> mundoWarS() {
        Set<Continente> continentes = new HashSet<>();

        // Países

        Pais alaska = new Pais("Alaska");
        Pais mackenzie = new Pais("Mackenzie");
        Pais groelandia = new Pais("Groelandia");
        Pais vancouver = new Pais("Vancouver");
        Pais ottawa = new Pais("Ottawa");
        Pais labrador = new Pais("Labrador");
        Pais california = new Pais("Califórnia");
        Pais novaYork = new Pais("Nova York");
        Pais mexico = new Pais("México");

        Pais colombia = new Pais("Colômbia");
        Pais chile = new Pais("Chile");
        Pais brasil = new Pais("Brasil");
        Pais argentina = new Pais("Argentina");

        Pais argelia = new Pais("Argélia");
        Pais egito = new Pais("Egito");
        Pais sudao = new Pais("Sudão");
        Pais congo = new Pais("Congo");
        Pais africaDoSul = new Pais("Africa do Sul");
        Pais madagascar = new Pais("Madagascar");

        Pais suecia = new Pais("Suécia");
        Pais islandia = new Pais("Islândia");
        Pais alemanha = new Pais("Alemanha");
        Pais moscou = new Pais("Moscou");
        Pais inglaterra = new Pais("Inglaterra");
        Pais portugal = new Pais("Portugal");
        Pais polonia = new Pais("Polônia");

        Pais omsk = new Pais("Omsk");
        Pais dudinka = new Pais("Dudinka");
        Pais siberia = new Pais("Sibéria");
        Pais mongolia = new Pais("Mongólia");
        Pais tchita = new Pais("Tchita");
        Pais vladivostok = new Pais("Vladivostok");
        Pais orienteMedio = new Pais("Oriente Médio");
        Pais aral = new Pais("Aral");
        Pais india = new Pais("Índia");
        Pais vietna = new Pais("Vietnã");
        Pais china = new Pais("China");
        Pais japao = new Pais("Japão");

        Pais sumatra = new Pais("Sumatra");
        Pais borneo = new Pais("Broneo");
        Pais novaGuine = new Pais("Nova Guiné");
        Pais australia = new Pais("Austrália");


        // Continentes

        continentes.add(new Continente("América do Norte", 5, alaska, mackenzie, groelandia, vancouver, ottawa, labrador, california, novaYork, mexico));
        continentes.add(new Continente("América do Sul", 2, colombia, chile, brasil, argentina));
        continentes.add(new Continente("África", 3, argelia, egito, sudao, congo, africaDoSul, madagascar));
        continentes.add(new Continente("Europa", 5, suecia, islandia, alemanha, moscou, inglaterra, portugal, polonia));
        continentes.add(new Continente("Ásia", 7, omsk, dudinka, siberia, mongolia, tchita, vladivostok, orienteMedio, aral, india, vietna, china, japao));
        continentes.add(new Continente("Oceania", 2, sumatra, borneo, novaGuine, australia));

        // Fronteiras

        alaska.addFronteira(mackenzie, vancouver, vladivostok);
        mackenzie.addFronteira(alaska, vancouver, mackenzie, ottawa, labrador, groelandia);
        vancouver.addFronteira(california, ottawa, mackenzie, alaska);
        ottawa.addFronteira(mackenzie, vancouver, labrador, california, novaYork);
        labrador.addFronteira(groelandia, ottawa, novaYork);
        groelandia.addFronteira(mackenzie, labrador, islandia);
        california.addFronteira(mexico, novaYork, ottawa, vancouver);
        novaYork.addFronteira(mexico, california, ottawa, labrador);
        mexico.addFronteira(california, novaYork, colombia);

        colombia.addFronteira(mexico, chile, brasil);
        chile.addFronteira(argentina, brasil, colombia);
        argentina.addFronteira(chile, brasil);
        brasil.addFronteira(argentina, chile, colombia, argelia);

        argelia.addFronteira(portugal, egito, sudao, congo, brasil);
        congo.addFronteira(argelia, africaDoSul, sudao);
        africaDoSul.addFronteira(congo, sudao, madagascar);
        madagascar.addFronteira(africaDoSul, sudao);
        sudao.addFronteira(madagascar, africaDoSul, congo, argelia, egito);
        egito.addFronteira(sudao, argelia, orienteMedio, polonia, portugal);

        portugal.addFronteira(egito, argelia, inglaterra, alemanha, polonia);
        inglaterra.addFronteira(portugal, alemanha, islandia, suecia);
        islandia.addFronteira(groelandia, inglaterra, suecia);
        alemanha.addFronteira(portugal, polonia, moscou, inglaterra);
        suecia.addFronteira(inglaterra, moscou);
        polonia.addFronteira(moscou, alemanha, portugal, orienteMedio);
        moscou.addFronteira(suecia, polonia, orienteMedio, aral, omsk, alemanha);

        orienteMedio.addFronteira(egito, polonia, moscou, aral, india);
        aral.addFronteira(india, orienteMedio, moscou, omsk, china);
        omsk.addFronteira(moscou, dudinka, mongolia, china, aral);
        dudinka.addFronteira(siberia, tchita, mongolia, omsk);
        siberia.addFronteira(dudinka, tchita, vladivostok);
        vladivostok.addFronteira(siberia, tchita, china, alaska);
        japao.addFronteira(vladivostok, china);
        tchita.addFronteira(dudinka, siberia, vladivostok, china, mongolia, omsk);
        mongolia.addFronteira(omsk, dudinka, tchita, china);
        china.addFronteira(aral, omsk, mongolia, vladivostok, japao, vietna, india);
        india.addFronteira(orienteMedio, aral, china, vietna, sumatra);
        vietna.addFronteira(india, china, borneo);

        sumatra.addFronteira(india, australia);
        australia.addFronteira(sumatra, borneo, novaGuine);
        novaGuine.addFronteira(australia, borneo);
        borneo.addFronteira(novaGuine, australia, vietna);

        return continentes;
    }


    public static Set<Continente> mundoRisk() {
        Set<Continente> continentes = new HashSet<>();

        // Países
        Pais alaska = new Pais("AL", "Alaska");
        Pais northwestTerritory = new Pais("NT", "Northwest Territory");
        Pais greenland = new Pais("GR", "Greenland");
        Pais alberta = new Pais("AB", "Alberta");
        Pais ontario = new Pais("ON", "Ontario");
        Pais quebec = new Pais("QU", "Quebec");
        Pais westernUS = new Pais("WU", "Western United States");
        Pais easternUS = new Pais("EU", "Eastern United States");
        Pais centralAmerica = new Pais("CA", "Central America");

        Pais venezuela = new Pais("VE", "Venezuela");
        Pais peru = new Pais("PE", "Peru");
        Pais brasil = new Pais("BR", "Brasil");
        Pais argentina = new Pais("AR", "Argentina");

        Pais eastAfrica = new Pais("EF", "East Africa");
        Pais egypt = new Pais("EG", "Egypt");
        Pais northAfrica = new Pais("NA", "North Africa");
        Pais congo = new Pais("CO", "Congo");
        Pais southAfrica = new Pais("SA", "South Africa");
        Pais madagascar = new Pais("MA", "Madagascar");

        Pais scandinavia = new Pais("SC", "Scandinavia");
        Pais iceland = new Pais("IC", "Iceland");
        Pais ukraine = new Pais("UK", "Ukraine");
        Pais northenEurope = new Pais("NE", "Northen Europe");
        Pais granBritain = new Pais("GB", "Gran Britain");
        Pais westernEurope = new Pais("WE", "Western Europe");
        Pais southernEurope = new Pais("SE", "Southern Europe");

        Pais ural = new Pais("UR", "Ural");
        Pais afghanistan = new Pais("AF", "Afghanistan");
        Pais middleEast = new Pais("ME", "Middle East");
        Pais yakutsk = new Pais("YA", "Yakutsk");
        Pais irkutsk = new Pais("IR", "Irkutsk");
        Pais kamchatka = new Pais("KA", "Kamchatka");
        Pais mongolia = new Pais("MO", "Mongolia");
        Pais siberia = new Pais("SB", "Siberia");
        Pais india = new Pais("IN", "Índia");
        Pais siam = new Pais("SI", "Siam");
        Pais china = new Pais("CH", "China");
        Pais japan = new Pais("JA", "Japão");

        Pais indonesia = new Pais("ID", "Indonesia");
        Pais easternAustralia = new Pais("EA", "Eastern Australia");
        Pais newGuine = new Pais("NG", "New Guiné");
        Pais weasternAustralia = new Pais("WA", "Weastern Australia");


        // Continentes

        continentes.add(new Continente("NA", "América do Norte", 5, alaska, northwestTerritory, greenland, alberta, ontario, quebec, westernUS, easternUS, centralAmerica));
        continentes.add(new Continente("SA", "América do Sul", 2, venezuela, peru, brasil, argentina));
        continentes.add(new Continente("AF", "África", 3, eastAfrica, egypt, northAfrica, congo, southAfrica, madagascar));
        continentes.add(new Continente("EU", "Europa", 5, scandinavia, iceland, ukraine, northenEurope, granBritain, westernEurope, southernEurope));
        continentes.add(new Continente("AS", "Ásia", 7, ural, afghanistan, middleEast, yakutsk, irkutsk, kamchatka, mongolia, siberia, india, siam, china, japan));
        continentes.add(new Continente("AU", "Australia", 2, indonesia, easternAustralia, newGuine, weasternAustralia));

        // Fronteiras

        alaska.addFronteira(kamchatka, northwestTerritory, alberta);
        northwestTerritory.addFronteira(alaska, alberta, ontario, greenland);
        greenland.addFronteira(northwestTerritory, quebec, iceland);
        alberta.addFronteira(alaska, northwestTerritory, ontario, westernUS);
        ontario.addFronteira(northwestTerritory, alberta, quebec, easternUS, westernUS);
        quebec.addFronteira(greenland, easternUS, ontario);
        westernUS.addFronteira(centralAmerica, ontario, alberta, easternUS);
        easternUS.addFronteira(westernUS, centralAmerica, quebec, ontario);
        centralAmerica.addFronteira(westernUS, easternUS, venezuela);

        venezuela.addFronteira(centralAmerica, peru, brasil);
        peru.addFronteira(argentina, brasil, venezuela);
        argentina.addFronteira(peru, brasil);
        brasil.addFronteira(argentina, peru, venezuela, northAfrica);

        northAfrica.addFronteira(westernEurope, southernEurope, egypt, congo, eastAfrica, brasil);
        congo.addFronteira(northAfrica, southAfrica, eastAfrica);
        southAfrica.addFronteira(congo, eastAfrica, madagascar);
        madagascar.addFronteira(southAfrica, eastAfrica);
        eastAfrica.addFronteira(madagascar, southAfrica, congo, northAfrica, egypt);
        egypt.addFronteira(northAfrica, eastAfrica, middleEast, southernEurope, westernEurope);

        westernEurope.addFronteira(egypt, northAfrica, granBritain, northenEurope, southernEurope);
        granBritain.addFronteira(iceland, scandinavia, westernEurope, northenEurope);
        iceland.addFronteira(greenland, granBritain);
        scandinavia.addFronteira(ukraine, granBritain);
        northenEurope.addFronteira(granBritain, ukraine, southernEurope, westernEurope);
        southernEurope.addFronteira(ukraine, northenEurope, westernEurope, middleEast, egypt);
        ukraine.addFronteira(scandinavia, northenEurope, southernEurope, ural, afghanistan, middleEast);

        middleEast.addFronteira(egypt, southernEurope, ukraine, afghanistan, india);
        ural.addFronteira(afghanistan, ukraine, siberia, china);
        afghanistan.addFronteira(ukraine, ural, india, china, middleEast);
        siberia.addFronteira(ural, china, mongolia, irkutsk, yakutsk);
        yakutsk.addFronteira(siberia, irkutsk, kamchatka);
        irkutsk.addFronteira(siberia, yakutsk, china, mongolia);
        japan.addFronteira(kamchatka, china);
        kamchatka.addFronteira(yakutsk, irkutsk, mongolia, japan, alaska);
        mongolia.addFronteira(siberia, irkutsk, kamchatka, china);
        china.addFronteira(ural, afghanistan, india, siam, japan, mongolia, siberia);
        india.addFronteira(middleEast, afghanistan, china, siam, indonesia);
        siam.addFronteira(india, china, newGuine);

        indonesia.addFronteira(india, weasternAustralia);
        easternAustralia.addFronteira(indonesia, weasternAustralia, newGuine);
        newGuine.addFronteira(easternAustralia, weasternAustralia, siam);
        weasternAustralia.addFronteira(easternAustralia, newGuine);

        return continentes;
    }
}
