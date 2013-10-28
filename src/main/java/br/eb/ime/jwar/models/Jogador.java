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

import java.util.LinkedList;
import java.util.List;

public class Jogador {

    private final Cor cor;
    private List<Carta> cartas;
    private Objetivo objetivo;
    private Tabuleiro tabuleiro;

    public enum Cor {
        azul,
        amarelo,
        vermelho,
        verde,
        branco,
        preto
    }

    public Jogador(Cor cor) {
        this.cartas = new LinkedList<>();
        this.cor = cor;
        this.tabuleiro = null;
    }

    public void setTabuleiro(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Cor getCor() {
        return this.cor;
    }

    public String getCorName() {
        switch (getCor()) {
            case azul:
                return "Azul";
            case amarelo:
                return "Amarelo";
            case vermelho:
                return "Vermelho";
            case verde:
                return "Verde";
            case branco:
                return "Branco";
            case preto:
                return "Preto";
            default:
                return "???";
        }
    }

    public void addCarta(Carta carta) {
        this.cartas.add(carta);
    }

    public List<Carta> getCartas() {
        return this.cartas;
    }

    public void setObjetivo(Objetivo objetivo) {
        this.objetivo = objetivo;
        objetivo.setDono(this);
    }

    public Objetivo getObjetivo() {
        return this.objetivo;
    }

    public String toString() {
        return getCorName();
    }
}
