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

package com.jansegre.jwar;

import com.jansegre.jwar.excecoes.EntradaInvalida;
import com.jansegre.jwar.excecoes.EstadoInvalido;
import com.jansegre.jwar.models.*;
import com.jansegre.jwar.models.objetivos.Objetivo;
import com.jansegre.jwar.models.templates.Template;

import java.util.*;

public final class Jogo {

    /*
             início •──┐
                       ▼
             ┌──────────────────────┐
             │ DISTRIBUICAO_INICIAL │ ◄─── próximo jogador
             └─────────┬──────────┬─┘             │
                       │          └───────────────┘
                segunda rodada
                       │
                       ▼
            ┌────────────────────────┐        ┌──────────────────┐
        ┌─► │ REFORCANDO_TERRITORIOS │ ┌─────►│ ESPERANDO_DEFESA*│
        │   └──────────┬─────────────┘ │      └──┬────────────┬──┘
        │              │               │         │            │
        │      exercitos colocados     │     território conquistado?
 próximo jogador       │               │         │            │
        │              ▼               │        não          sim
        │     ┌───────────────────┐    │         │            │
        │     │ ESCOLHENDO_ATAQUE ├────┘   ┌─────┘            │
        │     └────────┬──────────┘        │                  ▼
        │              │      ▲            │ ┌─────────────────────┐
        │           pronto    └────────────┴─┤ OCUPANDO_TERRITORIO │
 [receber carta]       │                     └──────────┬──────────┘
     pronto            ▼                                │
        │     ┌────────────────────┐            objetivo alcançado?
        └─────┤ DESLOCAR_EXERCITOS │                    │
              └────────────────────┘                    └──■ fim

   * esse estado existe para que o atacado
     possa influencias nos dados de defesa

   Legenda:

   ┌────────┐       │              │          • início
   │ ESTADO │   transição   [ação opcional]
   └────────┘       │              │          ■ fim
                    ▼
    */

    public enum Estado {
        NIL, // para usar em não mudanças
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
    private boolean jogoComecou;
    private Map<Pais, Integer> exercitosMovidos;
    private Map<Continente, Integer> exercitosNoContinente;
    private boolean usarCartas;


    public Jogo(Template template, Cor... cores) {
        this(template, false, cores);
    }

