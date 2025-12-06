import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SlotMachinePanel extends JPanel implements Runnable {
    
    private Roulette roulette;
    private JLabel[][] slots;
    private JLabel resultLabel;
    private boolean isSpinning = false;
    private Timer spinTimer;
    private int spinCount = 0;
    private static final int MAX_SPIN_COUNT = 30;
    
    private static final int START_X = 50;
    private static final int START_Y_SLOT = 80; 
    
    private LeverHeadButton leverButton; 
    private RectangularButton payButton;
    private RectangularButton symbolButton;
    private RectangularButton patternButton;
    private RectangularButton relicOwnedButton;
    private RectangularButton relicShopButton;
    private RectangularButton phoneButton;
    
    private static final Color COLOR_PAY = new Color(50, 150, 250); 
    private static final Color COLOR_SYMBOL = new Color(255, 180, 0); 
    private static final Color COLOR_PATTERN = new Color(150, 255, 100); 
    private static final Color COLOR_RELIC_OWNED = new Color(255, 100, 150); 
    private static final Color COLOR_RELIC_SHOP = new Color(100, 200, 200); 
    private static final Color COLOR_PHONE = new Color(180, 100, 255); 

    private static final int MENU_BUTTON_WIDTH = 100; 
    private static final int MENU_BUTTON_HEIGHT = 50; 
    private static final int NORTH_PANEL_HEIGHT = 70; 
    private static final int EAST_PANEL_WIDTH = 150; 
    
    private static final int LEVER_HEAD_SIZE = 40; 
    private static final int LEVER_BAR_THICKNESS = 12; 

    // 룰렛 보드 크기 계산
    private static final int SLOT_SIZE = 80;
    private static final int SLOT_SPACING = 10;
    private static final int BOARD_PADDING = 15;
    private static final int ROULETTE_COLS = 5;
    private static final int ROULETTE_ROWS = 3;
    private static final int SLOT_TOTAL_WIDTH = ROULETTE_COLS * SLOT_SIZE + (ROULETTE_COLS - 1) * SLOT_SPACING + BOARD_PADDING * 2 + START_X * 2;
    private static final int LEVER_CENTER_X = SLOT_TOTAL_WIDTH + EAST_PANEL_WIDTH / 2;
    
    private static final int BOARD_HEIGHT = ROULETTE_ROWS * SLOT_SIZE + (ROULETTE_ROWS - 1) * SLOT_SPACING + BOARD_PADDING * 2;
    private static final int BUTTON_Y_TOP_TARGET = START_Y_SLOT; 
    private static final int BAR_BASE_Y = START_Y_SLOT + BOARD_HEIGHT / 2; 
    private static final int TRAVEL_TO_MID = BAR_BASE_Y - (BUTTON_Y_TOP_TARGET + LEVER_HEAD_SIZE / 2);
    private static final int LEVER_MOVEMENT_DISTANCE = TRAVEL_TO_MID * 2; 
    
    private float leverPosition = 0.0f; 
    private Timer leverAnimator;
    private boolean isAnimating = false; 

    private static final int SOUTH_PANEL_HEIGHT = 40; 
    private static final int RESULT_LABEL_HEIGHT = 30;
    private static final int RESULT_LABEL_MARGIN = 20;
    private static final int TOTAL_WIDTH_UNADJUSTED = SLOT_TOTAL_WIDTH + EAST_PANEL_WIDTH; 
    private static final int TOTAL_WIDTH = TOTAL_WIDTH_UNADJUSTED; 
    private static final int TOTAL_HEIGHT = NORTH_PANEL_HEIGHT + START_Y_SLOT + BOARD_HEIGHT + RESULT_LABEL_MARGIN + RESULT_LABEL_HEIGHT + RESULT_LABEL_MARGIN + SOUTH_PANEL_HEIGHT; 
    
    private User user;
    
    private JLabel moneyLabel;
    private JLabel interestLabel;
    private JLabel ticketLabel;
    private JLabel deadlineLabel;
    private JLabel roundLabel;
    private JLabel deadlineMoneyLabel;
    private JLabel totalMoneyLabel;

    private Thread gameThread;
    private BufferedImage bufferImage;
    private Graphics bufferG;
    
    public SlotMachinePanel() {
        user = new User();
        roulette = new Roulette();
        setLayout(null); 
        setPreferredSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT)); 
        setBackground(Color.DARK_GRAY);
        
        initializeRouletteBoard();
        
        JPanel northContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        northContainer.setBackground(Color.DARK_GRAY.darker()); 
        northContainer.setBounds(0, 0, TOTAL_WIDTH, NORTH_PANEL_HEIGHT); 

        payButton = createMenuButton("납입", "납입 버튼 화면", COLOR_PAY);
        symbolButton = createMenuButton("무늬", "무늬 버튼 화면", COLOR_SYMBOL);
        patternButton = createMenuButton("패턴", "패턴 버튼 화면", COLOR_PATTERN);
        relicOwnedButton = createMenuButton("소지 유물", "소지 유물 버튼 화면", COLOR_RELIC_OWNED);
        relicShopButton = createMenuButton("유물 상점", "유물 상점 버튼 화면", COLOR_RELIC_SHOP);
        phoneButton = createMenuButton("전화", "전화 버튼 화면", COLOR_PHONE);
        
        northContainer.add(payButton);
        northContainer.add(symbolButton);
        northContainer.add(patternButton);
        northContainer.add(relicOwnedButton);
        northContainer.add(relicShopButton);
        northContainer.add(phoneButton);

        add(northContainer); 
        
        leverAnimator = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAnimating) {
                    float step = 0.1f; 
                    boolean targetDown = leverButton.getModel().isPressed() || isSpinning; 
                    
                    if (targetDown) {
                        leverPosition = Math.min(1.0f, leverPosition + step);
                    } else {
                        leverPosition = Math.max(0.0f, leverPosition - step);
                    }
                    
                    if ((targetDown && leverPosition == 1.0f) || (!targetDown && leverPosition == 0.0f)) {
                        if (leverPosition == 0.0f) {
                            isAnimating = false;
                            leverAnimator.stop();
                        }
                    }
                    repaint(); 
                }
            }
        });

        leverButton = new LeverHeadButton(); 
        leverButton.setBounds(0, 0, LEVER_HEAD_SIZE, LEVER_HEAD_SIZE);
        
        leverButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isSpinning && !isAnimating) {
                    isAnimating = true;
                    leverAnimator.start();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isSpinning && leverPosition >= 0.9f) {
                    startSpinProcess();
                }
            }
        });

        add(leverButton); 
        setComponentZOrder(leverButton, 0); 
        
        JPanel southContainer = new JPanel(new GridLayout(1, 7, 5, 0)); 
        southContainer.setBackground(Color.BLACK); 
        southContainer.setBounds(0, TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT, TOTAL_WIDTH, SOUTH_PANEL_HEIGHT); 
        
        Font statusFont = new Font("Malgun Gothic", Font.PLAIN, 12);
        if (statusFont.getFamily().equals("Malgun Gothic") == false) {
             statusFont = new Font("Dotum", Font.PLAIN, 12); 
        }

        moneyLabel = createStatusLabel(statusFont);
        interestLabel = createStatusLabel(statusFont);
        ticketLabel = createStatusLabel(statusFont);
        deadlineLabel = createStatusLabel(statusFont);
        roundLabel = createStatusLabel(statusFont);
        deadlineMoneyLabel = createStatusLabel(statusFont);
        totalMoneyLabel = createStatusLabel(statusFont);
        
        southContainer.add(moneyLabel);
        southContainer.add(interestLabel);
        southContainer.add(ticketLabel);
        southContainer.add(deadlineLabel);
        southContainer.add(roundLabel);
        southContainer.add(deadlineMoneyLabel);
        southContainer.add(totalMoneyLabel);
        
        add(southContainer);
        
        // 결과 레이블 추가
        resultLabel = new JLabel("게임을 시작하세요!");
        Font resultFont = new Font("Malgun Gothic", Font.BOLD, 16);
        if (resultFont.getFamily().equals("Malgun Gothic") == false) {
            resultFont = new Font("Dotum", Font.BOLD, 16);
        }
        resultLabel.setFont(resultFont);
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setBounds(START_X, START_Y_SLOT + BOARD_HEIGHT + 20, SLOT_TOTAL_WIDTH - START_X * 2, 30);
        add(resultLabel);
        
        updateStatusBar();

        gameThread = new Thread(this);
        gameThread.start();
    }
    
    private void initializeRouletteBoard() {
        slots = new JLabel[roulette.getRows()][roulette.getCols()];
        
        for (int i = 0; i < roulette.getRows(); i++) {
            for (int j = 0; j < roulette.getCols(); j++) {
                slots[i][j] = new JLabel("", JLabel.CENTER);
                slots[i][j].setOpaque(true);
                slots[i][j].setBackground(Color.WHITE);
                slots[i][j].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 3),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                slots[i][j].setBounds(
                    START_X + BOARD_PADDING + j * (SLOT_SIZE + SLOT_SPACING),
                    START_Y_SLOT + BOARD_PADDING + i * (SLOT_SIZE + SLOT_SPACING),
                    SLOT_SIZE,
                    SLOT_SIZE
                );
                setRandomSymbol(slots[i][j]);
                add(slots[i][j]);
            }
        }
    }
    
    private void setRandomSymbol(JLabel slot) {
        int symbolIndex = roulette.generateRandomSymbol();
        int[] symbolTypes = roulette.getSymbolTypes();
        slot.setIcon(new SymbolIcon(symbolTypes[symbolIndex], SLOT_SIZE - 10));
    }
    
    private void startSpinProcess() {
        if (isSpinning) return;
        
        handleSpinButtonClick(); 
        
        Timer delayTimer = new Timer(500, new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer)e.getSource()).stop();
                isAnimating = true;
                leverAnimator.start(); 
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
    
    private JLabel createStatusLabel(Font font) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(font);
        return label;
    }

    private void updateStatusBar() {
        int interestRatePct = (int)(user.getInterest() * 100); 
        int calculatedInterestAmount = (int)(user.getTotal_money() * user.getInterest()); 

        moneyLabel.setText("금액: " + user.getRoulatte_money());
        interestLabel.setText("이자: " + interestRatePct + "% (" + calculatedInterestAmount + ")");
        ticketLabel.setText("티켓: " + user.getTicket());
        deadlineLabel.setText("기한: " + user.getDeadline());
        roundLabel.setText("라운드: " + user.getRound());
        deadlineMoneyLabel.setText("목표: " + user.getDeadline_money());
        totalMoneyLabel.setText("납입: " + user.getTotal_money());
    }
    
    private RectangularButton createMenuButton(String label, String frameTitle, Color color) {
        RectangularButton button = new RectangularButton(label, color);
        Dimension buttonDim = new Dimension(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        button.setPreferredSize(buttonDim);
        button.setMinimumSize(buttonDim);
        button.setMaximumSize(buttonDim); 
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNewFrame(frameTitle);
            }
        });
        return button;
    }

    private void showNewFrame(String title) {
        JFrame frame = new JFrame(title);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        
        JLabel label = new JLabel(title + " UI가 여기에 표시됩니다.", SwingConstants.CENTER);
        
        Font font = new Font("Malgun Gothic", Font.BOLD, 16);
        if (font.getFamily().equals("Malgun Gothic") == false) {
            font = new Font("Dotum", Font.BOLD, 16);
        }
        label.setFont(font);

        frame.add(label);
        frame.setVisible(true);
    }
    
    private void handleSpinButtonClick() {
        if (isSpinning) return;
        
        user.setRoulatte_money(user.getRoulatte_money() - 100); 
        user.setRound(user.getRound() + 1);
        updateStatusBar();
        
        startSpin();
    }
    
    private void startSpin() {
        isSpinning = true;
        resultLabel.setText("돌리는 중...");
        resultLabel.setForeground(Color.WHITE);
        spinCount = 0;
        
        spinTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 모든 슬롯 랜덤 변경
                for (int i = 0; i < roulette.getRows(); i++) {
                    for (int j = 0; j < roulette.getCols(); j++) {
                        setRandomSymbol(slots[i][j]);
                    }
                }
                
                spinCount++;
                
                if (spinCount >= MAX_SPIN_COUNT) {
                    spinTimer.stop();
                    finishSpin();
                }
            }
        });
        
        spinTimer.start();
    }
    
    private void finishSpin() {
        // 최종 결과 생성
        int[][] results = roulette.generateResults();
        int[] symbolTypes = roulette.getSymbolTypes();
        
        for (int i = 0; i < roulette.getRows(); i++) {
            for (int j = 0; j < roulette.getCols(); j++) {
                slots[i][j].setIcon(new SymbolIcon(symbolTypes[results[i][j]], SLOT_SIZE - 10));
            }
        }
        
        // 결과 확인
        Roulette.PatternResult patternResult = roulette.checkResults(results);
        
        if (patternResult.hasWin()) {
            resultLabel.setText("<html><center>" + patternResult.getMessage().replace("\n", "<br>") + "</center></html>");
            resultLabel.setForeground(new Color(39, 174, 96));
        } else {
            resultLabel.setText("아쉽네요! 다시 시도해보세요!");
            resultLabel.setForeground(new Color(231, 76, 60));
        }
        
        isSpinning = false;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double FPS = 60.0;
        final double nsPerTick = 1000000000 / FPS; 
        double delta = 0;

        while (gameThread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                repaint(); 
                delta--;
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (bufferImage == null) {
            bufferImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            bufferG = bufferImage.getGraphics();
        }
        
        draw(bufferG);
        g.drawImage(bufferImage, 0, 0, this);
    }
    
    private void draw(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
        // 룰렛 보드 배경
        g2d.setColor(new Color(195, 207, 226)); 
        int boardWidth = ROULETTE_COLS * SLOT_SIZE + (ROULETTE_COLS - 1) * SLOT_SPACING + BOARD_PADDING * 2;
        g2d.fillRect(START_X, START_Y_SLOT, boardWidth, BOARD_HEIGHT);
        
        // 룰렛 보드 테두리
        g2d.setColor(new Color(102, 126, 234));
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRect(START_X, START_Y_SLOT, boardWidth, BOARD_HEIGHT);
        
        // 오른쪽 패널 배경
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(SLOT_TOTAL_WIDTH, NORTH_PANEL_HEIGHT, EAST_PANEL_WIDTH, TOTAL_HEIGHT - NORTH_PANEL_HEIGHT - SOUTH_PANEL_HEIGHT);
        
        drawLeverBar(g2d); 
    }
    
    private void drawLeverBar(Graphics2D g2d) {
        
        int buttonCenterY = BUTTON_Y_TOP_TARGET + LEVER_HEAD_SIZE / 2 + 
                            (int) (LEVER_MOVEMENT_DISTANCE * leverPosition);
        
        int buttonX = LEVER_CENTER_X - LEVER_HEAD_SIZE / 2;
        int buttonY = buttonCenterY - LEVER_HEAD_SIZE / 2;
        
        int barStartDrawY; 
        int barEndDrawY;   

        if (buttonCenterY <= BAR_BASE_Y) { 
            barStartDrawY = buttonCenterY;
            barEndDrawY = BAR_BASE_Y; 
        } else { 
            barStartDrawY = BAR_BASE_Y; 
            barEndDrawY = buttonCenterY;
        }
        
        g2d.setColor(new Color(150, 150, 150));
        g2d.setStroke(new BasicStroke(LEVER_BAR_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        g2d.drawLine(LEVER_CENTER_X, barStartDrawY, LEVER_CENTER_X, barEndDrawY);
        
        g2d.setColor(new Color(60, 60, 60));
        g2d.fillOval(LEVER_CENTER_X - LEVER_BAR_THICKNESS, BAR_BASE_Y - LEVER_BAR_THICKNESS, LEVER_BAR_THICKNESS * 2, LEVER_BAR_THICKNESS * 2);

        if (leverButton != null) {
            leverButton.setBounds(buttonX, buttonY, LEVER_HEAD_SIZE, LEVER_HEAD_SIZE);
        }
    }
    

    
    private class RectangularButton extends JButton {
        private Color buttonColor;
    
        public RectangularButton(String label, Color color) {
            super(label);
            this.buttonColor = color;
            setOpaque(false); 
            setContentAreaFilled(false); 
            setFocusPainted(false); 
            setForeground(Color.BLACK); 
            
            setFont(new Font("Malgun Gothic", Font.BOLD, 12)); 
            if (getFont().getFamily().equals("Malgun Gothic") == false) {
                 setFont(new Font("Dotum", Font.BOLD, 12)); 
            }
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            Color currentColor = buttonColor;
            if (getModel().isArmed()) {
                currentColor = buttonColor.darker(); 
            }
            g.setColor(currentColor);
            g.fillRect(0, 0, getSize().width, getSize().height);
            super.paintComponent(g); 
        }
    
        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
        }
    }

    private class LeverHeadButton extends JButton {
        private Shape shape;
    
        public LeverHeadButton() {
            super(""); 
            setContentAreaFilled(false); 
            setFocusPainted(false); 
            setBackground(Color.RED); 
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            Color currentColor = getBackground();
            if (leverPosition > 0.5f) { 
                currentColor = getBackground().darker(); 
            }
            g.setColor(currentColor);
            g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(getSize().width / 4, getSize().height / 4, getSize().width / 4, getSize().height / 4);
        }
    
        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(Color.BLACK);
            g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
        }
    
        @Override
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
            }
            return shape.contains(x, y);
        }
    }
}