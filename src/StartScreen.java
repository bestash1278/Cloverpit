import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 시작 화면
 * 새 게임 시작 또는 기존 게임 계속 기능 제공
 */
public class StartScreen extends JPanel {
    private JFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private SoundManager soundManager;
    
    public StartScreen(JFrame frame, CardLayout cl, JPanel cp) {
        this.parentFrame = frame;
        this.cardLayout = cl;
        this.cardPanel = cp;
        this.soundManager = new SoundManager();
        
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
        
        JButton continueGameButton = createMenuButton("기존 게임 계속", new Color(150, 255, 100));
        continueGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showIdInputDialog();
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
        showIdInputDialogForNewGame();
    }
    
    private void showIdInputDialogForNewGame() {
        JDialog idDialog = new JDialog(parentFrame, "아이디 입력", true);
        idDialog.setSize(400, 200);
        idDialog.setLocationRelativeTo(parentFrame);
        idDialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("새 게임 아이디를 입력하세요:");
        Font font = new Font("Malgun Gothic", Font.PLAIN, 16);
        if (!font.getFamily().equals("Malgun Gothic")) {
            font = new Font("Dotum", Font.PLAIN, 16);
        }
        label.setFont(font);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        
        JTextField idField = new JTextField(20);
        idField.setFont(font);
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(idField);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = idField.getText().trim();
                if (userId.isEmpty()) {
                    JOptionPane.showMessageDialog(idDialog, "아이디를 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User existingUser = User.loadFromCSV(userId);
                if (existingUser != null) {
                    int result = JOptionPane.showConfirmDialog(idDialog, 
                        "이미 존재하는 아이디입니다. 덮어쓰시겠습니까?", 
                        "확인", 
                        JOptionPane.YES_NO_OPTION);
                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                User newUser = new User(userid);
                idDialog.dispose();
                startGame(newUser);
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idDialog.dispose();
            }
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);
        
        idDialog.add(panel, BorderLayout.CENTER);
        idDialog.setVisible(true);
        
        // 다이얼로그가 열릴 때 텍스트 필드에 포커스
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                idField.requestFocus();
            }
        });
    }
    
    private void showIdInputDialog() {
        JDialog idDialog = new JDialog(parentFrame, "아이디 입력", true);
        idDialog.setSize(400, 200);
        idDialog.setLocationRelativeTo(parentFrame);
        idDialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("아이디를 입력하세요:");
        Font font = new Font("Malgun Gothic", Font.PLAIN, 16);
        if (!font.getFamily().equals("Malgun Gothic")) {
            font = new Font("Dotum", Font.PLAIN, 16);
        }
        label.setFont(font);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        
        JTextField idField = new JTextField(20);
        idField.setFont(font);
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(idField);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = idField.getText().trim();
                if (userId.isEmpty()) {
                    JOptionPane.showMessageDialog(idDialog, "아이디를 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User loadedUser = User.loadFromCSV(userId);
                if (loadedUser == null) {
                    JOptionPane.showMessageDialog(idDialog, "해당 아이디의 저장된 게임을 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                idDialog.dispose();
                startGame(loadedUser);
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idDialog.dispose();
            }
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);
        
        idDialog.add(panel, BorderLayout.CENTER);
        idDialog.setVisible(true);
        
        // 다이얼로그가 열릴 때 텍스트 필드에 포커스
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                idField.requestFocus();
            }
        });
    }
    
    /**
     * 게임 시작
     * 백그라운드 음악 중지, 사용자 설정, 게임 패널로 전환
     */
    private void startGame(User user) {
        soundManager.stopBackgroundMusic();
        Main.setCurrentUser(user);
        SlotMachinePanel gamePanel = new SlotMachinePanel(user);
        cardPanel.add(gamePanel, "GAME");
        cardLayout.show(cardPanel, "GAME");
    }
}

