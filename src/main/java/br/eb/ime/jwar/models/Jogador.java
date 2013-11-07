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

import br.eb.ime.jwar.models.objetivos.Objetivo;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class Jogador {

    private final Cor cor;
    private List<Carta> cartas;
    private Objetivo objetivo;
    private Tabuleiro tabuleiro;

    public Jogador(Cor cor) {
        this.cartas = new LinkedList<>();
        this.cor = cor;
        this.tabuleiro = null;
    }

    public void setTabuleiro(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    @JsonProperty
    public Cor getCor() {
        return this.cor;
    }

    @JsonProperty
    public String getNome() {
        return this.cor.toString();//TODO: esquema de nomes
    }

    public Collection<Pais> getPaises() {
        List<Pais> paises = new ArrayList<>();
        for (Pais pais : tabuleiro.getPaises())
            if (pais.getDono() == this)
                paises.add(pais);
        return paises;
    }

    public Collection<Continente> getContinentes() {
        List<Continente> continentes = new ArrayList<>();
        for (Continente continente : tabuleiro.getContinentes())
            if (continente.getDono() == this)
                continentes.add(continente);
        return continentes;
    }

    public boolean ehDono(Pais pais) {
        return pais.getDono() == this;
    }

    public boolean ehDono(Carta carta) {
        return this.cartas.contains(carta);
    }

    public void addCarta(Carta carta) {
        this.cartas.add(carta);
    }

    public void removeCarta(Carta carta) {
        if (this.cartas.contains(carta)) {
            this.cartas.remove(carta);
        }
    }

    @JsonProperty
    public List<Carta> getCartas() {
        return this.cartas;
    }

    public void setObjetivo(Objetivo objetivo) {
        this.objetivo = objetivo;
        objetivo.setDono(this);
    }

    @JsonProperty
    public Objetivo getObjetivo() {
        return this.objetivo;
    }

    public String toString() {
        return cor.toString();
    }
}
