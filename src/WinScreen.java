import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 승리 화면
 * 기한 10을 완료했을 때 표시되는 화면
 */
public class WinScreen extends JPanel {
    private User user;
    private BufferedImage backgroundImage;
    
    public WinScreen(JFrame frame, CardLayout cl, JPanel cp, User user) {
        this.user = user;
        
        // 배경 이미지 로드 (win_background.png 사용, 없으면 기본 배경)
        try {
            java.net.URL imgUrl = getClass().getResource("/win_background.png");
            if (imgUrl != null) {
                backgroundImage = ImageIO.read(imgUrl);
            }
        } catch (IOException e) {
            backgroundImage = null;
        }
        
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(1600, 900));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("승리!");
        Font titleFont = new Font("Malgun Gothic", Font.BOLD, 64);
        if (!titleFont.getFamily().equals("Malgun Gothic")) {
            titleFont = new Font("Dotum", Font.BOLD, 64);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(255, 215, 0)); // 골드 색상
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        Font infoFont = new Font("Malgun Gothic", Font.PLAIN, 24);
        if (!infoFont.getFamily().equals("Malgun Gothic")) {
            infoFont = new Font("Dotum", Font.PLAIN, 24);
        }
        
        if (this.user != null && this.user.getUser_name() != null && !this.user.getUser_name().isEmpty()) {
            JLabel saveLabel = new JLabel("축하합니다! 기한 10을 모두 완료하셨습니다!");
            saveLabel.setFont(infoFont);
            saveLabel.setForeground(new Color(255, 255, 150));
            saveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(saveLabel);
            
            infoPanel.add(Box.createVerticalStrut(20));
            
            JLabel gameLabel = new JLabel("게임 데이터가 저장되었습니다.");
            gameLabel.setFont(infoFont);
            gameLabel.setForeground(new Color(150, 255, 100));
            gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(gameLabel);
        }
        
        add(infoPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
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

