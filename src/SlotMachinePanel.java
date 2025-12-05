import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class SlotMachinePanel extends JPanel implements Runnable {
    
    private static final String[] SYMBOLS = {"Circle", "Star", "Triangle", "Square", "Diamond", "Hexagon", "Cross"};
    
    private static final int NUM_REELS = 5; 
    private static final int VISIBLE_SYMBOLS = 3; 
    private static final int START_X = 50;
    private static final int START_Y_SLOT = 80; 
    private static final int REEL_WIDTH = 120; 
    private static final int SYMBOL_HEIGHT = 100;
    private static final int REEL_SPACING = 20; 
    private static final int REEL_HEIGHT = VISIBLE_SYMBOLS * SYMBOL_HEIGHT; 
    
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

    private static final int SLOT_TOTAL_WIDTH = REEL_WIDTH * NUM_REELS + REEL_SPACING * (NUM_REELS - 1) + START_X * 2; 
    private static final int LEVER_CENTER_X = SLOT_TOTAL_WIDTH + EAST_PANEL_WIDTH / 2;
    
    private static final int BUTTON_Y_TOP_TARGET = START_Y_SLOT; 
    private static final int BAR_BASE_Y = START_Y_SLOT + REEL_HEIGHT / 2; 
    private static final int TRAVEL_TO_MID = BAR_BASE_Y - (BUTTON_Y_TOP_TARGET + LEVER_HEAD_SIZE / 2);
    private static final int LEVER_MOVEMENT_DISTANCE = TRAVEL_TO_MID * 2; 
    
    private float leverPosition = 0.0f; 
    private Timer leverAnimator;
    private boolean isSpinning = false; 
    private boolean isAnimating = false; 

    private static final int SOUTH_PANEL_HEIGHT = 40; 
    private static final int TOTAL_WIDTH_UNADJUSTED = SLOT_TOTAL_WIDTH + EAST_PANEL_WIDTH; 
    private static final int TARGET_HEIGHT = 512;
    private static final int TOTAL_WIDTH = TOTAL_WIDTH_UNADJUSTED; 
    private static final int TOTAL_HEIGHT = TARGET_HEIGHT; 
    
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
    private Reel[] reels; 
    private Random random = new Random();
    
    public SlotMachinePanel() {
        user = new User();
        setLayout(null); 
        setPreferredSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT)); 
        setBackground(Color.DARK_GRAY);
        
        reels = new Reel[NUM_REELS];
        for (int i = 0; i < NUM_REELS; i++) {
            reels[i] = new Reel();
        }
        
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
        
        updateStatusBar();

        gameThread = new Thread(this);
        gameThread.start();
    }
    
    private void startSpinProcess() {
        if (isSpinning) return;
        
        isSpinning = true;

        handleSpinButtonClick(); 
        
        Timer delayTimer = new Timer(500, new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer)e.getSource()).stop();
                isSpinning = false; 
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
        for (int i = 0; i < NUM_REELS; i++) { 
            reels[i].randomizeSymbols(); 
        }
        user.setRoulatte_money(user.getRoulatte_money() - 100); 
        user.setRound(user.getRound() + 1);
        updateStatusBar(); 
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
    
        g2d.setColor(new Color(30, 30, 30)); 
        int totalWidth = REEL_WIDTH * NUM_REELS + REEL_SPACING * (NUM_REELS - 1);
        g2d.fillRect(START_X, START_Y_SLOT, totalWidth, REEL_HEIGHT);
    
        for (int i = 0; i < NUM_REELS; i++) {
            int reelX = START_X + i * (REEL_WIDTH + REEL_SPACING);
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3)); 
            g2d.drawRect(reelX, START_Y_SLOT, REEL_WIDTH, REEL_HEIGHT);

            String[] currentSymbols = reels[i].getVisibleSymbols();
        
            for (int j = 0; j < VISIBLE_SYMBOLS; j++) {
                int symbolY = START_Y_SLOT + j * SYMBOL_HEIGHT;
                
                g2d.setColor(new Color(100, 100, 100)); 
                g2d.drawRect(reelX, symbolY, REEL_WIDTH, SYMBOL_HEIGHT);
        
                drawSymbol(g2d, currentSymbols[j], reelX, symbolY);
            }
        }
        
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
    
    private void drawSymbol(Graphics2D g2d, String symbol, int x, int y) {
        int centerX = x + REEL_WIDTH / 2;
        int centerY = y + SYMBOL_HEIGHT / 2;
        int size = Math.min(REEL_WIDTH, SYMBOL_HEIGHT) / 3;
        
        switch (symbol) {
            case "Circle":
                g2d.setColor(Color.YELLOW);
                g2d.fillOval(centerX - size, centerY - size, 2 * size, 2 * size);
                break;
            case "Square":
                g2d.setColor(Color.GREEN);
                g2d.fillRect(centerX - size, centerY - size, 2 * size, 2 * size);
                break;
            case "Triangle":
                g2d.setColor(Color.BLUE);
                int[] tx = {centerX, centerX - size, centerX + size};
                int[] ty = {centerY - size, centerY + size, centerY + size};
                g2d.fillPolygon(tx, ty, 3);
                break;
            case "Star":
                g2d.setColor(Color.RED);
                int numPoints = 5;
                int outerRadius = size;
                int innerRadius = size / 2;
                int[] sx = new int[numPoints * 2];
                int[] sy = new int[numPoints * 2];
                
                for (int i = 0; i < numPoints * 2; i++) {
                    double radius = (i % 2 == 0) ? outerRadius : innerRadius;
                    double angle = Math.PI / 2 + i * Math.PI / numPoints;
                    sx[i] = (int) (centerX + radius * Math.cos(angle));
                    sy[i] = (int) (centerY - radius * Math.sin(angle));
                }
                g2d.fillPolygon(sx, sy, numPoints * 2);
                break;
            case "Diamond": 
                g2d.setColor(Color.CYAN);
                int[] dx = {centerX, centerX + size, centerX, centerX - size};
                int[] dy = {centerY - size, centerY, centerY + size, centerY};
                g2d.fillPolygon(dx, dy, 4);
                break;
            case "Hexagon": 
                g2d.setColor(Color.MAGENTA);
                int numSides = 6;
                int[] hx = new int[numSides];
                int[] hy = new int[numSides];
                
                for (int i = 0; i < numSides; i++) {
                    double angle = i * 2 * Math.PI / numSides;
                    hx[i] = (int) (centerX + size * Math.cos(angle));
                    hy[i] = (int) (centerY + size * Math.sin(angle));
                }
                g2d.fillPolygon(hx, hy, numSides);
                break;
            case "Cross": 
                g2d.setColor(Color.ORANGE);
                int halfSize = size / 2;
                g2d.fillRect(centerX - halfSize, centerY - size, 2 * halfSize, 2 * size);
                g2d.fillRect(centerX - size, centerY - halfSize, 2 * size, 2 * halfSize);
                break;
        }
    }


    private class Reel {
        private String[] visibleSymbols = new String[VISIBLE_SYMBOLS];
        private Random reelRandom = new Random();

        public Reel() {
            for (int i = 0; i < VISIBLE_SYMBOLS; i++) {
                visibleSymbols[i] = SYMBOLS[0];
            }
        }

        public void randomizeSymbols() {
            for (int i = 0; i < VISIBLE_SYMBOLS; i++) {
                int randomIndex = reelRandom.nextInt(SYMBOLS.length);
                visibleSymbols[i] = SYMBOLS[randomIndex];
            }
        }

        public String[] getVisibleSymbols() {
            return visibleSymbols;
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