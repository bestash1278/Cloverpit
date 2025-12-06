import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 메인 애플리케이션 클래스
 * 화면 전환과 사용자 데이터 관리를 담당
 */
public class Main {
    private static User currentUser = null;
    private static JFrame mainFrame = null;
    private static CardLayout cardLayout = null;
    private static JPanel cardPanel = null;
    
    /**
     * 프로그램 진입점
     * CardLayout을 사용하여 시작 화면, 게임 화면, 종료 화면을 관리
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            mainFrame = new JFrame("클로버핏 (Cloverpit)");
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            
            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    exitGame();
                }
            });
            
            cardLayout = new CardLayout();
            cardPanel = new JPanel(cardLayout);
            
            StartScreen startScreen = new StartScreen(mainFrame, cardLayout, cardPanel);
            cardPanel.add(startScreen, "START");
            
            mainFrame.add(cardPanel, BorderLayout.CENTER);
            mainFrame.setSize(1600, 900);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setResizable(false);
            mainFrame.setVisible(true);
            
            cardLayout.show(cardPanel, "START");
        });
    }
    
    /**
     * 현재 사용자 설정
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * 현재 사용자 반환
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 게임 종료 처리
     * 사용자 데이터를 CSV 파일에 저장하고 종료 화면으로 전환
     */
    public static void exitGame() {
        if (currentUser != null && currentUser.getUserId() != null && !currentUser.getUserId().isEmpty()) {
            currentUser.saveToCSV();
        }
        
        if (cardPanel != null && cardLayout != null) {
            EndScreen endScreen = new EndScreen(mainFrame, cardLayout, cardPanel, currentUser);
            cardPanel.add(endScreen, "END");
            cardLayout.show(cardPanel, "END");
        } else {
            System.exit(0);
        }
    }
}
