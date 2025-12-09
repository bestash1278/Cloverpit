import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Call_Screen extends JPanel {
    private final Call callLogic; // 전화 로직 객체
    private JLabel chanceLabel;   // 남은 기회 표시 레이블
    private JButton[] abilityButtons = new JButton[3]; // 3개의 능력 선택 버튼
    private JButton rerollButton; // 리롤 버튼 필드 추가

    // 생성자
    public Call_Screen(Call callLogic) {
        this.callLogic = callLogic;
        setLayout(new BorderLayout()); // 전체 레이아웃

        initializeUI();
        updateUI(); // 초기 UI 갱신
    }

 // Call_Screen.java (initializeUI 메서드 수정)

    private void initializeUI() {
        setLayout(new BorderLayout()); // 전체 레이아웃 (유지)

        // --- 1. 상단: 남은 기회 표시 (BorderLayout.NORTH 유지) ---
        chanceLabel = new JLabel("남은 전화 기회: " + callLogic.getCall_count() + "회", SwingConstants.CENTER);
        add(chanceLabel, BorderLayout.NORTH); 

        // --- 2. 중앙 컨테이너 설정 ---
        // 전체 중앙 영역을 차지할 컨테이너 (이 컨테이너의 CENTER에 버튼을 배치할 것)
        JPanel centerContainer = new JPanel(new GridBagLayout()); // ⭐ GridBagLayout으로 변경하여 내부 요소 중앙 정렬 및 크기 제어 용이

        // 능력 선택 패널: 1행 3열 (버튼 자체의 크기를 정의)(25의 간격)
        JPanel selectionPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        
        // ⭐ ⭐ 핵심: 버튼 패널의 선호 높이를 명시적으로 설정하여 세로 공간을 제한 ⭐ ⭐
        // 버튼의 선호 높이와 폭을 지정하는 것이 정확합니다. (예: 높이 100 픽셀)
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
        
        // ⭐ ⭐ centerContainer의 중앙에 selectionPanel 배치 ⭐ ⭐
        // GridBagLayout을 사용하면 centerContainer의 중앙에 selectionPanel이 배치됩니다.
        centerContainer.add(selectionPanel, new GridBagConstraints()); 
        
        add(centerContainer, BorderLayout.CENTER); // 메인 패널의 중앙에 컨테이너 추가
        
        // --- 3. 하단: 리롤 버튼 (BorderLayout.SOUTH 유지) ---
        // ... (기존 하단 southPanel 로직 유지) ...
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rerollButton = new JButton("리롤 ("+ callLogic.getCallReroll_count()+"티켓 사용)"); 
        rerollButton.addActionListener(e -> {
            handleRerollAction();
        });
        southPanel.add(rerollButton);
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        add(southPanel, BorderLayout.SOUTH);

    }
    
 // ⭐ 리롤 버튼 동작 처리 함수 (핵심)
    private void handleRerollAction() {
        if (callLogic.getCall_count() > 0) {
            // 1. Call 로직에서 기회 차감 및 능력 리롤 실행
            callLogic.useCallForReroll(); 
            
            // 2. UI 갱신 (남은 기회 및 능력 목록 갱신)
            updateUI(); 
            
            JOptionPane.showMessageDialog(this, "능력을 리롤했습니다! 남은 티켓 수: " + callLogic.getTicket() + "개");
        } else {
            JOptionPane.showMessageDialog(this, "리롤을 사용할 티켓이 남아있지 않습니다.", "사용 불가", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // ⭐ 능력 선택 처리 함수 (버튼 클릭 시 호출)
    private void handleAbilitySelection(int index) {
        if (callLogic.useCall(index)) {
            JOptionPane.showMessageDialog(this, "능력 [" + callLogic.getCurrentSelections().get(index).getName() + "]을 사용했습니다!");
            updateUI(); // 사용 후 UI 갱신 (남은 기회, 새로운 선택지)
        } else {
            JOptionPane.showMessageDialog(this, "전화를 사용할 기회가 남아있지 않습니다.", "사용 불가", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ⭐ 화면 UI 갱신 함수 (핵심)
    public void updateUI() {
    	if (callLogic == null) return; 
    	int chances = callLogic.getCall_count();
        chanceLabel.setText("남은 전화 기회: " + chances + "회");
        
        List<CallInfo> selections = callLogic.getCurrentSelections();
        for (int i = 0; i < abilityButtons.length; i++) {
            if (i < selections.size()) {
                CallInfo info = selections.get(i);
                // 버튼 텍스트를 능력 이름과 설명으로 설정
                abilityButtons[i].setText("<html><center>" + info.getName() + "<br><font size='3'>" + info.getDescription() + "</font></center></html>");
                abilityButtons[i].setEnabled(chances > 0);
            } else {
                abilityButtons[i].setText("선택지 없음");
                abilityButtons[i].setEnabled(false);
            }
        }
     // ⭐ 리롤 버튼 활성화/비활성화 (기회가 있어야 리롤 가능)
        rerollButton.setEnabled(chances > 0);
        revalidate();
        repaint();
    }
}