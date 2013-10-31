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

public enum Cor {

    AZUL,
    AMARELO,
    VERMELHO,
    VERDE,
    BRANCO,
    PRETO;

    private String text;

    static {
        AZUL.text = "Azul";
        AMARELO.text = "Amarelo";
        VERMELHO.text = "Vermelho";
        VERDE.text = "Verde";
        BRANCO.text = "Branco";
        PRETO.text = "Preto";
    }

    @Override
    public String toString() {
        return text;
    }
}
