import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 메인 애플리케이션 클래스
 * 화면 전환과 사용자 데이터 관리를 담당
 */
public class Main {
    private static JFrame mainFrame = null;
    private static CardLayout cardLayout = null;
    private static JPanel cardPanel = null;
    
    /**
     * 프로그램 진입점
     * CardLayout을 사용하여 시작 화면, 게임 화면, 종료 화면을 관리
     */
    private static SlotMachinePanel currentGamePanel = null;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            mainFrame = new JFrame("클로버핏 (Cloverpit)");
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            
            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (currentGamePanel != null) {
                        currentGamePanel.saveOnExit();
                    }
                    mainFrame.dispose();
                    System.exit(0);
                }
            });
            
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            mainFrame.addWindowListener(new WindowAdapter() {
             @Override
             public void windowClosing(WindowEvent e) {
                 slotPanel.saveOnExit();   
                 mainFrame.dispose();
                 System.exit(0);
             }
         });
            cardLayout = new CardLayout();
            cardPanel = new JPanel(cardLayout);
            
            StartScreen startScreen = new StartScreen(mainFrame, cardLayout, cardPanel);
            cardPanel.add(startScreen, "START");
            mainFrame.pack(); 
            mainFrame.add(cardPanel, BorderLayout.CENTER);
            mainFrame.setSize(1600, 900);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setResizable(false);
            mainFrame.setVisible(true);
            
            cardLayout.show(cardPanel, "START");
        });
    }
    
    public static void setCurrentGamePanel(SlotMachinePanel panel) {
        currentGamePanel = panel;
    }
    
    /**
     * 게임 종료 처리
     * 현재 사용자 데이터 저장 후 종료 화면으로 전환
     */
    public static void exitGame() {
        if (cardPanel != null && cardLayout != null) {
            EndScreen endScreen = new EndScreen(mainFrame, cardLayout, cardPanel, null);
            cardPanel.add(endScreen, "END");
            cardLayout.show(cardPanel, "END");
        } else {
            System.exit(0);
        }
    }
}
