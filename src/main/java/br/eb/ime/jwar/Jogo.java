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

import br.eb.ime.jwar.models.*;
import br.eb.ime.jwar.models.templates.Template;

import java.util.*;

public class Jogo {

    private Tabuleiro tabuleiro;
    private Jogador atual;
    private int rodadas;
    private Estado estadoDoJogo;
    private List<Carta> cartas;
    private int trocaAtual;

    public enum Estado {

        Reforcando_Territorios,
        Escolhendo_Alvo,
        Jogando_Dados,
        Verificando_Resultado,
        Fazendo_Tranferencias,
        Trocando_Cartas
    }

    public Jogo(List<Cor> cores, Template template) {
        if (cores.size() < 2) {
            throw new IllegalArgumentException("cores must have at least 2 elements");
        }

        rodadas = 0;

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
        criarCartas(tabuleiro.getPaises());
        this.trocaAtual = 0;
    }

    public Jogador jogadorAtual() {
        return atual;
    }

    public void avancaJogador() {
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

    public void avancaRodada() {
        //TODO: fazer as coisas que precisam quando a rodada avança, ex.: distribuir novos exércitos
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

    private void criarCartas(Collection<Pais> paisesInicias) {
        List<Pais> paises = new ArrayList<>(paisesInicias);
        this.cartas = new ArrayList<>();

        Collections.shuffle(paises);

        //distribuir os simbolos
        for (int i = 0; i < paises.size() / 3; i++) {
            Carta auxiliar = new Carta(paises.get(i), Carta.Simbolo.circulo);
            this.cartas.add(auxiliar);
        }
        for (int i = paises.size() / 3; i < paises.size() * 2 / 3; i++) {
            Carta auxiliar = new Carta(paises.get(i), Carta.Simbolo.quadrado);
            this.cartas.add(auxiliar);

        }
        for (int i = paises.size() * 2 / 3; i < paises.size(); i++) {
            Carta auxiliar = new Carta(paises.get(i), Carta.Simbolo.triangulo);
            this.cartas.add(auxiliar);
        }
    }

    public void darCarta(Jogador jogador) {

        Carta carta = this.cartas.remove(0);
        jogador.addCarta(carta);
        if (jogador.getCartas().size() > 5) {
            // o jogador deve descartar uma carta
        }
    }

    public void descartarCarta(Jogador jogador, Carta carta) {
        this.cartas.add(carta);
        jogador.removeCarta(carta);
    }

    public int fazerTrocaDeCartas(Jogador jogador, List<Carta> cartas) throws Exception {
        if (cartas.size() != 3) {
            throw new IllegalArgumentException("cartas must have three elements");
        }
        // verificar se o usuario tem as cartas usadas
        if(!jogador.getCartas().contains(cartas.get(0)) || !jogador.getCartas().contains(cartas.get(1)) || !jogador.getCartas().contains(cartas.get(2)))
        {
             throw new Exception("Invalid cartas for this user! He not has one or more these cartas");
        }
            
            
        // numero de exercitos ganho
        int nExercitos = 0;
        // verificar se as cartas tem simbolos iguais ou diferentes
        if ((cartas.get(0).getSimbolo() == cartas.get(1).getSimbolo() && cartas.get(1).getSimbolo() == cartas.get(2).getSimbolo())
                || (cartas.get(0).getSimbolo() != cartas.get(1).getSimbolo() && cartas.get(1).getSimbolo() != cartas.get(2).getSimbolo() && cartas.get(2).getSimbolo() != cartas.get(0).getSimbolo())) {
            this.trocaAtual++;
            if (this.trocaAtual <= 4) {
                nExercitos += (this.trocaAtual * 2 + 2);
            } else {
                nExercitos += (this.trocaAtual * 5 - 10);
            }

            // verificar se o usuario tem os paises indicados nas cartas, e atribuir bonus
            if (jogador.getPaises().contains(cartas.get(0).getPais())) {
                nExercitos += 2;
            }

            if (jogador.getPaises().contains(cartas.get(1).getPais())) {
                nExercitos += 2;
            }
            if (jogador.getPaises().contains(cartas.get(2).getPais())) {
                nExercitos += 2;
            }

            jogador.removeCarta(cartas.get(0));          
            jogador.removeCarta(cartas.get(1));
            jogador.removeCarta(cartas.get(2));
            this.cartas.add(cartas.get(0));           
            this.cartas.add(cartas.get(1));
            this.cartas.add(cartas.get(2));
        }
        
        return nExercitos;
    }
}
