import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Call_Screen extends JPanel {
    private final Call callLogic; // 전화 로직 객체
    private JLabel chanceLabel;   // 남은 기회 표시 레이블
    private JButton[] abilityButtons = new JButton[3]; // 3개의 능력 선택 버튼
    private JButton rerollButton; // 리롤 버튼 필드 추가
    private Image backgroundImage; //배경 객체 생성

    // 생성자
    public Call_Screen(Call callLogic) {
        this.callLogic = callLogic;
        
        try {

            java.net.URL imgUrl = getClass().getResource("/background_call.png");
            if (imgUrl != null) {
                backgroundImage = javax.imageio.ImageIO.read(imgUrl);
            } else {
                System.err.println("이미지를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setLayout(new BorderLayout());
        initializeUI();
        updateUI(); 
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        
        chanceLabel = new JLabel("남은 전화 기회: " + callLogic.getCall_count() + "회", SwingConstants.CENTER);

        chanceLabel.setForeground(Color.WHITE); 
        chanceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(chanceLabel, BorderLayout.NORTH);
        //창 설정
        JPanel centerContainer = new JPanel(new GridBagLayout()); 
        centerContainer.setOpaque(false);
        JPanel selectionPanel = new JPanel(new GridLayout(1, 3, 25, 0));	//1열 3개씩 25간격으로
        selectionPanel.setOpaque(false);
        selectionPanel.setPreferredSize(new Dimension(700, 400)); //선택지창 크기 지정
        selectionPanel.setMaximumSize(new Dimension(700, 500));	//선택지창 최대 넓이 지정
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // 상하 패딩을 줄임
        
        for (int i = 0; i < abilityButtons.length; i++) {
            final int index = i;
            JButton button = new JButton("능력 " + (i + 1));
           
            button.addActionListener(e -> {
                handleAbilitySelection(index);
            });
            abilityButtons[i] = button;
            selectionPanel.add(button);
            
        }
        
        centerContainer.add(selectionPanel, new GridBagConstraints()); 
        add(centerContainer, BorderLayout.CENTER); 
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setOpaque(false);
        rerollButton = new JButton("리롤 ("+ callLogic.getCallReroll_cost()+"티켓 사용)"); 
        rerollButton.addActionListener(e -> {
            handleRerollAction();
        });
        updateRerollButtonText();
        southPanel.add(rerollButton);
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        add(southPanel, BorderLayout.SOUTH);

    }
    
    public void updateRerollButtonText() {
        int cost = callLogic.getCallReroll_cost();
        rerollButton.setText("리롤");
    }
    
    private void handleRerollAction() {
        if (callLogic.getTicket() > callLogic.getCallReroll_cost()) {
            callLogic.useCallForReroll(); 
            updateUI(); 
            
            JOptionPane.showMessageDialog(this, "능력을 리롤했습니다! 남은 티켓 수: " + callLogic.getTicket() + "개" + "\n다음 리롤 비용 : "+ callLogic.getCallReroll_cost());
        } else {
            JOptionPane.showMessageDialog(this, "리롤을 사용할 티켓이 남아있지 않습니다.", "사용 불가", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // 능력 선택 처리 함수 (버튼 클릭 시 호출)
    private void handleAbilitySelection(int index) {
        if (callLogic.useCall(index)) {
        	JOptionPane.showMessageDialog(this, 
                    "능력 [" + callLogic.getCurrentSelections().get(index).getName() + "]을 사용했습니다!");
        	updateUI();
        }
        else {
            JOptionPane.showMessageDialog(this, "전화를 사용할 기회가 남아있지 않습니다.", "사용 불가", JOptionPane.WARNING_MESSAGE);
        }
    }

    //화면 UI 갱신 함수 (핵심)
    public void updateUI() {
    	if (callLogic == null) return; 
    	boolean chances = callLogic.getCall_count();
    	chanceLabel.setText("전화 가능 여부 : " + (chances ? "가능" : "불가능"));
        
        List<CallInfo> selections = callLogic.getCurrentSelections();
        for (int i = 0; i < abilityButtons.length; i++) {
            if (i < selections.size()) {
                CallInfo info = selections.get(i);
                // 버튼 텍스트를 능력 이름과 설명으로 설정
                abilityButtons[i].setText("<html><center>" + info.getName() + "<br><font size='3'>" + info.getDescription() + "</font></center></html>");
                abilityButtons[i].setEnabled(chances);
                }
            else {
                abilityButtons[i].setText("선택지 없음");
                abilityButtons[i].setEnabled(false);
            }
        }
     //리롤 버튼 활성화/비활성화 (기회가 있어야 리롤 가능)
        rerollButton.setEnabled(chances);
        updateRerollButtonText();
        revalidate();
        repaint();
    }
}

