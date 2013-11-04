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

    public enum Simbolo {
        QUADRADO,
        CIRCULO,
        TRIANGULO;

        private String text;
        private Simbolo next;

        static {
            QUADRADO.text = "▢";
            QUADRADO.next = CIRCULO;
            CIRCULO.text = "◯";
            CIRCULO.next = TRIANGULO;
            TRIANGULO.text = "△";
            TRIANGULO.next = QUADRADO;
        }

        public Simbolo getNext() {
            return next;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    final private Pais pais;
    final private Simbolo simbolo;
    final private boolean curinga;

    // Carta curinga
    public Carta() {
        this.pais = null;
        this.simbolo = null;
        this.curinga = true;
    }

    // Carta com símbolo
    public Carta(Pais nome, Simbolo simbolo) {
        this.pais = nome;
        this.simbolo = simbolo;
        this.curinga = false;
    }

    public Pais getPais() {
        return this.pais;
    }

    public boolean ehCuringa() {
        return this.curinga;
    }

    public Simbolo getSimbolo() {
        return this.simbolo;
    }

    public static boolean compativeis(Carta carta1, Carta carta2, Carta carta3) {
        // todas iguais:
        if (carta1.simbolo == carta2.simbolo && carta2.simbolo == carta3.simbolo)
            return true;

        // todas diferentes:
        if (carta1.simbolo != carta2.simbolo && carta1.simbolo != carta3.simbolo && carta2.simbolo != carta3.simbolo)
            return true;

        // troca com curinga
        if (carta1.curinga || carta2.curinga || carta3.curinga)
            return true;

        // não são compatíveis
        return false;
    }

    @Override
    public String toString() {
        return curinga ? "Curinga ▢◯△" : this.simbolo.toString() + " " + this.pais.toString();
    }
}
