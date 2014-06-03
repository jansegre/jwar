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

import com.jansegre.jwar.models.Cor;
import com.jansegre.jwar.models.Jogador;
import com.jansegre.jwar.models.Pais;

import java.util.Collection;

public class DerrotarJogador extends Objetivo {

    final private Cor corInimigo;
    final private Objetivo alternativo;

    // objetivo alternativo é usado no caso do inimigo ser o próprio dono
    public DerrotarJogador(Cor corInimigo, Objetivo alternativo) {
        this.corInimigo = corInimigo;
        this.alternativo = alternativo;
        this.description = "derrotar todos os exércitos do jogador " + corInimigo +
                " se esse for você ou não estiver jogando então " + alternativo.toString();
    }

    @Override
    public void setDono(Jogador jogador) {
        super.setDono(jogador);
        this.alternativo.setDono(jogador);
    }

    @Override
    public boolean satisfeito() {
        if (dono.getCor() == corInimigo || !getTabuleiro().getCores().contains(corInimigo)) {
            return alternativo.satisfeito();
        }

        Collection<Pais> paises = getTabuleiro().getPaises();
        for (Pais pais : paises)
            if (pais.getDono().getCor() == corInimigo)
                return false;

        return true;
    }
}
