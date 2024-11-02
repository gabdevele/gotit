package schiraldi.gabriele.socket.server;

import schiraldi.gabriele.socket.Strings;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

public class GameLogic {
    private static final ConcurrentMap<Integer, Game> games = new ConcurrentHashMap<>();

    private synchronized int generateId() {
        Random random = new Random();
        int id;
        do {
            id = 1000 + random.nextInt(9000);
        } while (games.containsKey(id));
        return id;
    }

    public synchronized Game createGame(Player creator) {
        int id = generateId();
        Game game = new Game(id, creator);
        games.put(id, game);
        return game;
    }

    public synchronized Game joinGame(int gameId, Player player) throws GameErrorException {
        Game game = games.get(gameId);
        if (game == null) throw new GameErrorException(Strings.get("game.not.found"));
        if (game.getPlayers().size() >= 2) throw new GameErrorException(Strings.get("game.full"));
        if (game.getState() != Game.GameState.WAITING) throw new GameErrorException(Strings.get("game.already.started"));
        game.addPlayer(player);
        return game;
    }

    public synchronized String listGames() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Game> entry : games.entrySet()) {
            sb.append("ID: ").append(entry.getKey()).append(" - Giocatori: ");
            for (Player player : entry.getValue().getPlayers()) {
                sb.append(player.getName()).append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public synchronized void removeGame(int gameId) {
        games.remove(gameId);
    }

    public static class Game {
        private final int id;
        public enum GameState {
            WAITING,
            STARTED,
            FINISHED
        }
        private final List<Player> players;
        private final ConcurrentMap<String, List<String>> wordsMap;
        private GameState state;
        private int round;

        public Game(int id, Player creator) {
            this.id = id;
            players = Collections.synchronizedList(new ArrayList<>());
            state = GameState.WAITING;
            wordsMap = new ConcurrentHashMap<>();
            round = 1;
            addPlayer(creator);
        }

        public int getId() {
            return id;
        }

        public void addPlayer(Player player) {
            players.add(player);
            wordsMap.put(player.getId(), Collections.synchronizedList(new ArrayList<>()));
        }

        public void removePlayer(Player player) {
            players.remove(player);
            wordsMap.remove(player.getId());
        }

        public int getRound() {
            return round;
        }

        public void addWord(String playerId, String word) throws WordAlreadyUsedException, GameErrorException {
            if (state != GameState.STARTED) {
                throw new GameErrorException(Strings.get("game.not.started"));
            }
            List<String> words = wordsMap.get(playerId);
            if (words.contains(word)) {
                throw new WordAlreadyUsedException(Strings.get("word.already.used"));
            }
            words.add(word);
        }

        public Player getEnemy(Player player) {
            for (Player p : players) {
                if (!p.getId().equals(player.getId())) {
                    return p;
                }
            }
            return null;
        }

        public List<Player> getPlayers() {
            return players;
        }

        public GameState getState() {
            return state;
        }

        public void setState(GameState state) {
            this.state = state;
        }

        public boolean sameRound() {
            return players.size() == 2 && wordsMap.get(players.get(0).getId()).size() == round && wordsMap.get(players.get(1).getId()).size() == round;
        }

        public String getLastWord(Player player) {
            List<String> words = wordsMap.get(player.getId());
            if (words.isEmpty()) return "";
            return words.get(words.size() - 1);
        }

        public boolean checkForWin() {
            if (players == null || players.size() < 2) return false;

            List<String> player1Words = wordsMap.get(players.get(0).getId());
            List<String> player2Words = wordsMap.get(players.get(1).getId());

            if (player1Words == null || player2Words == null) return false;
            if (player1Words.isEmpty() || player2Words.isEmpty()) return false;

            String player1Word = player1Words.get(round - 1);
            String player2Word = player2Words.get(round - 1);

            if (player1Word.equals(player2Word)) {
                state = GameState.FINISHED;
                return true;
            } else {
                round++;
            }

            return false;
        }
    }

    public static class WordAlreadyUsedException extends Exception {
        public WordAlreadyUsedException(String message) {
            super(message);
        }
    }

    public static class GameErrorException extends Exception {
        public GameErrorException(String message) {
            super(message);
        }
    }
}