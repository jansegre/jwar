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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Pais {

    protected String codigo;
    protected String nome;
    protected Set<Pais> fronteiras;
    protected int exercitos;
    protected Continente continente;
    protected Jogador dono;

    public Pais(String nome) {
        this(nome.replaceAll("\\s", "").toLowerCase(), nome);
        System.err.println("DEPRECATED: Pais(String nome)");
    }

    public Pais(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
        this.fronteiras = new HashSet<>();
        this.exercitos = 1;
        this.continente = null;
    }

    public void setContinente(Continente continente) {
        this.continente = continente;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    // CHAMAR ESTE METODO PARA VERIFICAR SE UM ATAQUE EH POSSIVEL
    public Set<Pais> getFronteiras() {
        return fronteiras;
    }

    // METODO PARA MUDAR O NUMERO DE EXERCITOS DE UM PAIS
    public int getExercitos() {
        return exercitos;
    }

    public void setExercitos(int numExercitos) {
        exercitos = numExercitos;
    }

    // METODO PARA MUDAR O DONO DO PAIS,
    // DURANTE O JOGO QUANDO UM PAIS MUDAR DE DONO
    // CHAMAR ESTE METODO
    public void setDono(Jogador jogador) {
        this.dono = jogador;
    }

    public Jogador getDono() {
        return this.dono;
    }

    // CHAMAR ESTE METODO APENAS NA CONSTRUCAO DO JOGO
    public void addFronteira(Pais... paises) {
        fronteiras.addAll(Arrays.asList(paises));
    }

    public boolean fazFronteira(Pais pais) {
        return fronteiras.contains(pais);
    }

    public String toString() {
        return nome;
    }

    public String showShortSummary() {
        return this + " " + codigo + ": " + dono + " [" + exercitos + "]";
    }

    public String showSummary() {
        String out = showShortSummary();
        for (Pais fronteira : fronteiras)
            out += " " + fronteira.getCodigo();
        return out;
    }
}
