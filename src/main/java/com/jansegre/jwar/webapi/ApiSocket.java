package com.jansegre.jwar.webapi;

import com.jansegre.jwar.Jogo;
import com.jansegre.jwar.JogoTextual;
import com.jansegre.jwar.models.Continente;
import com.jansegre.jwar.models.Jogador;
import com.jansegre.jwar.models.Pais;
import com.jansegre.jwar.models.Tabuleiro;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * Handles a server-side channel.
 */
@ChannelHandler.Sharable
public class ApiSocket extends SimpleChannelInboundHandler<String> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, Room> roomMap;
    private final RoomManager roomManager;
    private JogoTextual jogoTextual;
    private String response;
    private int responseCode;

    public ApiSocket(RoomManager roomManager) {
        this.roomManager = roomManager;
        this.roomMap = roomManager.roomMap;
        this.jogoTextual = new JogoTextual() {
            @Override
            public void error(String out) {
                responseCode = Math.max(responseCode, 500);
                res += out + "\n";
            }

            @Override
            public void output(String out) {
                responseCode = Math.max(responseCode, 100);
                res += out + "\n";
            }

            @Override
            public void flush() {
                response = res;
                res = "";
            }

            public String res = "";
        };
    }

    //@Override
    //public void channelActive(ChannelHandlerContext ctx) throws Exception {
    //    // Send greeting for a new connection.
    //    //ctx.write("[100, \"Welcome to JWar! It is " + new Date() + " now.\"].\r\n");
    //    //ctx.flush();
    //    log.info("Connected.");
    //}

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {

        // Generate and write a response.
        response = "";
        responseCode = 0;
        String[] command = request.split("\\s", 3);
        log.info("=> {}", request);
        ChannelFuture future;

        if (command.length > 1 && !roomMap.containsKey(command[1])) {
            responseCode = 404;
            response = "Jogo não encontrado.";
            future = send(ctx, "[" + responseCode + ", \"" + response + "\"].");
        } else if (command.length == 2 && command[0].equalsIgnoreCase("state")) {
            Jogo jogo = roomMap.get(command[1]).jogo;
            // Send the game state formatted for the prolog engine
            //TODO: explain
            response = "[[";
            // Pais infos
            Tabuleiro tabuleiro = jogo.getTabuleiro();
            for (Iterator<Pais> paisIterator = tabuleiro.getPaises().iterator(); paisIterator.hasNext();) {
                Pais pais = paisIterator.next();
                response += pais.getCodigo().toLowerCase();
                if (paisIterator.hasNext())
                    response += ",";
            }
            response += "],[";
            for (Iterator<Pais> paisIterator = tabuleiro.getPaises().iterator(); paisIterator.hasNext();) {
                Pais pais = paisIterator.next();
                response += pais.getExercitos();
                if (paisIterator.hasNext())
                    response += ",";
            }
            response += "],[";
            for (Iterator<Pais> paisIterator = tabuleiro.getPaises().iterator(); paisIterator.hasNext();) {
                Pais pais = paisIterator.next();
                response += pais.getCorDono().toString().toLowerCase();
                if (paisIterator.hasNext())
                    response += ",";
            }
            response += "],[";
            // Jogador infos
            for (Iterator<Jogador> jogadorIterator = tabuleiro.getJogadores().iterator(); jogadorIterator.hasNext();) {
                Jogador jogador = jogadorIterator.next();
                response += jogador.getCor().toString().toLowerCase();
                if (jogadorIterator.hasNext())
                    response += ",";
            }
            response += "],[";
            for (Iterator<Jogador> jogadorIterator = tabuleiro.getJogadores().iterator(); jogadorIterator.hasNext();) {
                Jogador jogador = jogadorIterator.next();
                response += jogador.getObjetivo().toPrologString();
                if (jogadorIterator.hasNext())
                    response += ",";
            }
            response += "],";
            response += jogo.jogadorAtual().getCor().toString().toLowerCase();
            response += ",";
            response += jogo.getRodadas();
            response += ",";
            switch (jogo.getEstadoAtual()) {
                case DISTRIBUICAO_INICIAL:
                case REFORCANDO_TERRITORIOS:
                    response += "placing";
                    break;
                case ESCOLHENDO_ATAQUE:
                    response += "attacking";
                    break;
                case OCUPANDO_TERRITORIO:
                    response += "occupying";
                    break;
                case DESLOCAR_EXERCITOS:
                    response += "moving";
                    break;
                default:
                    response += "wait";
                    break;
            }
            response += ",";
            response += jogo.getExercitosParaDistribuir();
            response += ",";
            response += "[";
            if (jogo.getPaisAtaque() != null && jogo.getPaisDefesa() != null) {
                response += jogo.getPaisDefesa().getCodigo().toLowerCase();
                response += ",";
                response += jogo.getPaisAtaque().getCodigo().toLowerCase();
            }
            response += "],[";
            for (Iterator<Pais> paisIterator = tabuleiro.getPaises().iterator(); paisIterator.hasNext();) {
                Pais pais = paisIterator.next();
                int exercitosMovidos = 0;
                if (jogo.getExercitosMovidos().containsKey(pais))
                    exercitosMovidos = jogo.getExercitosMovidos().get(pais);
                response += (pais.getExercitos() - exercitosMovidos);
                if (paisIterator.hasNext())
                    response += ",";
            }
            response += "]]";
            responseCode = 200;
            future = send(ctx, "[" + responseCode + ", " + response + "].");
        } else if (command.length == 2 && command[0].equalsIgnoreCase("map")) {
            Jogo jogo = roomMap.get(command[1]).jogo;
            // Send the map formatted for the prolog engine
            //TODO: explain
            response = "[[[";
            for (Iterator<Continente> continenteIterator = jogo.getTemplate().getContinentes().iterator(); continenteIterator.hasNext(); ) {
                Continente continente = continenteIterator.next();
                for (Iterator<Pais> paisIterator = continente.getPaises().iterator(); paisIterator.hasNext(); ) {
                    Pais pais = paisIterator.next();
                    response += pais.getCodigo().toLowerCase() + ",[";
                    for (Iterator<Pais> paisFrotIterator = pais.getFronteiras().iterator(); paisFrotIterator.hasNext(); ) {
                        Pais paisFront = paisFrotIterator.next();
                        response += paisFront.getCodigo().toLowerCase();
                        if (paisFrotIterator.hasNext())
                            response += ",";
                    }
                    response += "]]";
                    if (paisIterator.hasNext())
                        response += ",[";
                }
                if (continenteIterator.hasNext())
                    response += ",[";
            }
            response += "],[[";
            for (Iterator<Continente> continenteIterator = jogo.getTemplate().getContinentes().iterator(); continenteIterator.hasNext(); ) {
                Continente continente = continenteIterator.next();
                response += continente.getCodigo().toLowerCase() + ",[";
                for (Iterator<Pais> paisIterator = continente.getPaises().iterator(); paisIterator.hasNext(); ) {
                    Pais pais = paisIterator.next();
                    response += pais.getCodigo().toLowerCase();
                    if (paisIterator.hasNext())
                        response += ",";
                }
                response += "],";
                response += continente.getBonus();
                response += "]";
                if (continenteIterator.hasNext())
                    response += ",[";
            }
            response += "]]";
            responseCode = 200;
            future = send(ctx, "[" + responseCode + ", " + response + "].");
        } else if (command.length == 3 && command[0].equalsIgnoreCase("cmd")) {
            Jogo jogo = roomMap.get(command[1]).jogo;
            jogoTextual.setJogo(jogo);
            //TODO: translate command[2] from prolog to something feedable to jogoTextual
            String[] cmd = command[2].replaceAll("[\\[\\]]", "").split(",");
            String realCmd = null;
            switch (cmd[0]) {
                case "place":
                    realCmd = "REF " + cmd[1].toUpperCase() + " 1";
                    break;
                case "attack":
                    realCmd = "ATK " + cmd[2].toUpperCase() + " " + cmd[1].toUpperCase();
                    break;
                case "occupy":
                    realCmd = "OCC " + cmd[1];
                    break;
                case "done":
                case "next":
                    realCmd = "OK";
                    break;
                case "move":
                    realCmd = "MOV " + cmd[1].toUpperCase() + " " + cmd[2].toUpperCase() + " 1";
                    break;
            }
            if (realCmd != null) {
                log.info("Performing: " + realCmd);
                jogoTextual.feedCommand(realCmd);
            }
            roomManager.pushStateToRoom(command[1], jogo);
            future = send(ctx, "[" + responseCode + ", \"" + response.trim().replaceAll("\n", ";") + "\"].");
        } else {
            responseCode = 400;
            response = "Comando inválido, formato: `<CMD|MAP|STATUS> <room number> [command]`.";
            future = send(ctx, "[" + responseCode + ", \"" + response + "\"].");
        }

        // We do not need to write a ChannelBuffer here.
        // We know the encoder inserted at TelnetPipelineFactory will do the conversion.

        // Close connection after response is sent
        future.addListener(ChannelFutureListener.CLOSE);
    }

    private ChannelFuture send(ChannelHandlerContext ctx, String msg) {
        log.info("<= {}", msg);
        return ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
