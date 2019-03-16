
package main;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Yazilim Laboratuvari II Proje 1
 * @author Oguz Aktas
 */
public class Button extends JButton {
    
    public Button() {
        super();
        init();
    }
    
    public Button(Image image) {
        super(new ImageIcon(image));
        init();
    }

    private void init() {
        BorderFactory.createLineBorder(Color.GRAY, 2);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.YELLOW, (int) 1.8));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.WHITE, (int) 1.8));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.RED, (int) 1.8));
            }
        });
    }
    
}