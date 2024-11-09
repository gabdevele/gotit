package schiraldi.gabriele.socket.server;

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
                    logger.log(Level.INFO, Utils.getString("connection.closed"));
                    break;
                } catch (ClassNotFoundException | IOException e) {
                    logger.log(Level.SEVERE, Utils.getString("error.handling.message"), e);
                    break;
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, Utils.getString("error.initializing.streams"), e);
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
                sendMessage(Utils.getString("invalid.command"));
                break;
        }
    }

    private void handleName(String name) {
        player = new Player(name, this);
    }

    private void handleCreate() {
        game = gameLogic.createGame(player);
        sendMessage(Utils.keyString(Utils.getString("code"), String.valueOf(game.getId())));
    }

    private void handleList() {
        sendMessage(Utils.keyString(Utils.getString("list"), gameLogic.listGames()));
    }

    private void handleJoin(String gameId) {
        try {
            game = gameLogic.joinGame(Integer.parseInt(gameId), player);
        } catch (GameLogic.GameErrorException e) {
            sendMessage(Utils.keyString(Utils.getString("error"), e.getMessage()));
            return;
        }
        Player enemy = game.getEnemy(player);
        if (enemy != null) {
            enemy.sendMessage(Utils.keyString(Utils.getString("newPlayer"), player.getName()));
            sendMessage(Utils.keyString(Utils.getString("joined"), enemy.getName()));
        }
    }

    private void handleWord(String word) {
        if (Utils.wordInvalid(word)) {
            sendMessage(Utils.getString("invalid.word"));
            return;
        }
        try {
            game.addWord(player.getId(), word);
        } catch (GameLogic.GameErrorException e) {
            sendMessage(Utils.keyString(Utils.getString("error"), e.getMessage()));
        } catch (GameLogic.WordAlreadyUsedException ex){
            sendMessage(Utils.keyString(Utils.getString("lose"), "word"));
        }

        if(game.sameRound()){
            if(game.checkForWin()){
                game.getPlayers().forEach(p -> p.sendMessage(Utils.keyString(Utils.getString("win"), game.getLastWord(player))));
                gameLogic.removeGame(game.getId());
            } else {
                game.getPlayers().forEach(p -> p.sendMessage(Utils.getString("failed") + ":" + game.getLastWord(game.getEnemy(player)) + ":" + game.getRound()));
            }
        }
    }

    private void handleExit() {
        game.removePlayer(player);
        if(game.getPlayers().isEmpty()){
            gameLogic.removeGame(game.getId());
        } else {
            game.getEnemy(player).sendMessage(Utils.getString("playerLeft"));
        }
    }

    public void sendMessage(String message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            logger.log(Level.SEVERE, Utils.getString("error.sending.message"), e);
        }
    }

    public void startGame() {
        sendMessage(Utils.getString("starting"));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            sendMessage(Utils.keyString(Utils.getString("started"), game.getEnemy(player).getName()));
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
            logger.log(Level.SEVERE, Utils.getString("error.closing.connection"), e);
        } finally {
            Thread.currentThread().interrupt();
        }
    }
}