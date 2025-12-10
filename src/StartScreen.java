import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 시작 화면
 * 새 게임 시작 또는 저장된 게임 시작 기능 제공
 */
public class StartScreen extends JPanel {
    private JFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private SoundManager soundManager;
    private SaveManagerCsv saveManager;
    private BufferedImage backgroundImage;
    
    public StartScreen(JFrame frame, CardLayout cl, JPanel cp) {
        this.parentFrame = frame;
        this.cardLayout = cl;
        this.cardPanel = cp;
        this.soundManager = new SoundManager();
        this.saveManager = new SaveManagerCsv();
        
        // 배경 이미지 로드
        try {
            backgroundImage = ImageIO.read(new File("res/start_background.png"));
        } catch (IOException e) {
            backgroundImage = null;
        }
        
        setLayout(null); // 절대 위치 레이아웃 사용
        setOpaque(false);
        
        soundManager.playBackgroundMusic();
        
        // 버튼 위치 설정 (배경 이미지의 네모 상자 영역에 맞춤)
        // 화면 중앙 하단 영역: x=500, y=550부터 시작 (1600x900 기준)
        int buttonX = 693;
        int buttonY = 500;
        int buttonWidth = 200;
        int buttonHeight = 60;
        int buttonSpacing = 10;
        
        JButton newGameButton = createMenuButton("새 게임 시작", new Color(50, 150, 250));
        newGameButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewGame();
            }
        });
        add(newGameButton);
        
        JButton continueGameButton = createMenuButton("저장된 게임 시작", new Color(150, 255, 100));
        continueGameButton.setBounds(buttonX, buttonY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight);
        continueGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSavedGame();
            }
        });
        add(continueGameButton);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // 이미지 로드 실패 시 기본 배경색
            g.setColor(new Color(30, 30, 50));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        if (!button.getFont().getFamily().equals("Malgun Gothic")) {
            button.setFont(new Font("Dotum", Font.BOLD, 20));
        }
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void startNewGame() {
        User existingUser = saveManager.load();
        if (existingUser != null) {
            int result = JOptionPane.showConfirmDialog(parentFrame, 
                "이미 저장된 게임이 있습니다. 새 게임을 시작하면 기존 게임이 덮어씌워집니다.\n계속하시겠습니까?", 
                "새 게임 시작", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        deleteSaveFile();
        startGame();
    }
    
    private void loadSavedGame() {
        if (saveManager.load() == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "저장된 게임을 찾을 수 없습니다.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        startGame();
    }
    
    private void deleteSaveFile() {
        java.io.File saveFile = new java.io.File("data/user_save.csv");
        if (saveFile.exists()) {
            saveFile.delete();
        }
    }
    
    /**
     * 게임 시작
     * 백그라운드 음악 중지, 게임 패널로 전환
     */
    private void startGame() {
        soundManager.stopBackgroundMusic();
        SlotMachinePanel gamePanel = new SlotMachinePanel();
        Main.setCurrentGamePanel(gamePanel);
        cardPanel.add(gamePanel, "GAME");
        cardLayout.show(cardPanel, "GAME");
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
