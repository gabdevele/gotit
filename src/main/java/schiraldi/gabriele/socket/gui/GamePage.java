package schiraldi.gabriele.socket.gui;

import com.formdev.flatlaf.FlatClientProperties;
import schiraldi.gabriele.socket.Utils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class GamePage extends BackgroundPanel implements MessageListener {

    private final JLabel textLabel;
    private final JTextField wordField;
    private final JTextArea logArea;
    private final JButton exitButton;

    public GamePage(CardLayout cardLayout, JPanel mainPanel, Client client) {
        JPanel parentPanel = new JPanel(new BorderLayout());
        parentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        parentPanel.setOpaque(false);

        JPanel borderPanel = new JPanel(new GridBagLayout());
        borderPanel.setBackground(new Color(0, 0, 0, 150));
        borderPanel.setOpaque(false);
        borderPanel.putClientProperty(FlatClientProperties.STYLE,
                "border: 16,16,16,16,#160623,,30"
        );

        textLabel = new JLabel(Utils.getString("game.round.label", 1), JLabel.CENTER);
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(textLabel.getFont().deriveFont(20f));

        wordField = new JTextField(20);
        wordField.setOpaque(false);
        wordField.setFont(wordField.getFont().deriveFont(20f));
        wordField.setHorizontalAlignment(JTextField.CENTER);
        wordField.putClientProperty(FlatClientProperties.STYLE,
                "border: 16,16,16,16,#902CDD,,60"
        );
        wordField.addActionListener(e -> {
            client.sendMessage(Utils.keyString("word", wordField.getText()));
            wordField.setEnabled(false);
        });
        wordField.setToolTipText(Utils.getString("game.word.tooltip"));

        JPanel logBackground = new JPanel(new BorderLayout());
        logBackground.setBackground(new Color(164, 136, 221, 150));
        logBackground.setOpaque(false);
        logBackground.putClientProperty(FlatClientProperties.STYLE,
                "border: 16,16,16,16,#902CDD,,30"
        );

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setForeground(Color.WHITE);
        logArea.setFont(logArea.getFont().deriveFont(14f));
        logArea.setOpaque(false);
        logArea.setToolTipText(Utils.getString("game.log.tooltip"));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(200, 100));


        exitButton = new JButton(Utils.getString("game.exit"));
        exitButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "HomePage");
        });
        exitButton.setVisible(false);

        logBackground.add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10, 0, 10, 0);

        borderPanel.add(textLabel, gbc);

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        borderPanel.add(wordField, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        borderPanel.add(logBackground, gbc);
        gbc.gridy = 3;
        borderPanel.add(exitButton, gbc);
        parentPanel.add(borderPanel, BorderLayout.CENTER);
        add(parentPanel, BorderLayout.CENTER);
    }

    @Override
    public void onMessage(String message) {
        String[] msg = message.split(":");
        switch (msg[0]) {
            case "started":
                wordField.setText("");
                logArea.removeAll();
                logArea.append(Utils.getString("game.started", msg[1]) + "\n");
                break;
            case "failed":
                logArea.append(Utils.getString("game.failed", msg[1]) + "\n");
                wordField.setText("");
                wordField.setEnabled(true);
                textLabel.setText(Utils.getString("game.round.label", msg[2]));
                break;
            case "lost":
                //TODO: implementare sconfitta
                break;
            case "win":
                //TODO: sistemare grafica vittoria
                exitButton.setVisible(true);
                logArea.append(Utils.getString("game.win", msg[1]) + "\n");
                wordField.setVisible(false);
                textLabel.setVisible(false);
                break;
        }
    }
}