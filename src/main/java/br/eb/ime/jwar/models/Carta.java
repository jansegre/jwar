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

public class Carta {

    final protected Pais pais;
    final protected Simbolo simbolo;

    public enum Simbolo {
        quadrado,
        circulo,
        triangulo
    }

    public Carta(Pais nome, Simbolo simbolo) {
        this.pais = nome;
        this.simbolo = simbolo;
    }

    public Pais getPais() {
        return this.pais;
    }


    public Simbolo getSimbolo() {
        return this.simbolo;
    }

    public String getSimboloString() {
        switch (this.simbolo) {
            case quadrado:
                return "▢";
            case circulo:
                return "◯";
            case triangulo:
                return "△";
            default:
                return "?";
        }
    }

}
