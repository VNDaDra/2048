package edu.ngtu.game2048;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author tuwap
 */
public class Run {
    public static void main (String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { 
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
            } catch(Exception ignored){
                System.out.println(ignored);}
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("2048");
            frame.add(new Game2048(), BorderLayout.CENTER);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setMinimumSize(new Dimension(425, 525));
        });
    }
}
