import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SlotMachinePanel extends JPanel implements Runnable {

    // ----- 게임 보드 / 룰렛 -----
    private Roulette roulette;
    private JLabel[][] slots;

    // ----- 스핀/라운드 상태 -----
    private boolean isSpinning = false;
    private Timer spinTimer;
    private int spinCount = 0;
    private static final int MAX_SPIN_COUNT = 30;
    private boolean roundStarted = false;

    // ----- 레이아웃 상수 -----
    private static final int START_X = 100;
    private static final int START_Y_SLOT = 120;

    private LeverHeadButton leverButton;
    private RectangularButton payButton;
    private RectangularButton symbolButton;
    private RectangularButton patternButton;
    private RectangularButton relicOwnedButton;
    private RectangularButton relicShopButton;
    private RectangularButton phoneButton;
    private RectangularButton roundStartButton;

    private static final Color COLOR_PAY = new Color(50, 150, 250);
    private static final Color COLOR_SYMBOL = new Color(255, 180, 0);
    private static final Color COLOR_PATTERN = new Color(150, 255, 100);
    private static final Color COLOR_RELIC_OWNED = new Color(255, 100, 150);
    private static final Color COLOR_RELIC_SHOP = new Color(100, 200, 200);
    private static final Color COLOR_PHONE = new Color(180, 100, 255);

    private static final int MENU_BUTTON_WIDTH = 150;
    private static final int MENU_BUTTON_HEIGHT = 60;
    private static final int NORTH_PANEL_HEIGHT = 100;
    private static final int EAST_PANEL_WIDTH = 200;

    private static final int LEVER_HEAD_SIZE = 60;
    private static final int LEVER_BAR_THICKNESS = 18;

    private static final int TARGET_WIDTH = 1600;
    private static final int TARGET_HEIGHT = 900;

    private static final int SLOT_SIZE = 120;
    private static final int SLOT_SPACING = 15;
    private static final int BOARD_PADDING = 20;
    private static final int ROULETTE_COLS = 5;
    private static final int ROULETTE_ROWS = 3;
    private static final int BOARD_WIDTH =
            ROULETTE_COLS * SLOT_SIZE + (ROULETTE_COLS - 1) * SLOT_SPACING + BOARD_PADDING * 2;
    private static final int LEVER_CENTER_X =
            START_X + BOARD_WIDTH + 50 + EAST_PANEL_WIDTH / 2;

    private static final int BOARD_HEIGHT =
            ROULETTE_ROWS * SLOT_SIZE + (ROULETTE_ROWS - 1) * SLOT_SPACING + BOARD_PADDING * 2 + 30;
    private static final int BUTTON_Y_TOP_TARGET = START_Y_SLOT;
    private static final int BAR_BASE_Y = START_Y_SLOT + BOARD_HEIGHT / 2;
    private static final int TRAVEL_TO_MID = BAR_BASE_Y - (BUTTON_Y_TOP_TARGET + LEVER_HEAD_SIZE / 2);
    private static final int LEVER_MOVEMENT_DISTANCE = TRAVEL_TO_MID * 2;

    private float leverPosition = 0.0f;
    private Timer leverAnimator;
    private boolean isAnimating = false;

    private static final int SOUTH_PANEL_HEIGHT = 60;

    private static final int TOTAL_WIDTH = TARGET_WIDTH;
    private static final int TOTAL_HEIGHT = TARGET_HEIGHT;

    // 라운드/기한 규칙
    // 기한당 라운드 수 : 3
    // 라운드당 스핀 수 : 7 또는 3 (선택)
    private static int SPINS_PER_ROUND = 7;
    private static final int ROUNDS_PER_DEADLINE = 3;

    // ----- 유저 / 매니저 -----
    private User user;
    private RoundManager roundManager;
    private SaveManagerCsv saveManager;
    private SoundManager soundManager;

    // ----- 상태 표시용 라벨 -----
    private JLabel moneyLabel;
    private JLabel interestLabel;
    private JLabel ticketLabel;
    private JLabel deadlineLabel;
    private JLabel roundLabel;
    private JLabel deadlineMoneyLabel;
    private JLabel totalMoneyLabel;
    private JLabel spinLeftLabel;

    // ----- 게임 루프 / 더블버퍼 -----
    private Thread gameThread;
    private BufferedImage bufferImage;
    private Graphics bufferG;

    // ------------------------------------
    // 생성자
    // ------------------------------------
    public SlotMachinePanel() {
        saveManager = new SaveManagerCsv();
        User loaded = saveManager.load();

        if (loaded == null) {
            loaded = new User();
            // 기본값 보정
            if (loaded.getRound() <= 0) loaded.setRound(1);
            if (loaded.getDeadline() <= 0) loaded.setDeadline(1);
            loaded.setRound_spin_left(0); // 처음에 라운드 선택 다이얼로그 띄우려고 0으로
        }

        init(loaded);
    }

    public SlotMachinePanel(User user) {
        init(user);
    }

    // 공통 초기화
    private void init(User user) {
        this.user = user;
        this.soundManager = new SoundManager();
        this.saveManager = new SaveManagerCsv();
        this.roundManager = new RoundManager(user);

        if (user.getRound() <= 0) {
            user.setRound(1);
        }
        if (user.getRound_spin_left() < 0) {
            user.setRound_spin_left(0);
        }
        if (user.getDeadline() <= 0) {
            user.setDeadline(1);
        }

        roulette = new Roulette();
        setLayout(null);
        setPreferredSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT));
        setBackground(Color.DARK_GRAY);

        initializeRouletteBoard();

        // ----- 상단 메뉴 버튼 패널 -----
        JPanel northContainer =
                new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        northContainer.setBackground(Color.DARK_GRAY.darker());
        northContainer.setBounds(0, 0, TOTAL_WIDTH, NORTH_PANEL_HEIGHT);

        payButton = createMenuButton("납입", "납입 버튼 화면", COLOR_PAY);
        symbolButton = createMenuButton("무늬", "무늬 버튼 화면", COLOR_SYMBOL);
        patternButton = createMenuButton("패턴", "패턴 버튼 화면", COLOR_PATTERN);
        relicOwnedButton = createMenuButton("소지 유물", "소지 유물 버튼 화면", COLOR_RELIC_OWNED);
        relicShopButton = createMenuButton("유물 상점", "유물 상점 버튼 화면", COLOR_RELIC_SHOP);
        phoneButton = createMenuButton("전화", "전화", COLOR_PHONE);

        northContainer.add(payButton);
        northContainer.add(symbolButton);
        northContainer.add(patternButton);
        northContainer.add(relicOwnedButton);
        northContainer.add(relicShopButton);
        northContainer.add(phoneButton);

        add(northContainer);

        // ----- 레버 애니메이션 -----
        leverAnimator = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAnimating) return;

                float step = 0.1f;

                if (!isSpinning) {
                    // 위에서 아래로
                    if (leverPosition < 0.9f) {
                        leverPosition = Math.min(0.9f, leverPosition + step);
                    } else {
                        // 아래까지 내려왔으면 스핀 시작
                        isAnimating = false;
                        leverAnimator.stop();

                        Runnable startRoulette = new Runnable() {
                            @Override
                            public void run() {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleSpinButtonClick();
                                    }
                                });
                            }
                        };

                        if (soundManager.isLeverSoundPlaying()) {
                            soundManager.setLeverSoundFinishedCallback(startRoulette);
                        } else {
                            startRoulette.run();
                        }
                        return;
                    }
                } else {
                    // 스핀 중에는 다시 위로 복귀
                    if (leverPosition > 0.0f) {
                        leverPosition = Math.max(0.0f, leverPosition - step);
                    } else {
                        isAnimating = false;
                        leverAnimator.stop();
                    }
                }

                repaint();
            }
        });

        // 레버 버튼
        leverButton = new LeverHeadButton();
        leverButton.setBounds(0, 0, LEVER_HEAD_SIZE, LEVER_HEAD_SIZE);
        leverButton.setEnabled(false);
        leverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSpinning && !isAnimating && roundStarted) {
                    isAnimating = true;
                    leverAnimator.start();
                    soundManager.playLeverSound();
                }
            }
        });
        add(leverButton);
        setComponentZOrder(leverButton, 0);

        // ----- 상태 표시 라벨 (데이터만 가지고 있고, 실제 출력은 drawStatusBar에서 함) -----
        JPanel southContainer = new JPanel(new GridLayout(1, 8, 5, 0));
        southContainer.setBackground(Color.BLACK);
        southContainer.setBounds(0, TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT, TOTAL_WIDTH, SOUTH_PANEL_HEIGHT);

        Font statusFont = new Font("Malgun Gothic", Font.PLAIN, 12);
        if (!statusFont.getFamily().equals("Malgun Gothic")) {
            statusFont = new Font("Dotum", Font.PLAIN, 12);
        }

        roundStartButton = new RectangularButton("라운드 시작", new Color(100, 200, 100));
        roundStartButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        if (!roundStartButton.getFont().getFamily().equals("Malgun Gothic")) {
            roundStartButton.setFont(new Font("Dotum", Font.BOLD, 16));
        }

        moneyLabel = createStatusLabel(statusFont);
        interestLabel = createStatusLabel(statusFont);
        ticketLabel = createStatusLabel(statusFont);
        deadlineLabel = createStatusLabel(statusFont);
        roundLabel = createStatusLabel(statusFont);
        deadlineMoneyLabel = createStatusLabel(statusFont);
        totalMoneyLabel = createStatusLabel(statusFont);
        spinLeftLabel = createStatusLabel(statusFont);

        roundStartButton.setBounds(
                LEVER_CENTER_X + LEVER_HEAD_SIZE / 2 + 20,
                BAR_BASE_Y - 30,
                150,
                100
        );
        roundStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRoundStartDialog();
            }
        });
        add(roundStartButton);
        setComponentZOrder(roundStartButton, 0);

        southContainer.add(moneyLabel);
        southContainer.add(interestLabel);
        southContainer.add(ticketLabel);
        southContainer.add(deadlineLabel);
        southContainer.add(roundLabel);
        southContainer.add(spinLeftLabel);
        southContainer.add(deadlineMoneyLabel);
        southContainer.add(totalMoneyLabel);

        // 처음 상태에 따라 레버/라운드 버튼 보이기 조절
        if (user.getRound_spin_left() <= 0) {
            roundStarted = false;
            leverButton.setEnabled(false);
            roundStartButton.setVisible(true);
        } else {
            roundStarted = true;
            leverButton.setEnabled(true);
            roundStartButton.setVisible(false);
        }

        updateStatusBar();

        gameThread = new Thread(this);
        gameThread.start();

        // 아직 라운드가 시작되지 않았다면 최초 진입 시 다이얼로그 띄우기
        if (!roundStarted) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    showRoundStartDialog();
                }
            });
        }
    }

    private JLabel createStatusLabel(Font font) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(font);
        return label;
    }

    // ------------------------------------
    // 룰렛 보드 / 심볼
    // ------------------------------------
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
        slot.setIcon(new SymbolIcon(symbolTypes[symbolIndex], SLOT_SIZE - 20));
    }

    // ------------------------------------
    // 상태바 갱신
    // ------------------------------------
    private void updateStatusBar() {
        int interestRatePct = (int) (user.getInterest() * 100);
        int calculatedInterestAmount = (int) (user.getTotal_money() * user.getInterest());
        int roundLeft = ROUNDS_PER_DEADLINE - user.getRound() + 1;
        if (roundLeft < 0) roundLeft = 0;

        moneyLabel.setText("금액: " + user.getRoulatte_money());
        interestLabel.setText("이자: " + interestRatePct + "% (" + calculatedInterestAmount + ")");
        ticketLabel.setText("티켓: " + user.getTicket());
        deadlineLabel.setText("기한: " + user.getDeadline());
        roundLabel.setText("라운드: " + roundLeft + "/" + ROUNDS_PER_DEADLINE);
        spinLeftLabel.setText("남은 스핀: " + user.getRound_spin_left() + "/" + SPINS_PER_ROUND);
        deadlineMoneyLabel.setText("목표: " + user.getDeadline_money());
        totalMoneyLabel.setText("납입: " + user.getTotal_money());
    }

    // ------------------------------------
    // 상단 메뉴 버튼
    // ------------------------------------
    private RectangularButton createMenuButton(String label, String frameTitle, Color color) {
        RectangularButton button = new RectangularButton(label, color);
        Dimension buttonDim = new Dimension(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
        button.setPreferredSize(buttonDim);
        button.setMinimumSize(buttonDim);
        button.setMaximumSize(buttonDim);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("종료".equals(label)) {
                    Main.exitGame();
                } else {
                    showNewFrame(frameTitle);
                }
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
        if (!font.getFamily().equals("Malgun Gothic")) {
            font = new Font("Dotum", Font.BOLD, 16);
        }
        label.setFont(font);

        frame.add(label);
        frame.setVisible(true);
    }

    // ------------------------------------
    // 라운드 시작 다이얼로그 + 스핀 처리
    // ------------------------------------
    private void showRoundStartDialog() {
        if (roundStarted) {
            JOptionPane.showMessageDialog(this, "이미 라운드가 시작되었습니다.");
            return;
        }

        Object[] options = {
                "돌리기 7회 + 티켓 1개",
                "돌리기 3회 + 티켓 2개"
        };

        int choice = JOptionPane.showOptionDialog(
                this,
                "라운드 시작 옵션을 선택하세요:",
                "라운드 시작",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            SPINS_PER_ROUND = 7;
            user.setRound_spin_left(7);
            user.addTicket(1);
            roundStarted = true;
            leverButton.setEnabled(true);
            roundStartButton.setVisible(false);
            updateStatusBar();
            JOptionPane.showMessageDialog(this,
                    "라운드가 시작되었습니다!\n돌리기 횟수: 7회\n티켓 지급: 1개");
        } else if (choice == JOptionPane.NO_OPTION) {
            SPINS_PER_ROUND = 3;
            user.setRound_spin_left(3);
            user.addTicket(2);
            roundStarted = true;
            leverButton.setEnabled(true);
            roundStartButton.setVisible(false);
            updateStatusBar();
            JOptionPane.showMessageDialog(this,
                    "라운드가 시작되었습니다!\n돌리기 횟수: 3회\n티켓 지급: 2개");
        }
    }

    // 스핀 버튼(레버) 처리
    private void handleSpinButtonClick() {
        if (isSpinning) return;

        if (!roundStarted) {
            JOptionPane.showMessageDialog(this, "먼저 라운드를 시작해주세요.");
            return;
        }

        // RoundManager가 user.round_spin_left만 줄여준다고 가정
        if (!roundManager.consumeSpin()) {
            // 이미 더 이상 돌릴 수 없음 → 이번 라운드 종료 처리
            onRoundFinished();
            return;
        }

        // consumeSpin() 호출 후에 0이 되었다 = 이번 라운드 스핀 다 씀
        if (user.getRound_spin_left() <= 0) {
            onRoundFinished();
            return;
        }

        updateStatusBar();
        startSpin();
    }
    
    /**
     * 한 라운드의 스핀을 모두 사용했을 때 호출되는 공통 처리
     * - 라운드 수 관리
     * - 기한 종료 시 납입 금액 판정(성공/탈락)
     */
    private void onRoundFinished() {
        roundStarted = false;
        leverButton.setEnabled(false);
        roundStartButton.setVisible(true);

        int currentRound = user.getRound();

        if (currentRound < ROUNDS_PER_DEADLINE) {
            user.setRound(currentRound + 1);
            user.setRound_spin_left(0);
        } else {
            if (user.getTotal_money() < user.getDeadline_money()) {
                JOptionPane.showMessageDialog(
                    this,
                    "납입 금액이 목표 금액(" + user.getDeadline_money() +
                    ")에 미달하여 탈락했습니다.\n게임을 종료합니다."
                );

                saveOnExit();
                Main.exitGame();
                return;
            }

            JOptionPane.showMessageDialog(
                this,
                "기한 " + user.getDeadline() + "의 3라운드를 모두 사용했습니다.\n" +
                "다음 기한으로 넘어갑니다."
            );

            user.setDeadline(user.getDeadline() + 1);
            user.setRound(1);
            user.setRound_spin_left(0);

            // 필요하다면 여기에서 기한별 목표 금액/난이도 조정도 가능
            // 예시) user.setDeadline_money(user.getDeadline_money() + 50);
        }

        updateStatusBar();
    }

    private void startSpin() {
        isSpinning = true;
        spinCount = 0;
        soundManager.playSpinSound();

        isAnimating = true;
        leverAnimator.start();

        spinTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

        spinTimer.setInitialDelay(0);
        spinTimer.start();
    }

    private void finishSpin() {
        int[][] results = roulette.generateResults();
        int[] symbolTypes = roulette.getSymbolTypes();

        for (int i = 0; i < roulette.getRows(); i++) {
            for (int j = 0; j < roulette.getCols(); j++) {
                slots[i][j].setIcon(new SymbolIcon(symbolTypes[results[i][j]], SLOT_SIZE - 20));
            }
        }

        roulette.checkResults(results);
        soundManager.stopSpinSound();
        isSpinning = false;
        user.setRoulatte_money(user.getRoulatte_money() + roulette.roulette_money);
        roulette.roulette_money = 0;
        updateStatusBar();
    }

    // ------------------------------------
    // 저장 / 게임 루프 / 그리기
    // ------------------------------------
    public void saveOnExit() {
        if (saveManager != null && user != null) {
            saveManager.save(user);
        }
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
        super.paintComponent(g);

        if (bufferImage == null ||
                bufferImage.getWidth() != getWidth() ||
                bufferImage.getHeight() != TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT) {
            bufferImage = new BufferedImage(
                    getWidth(),
                    TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT,
                    BufferedImage.TYPE_INT_ARGB);
            bufferG = bufferImage.getGraphics();
        }

        draw(bufferG);
        int statusBarY = TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT - 40;
        g.drawImage(bufferImage, 0, 0, getWidth(), statusBarY, null);

        drawStatusBar(g);
    }

    private void drawStatusBar(Graphics g) {
        int y = TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT - 40;

        g.setColor(Color.BLACK);
        g.fillRect(0, y, TOTAL_WIDTH, SOUTH_PANEL_HEIGHT);

        g.setColor(Color.WHITE);
        g.drawRect(0, y, TOTAL_WIDTH - 1, SOUTH_PANEL_HEIGHT - 1);

        g.setColor(Color.WHITE);
        Font statusFont = new Font("Malgun Gothic", Font.PLAIN, 16);
        if (!statusFont.getFamily().equals("Malgun Gothic")) {
            statusFont = new Font("Dotum", Font.PLAIN, 16);
        }
        g.setFont(statusFont);

        FontMetrics fm = g.getFontMetrics();
        int cellWidth = TOTAL_WIDTH / 8;
        int textY = y + (SOUTH_PANEL_HEIGHT + fm.getAscent() - fm.getDescent()) / 2;

        String moneyText = (moneyLabel != null) ? moneyLabel.getText() : "금액: " + user.getRoulatte_money();
        String interestText = (interestLabel != null) ? interestLabel.getText() : "이자: 0% (0)";
        String ticketText = (ticketLabel != null) ? ticketLabel.getText() : "티켓: " + user.getTicket();
        String deadlineText = (deadlineLabel != null) ? deadlineLabel.getText() : "기한: " + user.getDeadline();
        String roundText = (roundLabel != null) ? roundLabel.getText() : "라운드: 0/0";
        String spinLeftText = (spinLeftLabel != null) ? spinLeftLabel.getText() : "남은 스핀: 0/0";
        String deadlineMoneyText = (deadlineMoneyLabel != null) ? deadlineMoneyLabel.getText() : "목표: " + user.getDeadline_money();
        String totalMoneyText = (totalMoneyLabel != null) ? totalMoneyLabel.getText() : "납입: " + user.getTotal_money();

        int textX = cellWidth * 0 + (cellWidth - fm.stringWidth(moneyText)) / 2;
        g.drawString(moneyText, textX, textY);

        textX = cellWidth * 1 + (cellWidth - fm.stringWidth(interestText)) / 2;
        g.drawString(interestText, textX, textY);

        textX = cellWidth * 2 + (cellWidth - fm.stringWidth(ticketText)) / 2;
        g.drawString(ticketText, textX, textY);

        textX = cellWidth * 3 + (cellWidth - fm.stringWidth(deadlineText)) / 2;
        g.drawString(deadlineText, textX, textY);

        textX = cellWidth * 4 + (cellWidth - fm.stringWidth(roundText)) / 2;
        g.drawString(roundText, textX, textY);

        textX = cellWidth * 5 + (cellWidth - fm.stringWidth(spinLeftText)) / 2;
        g.drawString(spinLeftText, textX, textY);

        textX = cellWidth * 6 + (cellWidth - fm.stringWidth(deadlineMoneyText)) / 2;
        g.drawString(deadlineMoneyText, textX, textY);

        textX = cellWidth * 7 + (cellWidth - fm.stringWidth(totalMoneyText)) / 2;
        g.drawString(totalMoneyText, textX, textY);
    }

    private void draw(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(195, 207, 226));
        int boardWidth = ROULETTE_COLS * SLOT_SIZE + (ROULETTE_COLS - 1) * SLOT_SPACING + BOARD_PADDING * 2;
        g2d.fillRect(START_X, START_Y_SLOT, boardWidth, BOARD_HEIGHT);

        g2d.setColor(new Color(102, 126, 234));
        g2d.setStroke(new BasicStroke(8));
        g2d.drawRect(START_X, START_Y_SLOT, boardWidth, BOARD_HEIGHT);

        g2d.setColor(Color.DARK_GRAY);
        int rightPanelX = START_X + boardWidth + 50;
        g2d.fillRect(rightPanelX, NORTH_PANEL_HEIGHT,
                EAST_PANEL_WIDTH,
                TOTAL_HEIGHT - NORTH_PANEL_HEIGHT - SOUTH_PANEL_HEIGHT);

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
        g2d.fillOval(LEVER_CENTER_X - LEVER_BAR_THICKNESS,
                BAR_BASE_Y - LEVER_BAR_THICKNESS,
                LEVER_BAR_THICKNESS * 2,
                LEVER_BAR_THICKNESS * 2);

        if (leverButton != null) {
            leverButton.setBounds(buttonX, buttonY, LEVER_HEAD_SIZE, LEVER_HEAD_SIZE);
        }
    }

    // ------------------------------------
    // 커스텀 버튼/레버 컴포넌트
    // ------------------------------------
    private class RectangularButton extends JButton {
        private Color buttonColor;

        public RectangularButton(String label, Color color) {
            super(label);
            this.buttonColor = color;
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.BLACK);

            setFont(new Font("Malgun Gothic", Font.BOLD, 16));
            if (!getFont().getFamily().equals("Malgun Gothic")) {
                setFont(new Font("Dotum", Font.BOLD, 16));
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
            g2d.fillOval(getSize().width / 4, getSize().height / 4,
                    getSize().width / 4, getSize().height / 4);
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
