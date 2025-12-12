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
    private static String currentScreen = "START";
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            mainFrame = new JFrame("클로버핏 (Cloverpit)");
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            
            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // 시작 화면에서는 바로 종료
                    if ("START".equals(currentScreen)) {
                        System.exit(0);
                        return;
                    }
                    
                    // 게임 데이터 저장
                    if (currentGamePanel != null) {
                        currentGamePanel.saveOnExit();
                    }
                    
                    // EndScreen으로 전환
                    User user = (currentGamePanel != null) ? currentGamePanel.getUser() : null;
                    EndScreen endScreen = new EndScreen(mainFrame, cardLayout, cardPanel, user);
                    cardPanel.add(endScreen, "END");
                    cardLayout.show(cardPanel, "END");
                    currentScreen = "END";
                    mainFrame.revalidate();
                    mainFrame.repaint();
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
            currentScreen = "START";
        });
    }
    
    public static void setCurrentGamePanel(SlotMachinePanel panel) {
        currentGamePanel = panel;
        if (cardLayout != null && cardPanel != null) {
            cardLayout.show(cardPanel, "GAME");
            currentScreen = "GAME";
        }
    }
    
    public static void setCurrentScreen(String screen) {
        currentScreen = screen;
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
            currentScreen = "END";
        } else {
            System.exit(0);
        }
    }
    //탈락 전용 메서드
    public static void exitGameWithLose() {
        if (cardPanel != null && cardLayout != null) {
            LoseScreen loseScreen = new LoseScreen(mainFrame, cardLayout, cardPanel);
            cardPanel.add(loseScreen, "LOSE");
            cardLayout.show(cardPanel, "LOSE");
        } else {
            System.exit(0);
        }
    }
    /**
     * 패배 화면에서 '다시 시작' 눌렀을 때 호출.
     * - 세이브 파일은 무시하고 완전히 새 User로 새 게임을 시작한다.
     */
    public static void restartGame() {
        // 기존 게임 패널 제거
        if (currentGamePanel != null && cardPanel != null) {
            cardPanel.remove(currentGamePanel);
            currentGamePanel = null;
        }

        // 새 게임인데 예전 진행 상황을 남기고 싶지 않다면 사용
        SaveManagerCsv saveManager = new SaveManagerCsv();
        saveManager.reset();  

        // 완전 새 유저로 새 게임 시작
        User newUser = new User(); 
        SlotMachinePanel newPanel = new SlotMachinePanel(newUser);

        setCurrentGamePanel(newPanel);      
        cardPanel.add(newPanel, "GAME");
        cardLayout.show(cardPanel, "GAME");
    }

    /**
     * 패배 화면에서 '메뉴로 돌아가기' 눌렀을 때 호출.
     * StartScreen 으로만 돌아가고, 게임은 시작하지 않는다.
     */
    public static void goToMenu() {
        if (currentGamePanel != null && cardPanel != null) {
            cardPanel.remove(currentGamePanel);
            currentGamePanel = null;
        }
        // StartScreen 이 이미 "START" 이름으로 cardPanel 에 추가되어 있다고 가정
        cardLayout.show(cardPanel, "START");
    }

}
