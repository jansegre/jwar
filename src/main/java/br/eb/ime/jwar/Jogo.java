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

    /*
             início •──┐
                       ▼
             ┌──────────────────────┐
        ┌───►│ DISTRIBUICAO_INICIAL │ ◄─── próximo jogador
        │    └─────────┬──────────┬─┘             │
        │              │          └───────────────┘
        │       segunda rodada
 próximo jogador       │
        │              ▼
        │   ┌────────────────────────┐        ┌──────────────────┐
        │   │ REFORCANDO_TERRITORIOS │ ┌─────►│ ESPERANDO_DEFESA │
        │   └──────────┬─────────────┘ │      └──┬────────────┬──┘
        │              │               │         │            │
        │              ▼               │  defesa ganhou   ataque ganhou
        │     ┌───────────────────┐    │         │            │
        │     │ ESCOLHENDO_ATAQUE ├────┘         │            ▼
        │     └────────┬──────────┘              │ ┌─────────────────────┐
        │              │    ▲                    ├─┤ OCUPANDO_TERRITORIO │
 [receber carta]       │    └────────────────────┘ └──────────┬──────────┘
        │              ▼                                      │
        │     ┌────────────────────┐                  objetivo cumprido
        └─────┤ DESLOCAR_EXERCITOS │                          │
              └────────────────────┘                          └──■ fim



   Legenda:

   ┌────────┐       │              │          • início
   │ ESTADO │   transição   [ação opcional]
   └────────┘       │              │          ■ fim
                    ▼
    */

    private enum Estado {
        DISTRIBUICAO_INICIAL,
        REFORCANDO_TERRITORIOS,
        ESCOLHENDO_ATAQUE,
        ESPERANDO_DEFESA,
        OCUPANDO_TERRITORIO,
        DESLOCAR_EXERCITOS
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
    private boolean conquistouExercito;

    public Jogo(List<Cor> cores, Template template) {
        if (cores.size() < 2) {
            throw new EntradaInvalida("cores must have at least 2 elements");
        }

        this.template = template;

        rodadas = 0;
        trocaAtual = 0;
        exercitosParaDistribuir = 0;

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

        // começar o jogo
        estadoAtual = Estado.DISTRIBUICAO_INICIAL;
        calcularReforcos();
    }

    private void verificarEstado(Estado... estadosValidos) {
        if (!Arrays.asList(estadosValidos).contains(estadoAtual))
            throw new EstadoInvalido("A ação desejada não pode ser realizada no estado " + estadoAtual);
    }

    public Jogador jogadorAtual() {
        return atual;
    }

    public void avancaJogador() {
        verificarEstado(Estado.DISTRIBUICAO_INICIAL, Estado.DESLOCAR_EXERCITOS);
        if (exercitosParaDistribuir > 0)
            throw new EstadoInvalido("Ainda restam: " + exercitosParaDistribuir + " exércitos para serem distribuídos.");

        List<Jogador> jogadores = tabuleiro.getJogadores();
        int i = jogadores.indexOf(atual) + 1;
        atual = jogadores.get(i % jogadores.size());
        if (i == jogadores.size())
            avancaRodada();

        if (rodadas < 1)
            estadoAtual = Estado.DISTRIBUICAO_INICIAL;
        else
            estadoAtual = Estado.REFORCANDO_TERRITORIOS;

        calcularReforcos();
    }

    public int getRodadas() {
        return rodadas;
    }

    private void avancaRodada() {
        rodadas++;
    }

    // distribuir países
    private void distribuirPaises(Collection<Pais> paisesInicias) {
        //popular lista
        List<Pais> paises = new ArrayList<>(paisesInicias);
        Collections.shuffle(paises);

        // distribuir
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
        List<Integer> dados = new ArrayList<>(3);
        while (numDados-- > 0) {
            dados.add(gerador.nextInt(6) + 1);
        }
        return dados;
    }

    // retorna uma lista com 2 inteiros: 
    //1° = número de vitórias do ataque e 2° = número de vitórias da defesa
    public List<Integer> comparaDados(List<Integer> ataque, List<Integer> defesa) {
        Collections.sort(ataque);
        Collections.sort(defesa);
        int min = Math.min(ataque.size(), defesa.size());
        
        List<Integer> listaVitorias;
        listaVitorias = new ArrayList<>();
        int vitoriaAtaque = 0, vitoriaDefesa = 0;
        
        for(int i = 0; i<min; i++){
            if(ataque.get(i)>defesa.get(i)){
                vitoriaAtaque++;
            }
            else
                vitoriaDefesa++;
        }
        listaVitorias.add(vitoriaAtaque);
        listaVitorias.add(vitoriaDefesa);
        
        return listaVitorias;
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
        exercitosParaDistribuir += nExercitos;

        // devolver as cartas ao baralho
        atual.removeCarta(carta1);
        atual.removeCarta(carta2);
        atual.removeCarta(carta3);
        this.cartasAparte.push(carta1);
        this.cartasAparte.push(carta2);
        this.cartasAparte.push(carta3);
    }

    public void reforcarTerritorio(Pais pais, int nExercitos) {
        if (nExercitos > exercitosParaDistribuir)
            throw new EstadoInvalido("Você não possui tantos exércitos assim");

        exercitosParaDistribuir -= nExercitos;
        pais.adicionaExercitos(nExercitos);
    }

    public void calcularReforcos() {
        estadoAtual = Estado.REFORCANDO_TERRITORIOS;

        int nExercitos = atual.getPaises().size() / 2;
        for (Continente continente : atual.getContinentes())
            nExercitos += continente.getBonus();

        // acrescentar ou sobreescrever?
        //exercitosParaDistribuir += nExercitos;
        exercitosParaDistribuir = nExercitos;
    }
    
    // método para reforcar um determinado pais
    public void reforcarPais(Pais pais, int exercitos)
    {
        if(exercitos > this.exercitosParaDistribuir || this.exercitosParaDistribuir == 0)
        {
            throw new EntradaInvalida("Paises cannot be reinforced.");
        }
        
        pais.adicionaExercitos(exercitos);
        this.exercitosParaDistribuir-=exercitos;
    }

    public void deslocarExercitos(Pais paisOrigem, Pais paisDestino, int nExercito) {
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
    
    public boolean escolherAlvo(Pais atacante, Pais defensor){
        if((atacante.getDono()== atual)&&(atacante.fazFronteira(defensor))&&(atacante.getExercitos()>1)){
            estadoAtual = Estado.ESCOLHENDO_ATAQUE;
            return true;
        }
        else{
            throw new EntradaInvalida("You can't atack from this country\n");
        }
    }
    
    public void efetivarAtaque(Pais atacante, Pais defensor){
        List<Integer> listaVitorias;
        int dadosAtaque, dadosDefesa;
        
        dadosAtaque = atacante.getExercitos()-1;
        dadosDefesa = defensor.getExercitos();
        
        if(dadosAtaque>3){
            dadosAtaque = 3;
        }
        if(dadosDefesa>3){
            dadosDefesa = 3;
        }
        
        if(this.escolherAlvo(atacante, defensor)){
            listaVitorias = comparaDados(jogarDados(dadosAtaque), jogarDados(dadosDefesa));
                    
            //muda numero de exércitos dos países
            defensor.removeExercitos(listaVitorias.get(0));
            atacante.removeExercitos(listaVitorias.get(1));
            //muda dono do país defensor se for o caso
            if(defensor.getExercitos()==0){
                defensor.setDono(atual);
                //por default, coloca apenas um exercito no pais novo
                defensor.adicionaExercitos(1);
                atacante.removeExercitos(1);
            }
        }
    }
}
