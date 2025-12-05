import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RouletteUI extends JFrame {
    private Roulette roulette;
    private JLabel[][] slots;
    private JButton spinButton;
    private JLabel resultLabel;
    private boolean isSpinning = false;
    private Timer spinTimer;
    private int spinCount = 0;
    private static final int MAX_SPIN_COUNT = 30;
    
    public RouletteUI() {
        roulette = new Roulette();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("3x5 랜덤 룰렛");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(200, 200, 200, 200));
        mainPanel.setBackground(new Color(102, 126, 234));
        
        // 제목
        JLabel titleLabel = new JLabel("3x5 랜덤 룰렛");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // 룰렛 보드
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(roulette.getRows(), roulette.getCols(), 10, 10));
        boardPanel.setBackground(new Color(195, 207, 226));
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(102, 126, 234), 5),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
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
                slots[i][j].setPreferredSize(new Dimension(80, 80));
                setRandomSymbol(slots[i][j]);
                boardPanel.add(slots[i][j]);
            }
        }
        
        mainPanel.add(boardPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // 스핀 버튼
        spinButton = new JButton("돌리기");
        spinButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        spinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        spinButton.setPreferredSize(new Dimension(200, 50));
        spinButton.setBackground(new Color(118, 75, 162));
        spinButton.setForeground(Color.WHITE);
        spinButton.setFocusPainted(false);
        spinButton.setBorderPainted(false);
        spinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSpinning) {
                    startSpin();
                }
            }
        });
        mainPanel.add(spinButton);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 결과 레이블
        resultLabel = new JLabel("게임을 시작하세요!");
        resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setForeground(Color.WHITE);
        mainPanel.add(resultLabel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void setRandomSymbol(JLabel slot) {
        roulette.setLemonProbability(80.0);
        int symbolIndex = roulette.generateRandomSymbol();
        int[] symbolTypes = roulette.getSymbolTypes();
        slot.setIcon(new SymbolIcon(symbolTypes[symbolIndex], 70));
    }
    
    private void startSpin() {
        isSpinning = true;
        spinButton.setEnabled(false);
        resultLabel.setText("돌리는 중...");
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
                slots[i][j].setIcon(new SymbolIcon(symbolTypes[results[i][j]], 70));
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
        spinButton.setEnabled(true);
    }
    
    public static void main(String[] args) {
        // Look and Feel 설정
        try {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RouletteUI().setVisible(true);
            }
        });
    }
}

