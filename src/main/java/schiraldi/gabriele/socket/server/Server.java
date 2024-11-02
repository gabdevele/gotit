package schiraldi.gabriele.socket.server;

import schiraldi.gabriele.socket.SocketDefaults;
import schiraldi.gabriele.socket.Strings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server extends SocketDefaults {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    @Override
    public void start() {
        GameLogic gameLogic = new GameLogic();

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            logger.info(Strings.get("server.listening") + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info(Strings.get("client.connected", clientSocket.getRemoteSocketAddress().toString()));
                new ClientHandler(clientSocket, gameLogic).start();
            }
        } catch (IOException e) {
            logger.severe(Strings.get("server.error"));
        }
    }
}