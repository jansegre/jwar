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

import br.eb.ime.jwar.models.Cor;
import br.eb.ime.jwar.models.Jogador;
import br.eb.ime.jwar.models.Pais;
import br.eb.ime.jwar.models.templates.RiskSecretMission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Application {
    static public void main(String[] args) throws IOException {
        List<Cor> cores = new LinkedList<>();
        cores.add(Cor.AZUL);
        cores.add(Cor.VERMELHO);
        cores.add(Cor.AMARELO);
        cores.add(Cor.PRETO);
        Jogo jogo = new Jogo(cores, new RiskSecretMission());

        String command[], input;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Jogo iniciado. Estão jogando:");
        for (Jogador jogador : jogo.getTabuleiro().getJogadores())
            System.out.print(" " + jogador);
        System.out.println();
        System.out.println("O jogador " + jogo.jogadorAtual() + " começa.");

        boolean quit = false;
        while (!quit) {
            Jogador vencedor = jogo.vencedor();
            if (vencedor != null) {
                System.out.println("Parabéns!! O jogador " + vencedor + " ganhou a partida.");
                System.out.println("Seu objetivo era " + vencedor.getObjetivo());
                System.out.println(jogo.showFronteiras());
                break;
            }
            System.out.print("> ");
            input = reader.readLine();
            command = input.split("\\s");
            if (command.length > 0) switch (command[0]) {
                case "cur":
                case "current":
                case "atual":
                    System.out.println("Jogador atual: " + jogo.jogadorAtual());
                    break;
                case "obj":
                case "objetivo":
                case "mission":
                    System.out.println("Seu objetivo é " + jogo.jogadorAtual().getObjetivo() + ".");
                    break;
                case "ok":
                case "avançar":
                case "avancar":
                case "pronto":
                case "proximo":
                case "próximo":
                    jogo.avancaJogador();
                    System.out.println("Vez do jogador " + jogo.jogadorAtual());
                    break;
                case "cont":
                case "continentes":
                    System.out.print(jogo.showContinentes());
                    break;
                case "front":
                case "fronteiras":
                    System.out.print(jogo.showFronteiras());
                    break;
                case "sh":
                case "show":
                case "mostrar":
                    if (command.length > 1) {
                        Pais pais = jogo.getTabuleiro().getPaisBySlug(command[1]);
                        if (pais != null) {
                            System.out.println(pais.showSummary());
                        } else {
                            System.out.println("País não encontrado");
                            break;
                        }
                    } else {
                        System.out.println(jogo.showExercitos());
                    }
                    break;
                case "ex":
                case "exercitos":
                    if (command.length != 3) {
                        System.out.println("Erro! exemplo: ex BR 20");
                        System.out.println("      (define o numero de exercitos do brasil pra 20)");
                        break;
                    }
                    Pais pais = jogo.getTabuleiro().getPaisBySlug(command[1]);
                    if (pais == null) {
                        System.out.println("País não encontrado");
                        break;
                    }
                    int n;
                    try {
                        n = Integer.parseInt(command[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("Número mal formatado");
                        break;
                    }
                    pais.setExercitos(n);
                    break;
                case "atk":
                case "attack":
                case "ataque":
                case "atacar":
                    if (command.length != 3) {
                        System.out.println("Erro! exemplo: atk CH SB");
                        System.out.println("      (china, CH, irá atacar a sibéria, SB)");
                        break;
                    }
                    Pais atacante = jogo.getTabuleiro().getPaisBySlug(command[1]);
                    Pais defensor = jogo.getTabuleiro().getPaisBySlug(command[2]);
                    if (atacante == null || defensor == null) {
                        System.out.println("País não encontrado");
                        break;
                    }
                    //TODO: a seguinte parte contém lógica não deveria estar aqui
                    if (atacante.getDono() != jogo.jogadorAtual()) {
                        System.out.println("O país atacante: " + atacante + ", não é seu.");
                        break;
                    } else if (atacante.getDono() == defensor.getDono()) {
                        System.out.println("O país defensor: " + defensor + ", também é seu.");
                        break;
                    } else if (!atacante.fazFronteira(defensor)) {
                        System.out.println("Os países " + atacante + ", " + defensor + " não fazem fronteira.");
                        break;
                    }
                    int numDadosAtk = atacante.getExercitos() - 1;
                    if (numDadosAtk > 3) numDadosAtk = 3;
                    if (numDadosAtk <= 0) {
                        System.out.println(atacante.getCodigo() + "Não pode atacar, deve possuir pelo menos 2 exércitos");
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
                        System.out.println(atacante.getCodigo() + " ganhou a batalha");
                        int defEx = defensor.getExercitos();
                        if (defEx > 1) {
                            defensor.setExercitos(defEx - 1);
                        } else {
                            atacante.setExercitos(atacante.getExercitos() - 1);
                            defensor.setExercitos(1);
                            defensor.setDono(atacante.getDono());
                        }
                    } else {
                        System.out.println(defensor.getCodigo() + " ganhou a batalha");
                        atacante.setExercitos(atacante.getExercitos() - 1);
                    }
                    System.out.println(atacante.showShortSummary());
                    System.out.println(defensor.showShortSummary());
                    break;
                case "q":
                case "quit":
                case "exit":
                case "sair":
                case "fechar":
                    System.out.println("Tchau!");
                    quit = true;
                    break;
                case "":
                    break;
                default:
                    System.out.println("Comando '" + command[0] + "' não reconhecido");
                    break;
            }
        }
    }
}
