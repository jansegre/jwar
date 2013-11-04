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

import br.eb.ime.jwar.excecoes.EntradaInvalida;
import br.eb.ime.jwar.excecoes.EstadoInvalido;
import br.eb.ime.jwar.models.*;
import br.eb.ime.jwar.models.objetivos.Objetivo;
import br.eb.ime.jwar.models.templates.Template;

import java.util.*;

public class Jogo {

    private enum Estado {
        REFORCANDO_TERRITORIOS,
        ESCOLHENDO_ALVO,
        JOGANDO_DADOS,
        VERIFICANDO_RESULTADO,
        FAZENDO_TRANFERENCIAS,
        TROCANDO_CARTAS
    }

    private Template template;
    private Estado estadoAtual;
    private Tabuleiro tabuleiro;
    private LinkedList<Carta> cartas;
    private LinkedList<Carta> cartasAparte;
    private Jogador atual;
    private int rodadas;
    private int trocaAtual;
    private int exercitosParaDistribuir;

    public Jogo(List<Cor> cores, Template template) {
        if (cores.size() < 2) {
            throw new EntradaInvalida("cores must have at least 2 elements");
        }

        this.template = template;

        rodadas = 0;
        trocaAtual = 0;
        estadoAtual = Estado.REFORCANDO_TERRITORIOS;

        // jogadores
        List<Jogador> jogadores = new LinkedList<>();
        int numJogadores = cores.size();
        for (Cor cor : cores) {
            jogadores.add(new Jogador(cor));
        }
        Collections.shuffle(jogadores); // ordem aleatória de jogadores
        atual = jogadores.get(0);

        // tabuleiro
        tabuleiro = new Tabuleiro(template.getContinentes(), jogadores);
        distribuirPaises(tabuleiro.getPaises());
        distribuirObjetivos(template.getObjetivos());
        criarCartas(template.getBaralho());
    }

    private void verificarEstado(Estado... estadosValidos) {
        if (!Arrays.asList(estadosValidos).contains(estadoAtual))
            throw new EstadoInvalido("A ação desejada não pode ser realizada no estado " + estadoAtual);
    }

    public Jogador jogadorAtual() {
        return atual;
    }

    public void avancaJogador() {
        verificarEstado();
        List<Jogador> jogadores = tabuleiro.getJogadores();
        int i = jogadores.indexOf(atual) + 1;
        atual = jogadores.get(i % jogadores.size());
        if (i == jogadores.size()) {
            avancaRodada();
        }
    }

    public int getRodadas() {
        return rodadas;
    }

    private void avancaRodada() {
        rodadas++;
    }

    //distribuir países
    private void distribuirPaises(Collection<Pais> paisesInicias) {
        //popular lista
        List<Pais> paises = new ArrayList<>(paisesInicias);
        Collections.shuffle(paises);

        //distribuir
        Iterator<Pais> paisIterator = paises.iterator();
        while (paisIterator.hasNext()) {
            for (Jogador jogador : tabuleiro.getJogadores()) {
                if (paisIterator.hasNext()) {
                    paisIterator.next().setDono(jogador);
                } else {
                    System.err.println("Jogador " + jogador + " em desvantagem");
                }
            }
        }
    }

    private void distribuirObjetivos(Collection<Objetivo> objetivosIniciais) {
        LinkedList<Objetivo> objetivos = new LinkedList<>(objetivosIniciais);
        Collections.shuffle(objetivos);
        for (Jogador jogador : tabuleiro.getJogadores()) {
            jogador.setObjetivo(objetivos.pop());
        }
    }

    private void criarCartas(List<Carta> baralho) {
        this.cartas = new LinkedList<>(baralho);
        this.cartasAparte = new LinkedList<>();
        // embaralhar as cartas
        Collections.shuffle(this.cartas);
    }

    // retorna null se ninguém venceu ainda
    public Jogador vencedor() {
        for (Jogador jogador : tabuleiro.getJogadores()) {
            if (jogador.getObjetivo().satisfeito()) {
                return jogador;
            }
        }
        return null;
    }

    private static Random gerador = new Random();

    public List<Integer> jogarDados(int numDados) {
        List<Integer> dados = new ArrayList<>(numDados);
        //gerador.setSeed();??
        while (numDados-- > 0) {
            dados.add(gerador.nextInt(6) + 1);
        }
        return dados;
    }

    // retorna true se o ataque é estritamente maior
    public boolean comparaDados(List<Integer> ataque, List<Integer> defesa) {
        int somaAtaque = 0, somaDefesa = 0;
        for (int i : ataque) {
            somaAtaque += i;
        }
        for (int i : defesa) {
            somaDefesa += i;
        }
        return somaAtaque > somaDefesa;
    }

