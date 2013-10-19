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
import java.util.*;

public class Jogo {
        //distribuir países
        private boolean distribuirPaises(int numJogadores){
            int paisesPorJogador;
            paisesPorJogador = 42/numJogadores;
            List paises = new ArrayList();
            
            //popular lista
            paises.add("Alaska");
            paises.add("Mackenzie");
            paises.add("Groelandia");
            paises.add("Vancouver");
            paises.add("Ottawa");
            paises.add("Labrador");
            paises.add("Califórnia");
            paises.add("Nova York");
            paises.add("México");

            paises.add("Colômbia");
            paises.add("Chile");
            paises.add("Brasil");
            paises.add("Argentina");

            paises.add("Argélia");
            paises.add("Egito");
            paises.add("Sudão");
            paises.add("Congo");
            paises.add("Africa do Sul");
            paises.add("Madagascar");

            paises.add("Suécia");
            paises.add("Islândia");
            paises.add("Alemanha");
            paises.add("Moscou");
            paises.add("Inglaterra");
            paises.add("Portugal");
            paises.add("Polônia");

            paises.add("Omsk");
            paises.add("Dudinka");
            paises.add("Sibéria");
            paises.add("Mongólia");
            paises.add("Tchita");
            paises.add("Vladivostok");
            paises.add("Oriente Médio");
            paises.add("Aral");
            paises.add("Índia");
            paises.add("Vietnã");
            paises.add("China");
            paises.add("Japão");

            paises.add("Sumatra");
            paises.add("Broneo");
            paises.add("Nova Guiné");
            paises.add("Austrália");
            
            //shuffle
            Collections.shuffle(paises);
            
            //distribuir
            for(int i = 0, j = 0; i<42 && j<numJogadores; i++){
                //atribuir jogador ao pais
                //atribuir pais ao jogador
                
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
