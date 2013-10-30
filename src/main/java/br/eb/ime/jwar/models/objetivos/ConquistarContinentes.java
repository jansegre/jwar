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
package br.eb.ime.jwar.models.objetivos;

import br.eb.ime.jwar.models.Continente;
import br.eb.ime.jwar.models.Objetivo;
import br.eb.ime.jwar.models.Pais;

import java.util.List;

public class ConquistarContinentes extends Objetivo {

    private List<Continente> continentes;
    private int minExtraContinentes;

    public ConquistarContinentes(List<Continente> continentes) {
        this(continentes, 0);
    }

    public ConquistarContinentes(List<Continente> continentes, int minExtraContinentes) {
        this.continentes = continentes;
        this.minExtraContinentes = minExtraContinentes;
    }

    @Override
    public boolean satisfeito() {
        // conferir se é dono dos continentes desejados
        for (Continente continente : continentes)
            for (Pais pais : continente.getPaises())
                if (pais.getDono() != dono)
                    return false;

        // contar continentes extras
        if (minExtraContinentes > 0) {
            int count = 0;

            // iterar sobre os outros continentes
            for (Continente continente : dono.getTabuleiro().getContinentes()) {
                if (continentes.contains(continente))
                    continue;

                boolean owns = true;
                for (Pais pais : continente.getPaises())
                    if (pais.getDono() != dono) {
                        owns = false;
                        break;
                    }

                if (owns)
                    count++;
            }

            return count >= minExtraContinentes;
        } else {
            return true;
        }
    }
}
