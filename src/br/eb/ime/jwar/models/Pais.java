package br.eb.ime.jwar.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Pais {

    protected String nome;
    protected Set<Pais> fronteiras;
    protected int exercitos;
    protected Continente continente;
    protected Jogador dono;

    public Pais(String nome_) {
        nome = nome_;
        fronteiras = new HashSet<>();
        exercitos = 1;
        continente = null;
    }

    public String getNome() {
        return nome;
    }

    public Set<Pais> getFronteiras() {
        return fronteiras;
    }

    public int getExercitos() {
        return exercitos;
    }

    public void setDono(Jogador jogador) {
        this.dono = jogador;
    }

    public Jogador getDono() {
        return this.dono;
    }

    public void addFronteira(Pais... paises) {
        fronteiras.addAll(Arrays.asList(paises));
    }

    public boolean fazFronteira(Pais pais) {
        return fronteiras.contains(pais);
    }
}
