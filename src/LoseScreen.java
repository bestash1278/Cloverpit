import javax.swing.*;
import java.awt.*;

/**
 * 패배(탈락) 화면
 * - loose_background.png 를 배경으로 사용
 * - 다시 시작 / 메뉴로 돌아가기 / 게임 종료 버튼 제공
 */
public class LoseScreen extends JPanel {

    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private Image background;

    public LoseScreen(JFrame frame, CardLayout cardLayout, JPanel cardPanel) {
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setLayout(new BorderLayout());

        // 배경 이미지 로드
        ImageIcon bgIcon = new ImageIcon("res/loose_background.png");
        background = bgIcon.getImage();

        // 내용 표시용 패널 (투명)
        JPanel contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);

        // ===== 제목 라벨 =====
        JLabel titleLabel = new JLabel("탈락했습니다...", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(createFont(40, Font.BOLD));
        titleLabel.setBounds(0, 100, 1600, 60);
        contentPanel.add(titleLabel);

        // ===== 버튼들 =====
        // 다시 시작 버튼
        JButton restartButton = createMenuButton("다시 시작");
        restartButton.setBounds(460, 640, 200, 60);
        restartButton.addActionListener(e -> {
            // 세이브 초기화 + 새 게임 시작
            Main.restartGame();
        });
        contentPanel.add(restartButton);

        // 메뉴로 돌아가기 버튼
        JButton menuButton = createMenuButton("메뉴로 돌아가기");
        menuButton.setBounds(700, 640, 200, 60);
        menuButton.addActionListener(e -> {
            Main.goToMenu();
        });
        contentPanel.add(menuButton);

        // 게임 종료 버튼
        JButton exitButton = createMenuButton("게임 종료");
        exitButton.setBounds(940, 640, 200, 60);
        exitButton.addActionListener(e -> System.exit(0));
        contentPanel.add(exitButton);
    }

    private Font createFont(int size, int style) {
        Font font = new Font("Malgun Gothic", style, size);
        if (!font.getFamily().equals("Malgun Gothic")) {
            font = new Font("Dotum", style, size);
        }
        return font;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(createFont(24, Font.BOLD));
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
