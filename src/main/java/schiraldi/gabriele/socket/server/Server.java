package schiraldi.gabriele.socket.server;

import schiraldi.gabriele.socket.SocketDefaults;
import schiraldi.gabriele.socket.Utils;

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
            logger.info(Utils.getString("server.listening", SERVER_IP, String.valueOf(SERVER_PORT)));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info(Utils.getString("client.connected", clientSocket.getRemoteSocketAddress().toString()));
                new ClientHandler(clientSocket, gameLogic).start();
            }
        } catch (IOException e) {
            logger.severe(Utils.getString("server.error"));
        }
    }
}