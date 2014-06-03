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

package com.jansegre.jwar.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class Continente {
    final protected String codigo;
    final protected String nome;
    final protected int bonus;
    protected Set<Pais> paises;

    @Deprecated
    public Continente(String nome, int bonus) {
        this(nome.replaceAll("\\s", "").toLowerCase(), nome, bonus);
    }

    public Continente(String codigo, String nome, int bonus) {
        this.codigo = codigo;
        this.nome = nome;
        this.bonus = bonus;
        this.paises = new HashSet<>();
    }

    void addPais(Pais... paises) {
        this.paises.addAll(Arrays.asList(paises));
    }

    @JsonProperty
    public String getCodigo() {
        return codigo;
    }

    @JsonProperty
    public String getNome() {
        return nome;
    }

    public Set<Pais> getPaises() {
        return paises;
    }

    // se não tiver dono retorna null
    public Jogador getDono() {
        // deve ter pelo menos 1 país por razões óbvias
        Iterator<Pais> paisIterator = paises.iterator();
        Jogador jogador = paisIterator.next().getDono();

        while (paisIterator.hasNext())
            if (paisIterator.next().getDono() != jogador)
                return null;

        return jogador;
    }

    @JsonProperty
    public int getBonus() {
        return bonus;
    }

    public String toString() {
        return nome;
    }

    public String showSummary() {
        String out = this + " " + codigo + "[" + bonus + "]:";
        for (Pais pais : paises)
            out += " " + pais.getCodigo();
        return out;
    }
}
