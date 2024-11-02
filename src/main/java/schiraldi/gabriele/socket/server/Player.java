package schiraldi.gabriele.socket.server;

import java.util.UUID;

public class Player {
    private final UUID id;
    private final String name;
    private final ClientHandler clientHandler;

    public Player(String name, ClientHandler clientHandler) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.clientHandler = clientHandler;
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public void sendMessage(String message) {
        clientHandler.sendMessage(message);
    }

    public void startGame() {
        clientHandler.startGame();
    }
}
