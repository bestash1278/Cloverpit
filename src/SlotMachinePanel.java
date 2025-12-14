import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * 게임 메인 패널
 * 룰렛 게임 로직, 레버 애니메이션, 스핀 처리, 상태 표시를 담당
 */
public class SlotMachinePanel extends JPanel implements Runnable {
    
    private Roulette roulette;
    private JLabel[][] slots;
    private boolean isSpinning = false;
    private Timer spinTimer;
    private int spinCount = 0;
    private static final int MAX_SPIN_COUNT = 30;
    private boolean roundStarted = false;
    
    //이번 스핀이 기한 내 마지막 스핀인지 확인
    private boolean lastSpinOfDeadline = false;
    
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
    private static final int BOARD_WIDTH = ROULETTE_COLS * SLOT_SIZE + (ROULETTE_COLS - 1) * SLOT_SPACING + BOARD_PADDING * 2;
    private static final int LEVER_CENTER_X = START_X + BOARD_WIDTH + 50 + EAST_PANEL_WIDTH / 2;
    
    private static final int BOARD_HEIGHT = ROULETTE_ROWS * SLOT_SIZE + (ROULETTE_ROWS - 1) * SLOT_SPACING + BOARD_PADDING * 2 + 30;
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
    
    private User user;
    
    private JLabel moneyLabel;
    private JLabel interestLabel;
    private JLabel ticketLabel;
    private JLabel deadlineLabel;
    private JLabel roundLabel;
    private JLabel deadlineMoneyLabel;
    private JLabel totalMoneyLabel;
    private JLabel spinLeftLabel;

    private Thread gameThread;
    private BufferedImage bufferImage;
    private Graphics bufferG;
    private SoundManager soundManager;
    private SaveManagerCsv saveManager;
    private RoundManager roundManager;
    private Call call;	//전화 기능
    private static int SPINS_PER_ROUND = 7;
    private static final int ROUNDS_PER_DEADLINE = 3;
    
    // 패턴 완성 슬롯 빛나는 효과
    private java.util.Set<String> glowingSlots = new java.util.HashSet<>(); // "row,col" 형식으로 저장
    private Timer glowTimer;
    private float glowAlpha = 0.0f;
    private boolean glowIncreasing = true;
    
    private RoulatteInfo roulatte; // TODO 감자 :  룰렛정보클래스 만들어지면 그때 변경
	private ItemShop itemShop;
    private ItemShop_Screen itemShopScreen;	//유물화면 보관용
	private Payment_Screen paymentScreen;	//납입화면 보관용
	private ItemShop_Screen currentPanel;	//유물화면 보관용
	private Call_Screen callScreen;	//전화 화면
	private OwnItem ownItem;
	private OwnItem_Screen ownItemScreen;
	private SymbolPrice_Screen symbolPriceScreen;	//무늬 가격 화면
	private PatternPrice_Screen patternPriceScreen;	//패턴 가격 화면

    
    public SlotMachinePanel() {
        SaveManagerCsv tempSaveManager = new SaveManagerCsv();
        User loaded = tempSaveManager.load();
        if (loaded != null) {
            init(loaded);
        } else {
            User newUser = new User();
            newUser.setRound_spin_left(0);
            init(newUser);
        }
    }
    
    public SlotMachinePanel(User user) {
        init(user);
    }
    
