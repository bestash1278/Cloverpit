import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 종료 화면
 * 게임 종료 시 표시되는 화면
 */
public class EndScreen extends JPanel {
    private JFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private User user;
    
    public EndScreen(JFrame frame, CardLayout cl, JPanel cp, User user) {
        this.parentFrame = frame;
        this.cardLayout = cl;
        this.cardPanel = cp;
        this.user = user;
        
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 50));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 30, 50));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("게임 종료");
        Font titleFont = new Font("Malgun Gothic", Font.BOLD, 64);
        if (!titleFont.getFamily().equals("Malgun Gothic")) {
            titleFont = new Font("Dotum", Font.BOLD, 64);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(30, 30, 50));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        Font infoFont = new Font("Malgun Gothic", Font.PLAIN, 24);
        if (!infoFont.getFamily().equals("Malgun Gothic")) {
            infoFont = new Font("Dotum", Font.PLAIN, 24);
        }
        
        if (this.user != null && this.user.getUserId() != null && !this.user.getUserId().isEmpty()) {
            JLabel saveLabel = new JLabel("게임 데이터가 저장되었습니다.");
            saveLabel.setFont(infoFont);
            saveLabel.setForeground(new Color(150, 255, 100));
            saveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(saveLabel);
            infoPanel.add(Box.createVerticalStrut(20));
        }
        
        JLabel thanksLabel = new JLabel("게임을 플레이해주셔서 감사합니다!");
        thanksLabel.setFont(infoFont);
        thanksLabel.setForeground(Color.WHITE);
        thanksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(thanksLabel);
        
        add(infoPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 50));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 50, 0));
        
        JButton exitButton = createMenuButton("종료", new Color(231, 76, 60));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        buttonPanel.add(exitButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
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
        button.setPreferredSize(new Dimension(300, 80));
        
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
}

