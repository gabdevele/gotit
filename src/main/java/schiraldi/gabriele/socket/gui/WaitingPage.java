package schiraldi.gabriele.socket.gui;

import schiraldi.gabriele.socket.Strings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

public class WaitingPage extends BackgroundPanel implements MessageListener {
    private final JLabel codeLabel, waitingLabel, waitingDots, joinedPlayerLabel, startingLabel;
    private final Timer dotsTimer;
    private Timer startingTimer = null;
    private final JButton startBtn, exitBtn;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public WaitingPage(JFrame frame, CardLayout cardLayout, JPanel mainPanel, Client client) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        codeLabel = new JLabel("");
        codeLabel.setForeground(Color.WHITE);
        codeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        codeLabel.setMinimumSize(new Dimension(100, 50));

        Border dashedBorder = BorderFactory.createDashedBorder(Color.WHITE, 2, 2);
        Border paddingBorder = new EmptyBorder(10, 10, 10, 10);
        codeLabel.setBorder(new CompoundBorder(dashedBorder, paddingBorder));

        panel.add(codeLabel, gbc);

        waitingLabel = new JLabel(Strings.get("waiting.other.player"));
        waitingLabel.setForeground(Color.WHITE);
        panel.add(waitingLabel, gbc);

        waitingDots = new JLabel(Strings.get("waiting.dots"));
        waitingDots.setForeground(Color.WHITE);
        waitingDots.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(waitingDots, gbc);
        String[] dots = {Strings.get("waiting.dots.0"), Strings.get("waiting.dots.1"), Strings.get("waiting.dots.2")};
        dotsTimer = new Timer(500, e -> {
            String text = waitingDots.getText();
            int next = (Arrays.asList(dots).indexOf(text) + 1) % dots.length;
            waitingDots.setText(dots[next]);
        });
        dotsTimer.setRepeats(true);
        dotsTimer.start();

        joinedPlayerLabel = new JLabel("");
        joinedPlayerLabel.setForeground(Color.WHITE);
        joinedPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        joinedPlayerLabel.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.WHITE), paddingBorder));
        joinedPlayerLabel.setVisible(false);
        panel.add(joinedPlayerLabel, gbc);

        startingLabel = new JLabel(Strings.get("game.starting.in"));
        startingLabel.setForeground(Color.WHITE);
        startingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        startingLabel.setVisible(false);
        panel.add(startingLabel, gbc);

        startingTimer = new Timer(1000, e -> {
            int time = Integer.parseInt(startingLabel.getText().split(" ")[4]);
            if (time == 1) {
                startingTimer.stop();
            } else {
                startingLabel.setText(Strings.get("game.starting.in.prefix") + " " + (time - 1) + " " + Strings.get("game.starting.in.suffix"));
            }
        });

        startBtn = new JButton(Strings.get("button.start.game"));
        startBtn.setEnabled(false);
        startBtn.putClientProperty("JButton.buttonType", "roundRect");
        startBtn.setPreferredSize(new Dimension(180, 30));
        startBtn.addActionListener(e -> {
            client.sendMessage("start");
            startBtn.setEnabled(false);
        });

        exitBtn = new JButton(Strings.get("button.exit"));
        exitBtn.putClientProperty("JButton.buttonType", "roundRect");
        exitBtn.setPreferredSize(new Dimension(180, 30));
        exitBtn.addActionListener(e -> {
            client.sendMessage("exit");
            cardLayout.show(mainPanel, "HomePage");
        });

        panel.add(startBtn, gbc);
        panel.add(exitBtn, gbc);

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void onMessage(String message) {
        String[] msg = message.split(":");
        switch (msg[0]) {
            case "code":
                codeLabel.setText(msg[1]);
                break;
            case "newPlayer":
                waitingLabel.setVisible(false);
                waitingDots.setVisible(false);
                dotsTimer.stop();
                startBtn.setEnabled(true);
                joinedPlayerLabel.setText(Strings.get("other.player") + msg[1]);
                joinedPlayerLabel.setVisible(true);
                break;
            case "playerLeft":
                waitingLabel.setVisible(true);
                waitingDots.setVisible(true);
                dotsTimer.start();
                startBtn.setEnabled(false);
                joinedPlayerLabel.setVisible(false);
                break;
            case "joined":
                codeLabel.setVisible(false);
                joinedPlayerLabel.setText(Strings.get("other.player") + msg[1]);
                joinedPlayerLabel.setVisible(true);
                waitingLabel.setText(Strings.get("waiting.creator"));
                startBtn.setVisible(false);
                break;
            case "starting":
                dotsTimer.stop();

                startBtn.setVisible(false);
                codeLabel.setVisible(false);
                waitingLabel.setVisible(false);
                waitingDots.setVisible(false);
                joinedPlayerLabel.setVisible(false);
                exitBtn.setVisible(false);

                startingLabel.setVisible(true);
                startingTimer.start();

                break;
            case "started":
                cardLayout.show(mainPanel, "GamePage");
                break;
        }
    }
}