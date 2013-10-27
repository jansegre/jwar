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

import java.util.*;

public class Jogo {

    public List<Jogador> jogadores;
    public Set<Continente> continentes;

    public Jogo(List<Cor> cores, Set<Continente> continentes) {
        this.continentes = continentes;
        int numJogadores = cores.size();
        for (Cor cor : cores) {
            jogadores.add(new Jogador(cor));
        }
        this.distribuirPaises(numJogadores);
    }

    //distribuir países
    private boolean distribuirPaises(int numJogadores) {
        int paisesPorJogador;
        paisesPorJogador = 42 / numJogadores;
        List paises = new ArrayList();

        //popular lista
        Iterator iteratorContinentes = continentes.iterator();
        while (iteratorContinentes.hasNext()) {
            Iterator iteratorPaises = continentes.iterator().next().getPaises().iterator();
            while (iteratorPaises.hasNext()) {
                iteratorPaises.next();
            }
        }

        //shuffle
        Collections.shuffle(paises);

        //distribuir
        for (int i = 0, j = 0; i < 42 && j < numJogadores; i++) {
            //atribuir pais ao jogador (setDono no pais feito junto)
            jogadores.get(j).addDominio((Pais) paises.get(i));
            if ((i + 1) % paisesPorJogador == 0) {
                j++;
            }
        }
        return true;

    }

    //mudar dono do país
    public void mudarDono(Jogador donoNovo, Pais pais) {
        Jogador donoAntigo = pais.getDono();
        if (null != donoAntigo) {
            donoAntigo.removeDominio(pais);
        }
        donoNovo.addDominio(pais);
    }


    public void jogarDados(int n_dados) {
        Random gerador = new Random();

        for (int i = 0; i < n_dados; i++) {
            int aux = gerador.nextInt(6) + 1;
            System.out.print(aux + " ");
        }

        System.out.print("\n");
    }

    public void alterarExercitos(int n, Jogador jogador, Pais pais) {
        if (n > 0) {
            jogador.addExercitos(n, pais);
        } else {
            jogador.removeExercitos(n, pais);
        }

    }

}
