import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 탈락 화면 (Game Over Screen)
 * 게임 탈락 시 표시되는 통계 화면
 * 자체 실행 가능 (main 메서드 포함)
 */
public class GameOverScreen extends JPanel {
    private static final int WINDOW_WIDTH = 1600;
    private static final int WINDOW_HEIGHT = 900;
    
    // 더미 데이터 (실제로는 User, ItemShop, Call 객체에서 가져와야 함)
    private User user;
    private List<ItemInfo> ownedArtifacts; // 소지 유물 목록
    private BufferedImage backgroundImage;
    
    public GameOverScreen(User user, List<ItemInfo> ownedArtifacts) {
        this.user = user;
        this.ownedArtifacts = ownedArtifacts != null ? ownedArtifacts : new ArrayList<>();
        
        // 배경 이미지 로드
        try {
        	backgroundImage = ImageIO.read(getClass().getResource("/gameover_background.png"));
        	} catch (IOException e) {
            backgroundImage = null;
        }
        
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        
        initializeUI();
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
    
    private void initializeUI() {
        // 상단: 타이틀
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // 중앙: 3개 영역 (왼쪽: 유물, 가운데: 통계, 오른쪽: 전화 내역)
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // 하단: 종료 버튼
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("게임 오버");
        Font titleFont = new Font("Malgun Gothic", Font.BOLD, 64);
        if (!titleFont.getFamily().equals("Malgun Gothic")) {
            titleFont = new Font("Dotum", Font.BOLD, 64);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(231, 76, 60)); // 빨간색
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(titleLabel);
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // 왼쪽: 소지 유물
        JPanel leftPanel = createArtifactsPanel();
        panel.add(leftPanel);
        
        // 가운데: 통계 정보
        JPanel centerPanel = createStatisticsPanel();
        panel.add(centerPanel);
        
        // 오른쪽: 전화 내역
        JPanel rightPanel = createCallHistoryPanel();
        panel.add(rightPanel);
        
        return panel;
    }
    
    private JPanel createArtifactsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 40, 60));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            "소지 유물",
            0, 0,
            new Font("Malgun Gothic", Font.BOLD, 24),
            Color.WHITE
        ));
        
        JPanel artifactsContainer = new JPanel();
        artifactsContainer.setLayout(new BoxLayout(artifactsContainer, BoxLayout.Y_AXIS));
        artifactsContainer.setBackground(new Color(40, 40, 60));
        artifactsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        if (ownedArtifacts.isEmpty()) {
            JLabel emptyLabel = new JLabel("소지한 유물이 없습니다.");
            emptyLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            artifactsContainer.add(emptyLabel);
        } else {
            for (ItemInfo artifact : ownedArtifacts) {
                JPanel artifactItem = createArtifactItem(artifact);
                artifactsContainer.add(artifactItem);
                artifactsContainer.add(Box.createVerticalStrut(15));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(artifactsContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(40, 40, 60));
        scrollPane.getViewport().setBackground(new Color(40, 40, 60));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createArtifactItem(ItemInfo artifact) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(50, 50, 70));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // 유물 이미지
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(80, 80));
        imageLabel.setMinimumSize(new Dimension(80, 80));
        imageLabel.setMaximumSize(new Dimension(80, 80));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.DARK_GRAY);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        
        // 이미지 로드 시도
        try {
        	String path = artifact.getImagePath(); 
        	// 만약 getImagePath()가 "res/image.png"를 반환한다면, 
        	// JAR 내부용으로 바꾸기 위해 "res/"를 제거하거나 앞에 "/"를 붙여야 합니다.
        	String resourcePath = "/" + path.replace("res/", ""); 
        	java.net.URL imgUrl = getClass().getResource(resourcePath);
        	if (imgUrl != null) {
        	    BufferedImage img = ImageIO.read(imgUrl);
                }
        } catch (IOException e) {
            // 이미지 로드 실패 시 기본 배경색 유지
        }
        
        // 유물 정보
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(50, 50, 70));
        infoPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(artifact.getName());
        nameLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        
        JTextArea descArea = new JTextArea(artifact.getDescription());
        descArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
        descArea.setForeground(Color.LIGHT_GRAY);
        descArea.setBackground(new Color(50, 50, 70));
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descArea);
        
        panel.add(imageLabel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 40, 60));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            "게임 통계",
            0, 0,
            new Font("Malgun Gothic", Font.BOLD, 24),
            Color.WHITE
        ));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "게임 통계",
                0, 0,
                new Font("Malgun Gothic", Font.BOLD, 24),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        Font labelFont = new Font("Malgun Gothic", Font.BOLD, 20);
        Font valueFont = new Font("Malgun Gothic", Font.PLAIN, 18);
        if (!labelFont.getFamily().equals("Malgun Gothic")) {
            labelFont = new Font("Dotum", Font.BOLD, 20);
            valueFont = new Font("Dotum", Font.PLAIN, 18);
        }
        
        // 기한
        addStatItem(panel, "기한", user != null ? String.valueOf(user.getDeadline()) : "0", labelFont, valueFont);
        panel.add(Box.createVerticalStrut(20));
        
        // 총 낸 금액
        addStatItem(panel, "총 낸 금액", user != null ? String.valueOf(user.getTotal_money()) + "원" : "0원", labelFont, valueFont);
        panel.add(Box.createVerticalStrut(20));
        
        // 총 티켓 수
        addStatItem(panel, "현재재 티켓 수", user != null ? String.valueOf(user.getTicket()) + "개" : "0개", labelFont, valueFont);
        panel.add(Box.createVerticalStrut(20));
        
        // 유물 구매 횟수 (itemReroll_count를 사용하거나 별도 추적 필요)
        int itemPurchaseCount = user != null ? user.getItemReroll_count() : 0;
        addStatItem(panel, "유물 구매 횟수", String.valueOf(itemPurchaseCount) + "회", labelFont, valueFont);
        panel.add(Box.createVerticalStrut(20));
        
        // 실행한 돌리기 횟수 (계산: (round - 1) * SPINS_PER_ROUND + (SPINS_PER_ROUND - round_spin_left))
        int totalSpins = 0;
        if (user != null) {
            int SPINS_PER_ROUND = 7; // 기본값
            int rounds = user.getRound() - 1;
            int spinsInCurrentRound = SPINS_PER_ROUND - user.getRound_spin_left();
            totalSpins = rounds * SPINS_PER_ROUND + spinsInCurrentRound;
        }
        addStatItem(panel, "실행한 돌리기 횟수", String.valueOf(totalSpins) + "회", labelFont, valueFont);
        
        return panel;
    }
    
    private void addStatItem(JPanel parent, String label, String value, Font labelFont, Font valueFont) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(new Color(40, 40, 60));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setFont(labelFont);
        labelComponent.setForeground(new Color(200, 200, 200));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(valueFont);
        valueComponent.setForeground(Color.WHITE);
        valueComponent.setHorizontalAlignment(SwingConstants.RIGHT);
        
        itemPanel.add(labelComponent, BorderLayout.WEST);
        itemPanel.add(valueComponent, BorderLayout.EAST);
        
        parent.add(itemPanel);
    }
    
    private JPanel createCallHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 40, 60));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            "전화 사용 내역",
            0, 0,
            new Font("Malgun Gothic", Font.BOLD, 24),
            Color.WHITE
        ));
        
        JPanel historyContainer = new JPanel();
        historyContainer.setLayout(new BoxLayout(historyContainer, BoxLayout.Y_AXIS));
        historyContainer.setBackground(new Color(40, 40, 60));
        historyContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // user_call에서 전화 내역 리스트 가져오기
        List<String> callHistory = getCallHistoryFromUser();
        
        if (callHistory.isEmpty()) {
            JLabel emptyLabel = new JLabel("사용한 전화가 없습니다.");
            emptyLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            historyContainer.add(emptyLabel);
        } else {
            for (String callItem : callHistory) {
                JLabel callLabel = new JLabel("• " + callItem);
                callLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
                callLabel.setForeground(Color.WHITE);
                callLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                callLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                historyContainer.add(callLabel);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(historyContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(40, 40, 60));
        scrollPane.getViewport().setBackground(new Color(40, 40, 60));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 0, 50, 0));
        
        // 재시작 버튼
        JButton restartButton = createMenuButton("재시작하겠습니까?", new Color(50, 150, 250));
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(restartButton);
        panel.add(Box.createVerticalStrut(20));
        
        // 종료 버튼
        JButton exitButton = createMenuButton("종료", new Color(231, 76, 60));
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(exitButton);
        return panel;
    }
    
    /**
     * 게임 재시작
     * 룰렛 화면(SlotMachinePanel)으로 전환
     */
    private void restartGame() {
        // 현재 프레임 찾기
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame == null) {
            // main 메서드에서 직접 실행된 경우 새 프레임 생성
            parentFrame = new JFrame("클로버핏 (Cloverpit)");
            parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            parentFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            parentFrame.setLocationRelativeTo(null);
            parentFrame.setResizable(false);
        }
        
        // CardLayout 설정
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        
        // 게임 패널 생성
        SlotMachinePanel gamePanel = new SlotMachinePanel();
        Main.setCurrentGamePanel(gamePanel);
        
        // 카드 패널에 추가
        cardPanel.add(gamePanel, "GAME");
        
        // 프레임 내용 교체
        parentFrame.getContentPane().removeAll();
        parentFrame.add(cardPanel, BorderLayout.CENTER);
        parentFrame.setTitle("클로버핏 (Cloverpit)");
        
        // 게임 화면 표시
        cardLayout.show(cardPanel, "GAME");
        parentFrame.revalidate();
        parentFrame.repaint();
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
    
    /**
     * user_call 필드에서 전화 내역 리스트 가져오기
     */
    private List<String> getCallHistoryFromUser() {
        if (user == null) {
            return new ArrayList<>();
        }
        List<String> callHistory = user.getUser_call();
        return callHistory != null ? callHistory : new ArrayList<>();
    }
}

