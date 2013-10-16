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
import java.util.*;

public class Jogo {
	void dados(int n_dados){
		Random gerador = new Random();

		for(int i = 0 ; i<n_dados ;i++){
			int aux = gerador.nextInt(6)+1;
			System.out.print(aux + " ");
		}

		System.out.print("\n");
	}

	void add_exercitos(int n, Pais p){
		int n_max_exercito = 10;

		if(p.n_exercitos + n > n_max_exercito) p.n_exercitos = n_max_exercito;

		else	p.n_exercitos += n;
	}

	void remove_exercitos(int n, Pais p){
		if(p.n_exercitos < n) p.n_exercitos = 0;
		else p.n_exercitos -= n;
	}
}

class Pais{
	int n_exercitos;

	Pais(int n){
		n_exercitos = n;
	}
}
