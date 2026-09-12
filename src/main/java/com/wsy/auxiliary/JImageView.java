package com.wsy.auxiliary;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class JImageView extends JComponent {
    private String filePath;

    public JImageView() {
    }

    public JImageView(String filePath) {
        this.filePath = filePath;
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            try {
                Image image;
                if (inputStream != null) {
                    image = ImageIO.read(inputStream);
                    g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            log.error("Failed to load image resource: {}", filePath, e);
        }
    }
}
