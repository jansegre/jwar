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

import java.util.HashSet;
import java.util.Set;

public class Continente {
    final protected String codigo;
    final protected String nome;
    final protected int bonus;
    protected Set<Pais> paises;

    public Continente(String nome, int bonus, Pais... paises) {
        this(nome.replaceAll("\\s", "").toLowerCase(), nome, bonus, paises);
        System.err.println("DEPRECATED: Continente(String nome, int bonus, Pais... paises)");
    }

    public Continente(String codigo, String nome, int bonus, Pais... paises) {
        this.codigo = codigo;
        this.nome = nome;
        this.bonus = bonus;
        this.paises = new HashSet<>(paises.length);
        for (Pais pais : paises) {
            this.paises.add(pais);
            pais.setContinente(this);
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public Set<Pais> getPaises() {
        return paises;
    }

    public String toString() {
        return nome;
    }

    public String showSummary() {
        String out = this + " " + codigo + ": ";
        for (Pais pais : paises)
            out += " " + pais.getCodigo();
        return out;
    }
}
