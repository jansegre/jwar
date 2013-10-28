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
            //TODO: extrair essa lógica daqui
            for (Jogador jogador : jogo.getTabuleiro().getJogadores()) {
                if (jogador.getObjetivo().satisfeito(jogo.getTabuleiro())) {
                    System.out.println("parabéns!! o jogador " + jogador.getSlug() + " ganhou a partida.");
                    break;
                }
            }
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
                            break;
                        }
                    } else {
                        System.out.println(jogo.showExercitos());
                    }
                    break;
                case "ex":
                case "exercitos":
                    if (command.length != 3) {
                        System.out.println("erro! exemplo: exercitos brasil 20");
                        System.out.println("      (define o numero de exercitos do brasil pra 20)");
                        break;
                    }
                    Pais pais = jogo.getTabuleiro().getPaisBySlug(command[1]);
                    if (pais == null) {
                        System.out.println("país não encontrado");
                        break;
                    }
                    int n;
                    try {
                        n = Integer.parseInt(command[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("número mal formatado");
                        break;
                    }
                    pais.setExercitos(n);
                    break;
                case "atk":
                case "attack":
                case "atacar":
                    if (command.length != 3) {
                        System.out.println("erro! exemplo: atacar china siberia");
                        System.out.println("      (ira atacar a siberia pela china)");
                        break;
                    }
                    Pais atacante = jogo.getTabuleiro().getPaisBySlug(command[1]);
                    Pais defensor = jogo.getTabuleiro().getPaisBySlug(command[2]);
                    if (atacante == null || defensor == null) {
                        System.out.println("país não encontrado");
                        break;
                    }
                    //TODO: a seguinte parte contém lógica não deveria estar aqui
                    int numDadosAtk = atacante.getExercitos() - 1;
                    if (numDadosAtk > 3) numDadosAtk = 3;
                    if (numDadosAtk <= 0) {
                        System.out.println(atacante.getSlug() + "não pode atacar, deve possuir pelo menos 2 exércitos");
                        break;
                    }
                    int numDadosDef = defensor.getExercitos();
                    if (numDadosDef > 3) numDadosDef = 3;
                    List<Integer> dadosAtk = jogo.jogarDados(numDadosAtk);
                    List<Integer> dadosDef = jogo.jogarDados(numDadosDef);
                    System.out.print("dados ataque: ");
                    for (int d : dadosAtk)
                        System.out.print(" " + d);
                    System.out.println();
                    System.out.print("dados defesa: ");
                    for (int d : dadosDef)
                        System.out.print(" " + d);
                    System.out.println();
                    if (jogo.comparaDados(dadosAtk, dadosDef)) {
                        System.out.println(atacante.getSlug() + " ganhou a batalha");
                        int defEx = defensor.getExercitos();
                        if (defEx > 1) {
                            defensor.setExercitos(defEx - 1);
                        } else {
                            atacante.setExercitos(atacante.getExercitos() - 1);
                            defensor.setExercitos(1);
                            defensor.setDono(atacante.getDono());
                        }
                    } else {
                        System.out.println(defensor.getSlug() + " ganhou a batalha");
                        atacante.setExercitos(atacante.getExercitos() - 1);
                    }
                    System.out.println(atacante.showShortSummary());
                    System.out.println(defensor.showShortSummary());
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
