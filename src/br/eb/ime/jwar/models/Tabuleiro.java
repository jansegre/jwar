package br.eb.ime.jwar.models;

import java.util.HashSet;
import java.util.Set;

public class Tabuleiro {
    //TODO

    private static Set<Continente> criaMundo() {
        Set<Continente> continentes = new HashSet<>();

        // Países
        Pais alaska = new Pais("Alaska");
        Pais mackenzie = new Pais("Mackenzie");
        Pais groelandia = new Pais("Groelandia");
        Pais vancouver = new Pais("Vancouver");
        Pais ottawa = new Pais("Ottawa");
        Pais labrador = new Pais("Labrador");
        Pais california = new Pais("Califórnia");
        Pais novayork = new Pais("Nova York");
        Pais mexico = new Pais("México");

        // Continentes
        continentes.add(new Continente("América do Norte", alaska, mackenzie, groelandia, vancouver, ottawa, labrador, california, novayork, mexico));

        // Relações
        alaska.addFronteira(mackenzie, vancouver);
        mackenzie.addFronteira(alaska, vancouver, mackenzie, ottawa, labrador, groelandia);
        vancouver.addFronteira(california, ottawa, mackenzie, alaska);
        ottawa.addFronteira(mackenzie, vancouver, labrador, california, novayork);
        labrador.addFronteira(groelandia, ottawa, novayork);
        california.addFronteira(mexico, novayork, ottawa, vancouver);

        return continentes;
    }
}
