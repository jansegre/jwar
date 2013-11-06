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

package br.eb.ime.jwar.models.templates;

import br.eb.ime.jwar.models.Carta;
import br.eb.ime.jwar.models.Continente;
import br.eb.ime.jwar.models.objetivos.Objetivo;

import java.util.List;
import java.util.Set;

public abstract class Template {

    protected Set<Continente> continentes;
    protected List<Objetivo> objetivos;
    protected List<Carta> baralho;
    protected String mapfile;

    public Set<Continente> getContinentes() {
        return continentes;
    }

    public List<Objetivo> getObjetivos() {
        return objetivos;
    }

    public List<Carta> getBaralho() {
        return baralho;
    }

    public String getMapfile() {
        return mapfile;
    }

    public abstract int exercitosPorTroca(int i);
}
