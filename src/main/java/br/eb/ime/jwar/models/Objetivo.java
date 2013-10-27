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

public abstract class Objetivo {

    protected Jogador dono;

    // deve analisar o tabuleiro e dizer se o dono completou o objetivo
    public abstract boolean satisfeito(Tabuleiro tabuleiro);

    public void setDono(Jogador jogador) {
        this.dono = jogador;
    }
}
