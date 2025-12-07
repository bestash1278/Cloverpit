import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

//납입 ui
public class Payment_Screen extends JPanel implements ActionListener {
    private final Payment paymentLogic;	//Payment에서 값을 받기 위한 생성자 변수 선언.
    private Runnable updateMainStatus; // 상태바 업데이트를 위한 Runnable 인터페이스
    
    private Image backgroundImage;	//배경화면 이미지 변수 선언.
    // 클릭 영역 좌표 (예시: 화면의 특정 버튼 위치)// TODO : 특정 위치를 클릭가능한 공간으로 만드는 걸로 생각했는데. 만약 동적으로 게임창 크기가 변할때마다 위치 지정해야한다면 동적 계산 코드 만들어야함.
    private static final Rectangle CLICK_AREA = new Rectangle(600, 220, 50, 50);
    
    private JLabel deadline_bonus_coin_lable;
    private JLabel deadline_bonus_tiket_lable;
    private JLabel get_round_money_lable;	// 이번 라운드에 납입해야하는 총 금액
    private JLabel total_deposit_label; // 화면에 총액을 출력할 컴포넌트
    private JLabel interestLabel;
    
    
    // 1. 생성자에서 Payment 객체를 주입받아 필드에 저장 (의존성 유지)
    public Payment_Screen(Payment paymentLogic, Runnable updateMainStatus) {
        this.paymentLogic = paymentLogic;	//의존성 주입용
        this.updateMainStatus = updateMainStatus;
        
        // ... UI 구성 ...(임시)
        
        loadBackgroundImage("res/back_ground.png");     // 2. 배경 이미지 로드
        setLayout(null);        // 3. 레이아웃 설정 (null 레이아웃을 사용해 절대 좌표로 컴포넌트 배치)//원하는데로 셋팅하는거
        
        //마감기한 보너스 결과값 가져오기 위해 함수 호출
        Payment.get_deadline_bonus result = this.paymentLogic.deadline_bonus_count();
        
        // 마감기한 보너스 코인 영역 설정
        //임시 deadline_bonus_coin_lable = new JLabel(" 코인 : " + roundInfo.get_deadline_bonus_coin() + "원");
        deadline_bonus_coin_lable = new JLabel(" 코인 : " + result.deadline_bonus_coin() + "원");
        deadline_bonus_coin_lable.setBounds(50, 100, 300, 30); // 좌표 지정
        deadline_bonus_coin_lable.setForeground(Color.WHITE);
        deadline_bonus_coin_lable.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(deadline_bonus_coin_lable);
        
        // 마감기한 보너스 티켓 영역 설정
        //임시 deadline_bonus_tiket_lable = new JLabel("티켓 : " + roundInfo.get_deadline_bonus_tiket() + "개");
        deadline_bonus_tiket_lable = new JLabel("티켓 : " + result.deadline_bonus_tiket() + "개");
        deadline_bonus_tiket_lable.setBounds(50, 140, 300, 30); // 좌표 지정
        deadline_bonus_tiket_lable.setForeground(Color.WHITE);
        deadline_bonus_tiket_lable.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(deadline_bonus_tiket_lable);
        
        // 현재 라운드 목표 금액
        get_round_money_lable = new JLabel("목표 금액: " + paymentLogic.get_deadline_money() + "원");
        get_round_money_lable.setBounds(450, 100, 300, 30); // 좌표 지정
        get_round_money_lable.setForeground(Color.WHITE);
        get_round_money_lable.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(get_round_money_lable);
        
        // 총 납입액 영역 설정
        total_deposit_label = new JLabel("총 납입액: " + paymentLogic.get_total_money() + "원");
        total_deposit_label.setBounds(450, 140, 300, 30); // 좌표 지정
        total_deposit_label.setForeground(Color.WHITE);
        total_deposit_label.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(total_deposit_label);
        
        // 이자 값 출력 영역 설정 ()
        interestLabel = new JLabel("계산된 이자: 0원");	//초기값
        interestLabel.setBounds(450, 180, 300, 30); // 총액 아래에 배치
        interestLabel.setForeground(Color.YELLOW);
        interestLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(interestLabel);
        paymentLogic.get_total_money(); // 초기 총액 표시

        // 5. 클릭 이벤트 리스너 연결 (MouseAdapter 사용)
        addMouseListener(new ScreenClickListener()); 
        
        // 6. 패널 크기 설정 (이것은 MainFrame의 CardLayout에 맞게 조정될 것입니다)
        setPreferredSize(new Dimension(800, 600));
    }
    
    
    /**------------배경 이미지 그리기 (JPanel의 paintComponent 오버라이드)---------*/
    // PNG 이미지 로드 함수
    private void loadBackgroundImage(String path) {
        try {
            backgroundImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("배경 이미지 로드 실패: " + path);
            e.printStackTrace();
        }
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // 패널 크기에 맞게 이미지 그리기
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        // 디버깅 용: 클릭 영역을 시각적으로 표시 (나중에 제거)
        g.setColor(new Color(255, 0, 0, 100)); // 투명한 빨간색
        g.fillRect(CLICK_AREA.x, CLICK_AREA.y, CLICK_AREA.width, CLICK_AREA.height);
    }
    
    
    /**------------ 마우스 클릭 리스너 클래스 (내부 클래스로 구현)----------------*/
    private class ScreenClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
        	// 클릭된 좌표 (e.getX(), e.getY())
            Point clickedPoint = e.getPoint();

            // 버튼 클릭시
            if (CLICK_AREA.contains(clickedPoint)) {
                // boolean chack = paymentLogic.get_chack(); // 📌 이 라인 제거

                // if(chack) { // 📌 이 조건문 제거
                    
                    // 🚨 수정된 메서드를 호출하고 성공 여부를 바로 확인
                    if (paymentLogic.processPayment()) {
                        
                        int interest = paymentLogic.interest_count();    // 이자 업데이트
                        interestLabel.setText("계산된 이자: " + interest + "원");
                        
                        int total_deposit = paymentLogic.get_total_money(); // 총 납입액 업데이트
                        total_deposit_label.setText("총 납입액: " + total_deposit + "원");
                        
                        if (updateMainStatus != null) {
                            updateMainStatus.run(); // SlotMachinePanel의 updateStatusBar()가 실행됨
                        }
                        
                        get_round_money_lable.setText("목표 금액: " + paymentLogic.get_deadline_money() + "원");
                        
                    } else {
                        // 납입 실패 (잔액 부족, 이미 목표 달성 등) 시 사용자에게 메시지 표시
                        JOptionPane.showMessageDialog(null, "납입 불가: 잔액이 부족하거나 목표액을 달성했습니다.");
                    }
            } else {
                // 다른 영역 클릭 시 처리
                System.out.println("빈 영역 클릭: " + clickedPoint);
            }
            
         // 변경된 내용을 즉시 반영하도록 요청 (Swing 컴포넌트의 필수 과정)
            revalidate();
            repaint();
        }
    }
    



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}