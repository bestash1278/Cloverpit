import javax.swing.*;
import java.awt.*;

public class Main {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("클로버핏 (Cloverpit)");
            
            SlotMachinePanel slotPanel = new SlotMachinePanel();
            mainFrame.add(slotPanel, BorderLayout.CENTER);
            
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.pack(); 
            mainFrame.setLocationRelativeTo(null); 
            mainFrame.setResizable(false); 
            mainFrame.setVisible(true);
        });
    }
}