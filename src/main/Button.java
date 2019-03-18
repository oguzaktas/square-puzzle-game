
package main;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Yazilim Laboratuvari II Proje 1
 * @author Oguz Aktas
 */
public class Button extends JButton {
    
    private BufferedImage image;
    
    public Button() {
        super();
        init();
    }
    
    public Button(Image image) {
        super(new ImageIcon(image));
        init();
    }

    private void init() {
        BorderFactory.createLineBorder(Color.GRAY, 1);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            }
        });
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

}
