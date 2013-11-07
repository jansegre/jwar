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

import br.eb.ime.jwar.models.Jogador;
import br.eb.ime.jwar.models.Tabuleiro;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class Objetivo {

    @JsonProperty
    protected String description;

    protected Jogador dono;

    protected Objetivo() {
        this.description = "sem descrição";
    }

    public abstract boolean satisfeito();

    public void setDono(Jogador jogador) {
        this.dono = jogador;
    }

    protected Tabuleiro getTabuleiro() {
        return dono.getTabuleiro();
    }

    @Override
    public String toString() {
        return description;
    }
}
