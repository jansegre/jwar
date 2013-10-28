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

import br.eb.ime.jwar.models.Continente;
import br.eb.ime.jwar.models.Jogador;
import br.eb.ime.jwar.models.Jogador.Cor;
import br.eb.ime.jwar.models.Pais;
import br.eb.ime.jwar.models.Tabuleiro;
import br.eb.ime.jwar.models.objetivos.ConquistarMundo;

import java.util.*;

public class Jogo {

    public Set<Continente> continentes;
    private Tabuleiro tabuleiro;

    public Jogo(List<Cor> cores, Set<Continente> continentes) {
        List<Jogador> jogadores = new LinkedList<>();
        int numJogadores = cores.size();
        for (Cor cor : cores)
            jogadores.add(new Jogador(cor));
        tabuleiro = new Tabuleiro(continentes, jogadores);
        distribuirPaises();
        distribuirObjetivos();
    }

    //distribuir países
    private void distribuirPaises() {

        //popular lista
        List<Pais> paises = new ArrayList<>();
        for (Pais pais : tabuleiro.getPaises())
            paises.add(pais);
        Collections.shuffle(paises);

        //distribuir
        Iterator<Pais> paisIterator = paises.iterator();
        while (paisIterator.hasNext()) {
            for (Jogador jogador : tabuleiro.getJogadores()) {
                if (!paisIterator.hasNext()) {
                    System.err.println("jogador " + jogador.getSlug() + " em desvantagem");
                } else {
                    paisIterator.next().setDono(jogador);
                }
            }
        }
    }

    private void distribuirObjetivos() {
        //TODO mais objetivos
        for (Jogador jogador : tabuleiro.getJogadores()) {
            jogador.setObjetivo(new ConquistarMundo());
        }
    }

    //mudar dono do país
    public void mudarDono(Jogador donoNovo, Pais pais) {
        Jogador donoAntigo = pais.getDono();
        if (null != donoAntigo) {
            donoAntigo.removeDominio(pais);
        }
        donoNovo.addDominio(pais);
    }

    // retorna null se ninguém venceu ainda
    public Jogador vencedor() {
        for (Jogador jogador : tabuleiro.getJogadores())
            if (jogador.getObjetivo().satisfeito(tabuleiro))
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

    public void alterarExercitos(int n, Jogador jogador, Pais pais) {
        //TODO: verificar jogador
        pais.setExercitos(pais.getExercitos() + n);
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
            out += pais.getSlug() + ":";
            for (Pais vizinho : pais.getFronteiras())
                out += " " + vizinho.getSlug();
            out += System.lineSeparator();
        }
        return out;
    }

    public String showContinentes() {
        String out = "";
        for (Continente continente : tabuleiro.getContinentes()) {
            out += continente.getSlug() + ": ";
            for (Pais pais : continente.getPaises())
                out += " " + pais.getSlug();
            out += System.lineSeparator();
        }
        return out;
    }

    //TODO: mudar a aplicação para que isso não seja nescessário
    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }
}
