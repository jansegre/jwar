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

package br.eb.ime.jwar.models;

import java.util.List;
import java.util.Random;

public class Jogador {
   
    protected final Cor cor;
    protected List<Pais> dominios;
    protected List<Carta> cartas;
    protected Objetivo objetivo;
    
    
    public enum Cor{
        azul,
        amarelo,
        vermelho,
        verde,
        branco,
        preto
    }
    
    
    public Jogador(Cor cor)
    {
        this.cor = cor;
    }
    
    public Cor getCor()
    {
        return this.cor;
    }
    
    public void addCarta(Carta carta)
    {
        this.cartas.add(carta);
    }
    
    public List<Carta> getCartas()
    {
        return this.cartas;
    }
    
    public void addDominio(Pais pais)
    {
        this.dominios.add(pais);
        pais.setDono(this);
    }
    
    public List<Pais> getDominios()
    {
        return this.dominios;
    }
    
    public void addObjetivo(Objetivo objetivo)
    {
        this.objetivo = objetivo;
        objetivo.setDono(this);
    }
    
    public Objetivo getObjetivo()
    {
        return this.objetivo;
    }
    
    public void dados(int n_dados){
        Random gerador = new Random();

	for(int i = 0 ; i<n_dados ;i++){
		int aux = gerador.nextInt(6)+1;
		System.out.print(aux + " ");
	}

	System.out.print("\n");
    }

    public void addExercitos(int n, Pais p){
	int n_max_exercito = 10;

	if(p.exercitos + n > n_max_exercito) p.exercitos = n_max_exercito;

	else	p.exercitos += n;
    }

    public void removeExercitos(int n, Pais p){
	if(p.exercitos < n) p.exercitos = 0;
	else p.exercitos -= n;
    }
}
