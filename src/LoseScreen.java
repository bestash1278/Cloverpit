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

    // 기준 해상도
    private static final int SCREEN_WIDTH = 1600;
    private static final int SCREEN_HEIGHT = 900;

    public LoseScreen(JFrame frame, CardLayout cardLayout, JPanel cardPanel) {
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setLayout(new BorderLayout());

        // 배경 이미지 로드
        ImageIcon bgIcon = new ImageIcon("res/loose_background.png");
        background = bgIcon.getImage();

        // 내용 표시용 패널 (투명, 절대좌표)
        JPanel contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);

        // ===== 제목 라벨 =====
        JLabel titleLabel = new JLabel("탈락했습니다...", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(createFont(48, Font.BOLD));
        titleLabel.setBounds(
                0,
                120,
                SCREEN_WIDTH,
                80
        );
        contentPanel.add(titleLabel);

        // ===== 버튼 공통 설정 =====
        int buttonWidth = 220;
        int buttonHeight = 60;
        int buttonY = 680;
        int gap = 30;

        int totalWidth = buttonWidth * 3 + gap * 2;
        int startX = (SCREEN_WIDTH - totalWidth) / 2;

        // 다시 시작 버튼
        JButton restartButton = createMenuButton("다시 시작");
        restartButton.setBounds(startX, buttonY, buttonWidth, buttonHeight);
        restartButton.addActionListener(e -> Main.restartGame());
        contentPanel.add(restartButton);

        // 메뉴로 돌아가기 버튼
        JButton menuButton = createMenuButton("메뉴로 돌아가기");
        menuButton.setBounds(startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight);
        menuButton.addActionListener(e -> Main.goToMenu());
        contentPanel.add(menuButton);

        // 게임 종료 버튼
        JButton exitButton = createMenuButton("게임 종료");
        exitButton.setBounds(startX + (buttonWidth + gap) * 2, buttonY, buttonWidth, buttonHeight);
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
