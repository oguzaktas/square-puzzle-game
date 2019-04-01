package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Yazilim Laboratuvari II Proje 1
 * @author Oguz Aktas
 */
public class Main extends javax.swing.JFrame {

    private BufferedImage source;
    private BufferedImage resized;
    private Image image;
    private int width;
    private int height;
    private Button clickedButton;
    private List<Button> buttons;
    private final int NUMBER_OF_BUTTONS = 16;
    private final int DESIRED_WIDTH = 520; // Resim dosyasinin default genislik (width) boyutu 520 olarak belirlendi.
    private String filePath;
    private int score = 0; // Oyuna baslarken default olarak 0 puan verilir.
    private final BufferedImage[] images = new BufferedImage[16]; // Baslangicta dogru yerde bulunan 16 puzzle parcasini tutan degisken
    private boolean isSolved = false; // Puzzle'in bitip bitmedigini kontrol eden degisken
    private int numberOfMoves = 0; // Hamle sayisi

    /**
     * Creates new form Main
     * @throws java.io.IOException
     */
    public Main() throws IOException {
        initComponents();
        this.pack();
        this.setLocationRelativeTo(null);
        getHighestScore();
    }

    private BufferedImage loadImage() throws IOException {
        BufferedImage buffimg = ImageIO.read(new File(filePath));
        return buffimg;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private BufferedImage convertToBufferedImage(Image image) { // Image tipindeki resim dosyasini BufferedImage'e cevirir.
        BufferedImage newImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    private void buildPuzzle() throws IOException {
        jPanel1.removeAll();
        jPanel1.revalidate();
        jPanel1.repaint();
        try {
            source = loadImage();
            double ratio = DESIRED_WIDTH / (double) source.getWidth();
            int newHeight = (int) (source.getHeight() * ratio);
            resized = resizeImage(source, DESIRED_WIDTH, newHeight, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(jPanel1, "<html><b>Resim dosyasi okunamadi.</b></html>", "Error", JOptionPane.ERROR_MESSAGE);
        }

        width = resized.getWidth(null);
        height = resized.getHeight(null);
        buttons = new ArrayList<Button>();
        jPanel1.setPreferredSize(new Dimension(width, height));
        jPanel1.setLayout(new GridLayout(4, 4, 0, 0));
        for (int i = 0, k = 0; i < 4; i++, k += 4) {
            for (int j = 0; j < 4; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(), new CropImageFilter(j * (width / 4), i * (height / 4), (width / 4), (height / 4))));
                BufferedImage bufferedImage = convertToBufferedImage(image);
                Button button = new Button(bufferedImage);
                images[k + j] = bufferedImage;
                button.setImage(bufferedImage); // Her butonun bulundurdugu puzzle parcasi konumdan bagimsiz olarak kaydedildi.
                buttons.add(button);
            }
        }

        Collections.shuffle(buttons);
        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
            Button button = buttons.get(i);
            jPanel1.add(button);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            button.addActionListener(new ClickAction());
        }
        this.pack();
        setScore();
    }

