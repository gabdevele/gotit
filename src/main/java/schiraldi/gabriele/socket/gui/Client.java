package schiraldi.gabriele.socket.gui;

import schiraldi.gabriele.socket.SocketDefaults;
import schiraldi.gabriele.socket.Utils;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Client extends SocketDefaults {
    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private final JFrame frame;
    private final List<MessageListener> messageListeners = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();
            new Client();
        });
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();
            new Client();
        });
    }

    public Client() {
        frame = new JFrame("GotIt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 500));
        frame.setMinimumSize(new Dimension(800, 500));

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        WelcomePage welcomePage = new WelcomePage(frame, cardLayout, mainPanel, this);
        HomePage homePage = new HomePage(frame, cardLayout, mainPanel, this);
        WaitingPage waitingPage = new WaitingPage(cardLayout, mainPanel, this);
        GamePage gamePage = new GamePage(cardLayout, mainPanel, this);

        mainPanel.add(welcomePage, "WelcomePage");
        mainPanel.add(homePage, "HomePage");
        mainPanel.add(waitingPage, "WaitingPage");
        mainPanel.add(gamePage, "GamePage");

        messageListeners.add(homePage);
        messageListeners.add(waitingPage);
        messageListeners.add(gamePage);

        frame.getContentPane().add(mainPanel);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                closeConnection();
            }
        });

        frame.setVisible(true);

        start();
    }

    private void notifyMessageListeners(String message) {
        for (MessageListener listener : messageListeners) {
            listener.onMessage(message);
        }
    }

    @Override
    public void start() {
        try {
            clientSocket = new Socket(SERVER_IP, SERVER_PORT);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());

            logger.info(Utils.getString("client.connection.success"));
            new Thread(new ServerListener()).start();
        } catch (IOException e) {
            logger.severe(Utils.getString("client.io.error", e.getMessage()));
            System.exit(1);
        }
    }

    public void sendMessage(String message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            logger.severe(Utils.getString("client.send.error", e.getMessage()));
        }
    }

    private void closeConnection() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            logger.warning(Utils.getString("client.close.error", e.getMessage()));
        }
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String serverMsg = (String) ois.readObject();
                    logger.info(serverMsg);
                    if (serverMsg.split(":")[0].equals("error")) {
                        JOptionPane.showMessageDialog(frame, serverMsg.split(":")[1], Utils.getString("client.error"), JOptionPane.ERROR_MESSAGE);
                    } else {
                        notifyMessageListeners(serverMsg);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.severe(Utils.getString("client.connection.lost", e.getMessage()));
            }
        }
    }
}