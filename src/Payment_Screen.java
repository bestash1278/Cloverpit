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
import java.awt.image.BufferedImage;
import java.io.IOException;


//납입 ui
public class Payment_Screen extends JPanel implements ActionListener {
    private final Payment paymentLogic;	//Payment에서 값을 받기 위한 생성자 변수 선언.
    
    private Image backgroundImage;	//배경화면 이미지 변수 선언.
    // 클릭 영역 좌표 (예시: 화면의 특정 버튼 위치)
    private static final Rectangle CLICK_AREA = new Rectangle(600, 220, 50, 50);
    
    private JLabel deadline_bonus_coin_lable;
    private JLabel deadline_bonus_tiket_lable;
    private JLabel get_round_money_lable;	// 이번 라운드에 납입해야하는 총 금액
    private JLabel total_deposit_label; // 화면에 총액을 출력할 컴포넌트
    private JLabel interestLabel;
    private Image paymentMachineImage; 
    private Image bonusMachineImage;
    private Image depositImage;
    
    
    private Image loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("이미지 불러오기 실패: " + path);
            return null;
        }
    }

    private void loadScreenImages() {
        // ⭐ 원하는 배경색의 RGB 값을 설정합니다. (단일 색상이어야 합니다.)
        Color paymentBGColor = new Color(220, 16, 183); // 납입 기계 이미지의 배경색
        Color bonusBGColor = new Color(220, 15, 180);   // 보너스 기계 이미지의 배경색

        // 1. 납입 기계 이미지 로드 및 배경색 투명 처리
        paymentMachineImage = loadAndTransparentImage(
            "res/payment_machine.png", 
            paymentBGColor
        ); 

        // 2. 보너스 기계 이미지 로드 및 배경색 투명 처리
        bonusMachineImage = loadAndTransparentImage(
            "res/bonus_machine.png", 
            bonusBGColor
        ); 
        depositImage = loadImage("res/deposit_button.png");

    }
    
    /**
     * 이미지에서 특정 RGB 값을 투명하게 처리합니다. (Color Keying)
     * @param sourceImage 배경색이 포함된 원본 Image 객체
     * @param targetColor 투명하게 만들고자 하는 배경색 (java.awt.Color 객체)
     * @return 배경이 투명하게 처리된 BufferedImage 객체
     */
    private BufferedImage makeColorTransparent(Image sourceImage, Color targetColor) {
        if (sourceImage == null) return null;

        // 1. Image 객체를 BufferedImage로 변환
        // ImageIO.read()로 로드된 Image는 일반적으로 BufferedImage이지만, 안전을 위해 변환 과정을 거칩니다.
        BufferedImage image = new BufferedImage(
            sourceImage.getWidth(null), 
            sourceImage.getHeight(null), 
            BufferedImage.TYPE_INT_ARGB // 알파 채널(투명도)을 지원하는 타입으로 설정
        );
        Graphics g = image.getGraphics();
        g.drawImage(sourceImage, 0, 0, null);
        g.dispose();

        // 2. 투명화 작업 수행
        int targetRGB = targetColor.getRGB();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);

                // 해당 픽셀의 색상이 목표 색상과 일치하는지 확인
                if (pixel == targetRGB) {
                    // 일치하면 투명 픽셀로 설정 (알파 채널 0)
                    image.setRGB(x, y, 0x00000000); // 0xAARRGGBB, AA=00 (투명)
                }
            }
        }
        return image;
    }

    // ⭐ 헬퍼 메서드: 이미지를 로드하고 바로 투명 처리까지 수행하는 메서드
    private Image loadAndTransparentImage(String path, Color targetColor) {
        Image originalImage = loadImage(path); // 기존 loadImage 메서드 사용
        if (originalImage == null) return null;
        
        return makeColorTransparent(originalImage, targetColor);
    }
    
    
    // 1. 생성자에서 Payment 객체를 주입받아 필드에 저장 (의존성 유지)
    public Payment_Screen(Payment paymentLogic) {
        this.paymentLogic = paymentLogic;	//의존성 주입용
        setOpaque(false);
        
        loadBackgroundImage("res/back_ground.png");     
        loadScreenImages();
        setLayout(null);        // 3. 레이아웃 설정 (null 레이아웃을 사용해 절대 좌표로 컴포넌트 배치)
        
        //마감기한 보너스 결과값 가져오기 위해 함수 호출
        Payment.get_deadline_bonus result = this.paymentLogic.deadline_bonus_count();
        
        // 마감기한 보너스 코인 영역 설정
        deadline_bonus_coin_lable = new JLabel(" 코인 : " + result.deadline_bonus_coin() + "원");
        deadline_bonus_coin_lable.setBounds(50, 100, 300, 30); // 좌표 지정
        deadline_bonus_coin_lable.setForeground(Color.WHITE);
        deadline_bonus_coin_lable.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(deadline_bonus_coin_lable);
        
        // 마감기한 보너스 티켓 영역 설정
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
        paymentLogic.interest_count();
        interestLabel = new JLabel("계산된 이자:" + paymentLogic.get_interest() + "원");	//초기값
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
    
    public void updateLocalUI() {
        // 💡 Payment Logic 객체를 통해 최신 정보를 가져옵니다.
        
        // 이자 업데이트
        int interest = paymentLogic.interest_count();    
        interestLabel.setText("계산된 이자: " + interest + "원");
        
        // 총 납입액 업데이트
        int total_deposit = paymentLogic.get_total_money(); 
        total_deposit_label.setText("총 납입액: " + total_deposit + "원");
        
        // 목표 금액 업데이트
        get_round_money_lable.setText("목표 금액: " + paymentLogic.get_deadline_money() + "원");
        
        // 변경된 내용을 즉시 반영하도록 요청
        revalidate();
        repaint();
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
        
        if (paymentMachineImage != null) {
            int x = 100;    // 시작 X 좌표 (조정 필요)
            int y = 150;    // 시작 Y 좌표 (조정 필요)
            int width = 250;  // 너비 (조정 필요)
            int height = 350; // 높이 (조정 필요)
            g.drawImage(paymentMachineImage, x, y, width, height, this);
        }
        
        // ⭐ 3. 보너스 기계 이미지 그리기
        if (bonusMachineImage != null) {
            int x = 450;    // 시작 X 좌표 (조정 필요)
            int y = 150;    // 시작 Y 좌표 (조정 필요)
            int width = 300;  // 너비 (조정 필요)
            int height = 350; // 높이 (조정 필요)
            g.drawImage(bonusMachineImage, x, y, width, height, this);
        }
        if (depositImage != null) {
            int x = 600;    // 시작 X 좌표 (조정 필요)
            int y = 220;    // 시작 Y 좌표 (조정 필요)
            int width = 50;  // 너비 (조정 필요)
            int height = 50; // 높이 (조정 필요)
            g.drawImage(bonusMachineImage, x, y, width, height, this);
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
                // 🚨 수정된 메서드를 호출하고 성공 여부를 바로 확인
                if (paymentLogic.processPayment()) {
                	updateLocalUI();

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