    private void getHighestScore() throws IOException {
        int highest = 0;
        File file = new File("enyuksekskor.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        try (Scanner scan = new Scanner(new File("enyuksekskor.txt"))) {
            while (scan.hasNextLine()) {
                String currentLine = scan.nextLine();
                String words[] = currentLine.split(" ");
                int number = 0;
                for (String str : words) {
                    try {
                        boolean matches = str.matches("-?\\d+"); // Text dosyasindaki her bir satirda Integer olan kelimeleri secer.
                        if (matches) {
                            number = Integer.parseInt(str);
                        }
                        if (number > highest) {
                            highest = number;
                        }
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        lbl_highestScore.setText("En Yuksek Skor: " + highest);
    }

    private boolean compareImages(BufferedImage image1, BufferedImage image2) {
        if (image1.getWidth() == image2.getWidth() && image1.getHeight() == image2.getHeight()) {
            for (int j = 0; j < image1.getWidth(); j++) {
                for (int k = 0; k < image2.getHeight(); k++) {
                    if (image1.getRGB(j, k) != image2.getRGB(j, k)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private void setScore() throws IOException {
        isSolved = true;
        for (int i = 0; i < images.length; i++) {
            if (compareImages(images[i], buttons.get(i).getImage())) {
                score += 6;
            } else {
                isSolved = false;
            }
        }
        lbl_score.setText("Skor: " + score);
        if (isSolved) {
            if (numberOfMoves == 0) {
                score = 100;
                lbl_score.setText("Skor: " + score);
            }
            JOptionPane.showMessageDialog(jPanel1, "<html><b>Tebrikler! Puzzle tamamlanmistir.</b></html>", "Information", JOptionPane.INFORMATION_MESSAGE);
            printScore();
            getHighestScore();
            setPanelEnabled(jPanel1, false);
        }
        if (score == 0) {
            JOptionPane.showMessageDialog(jPanel1, "<html><b>Hicbir puzzle parcasi dogru yerde olmadigi icin karistirmaya devam ediniz.</b></html>", "Information", JOptionPane.INFORMATION_MESSAGE);
            setPanelEnabled(jPanel1, false);
        } else { // Bir veya daha fazla puzzle parcasi dogru yerdeyse Karistir butonu kapatilir.
            btn_shuffle.setEnabled(false);
        }
    }

    private void printScore() throws IOException {
        FileWriter fileWriter = new FileWriter("enyuksekskor.txt", true);
        try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(lbl_score.getText());
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
    }

    private void setPanelEnabled(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                setPanelEnabled((JPanel) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    private class ClickAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                changeButtons(e);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void changeButtons(ActionEvent e) throws IOException {
            JButton button = (JButton) e.getSource();
            if (clickedButton != null) {
                if (clickedButton != button) {
                    numberOfMoves++;
                }
                Collections.swap(buttons, buttons.indexOf(clickedButton), buttons.indexOf(button)); // Son tiklanan butonlarin yer degistirmesi islemi
                clickedButton = null;
                updateButtons();
                isSolved = true;
                score = 0;
                for (int i = 0; i < images.length; i++) {
                    if (compareImages(images[i], buttons.get(i).getImage())) {
                        score += 6;
                    } else {
                        isSolved = false;
                    }
                }
                score -= numberOfMoves;
                lbl_score.setText("Skor: " + score);
                if (isSolved) {
                    JOptionPane.showMessageDialog(jPanel1, "<html><b>Tebrikler! Puzzle tamamlanmistir.</b></html>", "Information", JOptionPane.INFORMATION_MESSAGE);
                    printScore();
                    getHighestScore();
                    setPanelEnabled(jPanel1, false);
                }
            } else { // Herhangi bir butona ilk tiklama olup olmadiginin kontrolu
                clickedButton = (Button) e.getSource();
            }
        }

        private void updateButtons() {
            jPanel1.removeAll();
            buttons.forEach((button) -> {
                jPanel1.add(button);
            });
            jPanel1.validate();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btn_selectImage = new javax.swing.JButton();
        btn_shuffle = new javax.swing.JButton();
        lbl_score = new javax.swing.JLabel();
        lbl_highestScore = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Square Puzzle Game");

        jPanel1.setBackground(new java.awt.Color(176, 176, 176));
        jPanel1.setPreferredSize(new java.awt.Dimension(520, 520));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 520, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 520, Short.MAX_VALUE)
        );

        btn_selectImage.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        btn_selectImage.setText("Resim Sec");
        btn_selectImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_selectImageActionPerformed(evt);
            }
        });

        btn_shuffle.setBackground(java.awt.Color.red);
        btn_shuffle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        btn_shuffle.setForeground(java.awt.Color.white);
        btn_shuffle.setText("Karistir");
        btn_shuffle.setEnabled(false);
        btn_shuffle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_shuffleActionPerformed(evt);
            }
        });

        lbl_score.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        lbl_score.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_score.setText("Skor : ");

        lbl_highestScore.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        lbl_highestScore.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_highestScore.setText("En Yuksek Skor : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(250, 250, 250)
                        .addComponent(btn_selectImage, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_highestScore, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_score, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(260, Short.MAX_VALUE)
                .addComponent(btn_shuffle, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(259, 259, 259))
            .addGroup(layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(60, 60, 60))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btn_selectImage, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30)
                .addComponent(btn_shuffle, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(lbl_score, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lbl_highestScore, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Main().setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void btn_shuffleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_shuffleActionPerformed
        try {
            buildPuzzle();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_shuffleActionPerformed

    private void btn_selectImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_selectImageActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "gif", "png", "bmp");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Open schedule file");
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            jPanel1.removeAll();
            jPanel1.revalidate();
            jPanel1.repaint();
            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();
            JOptionPane.showMessageDialog(jPanel1, "<html><b>Resim secme islemi tamamlanmistir, karistir butonuna basabilirsiniz.</b></html>", "Information", JOptionPane.INFORMATION_MESSAGE);
            btn_shuffle.setEnabled(true);
            score = 0;
            numberOfMoves = 0;
            isSolved = false;
            lbl_score.setText("Skor: " + score);
        }
    }//GEN-LAST:event_btn_selectImageActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_selectImage;
    private javax.swing.JButton btn_shuffle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbl_highestScore;
    private javax.swing.JLabel lbl_score;
    // End of variables declaration//GEN-END:variables
}
