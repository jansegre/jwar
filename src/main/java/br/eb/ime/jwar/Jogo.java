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

    public enum Estado {
        Reforcando_Territorios,
        Escolhendo_Alvo,
        Jogando_Dados,
        Verificando_Resultado,
        Fazendo_Tranferencias,
        Trocando_Cartas
    }


    public Jogo(List<Cor> cores, Template template) {
        if (cores.size() < 2)
            throw new IllegalArgumentException("cores must have at least 2 elements");

        rodadas = 0;

        // jogadores
        List<Jogador> jogadores = new LinkedList<>();
        int numJogadores = cores.size();
        for (Cor cor : cores)
            jogadores.add(new Jogador(cor));
        Collections.shuffle(jogadores); // ordem aleatória de jogadores
        atual = jogadores.get(0);

        // tabuleiro
        tabuleiro = new Tabuleiro(template.getContinentes(), jogadores);
        distribuirPaises(tabuleiro.getPaises());
        distribuirObjetivos(template.getObjetivos());
    }

    public Jogador jogadorAtual() {
        return atual;
    }

    public void avancaJogador() {
        List<Jogador> jogadores = tabuleiro.getJogadores();
        int i = jogadores.indexOf(atual) + 1;
        atual = jogadores.get(i % jogadores.size());
        if (i == jogadores.size())
            avancaRodada();
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
        while (paisIterator.hasNext())
            for (Jogador jogador : tabuleiro.getJogadores())
                if (paisIterator.hasNext())
                    paisIterator.next().setDono(jogador);
                else
                    System.err.println("Jogador " + jogador + " em desvantagem");
    }

    private void distribuirObjetivos(Collection<Objetivo> objetivosIniciais) {
        LinkedList<Objetivo> objetivos = new LinkedList<>(objetivosIniciais);
        Collections.shuffle(objetivos);
        for (Jogador jogador : tabuleiro.getJogadores())
            jogador.setObjetivo(objetivos.pop());
    }

    // retorna null se ninguém venceu ainda
    public Jogador vencedor() {
        for (Jogador jogador : tabuleiro.getJogadores())
            if (jogador.getObjetivo().satisfeito())
                return jogador;
        return null;
    }

    private static Random gerador = new Random();

    public List<Integer> jogarDados(int numDados) {
        List<Integer> dados = new ArrayList<>(numDados);
        //gerador.setSeed();??
        while (numDados-- > 0)
            dados.add(gerador.nextInt(6) + 1);
        return dados;
    }

    // retorna true se o ataque é estritamente maior
    public boolean comparaDados(List<Integer> ataque, List<Integer> defesa) {
        int somaAtaque = 0, somaDefesa = 0;
        for (int i : ataque)
            somaAtaque += i;
        for (int i : defesa)
            somaDefesa += i;
        return somaAtaque > somaDefesa;
    }

    public String showExercitos() {
        String out = "";
        for (Pais pais : tabuleiro.getPaises())
            out += pais.showShortSummary() + System.lineSeparator();
        return out;
    }

    public String showFronteiras() {
        String out = "";
        for (Pais pais : tabuleiro.getPaises()) {
            out += pais.getCodigo() + ":";
            for (Pais vizinho : pais.getFronteiras())
                out += " " + vizinho.getCodigo();
            out += System.lineSeparator();
        }
        return out;
    }

    public String showContinentes() {
        String out = "";
        for (Continente continente : tabuleiro.getContinentes())
            out += continente.showSummary() + System.lineSeparator();
        return out;
    }

    //TODO: mudar a aplicação para que isso não seja nescessário
    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }
}
