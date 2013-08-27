package br.eb.ime.jwar.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Continente {
    final protected String nome;
    final protected int bonus;
    protected Set<Pais> paises;

    public Continente(String nome_, int bonus_, Pais ... paises_) {
        nome = nome_;
        bonus = bonus_;
        paises = new HashSet<>(paises_.length);
        for (Pais pais: paises_) {
            paises.add(pais);
            pais.continente = this;
        }
    }
}
