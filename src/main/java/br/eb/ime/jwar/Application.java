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

import br.eb.ime.jwar.excecoes.ExcecaoDoJogo;
import br.eb.ime.jwar.models.*;
import br.eb.ime.jwar.models.templates.RiskSecretMission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Application {
    static private class ApplicationException extends ExcecaoDoJogo {
        public ApplicationException(String desc) {
            super(desc);
        }
    }

    static Jogo jogo;

    static public Pais getPaisByCodigo(String codigo) {
        Pais pais = jogo.getTabuleiro().getPaisByCodigo(codigo);
        if (pais == null)
            throw new ApplicationException("País de código " + codigo + " não encontrado");
        return pais;
    }

    static public int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ApplicationException("Número mal formatado");
        }
    }

    static public void main(String[] args) {
        jogo = new Jogo(Arrays.asList(Cor.AZUL, Cor.VERMELHO, Cor.AMARELO, Cor.PRETO), new RiskSecretMission());

        String command[], input;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Jogo iniciado. Estão jogando:");
        for (Jogador jogador : jogo.getTabuleiro().getJogadores())
            System.out.print(" " + jogador);
        System.out.println();
        System.out.println("O jogador " + jogo.jogadorAtual() + " começa.");
        System.out.println("Exércitos para distribuir: " + jogo.getExercitosParaDistribuir());

        boolean quit = false;
        while (!quit) {
            Jogador vencedor = jogo.vencedor();
            if (vencedor != null) {
                System.out.println("Parabéns!! O jogador " + vencedor + " ganhou a partida.");
                System.out.println("Seu objetivo era " + vencedor.getObjetivo() + ".");
                break;
            }
            System.out.print("> ");
            try {
                input = reader.readLine();
                command = input.split("\\s");
                if (command.length <= 0)
                    continue;
                switch (command[0]) {
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
                    case "ca":
                    case "cards":
                    case "cartas":
                        List<Carta> cartas = jogo.jogadorAtual().getCartas();
                        int cn = cartas.size();
                        if (cn == 0) {
                            System.out.println("Nenhuma carta ainda.");
                            break;
                        }
                        for (int i = 0; i < cn; ++i)
                            System.out.println(i + ": " + cartas.get(i));
                        break;
                    case "trk":
                    case "troca":
                        if (command.length != 4) {
                            System.out.println("Erro! Exemplo: troca 0 1 5");
                            break;
                        }
                        int i = parseInt(command[1]);
                        int j = parseInt(command[2]);
                        int k = parseInt(command[3]);
                        List<Carta> cartas1 = jogo.jogadorAtual().getCartas();
                        jogo.fazerTrocaDeCartas(cartas1.get(i), cartas1.get(j), cartas1.get(k));
                        break;
                    case "ok":
                    case "avançar":
                    case "avancar":
                    case "pronto":
                    case "proximo":
                    case "próximo":
                        switch (jogo.OK()) {
                            case DESLOCAR_EXERCITOS:
                                System.out.println("Agora você pode deslocar seus exércitos.");
                                break;
                            case ESCOLHENDO_ATAQUE:
                                System.out.println("Agora você pode efetuar ataques.");
                                break;
                            case DISTRIBUICAO_INICIAL:
                            case REFORCANDO_TERRITORIOS:
                                System.out.println("Vez do jogador " + jogo.jogadorAtual());
                                System.out.println("Exércitos para distribuir: " + jogo.getExercitosParaDistribuir());
                                break;
                            default:
                                System.out.println("Algo errado não está certo.");
                                break;
                        }
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
                            Pais pais = getPaisByCodigo(command[1]);
                            System.out.println(pais.showSummary());
                        } else {
                            System.out.println(jogo.showExercitos());
                        }
                        break;
                    case "my":
                    case "meu":
                    case "meus":
                        int count;
                        count = jogo.jogadorAtual().getContinentes().size();
                        if (count > 0) {
                            System.out.println("Você possui " + count + " continentes.");
                            for (Continente continente : jogo.jogadorAtual().getContinentes())
                                System.out.println("* " + continente.showSummary());
                        }
                        count = jogo.jogadorAtual().getPaises().size();
                        if (count > 0) {
                            System.out.println("Você possui " + count + " países.");
                            for (Pais pais : jogo.jogadorAtual().getPaises())
                                System.out.println(pais.showSummary());
                        }
                        break;
                    case "ne":
                    case "nei":
                    case "neighbors":
                    case "vz":
                    case "viz":
                    case "vizinhos":
                        if (command.length == 1) {
                            // Set é usado pois as entradas não se repetem
                            Set<Pais> vizinhos = new HashSet<>();
                            for (Pais pais : jogo.jogadorAtual().getPaises())
                                for (Pais vizinho : pais.getFronteiras())
                                    vizinhos.add(vizinho);
                            // loop nos vizinhos, agora é garantido que não há repetições
                            for (Pais vizinho : vizinhos)
                                System.out.println(vizinho.showSummary());
                        } else if (command.length == 2) {
                            Pais pais = getPaisByCodigo(command[1]);
                            for (Pais vizinho : pais.getFronteiras())
                                System.out.println(vizinho.showSummary());
                        } else {
                            System.out.println("Erro! Exemplo: viz br");
                            break;
                        }
                        break;
                    case "ref":
                    case "reforçar":
                    case "reforcar":
                    case "ex":
                    case "exercitos":
                        if (command.length == 1) {
                            System.out.println("Exércitos para distribuir: " + jogo.getExercitosParaDistribuir());
                        } else if (command.length == 3) {
                            switch (jogo.reforcarPais(
                                    getPaisByCodigo(command[1]),
                                    parseInt(command[2]))) {
                                case REFORCANDO_TERRITORIOS:
                                    System.out.println("Fim da etapa inicial, agora ataques são possíveis!");
                                    // no break
                                case DISTRIBUICAO_INICIAL:
                                    System.out.println("Vez do jogador " + jogo.jogadorAtual());
                                    // no break
                                case NIL:
                                    System.out.println("Exércitos para distribuir: " + jogo.getExercitosParaDistribuir());
                                    break;
                                case ESCOLHENDO_ATAQUE:
                                    System.out.println("Você agora está em modo de ataque.");
                                    break;
                                default:
                                    System.out.println("Algo errado ocorreu");
                                    break;
                            }
                        } else {
                            System.out.println("Erro! exemplo: ex BR 20");
                            System.out.println("      (define o numero de exercitos do brasil pra 20)");
                            break;
                        }

                        break;
                    case "mv":
                    case "mov":
                    case "move":
                    case "mover":
                    case "deslocar":
                        if (command.length != 4 && command.length != 3) {
                            System.out.println("Erro! exemplo: mov BR VE 5");
                            System.out.println("      (move 5 exércitos do brasil para venezuela");
                            break;
                        }
                        jogo.deslocarExercitos(
                                getPaisByCodigo(command[1]),
                                getPaisByCodigo(command[2]),
                                command.length == 3 ? 1 : parseInt(command[3]));
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
                        Pais atacante = getPaisByCodigo(command[1]);
                        Pais defensor = getPaisByCodigo(command[2]);

                        List<Integer> dadosAtk = jogo.atacarPais(atacante, defensor);
                        List<Integer> dadosDef = jogo.defenderPais();
                        System.out.print("dados ataque: ");
                        for (int d : dadosAtk)
                            System.out.print(" " + d);
                        System.out.println();
                        System.out.print("dados defesa: ");
                        for (int d : dadosDef)
                            System.out.print(" " + d);
                        System.out.println();
                        System.out.println("casualidades de ataque: " + jogo.getCasualidadesAtaque());
                        System.out.println("casualidades de defesa: " + jogo.getCasualidadesDefesa());
                        if (jogo.getEstadoAtual() == Jogo.Estado.OCUPANDO_TERRITORIO)
                            System.out.println("Território conquistado, ocupe-o com 'ocupar'.");
                        System.out.println(atacante.showShortSummary());
                        System.out.println(defensor.showShortSummary());
                        break;
                    case "oc":
                    case "ocupar":
                    case "occupy":
                        if (command.length > 2) {
                            System.out.println("Erro! exemplo: oc 2");
                            System.out.println("      (ocupa o país com 2 exércitos)");
                            break;
                        }
                        int oc = command.length > 1 ? parseInt(command[1]) : 1;
                        jogo.ocuparPais(oc);
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
            } catch (ExcecaoDoJogo e) {
                System.out.println(e.getLocalizedMessage());
            } catch (IOException e) {
                System.out.println("Ocorreu um erro inesperado ao ler sua entrada.");
            }
        }
    }
}
