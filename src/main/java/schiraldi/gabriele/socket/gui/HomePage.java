package schiraldi.gabriele.socket.gui;

import schiraldi.gabriele.socket.Utils;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class HomePage extends BackgroundPanel implements MessageListener {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    public HomePage(JFrame frame, CardLayout cardLayout, JPanel mainPanel, Client client) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JButton createGameButton = new JButton(Utils.getString("home.create.game"));
        createGameButton.putClientProperty("JButton.buttonType", "roundRect");
        createGameButton.setPreferredSize(new Dimension(150, 30));

        createGameButton.addActionListener(e -> {
                    client.sendMessage("create");
                    cardLayout.show(mainPanel, "WaitingPage");
                }
        );

        JButton joinGameButton = getJoinGameButton(frame, client);

        centerPanel.add(createGameButton, gbc);
        centerPanel.add(joinGameButton, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private static JButton getJoinGameButton(JFrame frame, Client client) {
        JButton joinGameButton = new JButton(Utils.getString("home.join.game"));
        joinGameButton.putClientProperty("JButton.buttonType", "roundRect");
        joinGameButton.setPreferredSize(new Dimension(200, 30));

        joinGameButton.addActionListener(e -> {
                    String code = JOptionPane.showInputDialog(frame, Utils.getString("home.enter.game.code"));
                    if (code != null && code.matches("\\d{4}") && !Utils.wordInvalid(code)) {
                        client.sendMessage("join:" + code);
                    } else {
                        JOptionPane.showMessageDialog(frame, Utils.getString("home.invalid.code"), Utils.getString("home.error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
        );
        return joinGameButton;
    }

    @Override
    public void onMessage(String message) {
        if(message.split(":")[0].equals(Utils.getString("joined"))){
            cardLayout.show(mainPanel, "WaitingPage");
        }
    }
}