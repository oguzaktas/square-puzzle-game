package main;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
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
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Yazilim Laboratuvari II Proje 1
 *
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
    private List<Point> solution;
    private final int NUMBER_OF_BUTTONS = 16;
    private final int DESIRED_WIDTH = 480; // Resim dosyasinin default genislik (width) boyutu 480 olarak belirlendi.
    private String filePath;
    private int score = 0; // Oyuna baslarken default olarak 0 puan verilir.

    /**
     * Creates new form Main
     * @throws java.io.FileNotFoundException
     */
    public Main() throws FileNotFoundException {
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
        File outputfile = new File("saved.png");
        ImageIO.write(resizedImage, "png", outputfile);
        return resizedImage;
    }

    private void buildPuzzle() {
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
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(), new CropImageFilter(j * (width / 4), i * (height / 4), (width / 4), (height / 4))));
                Button button = new Button(image);
                button.putClientProperty("position", new Point(i, j));
                buttons.add(button);
                /*
                if (i == 3 && j == 3) {
                    lastButton = new Button();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("position", new Point(i, j));
                } else {
                    buttons.add(button);
                }
                 */
            }
        }

        Collections.shuffle(buttons);
        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
            Button button = buttons.get(i);
            jPanel1.add(button);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE, (int) 1.8));
            button.addActionListener(new ClickAction());
        }
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void getHighestScore() throws FileNotFoundException {
        int highest = 0;
        try (Scanner file = new Scanner(new File("enyuksekskor.txt"))) {
            while (file.hasNextLine()) {
                String currentLine = file.nextLine();
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
    
    private void setScore() {
        
    }
    
    private void compareButtons() {
        
    }

    private class ClickAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            changeButtons(e);
            checkSolution();
        }

        private void changeButtons(ActionEvent e) {
            /*
            int lidx = 0;
            for (Button button : buttons) {
                if (button.isLastButton()) {
                    lidx = buttons.indexOf(button);
                }
            }
            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);
            if ((bidx - 1 == lidx) || (bidx + 1 == lidx) || (bidx - 4 == lidx) || (bidx + 4 == lidx)) {
                Collections.swap(buttons, lidx, bidx);
                updateButtons();
            }
             */
            JButton button = (JButton) e.getSource();
            if (clickedButton != null) {
                Collections.swap(buttons, buttons.indexOf(clickedButton), buttons.indexOf(button)); // Son tiklanan butonlarin yer degistirmesi islemi
                clickedButton = null;
                updateButtons();
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
        
        private void updateScore() {
            
        }

    }

    private void checkSolution() {
        /*
        List<Point> current = new ArrayList<Point>();
        for (JComponent button : buttons) {
            current.add((Point) button.getClientProperty("position"));
        }

        if (compareList(solution, current)) {
            JOptionPane.showMessageDialog(jPanel1, "Main tamamlandi.", "Tebrikler!", JOptionPane.INFORMATION_MESSAGE);
        }
        * */
    }

    public static boolean compareList(List list1, List list2) {
        return list1.toString().contentEquals(list2.toString());
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
        jPanel1.setPreferredSize(new java.awt.Dimension(480, 480));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
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
                        .addGap(235, 235, 235)
                        .addComponent(btn_selectImage, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(141, 141, 141)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_highestScore, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_score, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(65, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btn_shuffle, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(244, 244, 244))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(65, 65, 65))))
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
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Main().setVisible(true);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void btn_shuffleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_shuffleActionPerformed
        buildPuzzle();
        if (score == 0) { // Butonlarin dogru yerde olup olmadiginin kontrolu
            lbl_score.setText("Skor: " + score);
            JOptionPane.showMessageDialog(jPanel1, "<html><b>Hicbir puzzle parcasi dogru yerde olmadigi icin karistirmaya devam ediniz.</b></html>", "Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            btn_shuffle.setEnabled(false);
        }
    }//GEN-LAST:event_btn_shuffleActionPerformed

    private void btn_selectImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_selectImageActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "gif", "png");
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
