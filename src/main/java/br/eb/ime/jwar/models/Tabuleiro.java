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

import java.util.*;

public class Tabuleiro {

    private final Set<Continente> continentes;
    private final Map<String, Pais> paisMap;
    private final List<Jogador> jogadores;
    private final Set<Cor> cores;

    public Tabuleiro(Set<Continente> continentes, List<Jogador> jogadores) {
        this.continentes = continentes;

        this.jogadores = jogadores;
        for (Jogador jogador : jogadores)
            jogador.setTabuleiro(this);

        this.paisMap = new HashMap<>();
        for (Continente continente : continentes)
            for (Pais pais : continente.getPaises())
                this.paisMap.put(pais.getCodigo().toUpperCase(), pais);

        this.cores = new HashSet<>();
        for (Jogador jogador : jogadores) {
            if (this.cores.contains(jogador.getCor()))
                throw new IllegalArgumentException("Não pode haver cores repetidas nos jogadores.");
            else
                this.cores.add(jogador.getCor());
        }
    }

    public Pais getPaisBySlug(String slug) {
        return paisMap.get(slug.toUpperCase());
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public Collection<Continente> getContinentes() {
        return continentes;
    }

    public Collection<Pais> getPaises() {
        return paisMap.values();
    }

    public Collection<Cor> getCores() {
        return cores;
    }
}
