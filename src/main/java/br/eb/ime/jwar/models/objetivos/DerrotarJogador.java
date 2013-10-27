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
import br.eb.ime.jwar.models.Objetivo;
import br.eb.ime.jwar.models.Pais;
import br.eb.ime.jwar.models.Tabuleiro;

public class DerrotarJogador extends Objetivo {

    private Jogador inimigo;

    public DerrotarJogador(Jogador inimigo) {
        this.inimigo = inimigo;
    }

    public Jogador getInimigo() {
        return inimigo;
    }

    @Override
    public boolean satisfeito(Tabuleiro tabuleiro) {
        for (Pais pais : tabuleiro.getPaises())
            if (pais.getDono() == inimigo)
                return false;
        return true;
    }
}
