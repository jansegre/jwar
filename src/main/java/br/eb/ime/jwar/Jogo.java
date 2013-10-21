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

import java.util.*;

public class Jogo {
    
        public List<Jogador> jogadores;
        public Set<Continente> continentes;
    
        public Jogo(int numJogadores, List<Cor> cores)
        {
            continentes = Tabuleiro.mundoWarS();
            for (int i = 0; i < numJogadores; i++) {
             jogadores.add(new Jogador(cores.get(i))); 
            }
            this.distribuirPaises(numJogadores);
        }
    
    
        //distribuir países
        private boolean distribuirPaises(int numJogadores){
            int paisesPorJogador;
            paisesPorJogador = 42/numJogadores;
            List paises = new ArrayList();
            
            //popular lista
            while(continentes.iterator().hasNext()) {
                while(continentes.iterator().next().getPaises().iterator().hasNext()) {
                    paises.add(continentes.iterator().next().getPaises().iterator().next());
                }
            }
            
            //shuffle
            Collections.shuffle(paises);
            
            //distribuir
            for(int i = 0, j = 0; i<42 && j<numJogadores; i++){
                //atribuir pais ao jogador (setDono no pais feito junto)
                jogadores.get(j).addDominio((Pais)paises.get(i));
                if((i+1)%paisesPorJogador == 0)
                    j++;
            }
            return true;
        
        }
    
        //mudar dono do país
        
        public void mudarDono(Jogador donoAntigo, Jogador donoNovo, Pais pais){
            donoNovo.addDominio(pais);
            donoAntigo.removeDominio(pais);
        }
                            
          
}
