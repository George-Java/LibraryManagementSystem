package com.wsy.auxiliary;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
public class JImageView extends JComponent {
    private String filePath;
    public JImageView() {
    }
    public JImageView(String filePath) {
        this.filePath = filePath;
    }
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        Image image = null;
        try {
            if (inputStream != null) {
                image = ImageIO.read(inputStream);
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}