    public String showExercitos() {
        String out = "";
        for (Pais pais : tabuleiro.getPaises()) {
            out += pais.showShortSummary() + System.lineSeparator();
        }
        return out;
    }

    public String showFronteiras() {
        String out = "";
        for (Pais pais : tabuleiro.getPaises()) {
            out += pais.getCodigo() + ":";
            for (Pais vizinho : pais.getFronteiras()) {
                out += " " + vizinho.getCodigo();
            }
            out += System.lineSeparator();
        }
        return out;
    }

    public String showContinentes() {
        String out = "";
        for (Continente continente : tabuleiro.getContinentes()) {
            out += continente.showSummary() + System.lineSeparator();
        }
        return out;
    }

    //TODO: mudar a aplicação para que isso não seja nescessário
    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public void darCarta() {
        if (this.cartas.size() == 0) {
            Collections.shuffle(this.cartasAparte);
            LinkedList<Carta> tmp = this.cartas;
            this.cartas = this.cartasAparte;
            this.cartasAparte = tmp;
        }
        atual.addCarta(this.cartas.pop());
        if (atual.getCartas().size() > 5) //TODO: parametrizar?
            ;// o jogador deve descartar uma carta
    }

    public void descartarCarta(Carta carta) {
        this.cartasAparte.add(carta);
        atual.removeCarta(carta);
    }

    public void fazerTrocaDeCartas(Carta carta1, Carta carta2, Carta carta3) {

        // verificar se o usuario tem as cartas usadas
        if (!atual.ehDono(carta1) || !atual.ehDono(carta2) || !atual.ehDono(carta3)) {
            throw new EntradaInvalida("Invalid cartas for this user! He not has one or more these cartas");
        }

        // verificar se troca é possível
        if (!Carta.compativeis(carta1, carta2, carta3))
            throw new EntradaInvalida("Cartas incompatíveis para troca");

        // contar quantos exércitos serão recebidos
        this.trocaAtual++;
        int nExercitos = this.template.exercitosPorTroca(this.trocaAtual);
        for (Carta carta : Arrays.asList(carta1, carta2, carta3))
            if (!carta.ehCuringa() && atual.ehDono(carta.getPais()))
                nExercitos += 2; //TODO: parametrizar?

        // devolver as cartas ao baralho
        atual.removeCarta(carta1);
        atual.removeCarta(carta2);
        atual.removeCarta(carta3);
        this.cartasAparte.push(carta1);
        this.cartasAparte.push(carta2);
        this.cartasAparte.push(carta3);
    }

    public void reforcarTerritorios() {
        estadoAtual = Estado.REFORCANDO_TERRITORIOS;

        int nExercitos = atual.getPaises().size() / 2;
        for (Continente continente : atual.getContinentes())
            nExercitos += continente.getBonus();

        // acrescentar ou sobreescrever?
        //exercitosParaDistribuir += nExercitos;
        exercitosParaDistribuir = nExercitos;
    }

    public void transfereExercito(Pais paisOrigem, Pais paisDestino, int nExercito) {
        //TODO: dar um jeito de fazer esse método por completo, é preciso algum jeito
        //TODO  de comparar o estado antes de todas as transferências com o estado depois
        //TODO  das transferencias (atual), pois um exército não pode ser deslocado mais
        //TODO  de uma vez na mesma rodada, por exemplo, dados 3 paísese contíguos A, B e C:
        //TODO      A(3) B(1) C(2)
        //TODO  não é permitido que algum exército saia de B, então os exércitos de A ou C só podem diminuir
        //TODO  o sistema atual permite que sejam feitas essas duas transferências:
        //TODO      A(3) B(1) C(2) -> A(1) B(3) C(2) -> A(1) B(1) C(4)
        //TODO  mesmo que o último estado não seja permitido.


        //XXX: de onde veio essa regra??
        //if (nExercito > 2)
        //    throw new EntradaInvalida("Can't transfer more than 2 army units");

        if (nExercito >= paisOrigem.getExercitos())
            throw new EntradaInvalida("Can't leave a country without army units");

        // Confere se são vizinhos
        if (!paisDestino.fazFronteira(paisOrigem))
            throw new EntradaInvalida("Must be neighbor countries!");

        // Transação propriamente dita
        paisOrigem.removeExercitos(nExercito);
        paisDestino.adicionaExercitos(nExercito);
    }
}
