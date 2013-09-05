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
    final protected String nome;
    final protected int bonus;
    protected Set<Pais> paises;

    public Continente(String nome_, int bonus_, Pais ... paises_) {
        nome = nome_;
        bonus = bonus_;
        paises = new HashSet<>(paises_.length);
        for (Pais pais: paises_) {
            paises.add(pais);
            pais.setContinente(this);
        }
    }
}
