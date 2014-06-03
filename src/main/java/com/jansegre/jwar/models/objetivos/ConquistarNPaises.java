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

import com.jansegre.jwar.models.Pais;

public class ConquistarNPaises extends Objetivo {

    private final int nPaises;
    private final int minExercitos;

    public ConquistarNPaises(int nPaises) {
        this(nPaises, 1);
    }

    public ConquistarNPaises(int nPaises, int minExercitos) {
        this.nPaises = nPaises;
        this.minExercitos = minExercitos;
        this.description = "conquistar " + nPaises
                + " território" + (nPaises > 2 ? "s" : "");
        if (minExercitos > 1)
            this.description += " com pelo menos " + minExercitos + " exércitos cada";
    }

    @Override
    public boolean satisfeito() {
        int count = 0;
        for (Pais pais : getTabuleiro().getPaises())
            if (pais.getDono() == dono && pais.getExercitos() >= minExercitos)
                count++;
        return count >= nPaises;
    }
}
