import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("클로버핏 (Cloverpit)");
            
            SlotMachinePanel slotPanel = new SlotMachinePanel();
            mainFrame.add(slotPanel, BorderLayout.CENTER);
            
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            mainFrame.addWindowListener(new WindowAdapter() {
             @Override
             public void windowClosing(WindowEvent e) {
                 slotPanel.saveOnExit();   
                 mainFrame.dispose();
                 System.exit(0);
             }
         });
            mainFrame.pack(); 
            mainFrame.setLocationRelativeTo(null); 
            mainFrame.setResizable(false); 
            mainFrame.setVisible(true);
        });
    }
}