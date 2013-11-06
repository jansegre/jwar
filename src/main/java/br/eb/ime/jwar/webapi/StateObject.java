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

package br.eb.ime.jwar.webapi;

import br.eb.ime.jwar.Jogo;
import br.eb.ime.jwar.models.Tabuleiro;

public class StateObject {
    public Tabuleiro tabuleiro;
    public String mapfile;
    public boolean welcome;

    public StateObject(Jogo jogo, boolean welcome) {
        this.tabuleiro = jogo.getTabuleiro();
        this.mapfile = jogo.getTemplate().getMapfile();
        this.welcome = welcome;
    }

    public StateObject(Jogo jogo) {
        this(jogo, false);
    }
}
