package schiraldi.gabriele.socket.gui;

import schiraldi.gabriele.socket.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;

public class BackgroundPanel extends JPanel {
    private BufferedImage backgroundImage;
    private static final Logger logger = Logger.getLogger(BackgroundPanel.class.getName());
    protected final JPanel topPanel;

    public BackgroundPanel() {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/background.png")));
            if (backgroundImage == null) {
                logger.severe(Utils.getString("background.not.found"));
            }
        } catch (IOException e) {
            logger.severe(Utils.getString("background.loading.error"));
        }

        setLayout(new BorderLayout());

        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/logo.png"))).
                getImage().getScaledInstance(177, 82, Image.SCALE_DEFAULT)));
        topPanel.add(logo);
        add(topPanel, BorderLayout.NORTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}