    private void init(User user) {
        this.user = user;
        this.soundManager = new SoundManager();
        this.saveManager = new SaveManagerCsv();
        this.roundManager = new RoundManager(user);
        
        this.roulatte = new RoulatteInfo();
        this.ownItem = new OwnItem(user, this::updateStatusBar);
        this.ownItemScreen = new OwnItem_Screen(this.ownItem);
        this.call = new Call(user, roundManager, () -> callScreen.updateRerollButtonText());
        this.callScreen = new Call_Screen(this.call);
        this.itemShop = new ItemShop(
                user, 
                this::updateStatusBar, // 메인 상태바 갱신
                this.ownItemScreen::updateOwnedItemsUI // ⭐ 구매 후 소유 유물 화면 갱신
            );
        this.itemShopScreen = new ItemShop_Screen(this.itemShop);
        
        Payment paymentLogic = new Payment(this.user, this.roundManager, this.roulatte, 
        		this.itemShop, this::updateStatusBar,this::updateShopScreen, this.call, this::updateCallScreen);//------------------------------------------
        this.paymentScreen = new Payment_Screen(paymentLogic);
        
        this.roulatte = new RoulatteInfo();
        this.paymentScreen = new Payment_Screen(paymentLogic);
        updatePhoneButtonState();	//------------------------------------------------------------------
        this.symbolPriceScreen = new SymbolPrice_Screen(user);
        this.patternPriceScreen = new PatternPrice_Screen(user);
        
        roulette = new Roulette();
        roulette.setUser(this.user);
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
        phoneButton = createMenuButton("전화", "전화", COLOR_PHONE);
        
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
                    
                    if (!isSpinning) {
                        if (leverPosition < 0.9f) {
                            leverPosition = Math.min(0.9f, leverPosition + step);
                        } else {
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
                        if (leverPosition > 0.0f) {
                            leverPosition = Math.max(0.0f, leverPosition - step);
                        } else {
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
        
        roundStartButton = new RectangularButton("라운드 시작", new Color(100, 200, 100));
        roundStartButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        if (roundStartButton.getFont().getFamily().equals("Malgun Gothic") == false) {
            roundStartButton.setFont(new Font("Dotum", Font.BOLD, 16));
        }
        roundStartButton.setBounds(LEVER_CENTER_X + LEVER_HEAD_SIZE / 2 + 20, BAR_BASE_Y - 30, 150, 100);
        roundStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRoundStartDialog();
            }
        });
        add(roundStartButton);
        setComponentZOrder(roundStartButton, 0);
        
        if (user.getRound_spin_left() <= 0) {
            roundStarted = false;
            leverButton.setEnabled(false);
            roundStartButton.setVisible(true);
        } else {
            roundStarted = true;
            leverButton.setEnabled(true);
            roundStartButton.setVisible(false);
        } 
        
        initializeStatusLabels();
        
        updateStatusBar();
        
        // 빛나는 효과 타이머 초기화
        initializeGlowEffect();

        gameThread = new Thread(this);
        gameThread.start();
        
        if (!roundStarted) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    showRoundStartDialog();
                }
            });
        }
    }
    
    /**
     * 룰렛 보드 초기화
     * 3x5 그리드의 슬롯을 생성하고 배치
     */
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
    
    /**
     * SymbolInfo를 사용하여 슬롯에 아이콘을 설정합니다.
     * 변형자가 있으면 문양+변형자 조합 이미지를 표시합니다.
     */
    private void setSymbolWithModifier(JLabel slot, Roulette.SymbolInfo symbolInfo) {
        if (symbolInfo == null) {
            return;
        }
        
        int[] symbolTypes = roulette.getSymbolTypes();
        int symbolType = symbolTypes[symbolInfo.getSymbolIndex()];
        String modifier = symbolInfo.getModifier();
        
        // 변형자가 있으면 문양+변형자 조합 이미지 사용, 없으면 일반 문양 이미지 사용
        slot.setIcon(new SymbolIcon(symbolType, modifier, SLOT_SIZE - 20));
    }
    
    private void initializeStatusLabels() {
        moneyLabel = new JLabel("금액: 0");
        interestLabel = new JLabel("이자: 0% (0)");
        ticketLabel = new JLabel("티켓: 0");
        deadlineLabel = new JLabel("기한: 0");
        roundLabel = new JLabel("라운드: 0");
        deadlineMoneyLabel = new JLabel("목표: 0");
        totalMoneyLabel = new JLabel("납입: 0");
        spinLeftLabel = new JLabel("남은 스핀: 0/0");
    }
    
    private void updateStatusBar() {
        int interestRatePct = (int)(user.getInterest() * 100);
        int calculatedInterestAmount = (int)(user.getTotal_money() * user.getInterest());
        int roundLeft = ROUNDS_PER_DEADLINE - user.getRound() + 1;
        if (roundLeft < 0) roundLeft = 0;

        moneyLabel.setText("금액: " + user.getRoulatte_money());
        interestLabel.setText("이자: " + interestRatePct + "% (" + calculatedInterestAmount + ")");
        ticketLabel.setText("티켓: " + user.getTicket());
        deadlineLabel.setText("기한: " + user.getDeadline());
        roundLabel.setText("라운드: " + roundLeft + "/" + ROUNDS_PER_DEADLINE);
        spinLeftLabel.setText("남은 스핀:" + user.getRound_spin_left() + "/" + SPINS_PER_ROUND);
        deadlineMoneyLabel.setText("목표: " + user.getDeadline_money());
        totalMoneyLabel.setText("납입: " + user.getTotal_money());
        updatePhoneButtonState();	//전화 버튼 활성화 여부확인 함수---------------------------------------------------------------------

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
                if ("종료".equals(label)) {
                    Main.exitGame();
                } else {
                    showNewFrame(frameTitle);
                }
            }
        });
        return button;
    }

   /* ------ 화면 전환용 코드 -------*/
 // ItemShop_Screen이 현재 활성화된 화면이라면 즉시 갱신을 요청하는 메서드
    public void updateShopScreen() {
        // 현재 표시 중인 화면이 ItemShop_Screen인지 확인
        if (this.currentPanel == null) {
            // itemShopLogic에 저장된 최신 목록(리롤된 목록)으로 UI 갱신 요청
            this.itemShopScreen.updateShopUI(this.itemShop.getCurrentItems()); 
            
            // 화면을 다시 그리도록 요청 (paintComponent 재호출)
            this.itemShopScreen.revalidate();
            this.itemShopScreen.repaint();
            System.out.println("SlotMachinePanel: 라운드 전환으로 상점 화면 즉시 갱신 완료.");
        }
    }
    
    /**
     * 전화 화면(Call_Screen)이 현재 열려 있다면 즉시 UI를 갱신합니다.
     * Payment 클래스에서 라운드 전환 시 호출됩니다.
     */
    public void updateCallScreen() {
        if (this.callScreen != null) {
            this.callScreen.updateUI(); 
            
            // 팝업 창이 열려있다면 즉시 화면을 다시 그리도록 요청합니다.
            this.callScreen.revalidate();
            this.callScreen.repaint();
            System.out.println("SlotMachinePanel: 라운드 전환으로 전화 화면 즉시 갱신 요청 완료.");
        }
    }
    
    /**--------------------------------------------------------------------------------------------------
     * 사용자 call_count에 따라 전화 버튼의 활성화/비활성화 상태를 업데이트합니다.
     */
    private void updatePhoneButtonState() {
        if (user != null && phoneButton != null) {
            boolean isEnabled = user.getCall_count();
            phoneButton.setEnabled(isEnabled);
            
            if (!isEnabled) {
                phoneButton.setToolTipText("남은 전화 기회가 없습니다.");
            } else {
                phoneButton.setToolTipText(null);
            }
        }
    }
    //------------------------------------------------------------------------------------------------------

    // 새로운 프레임 보여주는 함수
    private void showNewFrame(String title) {
        // 1. JFrame 기본 설정
        JFrame frame = new JFrame(title);
        
        // UI 컴포넌트(JPanel)를 담을 변수 선언
        JPanel contentPanel = null;
        int width = 400; // 기본 너비
        int height = 300; // 기본 높이

        // 2. 제목에 따라 적절한 UI 클래스 인스턴스화
        switch (title) {
            case "납입 버튼 화면":
                // "납입 버튼 화면"에 해당하는 Payment_Screen 인스턴스 생성
                contentPanel = this.paymentScreen; // User 객체와 상태바 업데이트 콜백 전달
                width = 800;
                height = 600;
             // ⭐ 3. (중요) 상점 패널을 열 때, ItemShopLogic에 저장된 최신 목록으로 UI를 갱신
                // ItemShop_Screen 내부에 updateShopUI(List<ItemInfo> items) 메서드가 있어야 합니다.
                if (this.itemShopScreen != null) {
                     this.itemShopScreen.updateShopUI(this.itemShop.getCurrentItems()); 
                }
                break;
                
            case "유물 상점 버튼 화면":
                // "유물 상점 버튼 화면"에 해당하는 RelicShop_Screen 인스턴스 생성
            	contentPanel = this.itemShopScreen;
            	width = 800; 
                height = 600;
             // ItemShop Logic에 저장된 최신 목록(리롤된 목록)으로 UI를 갱신해야 합니다.
                if (this.itemShopScreen != null) {
                     this.itemShopScreen.updateShopUI(this.itemShop.getCurrentItems()); 
                     System.out.println("SlotMachinePanel: 상점 화면 열면서 UI 갱신 요청 완료."); // 💡 디버깅 코드 추가
                }
                break;
                
                
            case "전화":
                // 전화기능에 해당하는 Phone_Screen 인스턴스 생성
                contentPanel = this.callScreen;
                width = 800;
                height = 600;
                break;
                
            case "소지 유물 버튼 화면":
                contentPanel = this.ownItemScreen;
                width = 800;
                height = 600;
                if (this.ownItemScreen != null) {
                    this.ownItemScreen.updateUI(); 
                    System.out.println("SlotMachinePanel: 소지 유물 화면 갱신 요청 성공."); // 디버그 출력
                }
                break;

            case "무늬 버튼 화면":
                contentPanel = this.symbolPriceScreen;
                width = 1200;
                height = 800;
                if (this.symbolPriceScreen != null) {
                    this.symbolPriceScreen.updatePriceInfo();
                    System.out.println("SlotMachinePanel: 무늬 가격 화면 열기 성공.");
                }
                break;
                
            case "패턴 버튼 화면":
                contentPanel = this.patternPriceScreen;
                width = 1200;
                height = 800;
                if (this.patternPriceScreen != null) {
                    this.patternPriceScreen.updatePriceInfo();
                    System.out.println("SlotMachinePanel: 패턴 가격 화면 열기 성공.");
                }
                break;
                


            // 다른 메뉴 버튼은 임시 패널을 사용
            default:
                contentPanel = createPlaceholderPanel(title);
                width = 400;
                height = 300;
                break;
        }

        
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null); // 화면 중앙에 표시
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        
        // 3. 생성된 패널을 프레임에 추가하고 크기 설정
        if (contentPanel != null) {
            frame.add(contentPanel);
        }

        

        frame.setVisible(true);
        
        
    }

    // 임시 패널 생성 메서드 (기존 코드를 재사용/분리) //테스트용 더미 창 생성용.
    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title + " UI가 여기에 표시됩니다.", SwingConstants.CENTER);
        
        Font font = new Font("Malgun Gothic", Font.BOLD, 16);
        if (!font.getFamily().equals("Malgun Gothic")) {
            font = new Font("Dotum", Font.BOLD, 16);
        }
        label.setFont(font);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    /*------------------------*/
    
    /**
     * 라운드 시작 다이얼로그 표시
     * 돌리기 횟수와 티켓 지급 수를 선택할 수 있음
     */
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
            JOptionPane.showMessageDialog(this, "라운드가 시작되었습니다!\n돌리기 횟수: 7회\n티켓 지급: 1개");
        } else if (choice == JOptionPane.NO_OPTION) {
            SPINS_PER_ROUND = 3;
            user.setRound_spin_left(3);
            user.addTicket(2);
            roundStarted = true;
            leverButton.setEnabled(true);
            roundStartButton.setVisible(false);
            updateStatusBar();
            JOptionPane.showMessageDialog(this, "라운드가 시작되었습니다!\n돌리기 횟수: 3회\n티켓 지급: 2개");
        }
    }
    
    /**
     * 스핀 버튼 클릭 처리
     * - 스핀 가능 여부만 체크하고
     * - "이번 스핀이 기한 마지막 스핀인지" 표시만 해 둔다.
     * - 실제 기한/탈락 여부는 finishSpin() → checkDeadlineAfterLastSpin()에서 처리한다.
     */
    private void handleSpinButtonClick() {
        if (isSpinning) return;
        
        if (!roundStarted) {
            JOptionPane.showMessageDialog(this, "먼저 라운드를 시작해주세요.");
            return;
        }
        System.out.println(user.getLemonProbability());
        System.out.println(user.getLemon_probability_multipBonus());
        System.out.println(user.getLemon_probability_sumBonus());

        
        lastSpinOfDeadline = false;
        
        if (!roundManager.consumeSpin()) {
            JOptionPane.showMessageDialog(this,
                    "이번 라운드의 기회을 모두 사용했습니다.");
            roundStarted = false;
            leverButton.setEnabled(false);
            roundStartButton.setVisible(true);
            updateStatusBar();
            return;
        }
        
        updateStatusBar();
        
        int spinsLeft    = user.getRound_spin_left();
        int currentRound = user.getRound();
        
        boolean isLastRoundOfDeadline = (currentRound == ROUNDS_PER_DEADLINE);
        boolean isLastSpinOfRound     = (spinsLeft == 0);
        
        lastSpinOfDeadline = (isLastRoundOfDeadline && isLastSpinOfRound);
        
        startSpin();
    }
    
    
    /**
     * 룰렛 스핀 시작
     * 레버 애니메이션 시작, 스핀 사운드 재생, 슬롯 랜덤 변경
     */
    /**
     * 스핀 전에 유물 효과를 적용합니다.
     * ItemEffect가 있는 유물들의 효과를 실행합니다.
     */
    private void applyArtifactEffectsBeforeSpin() {
        java.util.List<String> ownedArtifactNames = user.getUserItem_List();
        
        for (String itemName : ownedArtifactNames) {
            ItemInfo item = ItemInfo.getArtifactTemplateByName(itemName); 
            
            if (item != null) {
                ItemEffect effect = item.getRouletteEffect(); 
                
                if (effect != null) {
                    DurationType type = effect.getDuration();

                    if (type == DurationType.STACKABLE) {
                        continue; 
                    }

                    ArtifactAction action = effect.getAction();
                    action.execute(user); 
                    
                    // System.out.println("DEBUG: [" + itemName + "] 효과 실행. 타입: " + type);
                }
            }
                
            
        }
    }
    
    private void startSpin() {
        // 스핀 전에 유물 효과 적용
        applyArtifactEffectsBeforeSpin();
        
        // 스핀 시작 시 빛나는 효과 초기화
        glowingSlots.clear();
        glowAlpha = 0.0f;
        
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
        
        for (int i = 0; i < roulette.getRows(); i++) {
            for (int j = 0; j < roulette.getCols(); j++) {
                setRandomSymbol(slots[i][j]);
            }
        }
        spinCount++;
    }
    
    /**
     * 스핀 완료 처리
     * 최종 결과 생성, 패턴 체크, 사운드 중지
     */
    private void finishSpin() {
        // 변형자를 포함한 결과 생성
        Roulette.SymbolInfo[][] symbolResults = roulette.generateResultsWithModifiers();
        
        // 화면에 표시
        for (int i = 0; i < roulette.getRows(); i++) {
            for (int j = 0; j < roulette.getCols(); j++) {
                setSymbolWithModifier(slots[i][j], symbolResults[i][j]);
            }
        }
        
        // 패턴 체크를 위한 일반 결과 배열 생성 (원래 문양 인덱스 사용)
        int[][] results = new int[roulette.getRows()][roulette.getCols()];
        for (int i = 0; i < roulette.getRows(); i++) {
            for (int j = 0; j < roulette.getCols(); j++) {
                // 변형자가 있어도 원래 문양 인덱스를 사용하여 패턴 체크
                results[i][j] = symbolResults[i][j].getSymbolIndex();
            }
        }
        
        // 변형자 정보를 포함하여 패턴 체크 (변형자 효과 적용)
        Roulette.PatternResult patternResult = roulette.checkResults(results, symbolResults);
        
        // 패턴이 완성된 슬롯 위치 저장 (빛나는 효과용) - 모든 패턴의 위치 수집
        if (patternResult.hasWin()) {
            java.util.ArrayList<int[]> allPatternPositions = roulette.getAllDetectedPatternPositions(results);
            glowingSlots.clear();
            for (int[] pos : allPatternPositions) {
                glowingSlots.add(pos[0] + "," + pos[1]);
            }
        } else {
            glowingSlots.clear();
        }
        
        soundManager.stopSpinSound();
        isSpinning = false;
        user.setRoulatte_money(user.getRoulatte_money() + roulette.roulette_money);
        roulette.roulette_money = 0;
        
        // 스핀 완료 후 TEMPORARY 타입 효과 리셋
        user.resetTemporarySpinBonuses();
        
        // 총 스핀 횟수 증가
        user.setTotal_spin(user.getTotal_spin() + 1);
        
        // 상태 UI 갱신 (즉시 반영되도록 EDT에서 실행)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateStatusBar();
                repaint();
            }
        });
        
        // 동기적으로도 한 번 업데이트 (즉시 반영)
        updateStatusBar();
        repaint();
        
        // ❗ "이번 스핀 이후"에 라운드/기한/탈락 여부를 판정
        checkDeadlineAfterLastSpin();
    }
    
    /**
     * 스핀이 모두 끝난 뒤 호출되는 메서드.
     *
     * - lastSpinOfDeadline == false 인 경우:
     *   ▷ "기한 마지막 스핀"이 아니므로, 라운드 종료만 처리(onRoundFinished)하고 끝.
     *
     * - lastSpinOfDeadline == true 인 경우:
     *   ▷ "기한 마지막 라운드의 마지막 스핀"이므로
     *      · 남은 납입 필요 금액 계산
     *      · 보유 금액으로 채울 수 있으면 납입 기회 제공
     *      · 부족하거나, 납입 거부 시 탈락 처리
     */
    private void checkDeadlineAfterLastSpin() {
        // 이 스핀이 마지막 스핀이 아닌 경우
        if (!lastSpinOfDeadline) {
            if (user.getRound_spin_left() == 0 &&
                user.getRound() < ROUNDS_PER_DEADLINE) {
                onRoundFinished();
            }
            return; 
        }

        // 기한 내 마지막 스핀 시
        int target = user.getDeadline_money(); 
        int paid   = user.getTotal_money();
        int have   = user.getRoulatte_money();
        int remain = target - paid;

        // 이미 납입 완료했다면 다음 기한으로
        if (remain <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "기한 " + user.getDeadline() + "의 목표 금액을 이미 달성했습니다.\n" +
                    "다음 기한으로 진행합니다."
            );
            goNextDeadline();
            return;
        }

        // 보유 금액이 납입 불가 시 탈락
        if (have < remain) {
            JOptionPane.showMessageDialog(
                    this,
                    "보유 금액이 부족하여 목표 금액을 채울 수 없습니다.\n" +
                    "남은 납입 필요 금액 : " + remain + "\n" +
                    "현재 보유 금액     : " + have + "\n\n" +
                    "게임에서 탈락했습니다."
            );
            saveOnExit();
            Main.exitGameWithLose();
            return;
        }

        // 보유 금액으로 납입 가능 시 납입 기회
        int choice = JOptionPane.showConfirmDialog(
                this,
                "이번 기한의 남은 납입 필요 금액은 " + remain + "원입니다.\n" +
                "현재 보유 금액은 " + have + "원입니다.\n\n" +
                "지금 바로 납입하여 다음 기한으로 진행하시겠습니까?",
                "마지막 납입 기회",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            // 보유 금액에서 차감하고 납입 금액에 더함
            user.addRoulatte_money(-remain);
            user.addTotal_money(remain);
            updateStatusBar();

            JOptionPane.showMessageDialog(
                    this,
                    "납입을 완료했습니다!\n" +
                    "다음 기한으로 진행합니다."
            );
            goNextDeadline();
        } else {
            // 플레이어가 납입을 선택하지 않음 → 탈락 처리
            JOptionPane.showMessageDialog(
                    this,
                    "납입을 하지 않아 목표 금액을 달성하지 못했습니다.\n" +
                    "게임에서 탈락했습니다."
            );
            saveOnExit();
            Main.exitGameWithLose();
        }
    }
    
    /**
     * 다음 기한으로 진행하는 메서드
     */
    private void goNextDeadline() {
        user.setDeadline(user.getDeadline() + 1);
        user.setRound(1);
        roundStarted = false;
        leverButton.setEnabled(false);
        roundStartButton.setVisible(true);
        updateStatusBar();
    }
    
    /**
     * 라운드 종료 처리
     */
    private void onRoundFinished() {
        user.setTotal_spin(user.getTotal_spin() + 1);
        if (user.getRound() < ROUNDS_PER_DEADLINE) {
            user.setRound(user.getRound() + 1);
        }
        roundStarted = false;
        leverButton.setEnabled(false);
        roundStartButton.setVisible(true);
        updateStatusBar();
    }
    
    /**
     * 게임 종료 시 저장
     */
    public void saveOnExit() {
        if (saveManager != null && user != null) {
            saveManager.save(user);
        }
    }
    
    public User getUser() {
        return user;
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
        
        if (bufferImage == null) {
            bufferImage = new BufferedImage(getWidth(), TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            bufferG = bufferImage.getGraphics();
        }
        
        if (bufferImage.getWidth() != getWidth() || bufferImage.getHeight() != TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT) {
            bufferImage = new BufferedImage(getWidth(), TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            bufferG = bufferImage.getGraphics();
        }
        
        draw(bufferG);
        int statusBarY = TOTAL_HEIGHT - SOUTH_PANEL_HEIGHT - 40;
        g.drawImage(bufferImage, 0, 0, getWidth(), statusBarY, null);
        
        // 빛나는 효과 그리기 (스핀 중이 아닐 때만)
        if (!isSpinning && !glowingSlots.isEmpty()) {
            drawGlowEffect(g);
        }
        
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
        g2d.fillRect(rightPanelX, NORTH_PANEL_HEIGHT, EAST_PANEL_WIDTH, TOTAL_HEIGHT - NORTH_PANEL_HEIGHT - SOUTH_PANEL_HEIGHT);
        
        drawLeverBar(g2d); 
    }
    
    /**
     * 빛나는 효과 초기화
     */
    private void initializeGlowEffect() {
        glowTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSpinning && !glowingSlots.isEmpty()) {
                    // 알파 값 애니메이션 (0.3 ~ 0.8 사이를 왕복)
                    if (glowIncreasing) {
                        glowAlpha += 0.05f;
                        if (glowAlpha >= 0.8f) {
                            glowAlpha = 0.8f;
                            glowIncreasing = false;
                        }
                    } else {
                        glowAlpha -= 0.05f;
                        if (glowAlpha <= 0.3f) {
                            glowAlpha = 0.3f;
                            glowIncreasing = true;
                        }
                    }
                    repaint();
                }
            }
        });
        glowTimer.start();
    }
    
    /**
     * 패턴 완성 슬롯에 빛나는 효과 그리기
     */
    private void drawGlowEffect(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 알파 블렌딩 설정
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha));
        
        for (String slotKey : glowingSlots) {
            String[] parts = slotKey.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            
            // 슬롯 위치 계산
            int x = START_X + BOARD_PADDING + col * (SLOT_SIZE + SLOT_SPACING);
            int y = START_Y_SLOT + BOARD_PADDING + row * (SLOT_SIZE + SLOT_SPACING);
            
            // 빛나는 효과 (노란색/금색 글로우)
            g2d.setColor(new Color(255, 255, 0, (int)(255 * glowAlpha)));
            g2d.setStroke(new BasicStroke(4.0f));
            g2d.drawRect(x - 2, y - 2, SLOT_SIZE + 4, SLOT_SIZE + 4);
            
            // 더 밝은 외곽 효과
            g2d.setColor(new Color(255, 215, 0, (int)(180 * glowAlpha)));
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawRect(x - 4, y - 4, SLOT_SIZE + 8, SLOT_SIZE + 8);
        }
        
        g2d.dispose();
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
            
            setFont(new Font("Malgun Gothic", Font.BOLD, 16));
            if (getFont().getFamily().equals("Malgun Gothic") == false) {
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