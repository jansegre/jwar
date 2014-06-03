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

package com.jansegre.jwar;

import com.jansegre.jwar.excecoes.EntradaInvalida;
import com.jansegre.jwar.excecoes.ExcecaoDoJogo;
import com.jansegre.jwar.models.Carta;
import com.jansegre.jwar.models.Continente;
import com.jansegre.jwar.models.Jogador;
import com.jansegre.jwar.models.Pais;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class JogoTextual {
    protected Jogo jogo;

    protected JogoTextual() {
    }

    public JogoTextual(Jogo jogo) {
        this.jogo = jogo;
    }

    public Pais getPaisByCodigo(String codigo) {
        Pais pais = jogo.getTabuleiro().getPaisByCodigo(codigo);
        if (pais == null)
            if (codigo.equalsIgnoreCase("AN"))//XXX: EASTEREGG
                throw new EntradaInvalida("Os penguins desse país se recusam a participar da guerra.");
            else
                throw new EntradaInvalida("País de código " + codigo + " não encontrado");
        return pais;
    }

    public int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new EntradaInvalida("Número mal formatado");
        }
    }

    abstract public void error(String out);

    abstract public void output(String out);

    public void output() {
        output("");
    }

    public void welcome() {
        output("Estão jogando:");
        for (Jogador jogador : jogo.getTabuleiro().getJogadores())
            output(" " + jogador);
        output();
        output("É a vez do jogador " + jogo.jogadorAtual() + " começa.");
        output("Exércitos para distribuir: " + jogo.getExercitosParaDistribuir());
    }

    // use this to flush the output of feedCommand
    public void flush() {
    }

    public boolean feedCommand(String input) {
        String[] command = input.split("\\s");
        if (command.length <= 0)
            return true;
        try {
            switch (command[0]) {
                case "cur":
                case "current":
                case "atual":
                    output("Jogador atual: " + jogo.jogadorAtual());
                    break;
                case "obj":
                case "objetivo":
                case "mission":
                    output("Seu objetivo é " + jogo.jogadorAtual().getObjetivo() + ".");
                    break;
                case "ca":
                case "cards":
                case "cartas":
                    List<Carta> cartas = jogo.jogadorAtual().getCartas();
                    int cn = cartas.size();
                    if (cn == 0) {
                        output("Nenhuma carta ainda.");
                        break;
                    }
                    for (int i = 0; i < cn; ++i)
                        output(i + ": " + cartas.get(i));
                    break;
                case "trk":
                case "troca":
                    if (command.length != 4) {
                        output("Erro! Exemplo: troca 0 1 5");
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
                            output("Agora você pode deslocar seus exércitos.");
                            break;
                        case ESCOLHENDO_ATAQUE:
                            output("Agora você pode efetuar ataques.");
                            break;
                        case DISTRIBUICAO_INICIAL:
                        case REFORCANDO_TERRITORIOS:
                            output("Vez do jogador " + jogo.jogadorAtual());
                            output("Exércitos para distribuir: " + jogo.getExercitosParaDistribuir());
                            break;
                        default:
                            output("Algo errado não está certo.");
                            break;
                    }
                    break;
                case "cont":
                case "continentes":
                    output(jogo.showContinentes());
                    break;
                case "front":
                case "fronteiras":
                    output(jogo.showFronteiras());
                    break;
                case "sh":
                case "show":
                case "mostrar":
                    if (command.length > 1) {
                        Pais pais = getPaisByCodigo(command[1]);
                        output(pais.showSummary());
                    } else {
                        output(jogo.showExercitos());
                    }
                    break;
                case "my":
                case "meu":
                case "meus":
                    int count;
                    count = jogo.jogadorAtual().getContinentes().size();
                    if (count > 0) {
                        output("Você possui " + count + " continentes.");
                        for (Continente continente : jogo.jogadorAtual().getContinentes())
                            output("* " + continente.showSummary());
                    }
                    count = jogo.jogadorAtual().getPaises().size();
                    if (count > 0) {
                        output("Você possui " + count + " países.");
                        for (Pais pais : jogo.jogadorAtual().getPaises())
                            output(pais.showSummary());
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
                            output(vizinho.showSummary());
                    } else if (command.length == 2) {
                        Pais pais = getPaisByCodigo(command[1]);
                        for (Pais vizinho : pais.getFronteiras())
                            output(vizinho.showSummary());
                    } else {
                        output("Erro! Exemplo: viz br");
                        break;
                    }
                    break;
                case "ref":
                case "reforçar":
                case "reforcar":
                case "ex":
                case "exercitos":
                    if (command.length == 1) {
                        output("Exércitos para distribuir: " + jogo.getExercitosParaDistribuir());
                    } else if (command.length == 3) {
                        switch (jogo.reforcarPais(
                                getPaisByCodigo(command[1]),
                                parseInt(command[2]))) {
                            case REFORCANDO_TERRITORIOS:
                                output("Fim da etapa inicial, agora ataques são possíveis!");
                                // no break
                            case DISTRIBUICAO_INICIAL:
                                output("Vez do jogador " + jogo.jogadorAtual());
                                // no break
                            case NIL:
                                output("Exércitos para distribuir: " + jogo.getExercitosParaDistribuir());
                                break;
                            case ESCOLHENDO_ATAQUE:
                                output("Você agora está em modo de ataque.");
                                break;
                            default:
                                output("Algo errado ocorreu");
                                break;
                        }
                    } else {
                        output("Erro! exemplo: ex BR 20");
                        output("      (define o numero de exercitos do brasil pra 20)");
                        break;
                    }
                    break;
                case "mv":
                case "mov":
                case "move":
                case "mover":
                case "deslocar":
                    if (command.length != 4 && command.length != 3) {
                        output("Erro! exemplo: mov BR VE 5");
                        output("      (move 5 exércitos do brasil para venezuela");
                        break;
                    }
                    jogo.deslocarExercitos(
                            getPaisByCodigo(command[1]),
                            getPaisByCodigo(command[2]),
                            command.length == 3 ? 1 : parseInt(command[3]));
                    output("Exércitos movidos.");
                    break;
                case "atk":
                case "attack":
                case "ataque":
                case "atacar":
                    if (command.length != 3) {
                        output("Erro! exemplo: atk CH SB");
                        output("      (china, CH, irá atacar a sibéria, SB)");
                        break;
                    }
                    Pais atacante = getPaisByCodigo(command[1]);
                    Pais defensor = getPaisByCodigo(command[2]);

                    List<Integer> dadosAtk = jogo.atacarPais(atacante, defensor);
                    List<Integer> dadosDef = jogo.defenderPais();
                    String buf;
                    buf = "Dados de ataque:";
                    for (int d : dadosAtk)
                        buf += " " + d;
                    output(buf);
                    buf = "Dados de defesa:";
                    for (int d : dadosDef)
                        buf += " " + d;
                    output(buf);
                    output("Exércitos perdidos pelo ataque: " + jogo.getCasualidadesAtaque());
                    output("Exércitos perdidos pela defesa: " + jogo.getCasualidadesDefesa());
                    if (jogo.getEstadoAtual() == Jogo.Estado.OCUPANDO_TERRITORIO)
                        output("Território conquistado, ocupe-o com 'ocupar'.");
                    output(atacante.showShortSummary());
                    output(defensor.showShortSummary());
                    break;
                case "oc":
                case "ocupar":
                case "occupy":
                    if (command.length > 2) {
                        output("Erro! exemplo: oc 2");
                        output("      (ocupa o país com 2 exércitos)");
                        break;
                    }
                    int oc = command.length > 1 ? parseInt(command[1]) : 1;
                    jogo.ocuparPais(oc);
                    output("País ocupado com " + oc + " exército" + (oc > 1 ? "s" : ""));
                    break;
                case "q":
                case "quit":
                case "exit":
                case "sair":
                case "fechar":
                    output("Tchau!");
                    flush();
                    return false;
                case "":
                    break;
                default:
                    output("Comando '" + command[0] + "' não reconhecido");
                    break;
            }
        } catch (ExcecaoDoJogo e) {
            error(e.getMessage());
        }
        flush();
        return true;
    }
}