    public Jogo(Template template, boolean usarCartas, Cor... cores) {
        if (cores.length < 2)
            throw new EntradaInvalida("cores must have at least 2 elements");

        this.usarCartas = usarCartas;
        this.template = template;
        this.rodadas = 0;
        this.trocaAtual = 0;
        this.exercitosParaDistribuir = 0;
        this.conquistouExercito = false;
        this.jogoComecou = false;
        this.exercitosMovidos = new HashMap<>();
        this.exercitosNoContinente = new HashMap<>();
        this.paisAtaque = null;
        this.paisDefesa = null;

        // jogadores
        List<Jogador> jogadores = new LinkedList<>();
        for (Cor cor : cores)
            jogadores.add(new Jogador(cor));
        Collections.shuffle(jogadores); // ordem aleatória de jogadores
        atual = jogadores.get(0);

        // tabuleiro
        tabuleiro = new Tabuleiro(template.getContinentes(), jogadores);
        distribuirPaises(tabuleiro.getPaises());
        distribuirObjetivos(template.getObjetivos());
        criarCartas(template.getBaralho());

        // começar o jogo
        //estadoAtual = Estado.REFORCANDO_TERRITORIOS;
        //jogoComecou = true;
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

    public Estado OK() {
        verificarEstado(Estado.ESCOLHENDO_ATAQUE, Estado.DESLOCAR_EXERCITOS, Estado.REFORCANDO_TERRITORIOS);
        switch (estadoAtual) {
            case ESCOLHENDO_ATAQUE:
                return estadoAtual = Estado.DESLOCAR_EXERCITOS;
            case DESLOCAR_EXERCITOS:
                aplicarDeslocamentos();
                return avancaJogador();
            case REFORCANDO_TERRITORIOS:
                if (exercitosParaDistribuir > 0)
                    throw new EstadoInvalido("Ainda restam: " + exercitosParaDistribuir + " exércitos para serem distribuídos.");
                return estadoAtual = Estado.ESCOLHENDO_ATAQUE;
            default:
                return Estado.NIL;
        }
    }

    public Map<Pais, Integer> getExercitosMovidos() {
        return exercitosMovidos;
    }

    public Map<Continente, Integer> getExercitosNoContinente() {
        return exercitosNoContinente;
    }

    public Estado getEstadoAtual() {
        return estadoAtual;
    }

    private Estado avancaJogador() {
        if (conquistouExercito && usarCartas)
            darCarta();

        List<Jogador> jogadores = tabuleiro.getJogadores();
        int i = jogadores.indexOf(atual) + 1;
        atual = jogadores.get(i % jogadores.size());
        if (i == jogadores.size())
            avancaRodada();

        // pular jogador se seus países tiverem acabado
        // XXX cuidado com loops infinitos!
        if (jogadorAtual().getPaises().size() == 0)
            return avancaJogador();

        calcularReforcos();
        conquistouExercito = false;
        if (jogoComecou)
            return estadoAtual = Estado.REFORCANDO_TERRITORIOS;
        else
            return estadoAtual = Estado.DISTRIBUICAO_INICIAL;
    }

    public int getRodadas() {
        return rodadas;
    }

    public Template getTemplate() {
        return template;
    }

    private void avancaRodada() {
        jogoComecou = true;
        rodadas++;
    }

    // distribuir países
    private void distribuirPaises(Collection<Pais> paisesInicias) {
        // popular lista
        List<Pais> paises = new ArrayList<>(paisesInicias);
        Collections.shuffle(paises);

        // distribuir
        Iterator<Pais> paisIterator = paises.iterator();
        while (paisIterator.hasNext()) {
            for (Jogador jogador : tabuleiro.getJogadores()) {
                if (paisIterator.hasNext()) {
                    paisIterator.next().setDono(jogador);
                } else {
                    //System.err.println("Jogador " + jogador + " em desvantagem");
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
    }

    public void fazerTrocaDeCartas(Carta carta1, Carta carta2, Carta carta3) {
        if (!usarCartas) {
            throw new EntradaInvalida("O uso de cartas foi desabilitado.");
        }
        verificarEstado(Estado.REFORCANDO_TERRITORIOS);

        // verificar se o usuario tem as cartas usadas
        if (!atual.ehDono(carta1) || !atual.ehDono(carta2) || !atual.ehDono(carta3)) {
            throw new EntradaInvalida("Uma das cartas especificadas não pertence ao jogador.");
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

    //TODO: fazer isso de um jeito melhor, guardar no país, seilá
    private Continente getContinente(Pais pais) {
        for (Continente continente : tabuleiro.getContinentes())
            if (continente.getPaises().contains(pais))
                return continente;
        return null;
    }

    // retorna o estado novo se ele mudar, se não retorna NIL
    public Estado reforcarPais(Pais pais, int nExercitos) {
        verificarEstado(Estado.REFORCANDO_TERRITORIOS, Estado.DISTRIBUICAO_INICIAL);

        if (pais.getDono() != atual)
            throw new EstadoInvalido("Esse país não é seu.");
        if (atual.getCartas().size() >= 5 && usarCartas)
            throw new EstadoInvalido("Você deve fazer uma troca primeiro.");

        Continente cont = getContinente(pais);
        if (exercitosNoContinente.containsKey(cont)) {
            int n = exercitosNoContinente.get(cont);
            if (nExercitos > n + exercitosParaDistribuir)
                throw new EstadoInvalido("Você não possui tantos exércitos assim");

            if (nExercitos >= n) {
                exercitosNoContinente.remove(cont);
                exercitosParaDistribuir -= (nExercitos - n);
                pais.adicionaExercitos(nExercitos);
            } else {
                exercitosNoContinente.put(cont, n - nExercitos);
                pais.adicionaExercitos(nExercitos);
            }
        } else {
            if (nExercitos > exercitosParaDistribuir)
                throw new EstadoInvalido("Você não possui tantos exércitos assim");

            exercitosParaDistribuir -= nExercitos;
            pais.adicionaExercitos(nExercitos);
        }

        // sempre irá remover primeiro do exercitosNoContinente, então se
        // o exercitosParaDistribuir chegar em 0 é por que terminou de distribuir
        if (totalReforcos() == 0) {
            if (estadoAtual == Estado.DISTRIBUICAO_INICIAL)
                return avancaJogador();
            else
                return estadoAtual = Estado.ESCOLHENDO_ATAQUE;
        } else {
            return Estado.NIL;
        }
    }

    public int getExercitosParaDistribuir() {
        //verificarEstado(Estado.REFORCANDO_TERRITORIOS, Estado.DISTRIBUICAO_INICIAL);
        return exercitosParaDistribuir;
    }

    public int totalReforcos() {
        int total = exercitosParaDistribuir;
        for (int ref : exercitosNoContinente.values())
            total += ref;
        return total;
    }

    private void calcularReforcos() {
        int nExercitos = atual.getPaises().size() / 2;
        for (Continente continente : atual.getContinentes())
            exercitosNoContinente.put(continente, continente.getBonus());
        exercitosParaDistribuir = nExercitos;
        if (totalReforcos() == 0)
            estadoAtual = Estado.ESCOLHENDO_ATAQUE;
    }

    public void deslocarExercitos(Pais paisOrigem, Pais paisDestino, int nExercito) {
        verificarEstado(Estado.DESLOCAR_EXERCITOS);

        //XXX: de onde veio essa regra??
        //if (nExercito > 2)
        //    throw new EntradaInvalida("Can't transfer more than 2 army units");

        if (paisOrigem.getDono() != atual)
            throw new EntradaInvalida("Você não é dono do país " + paisOrigem + ".");
        if (paisDestino.getDono() != atual)
            throw new EntradaInvalida("Você não é dono do país " + paisDestino + ".");
        if (nExercito > paisOrigem.getExercitos())
            throw new EntradaInvalida("País " + paisOrigem + "não possui tantos exércitos assim.");
        if (nExercito == paisOrigem.getExercitos())
            throw new EntradaInvalida("Não pode deixar o país sem exércitos.");
        if (!paisDestino.fazFronteira(paisOrigem))
            throw new EntradaInvalida("Os países devem ser vizinhos");

        // Transação propriamente dita
        paisOrigem.removeExercitos(nExercito);
        int preExistentes = 0;
        if (exercitosMovidos.containsKey(paisDestino))
            preExistentes = exercitosMovidos.get(paisDestino);
        exercitosMovidos.put(paisDestino, preExistentes + nExercito);
    }

    private void aplicarDeslocamentos() {
        for (Pais paisDestino : exercitosMovidos.keySet())
            paisDestino.adicionaExercitos(exercitosMovidos.get(paisDestino));
        // clear após o loop, modificar o set durante o loop é pedir pra dar merda
        exercitosMovidos.clear();
    }

    private static final int maxDados = 3;
    private List<Integer> dadosAtaque, dadosDefesa;
    private int casualidadesAtaque, casualidadesDefesa, maxOcupar;
    private Pais paisAtaque, paisDefesa;

    public Pais getPaisAtaque() {
        return paisAtaque;
    }

    public Pais getPaisDefesa() {
        return paisDefesa;
    }

    public int getCasualidadesAtaque() {
        return casualidadesAtaque;
    }

    public int getCasualidadesDefesa() {
        return casualidadesDefesa;
    }

    public List<Integer> getDadosAtaque() {
        return dadosAtaque;
    }

    public List<Integer> getDadosDefesa() {
        return dadosDefesa;
    }

    // retorna a lista de dados de ataque
    public List<Integer> atacarPais(Pais atacante, Pais defensor) {
        verificarEstado(Estado.ESCOLHENDO_ATAQUE);

        if (atacante.getDono() != atual)
            throw new EntradaInvalida("O país atacante: " + atacante + ", não é seu.");
        if (atacante.getDono() == defensor.getDono())
            throw new EntradaInvalida("O país defensor: " + defensor + ", também é seu.");
        if (!atacante.fazFronteira(defensor))
            throw new EntradaInvalida("Os países " + atacante + " e " + defensor + " não fazem fronteira.");

        int numDadosAtk = atacante.getExercitos() - 1;
        if (numDadosAtk > maxDados) numDadosAtk = maxDados;
        if (numDadosAtk <= 0)
            throw new EntradaInvalida(atacante.getCodigo() + " não pode atacar, deve possuir pelo menos 2 exércitos");

        paisAtaque = atacante;
        paisDefesa = defensor;
        estadoAtual = Estado.ESPERANDO_DEFESA;

        return dadosAtaque = jogarDados(numDadosAtk);
    }

    public List<Integer> defenderPais() {
        verificarEstado(Estado.ESPERANDO_DEFESA);

        int numDadosDef = paisDefesa.getExercitos();
        if (numDadosDef > maxDados) numDadosDef = maxDados;

        dadosDefesa = jogarDados(numDadosDef);

        // contar casualidades
        Collections.sort(dadosAtaque, Collections.reverseOrder());
        Collections.sort(dadosDefesa, Collections.reverseOrder());
        casualidadesAtaque = 0;
        casualidadesDefesa = 0;
        int dadosComparar = Math.min(dadosAtaque.size(), dadosDefesa.size());
        for (int i = 0; i < dadosComparar; i++) {
            if (dadosAtaque.get(i) > dadosDefesa.get(i))
                casualidadesDefesa++;
            else
                casualidadesAtaque++;
        }

        // quantos exércitos podem ocupar o país se vencer
        maxOcupar = dadosAtaque.size() - casualidadesAtaque;

        // subtrair casualidades e verificar se territrório foi conquistado
        paisAtaque.removeExercitos(casualidadesAtaque);
        paisDefesa.removeExercitos(casualidadesDefesa);

        if (paisDefesa.getExercitos() == 0) {
            paisDefesa.setDono(atual);
            conquistouExercito = true;
            estadoAtual = Estado.OCUPANDO_TERRITORIO;
        } else {
            estadoAtual = Estado.ESCOLHENDO_ATAQUE;
        }

        return dadosDefesa;
    }

    public void ocuparPais(int i) {
        verificarEstado(Estado.OCUPANDO_TERRITORIO);
        if (paisAtaque.getExercitos() <= i)
            throw new EstadoInvalido("O país de ataque não possui tantos exércitos assim.");
        if (i > maxOcupar)
            throw new EstadoInvalido("Não podem ser usados mais exércitos do que foram usados no ataque.");

        // ocupar de fato
        paisAtaque.removeExercitos(i);
        paisDefesa.adicionaExercitos(i);
        estadoAtual = Estado.ESCOLHENDO_ATAQUE;
    }
}
