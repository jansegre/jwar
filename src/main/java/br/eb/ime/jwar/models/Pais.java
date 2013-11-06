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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Pais {

    private final String codigo;
    private final String nome;
    private final Set<Pais> fronteiras;
    private int exercitos;
    private Jogador dono;

    @Deprecated
    public Pais(String nome) {
        this(null, nome.replaceAll("\\s", "").toLowerCase(), nome);
    }

    public Pais(Continente continente, String codigo, String nome) {
        continente.addPais(this);
        this.codigo = codigo;
        this.nome = nome;
        this.fronteiras = new HashSet<>();
        this.exercitos = 1;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    @JsonIgnore
    public Set<Pais> getFronteiras() {
        return fronteiras;
    }

    public Set<String> getCodigoFronteiras() {
        Set<String> codigoFronteiras = new HashSet<>();
        for (Pais fronteira : fronteiras)
            codigoFronteiras.add(fronteira.codigo);
        return codigoFronteiras;
    }

    public int getExercitos() {
        return exercitos;
    }

    public void setExercitos(int numExercitos) {
        exercitos = numExercitos;
    }

    public void adicionaExercitos(int numExercitos) {
        exercitos += numExercitos;
    }

    public void removeExercitos(int numExercitos) {
        exercitos -= numExercitos;
    }

    public void setDono(Jogador jogador) {
        this.dono = jogador;
    }

    @JsonIgnore
    public Jogador getDono() {
        return this.dono;
    }

    public Cor getCorDono() {
        return this.dono.getCor();
    }

    //XXX: CHAMAR ESTE METODO APENAS NA CONSTRUCAO DO JOGO
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
