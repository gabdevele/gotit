package schiraldi.gabriele.socket.server;

import schiraldi.gabriele.socket.Strings;
import schiraldi.gabriele.socket.Utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private Player player;
    private final GameLogic gameLogic;
    private GameLogic.Game game;
    private ObjectOutputStream oos;

    public ClientHandler(Socket socket, GameLogic gameLogic) {
        this.clientSocket = socket;
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {
        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                try {
                    String[] msg = ((String) ois.readObject()).split(":");
                    handleClientMessage(msg);
                } catch (SocketException | EOFException e) {
                    logger.log(Level.INFO, Strings.get("connection.closed"));
                    break;
                } catch (ClassNotFoundException | IOException e) {
                    logger.log(Level.SEVERE, Strings.get("error.handling.message"), e);
                    break;
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, Strings.get("error.initializing.streams"), e);
        } finally {
            closeConnection();
        }
    }

    private void handleClientMessage(String[] msg) {
        switch (msg[0]) {
            case "name":
                handleName(msg[1]);
                break;
            case "create":
                handleCreate();
                break;
            case "list":
                handleList();
                break;
            case "join":
                handleJoin(msg[1]);
                break;
            case "start":
                game.getPlayers().forEach(Player::startGame);
                break;
            case "word":
                handleWord(msg[1]);
                break;
            case "exit":
                handleExit();
                break;
            default:
                sendMessage(Strings.get("invalid.command"));
                break;
        }
    }

    private void handleName(String name) {
        player = new Player(name, this);
    }

    private void handleCreate() {
        game = gameLogic.createGame(player);
        sendMessage(Utils.keyString(Strings.get("code"), String.valueOf(game.getId())));
    }

    private void handleList() {
        sendMessage(Utils.keyString(Strings.get("list"), gameLogic.listGames()));
    }

    private void handleJoin(String gameId) {
        try {
            game = gameLogic.joinGame(Integer.parseInt(gameId), player);
        } catch (GameLogic.GameErrorException e) {
            sendMessage(Utils.keyString(Strings.get("error"), e.getMessage()));
            return;
        }
        Player enemy = game.getEnemy(player);
        if (enemy != null) {
            enemy.sendMessage(Utils.keyString(Strings.get("newPlayer"), player.getName()));
            sendMessage(Utils.keyString(Strings.get("joined"), enemy.getName()));
        }
    }

    private void handleWord(String word) {
        if (Utils.wordInvalid(word)) {
            sendMessage(Strings.get("invalid.word"));
            return;
        }
        try {
            game.addWord(player.getId(), word);
        } catch (GameLogic.GameErrorException e) {
            sendMessage(Utils.keyString(Strings.get("error"), e.getMessage()));
        } catch (GameLogic.WordAlreadyUsedException ex){
            sendMessage(Utils.keyString(Strings.get("lose"), "word"));
        }

        if(game.sameRound()){
            if(game.checkForWin()){
                game.getPlayers().forEach(p -> p.sendMessage(Utils.keyString(Strings.get("win"), game.getLastWord(player))));
                gameLogic.removeGame(game.getId());
            } else {
                game.getPlayers().forEach(p -> p.sendMessage(Strings.get("failed") + ":" + game.getLastWord(game.getEnemy(player)) + ":" + game.getRound()));
            }
        }
    }

    private void handleExit() {
        game.removePlayer(player);
        if(game.getPlayers().isEmpty()){
            gameLogic.removeGame(game.getId());
        } else {
            game.getEnemy(player).sendMessage(Strings.get("playerLeft"));
        }
    }

    public void sendMessage(String message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            logger.log(Level.SEVERE, Strings.get("error.sending.message"), e);
        }
    }

    public void startGame() {
        sendMessage(Strings.get("starting"));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            sendMessage(Utils.keyString(Strings.get("started"), game.getEnemy(player).getName()));
            game.setState(GameLogic.Game.GameState.STARTED);
            scheduler.shutdown();
        }, 6, TimeUnit.SECONDS);
    }

    private void closeConnection() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, Strings.get("error.closing.connection"), e);
        } finally {
            Thread.currentThread().interrupt();
        }
    }
}