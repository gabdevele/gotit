package schiraldi.gabriele.socket.gui;

import schiraldi.gabriele.socket.Utils;

import javax.swing.*;
import java.awt.*;

public class WelcomePage extends BackgroundPanel {
    private final JTextField nameField;

    public WelcomePage(JFrame frame, CardLayout cardLayout, JPanel mainPanel, Client client) {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel nameLabel = new JLabel(Utils.getString("welcome.enter.name"));
        nameLabel.setForeground(Color.WHITE);
        centerPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(200, 30));
        centerPanel.add(nameField, gbc);

        JButton playButton = new JButton(Utils.getString("welcome.play"));
        playButton.putClientProperty("JButton.buttonType", "roundRect");
        playButton.setPreferredSize(new Dimension(80, 30));
        playButton.addActionListener(e -> {
            String name = nameField.getText();
            if (Utils.wordInvalid(name)) {
                JOptionPane.showMessageDialog(frame, Utils.getString("welcome.invalid.name"), Utils.getString("welcome.error"), JOptionPane.ERROR_MESSAGE);
            } else {
                client.sendMessage(Utils.keyString("name", name));
                cardLayout.show(mainPanel, "HomePage");
            }
        });
        centerPanel.add(playButton, gbc);
        add(centerPanel, BorderLayout.CENTER);
    }
}