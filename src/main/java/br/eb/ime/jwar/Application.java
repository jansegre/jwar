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
import br.eb.ime.jwar.models.Pais;
import br.eb.ime.jwar.models.Tabuleiro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Application {
    static public void main(String[] args) throws IOException {
        List<Jogador.Cor> cores = new LinkedList<>();
        cores.add(Jogador.Cor.azul);
        cores.add(Jogador.Cor.vermelho);
        cores.add(Jogador.Cor.amarelo);
        cores.add(Jogador.Cor.preto);
        Jogo jogo = new Jogo(cores, Tabuleiro.mundoRisk());

        String command[], input;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Jogo iniciado.");
        System.out.print(jogo.showFronteiras());
        boolean quit = false;
        while (!quit) {
            System.out.print("> ");
            input = reader.readLine();
            command = input.split("\\s");
            if (command.length > 0) switch (command[0]) {
                case "continentes":
                    System.out.print(jogo.showContinentes());
                    break;
                case "fronteiras":
                    System.out.print(jogo.showFronteiras());
                    break;
                case "mostrar":
                case "show":
                    if (command.length > 1) {
                        Pais pais = jogo.getTabuleiro().getPaisBySlug(command[1]);
                        if (pais != null) {
                            System.out.println(pais.showSummary());
                        } else {
                            System.out.println("país não encontrado");
                        }
                    } else {
                        System.out.println(jogo.showExercitos());
                    }
                    break;
                case "q":
                case "sair":
                case "quit":
                    System.out.println("tchau!");
                    quit = true;
                    break;
                default:
                    System.out.println("comando não reconhecido");
                    break;
            }
        }
    }
}
