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

import br.eb.ime.jwar.models.Cor;
import br.eb.ime.jwar.models.Jogador;
import br.eb.ime.jwar.models.templates.RiskSecretMission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application extends JogoTextual {

    public Application(Jogo jogo) {
        super(jogo);
    }

    @Override
    public void error(String out) {
        System.out.println(out);
    }

    @Override
    public void output(String out) {
        System.out.println(out);
    }

    private void mainloop() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        welcome();

        boolean quit = false;
        while (!quit) {
            Jogador vencedor = jogo.vencedor();
            if (vencedor != null) {
                System.out.println("Parabéns!! O jogador " + vencedor + " ganhou a partida.");
                System.out.println("Seu objetivo era " + vencedor.getObjetivo() + ".");
                break;
            }
            System.out.print("> ");
            try {
                quit = !feedCommand(reader.readLine());
            } catch (IOException e) {
                System.out.println("Ocorreu um erro inesperado ao ler sua entrada.");
            }
        }
    }

    static public void main(String[] args) {
        Application app = new Application(new Jogo(new RiskSecretMission(), Cor.AZUL, Cor.VERMELHO, Cor.AMARELO, Cor.PRETO));
        app.mainloop();
    }
}
