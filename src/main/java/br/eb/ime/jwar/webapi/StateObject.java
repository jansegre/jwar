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
import br.eb.ime.jwar.models.Continente;
import br.eb.ime.jwar.models.Jogador;
import br.eb.ime.jwar.models.Pais;
import br.eb.ime.jwar.models.Tabuleiro;

import java.util.List;
import java.util.Map;

public class StateObject {
    public Jogador atual;
    public Jogador vencedor;
    public Jogo.Estado estado;
    public Tabuleiro tabuleiro;
    public String mapfile;
    public boolean welcome;
    public List<Integer> dadosAtaque, dadosDefesa;
    public int reforcos;
    public Map<Pais, Integer> movidos;
    public Map<Continente, Integer> exercitosNoContinente;
    public final String type = "STATE";

    public StateObject(Jogo jogo, boolean welcome) {
        this.tabuleiro = jogo.getTabuleiro();
        this.mapfile = jogo.getTemplate().getMapfile();
        this.welcome = welcome;
        this.atual = jogo.jogadorAtual();
        this.estado = jogo.getEstadoAtual();
        this.dadosAtaque = jogo.getDadosAtaque();
        this.dadosDefesa = jogo.getDadosDefesa();
        this.vencedor = jogo.vencedor();
        this.reforcos = jogo.totalReforcos();
        this.movidos = jogo.getExercitosMovidos();
    }

    public StateObject(Jogo jogo) {
        this(jogo, false);
    }
}
