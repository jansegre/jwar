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

public class DerrotarJogador extends Objetivo {

    private Jogador.Cor corInimigo;
    private Objetivo alternativo;

    // objetivo alternativo é usado no caso do inimigo ser o próprio dono
    public DerrotarJogador(Jogador.Cor corInimigo, Objetivo alternativo) {
        this.corInimigo = corInimigo;
        this.alternativo = alternativo;
    }

    @Override
    public void setDono(Jogador jogador) {
        super.setDono(jogador);
        this.alternativo.setDono(jogador);
    }

    public Jogador.Cor getCorInimigo() {
        return corInimigo;
    }

    @Override
    public boolean satisfeito() {
        if (dono.getCor() == corInimigo)
            return alternativo.satisfeito();

        for (Pais pais : getTabuleiro().getPaises())
            if (pais.getDono().getCor() == corInimigo)
                return false;

        return true;
    }
}
