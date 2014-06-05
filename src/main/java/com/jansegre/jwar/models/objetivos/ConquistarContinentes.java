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
package com.jansegre.jwar.models.objetivos;

import com.jansegre.jwar.models.Continente;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ConquistarContinentes extends Objetivo {

    private final List<Continente> continentes;
    private final int minExtraContinentes;

    public ConquistarContinentes(Continente... continentes) {
        this(0, continentes);
    }

    public ConquistarContinentes(int minExtraContinentes, Continente... continentes) {
        if (continentes.length == 0)
            throw new IllegalArgumentException("`continentes` não pode ser vazio");

        this.continentes = new ArrayList<>(Arrays.asList(continentes));
        this.minExtraContinentes = minExtraContinentes;

        Iterator<Continente> iter = this.continentes.iterator();
        Continente c = iter.next();
        this.description = "conquistar os continentes da " + c.getNome();
        while (iter.hasNext()) {
            c = iter.next();
            this.description += (iter.hasNext() || minExtraContinentes > 0 ? ", " : " e ") + c.getNome();
        }

        if (minExtraContinentes > 0)
            this.description += " e mais " + minExtraContinentes + " de sua escolha";
    }

    @Override
    public boolean satisfeito() {
        // conferir se é dono dos continentes desejados
        for (Continente continente : continentes)
            if (continente.getDono() != dono)
                return false;

        // contar continentes extras
        if (minExtraContinentes > 0) {
            int count = 0;

            // iterar sobre os outros continentes
            for (Continente continente : getTabuleiro().getContinentes()) {
                if (continentes.contains(continente))
                    continue;

                if (continente.getDono() == dono)
                    count++;
            }

            return count >= minExtraContinentes;
        } else {
            return true;
        }
    }

    @Override
    public String toPrologString() {
        String out = "[conts,";
        out += minExtraContinentes + ",";
        for (Iterator<Continente> continenteIterator = continentes.iterator(); continenteIterator.hasNext();) {
            Continente continente = continenteIterator.next();
            out += continente.getCodigo().toLowerCase();
            if (continenteIterator.hasNext())
                out += ",";
        }
        out += "]";
        return out;
    }
}
