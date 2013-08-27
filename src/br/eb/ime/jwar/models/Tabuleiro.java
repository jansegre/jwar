package br.eb.ime.jwar.models;

import java.util.HashSet;
import java.util.Set;

public class Tabuleiro {
    //TODO

    private static Set<Continente> mundoWarS() {
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
        moscou.addFronteira(suecia, polonia, orienteMedio, aral, omsk);

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
}
