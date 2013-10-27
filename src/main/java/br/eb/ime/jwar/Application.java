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

package br.eb.ime.jwar;

import br.eb.ime.jwar.models.Jogador;
import br.eb.ime.jwar.models.Tabuleiro;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Application {
    static public void main(String[] args) {
        List<Jogador.Cor> cores = new LinkedList<>();
        cores.add(Jogador.Cor.azul);
        cores.add(Jogador.Cor.vermelho);
        cores.add(Jogador.Cor.amarelo);
        cores.add(Jogador.Cor.preto);
        Jogo jogo = new Jogo(cores, Tabuleiro.mundoRisk());

        String command[], input;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Jogo iniciado.");
        System.out.print(jogo.showFronteiras());
        boolean quit = false;
        while (!quit) {
            System.out.print("> ");
            input = scanner.next().toLowerCase();
            command = input.split("\\s");
            if (command.length > 0) switch (command[0]) {
                case "mostrar":
                case "show":
                    System.out.println(jogo.showExercitos());
                    break;
                case "q":
                case "sair":
                case "quit":
                    System.out.println("Tchau!");
                    quit = true;
                    break;
                default:
                    System.out.println("Comando não reconhecido");
                    break;
            }
        }
    }
}
