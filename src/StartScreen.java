import javax.swing.*;
import java.awt.*;
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
    
    public StartScreen(JFrame frame, CardLayout cl, JPanel cp) {
        this.parentFrame = frame;
        this.cardLayout = cl;
        this.cardPanel = cp;
        this.soundManager = new SoundManager();
        this.saveManager = new SaveManagerCsv();
        
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 50));
        
        soundManager.playBackgroundMusic();
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 30, 50));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("클로버핏 (Cloverpit)");
        Font titleFont = new Font("Malgun Gothic", Font.BOLD, 64);
        if (!titleFont.getFamily().equals("Malgun Gothic")) {
            titleFont = new Font("Dotum", Font.BOLD, 64);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(30, 30, 50));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JButton newGameButton = createMenuButton("새 게임 시작", new Color(50, 150, 250));
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewGame();
            }
        });
        
        JButton continueGameButton = createMenuButton("저장된 게임 시작", new Color(150, 255, 100));
        continueGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSavedGame();
            }
        });
        
        buttonPanel.add(Box.createVerticalStrut(50));
        buttonPanel.add(newGameButton);
        buttonPanel.add(Box.createVerticalStrut(50));
        buttonPanel.add(continueGameButton);
        
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 32));
        if (!button.getFont().getFamily().equals("Malgun Gothic")) {
            button.setFont(new Font("Dotum", Font.BOLD, 32));
        }
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(400, 100));
        button.setMaximumSize(new Dimension(400, 100));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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
