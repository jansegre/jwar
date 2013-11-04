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

package br.eb.ime.jwar.models.templates;

import br.eb.ime.jwar.models.Carta;
import br.eb.ime.jwar.models.Continente;
import br.eb.ime.jwar.models.Cor;
import br.eb.ime.jwar.models.Pais;
import br.eb.ime.jwar.models.objetivos.ConquistarContinentes;
import br.eb.ime.jwar.models.objetivos.ConquistarNPaises;
import br.eb.ime.jwar.models.objetivos.DerrotarJogador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

// http://en.wikipedia.org/wiki/Risk_(game)#Secret_Mission
public class RiskSecretMission extends Template {

    public RiskSecretMission() {

        // Continentes

        Continente northAmerica = new Continente("NA", "América do Norte", 5);
        Continente southAmerica = new Continente("SA", "América do Sul", 2);
        Continente africa = new Continente("AF", "África", 3);
        Continente europe = new Continente("EU", "Europa", 5);
        Continente asia = new Continente("AS", "Ásia", 7);
        Continente australia = new Continente("AU", "Australia", 2);

        continentes = new HashSet<>();
        continentes.add(northAmerica);
        continentes.add(southAmerica);
        continentes.add(africa);
        continentes.add(europe);
        continentes.add(asia);
        continentes.add(australia);

        // Países

        Pais alaska = new Pais(northAmerica, "AL", "Alaska");
        Pais northwestTerritory = new Pais(northAmerica, "NT", "Northwest Territory");
        Pais greenland = new Pais(northAmerica, "GR", "Greenland");
        Pais alberta = new Pais(northAmerica, "AB", "Alberta");
        Pais ontario = new Pais(northAmerica, "ON", "Ontario");
        Pais quebec = new Pais(northAmerica, "QU", "Quebec");
        Pais westernUS = new Pais(northAmerica, "WU", "Western United States");
        Pais easternUS = new Pais(northAmerica, "EU", "Eastern United States");
        Pais centralAmerica = new Pais(northAmerica, "CA", "Central America");

        Pais venezuela = new Pais(southAmerica, "VE", "Venezuela");
        Pais peru = new Pais(southAmerica, "PE", "Peru");
        Pais brasil = new Pais(southAmerica, "BR", "Brasil");
        Pais argentina = new Pais(southAmerica, "AR", "Argentina");

        Pais eastAfrica = new Pais(africa, "EF", "East Africa");
        Pais egypt = new Pais(africa, "EG", "Egypt");
        Pais northAfrica = new Pais(africa, "NA", "North Africa");
        Pais congo = new Pais(africa, "CO", "Congo");
        Pais southAfrica = new Pais(africa, "SA", "South Africa");
        Pais madagascar = new Pais(africa, "MA", "Madagascar");

        Pais scandinavia = new Pais(europe, "SC", "Scandinavia");
        Pais iceland = new Pais(europe, "IC", "Iceland");
        Pais ukraine = new Pais(europe, "UK", "Ukraine");
        Pais northenEurope = new Pais(europe, "NE", "Northen Europe");
        Pais granBritain = new Pais(europe, "GB", "Gran Britain");
        Pais westernEurope = new Pais(europe, "WE", "Western Europe");
        Pais southernEurope = new Pais(europe, "SE", "Southern Europe");

        Pais ural = new Pais(asia, "UR", "Ural");
        Pais afghanistan = new Pais(asia, "AF", "Afghanistan");
        Pais middleEast = new Pais(asia, "ME", "Middle East");
        Pais yakutsk = new Pais(asia, "YA", "Yakutsk");
        Pais irkutsk = new Pais(asia, "IR", "Irkutsk");
        Pais kamchatka = new Pais(asia, "KA", "Kamchatka");
        Pais mongolia = new Pais(asia, "MO", "Mongolia");
        Pais siberia = new Pais(asia, "SB", "Siberia");
        Pais india = new Pais(asia, "IN", "Índia");
        Pais siam = new Pais(asia, "SI", "Siam");
        Pais china = new Pais(asia, "CH", "China");
        Pais japan = new Pais(asia, "JA", "Japão");

        Pais indonesia = new Pais(australia, "ID", "Indonesia");
        Pais easternAustralia = new Pais(australia, "EA", "Eastern Australia");
        Pais newGuine = new Pais(australia, "NG", "New Guiné");
        Pais weasternAustralia = new Pais(australia, "WA", "Weastern Australia");

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

        // Objetivos

        objetivos = new ArrayList<>(Arrays.asList(
                new ConquistarContinentes(1, europe, australia),
                new ConquistarContinentes(1, europe, southAmerica),
                new ConquistarContinentes(northAmerica, africa),
                new ConquistarContinentes(northAmerica, australia),
                new ConquistarContinentes(asia, southAmerica),
                new ConquistarContinentes(asia, africa),
                new ConquistarNPaises(24),
                new ConquistarNPaises(18, 2),
                new DerrotarJogador(Cor.AMARELO, new ConquistarNPaises(24)),
                new DerrotarJogador(Cor.AZUL, new ConquistarNPaises(24)),
                new DerrotarJogador(Cor.BRANCO, new ConquistarNPaises(24)),
                new DerrotarJogador(Cor.PRETO, new ConquistarNPaises(24)),
                new DerrotarJogador(Cor.VERDE, new ConquistarNPaises(24)),
                new DerrotarJogador(Cor.VERMELHO, new ConquistarNPaises(24))
        ));

        // Baralho

        baralho = new ArrayList<>();

        // dois curingas
        baralho.add(new Carta());
        baralho.add(new Carta());

        // e uma carta por país
        Carta.Simbolo simbolo = Carta.Simbolo.QUADRADO;
        for (Continente continente : continentes)
            for (Pais pais : continente.getPaises()) {
                baralho.add(new Carta(pais, simbolo));
                simbolo = simbolo.getNext();
            }

    }

    @Override
    public int exercitosPorTroca(int i) {
        if (i <= 4)
            return 2 * (i + 1);
        else
            return 5 * (i - 2);
    }
}
