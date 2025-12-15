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
    private static final Rectangle CLICK_AREA = new Rectangle(570, 270, 50, 50);
    
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
        Color paymentBGColor = new Color(220, 16, 183); // 납입 기계 이미지의 배경색
        Color bonusBGColor = new Color(220, 15, 180);   // 보너스 기계 이미지의 배경색

        paymentMachineImage = loadAndTransparentImage(
            "res/payment_machine.png", 
            paymentBGColor
        ); 

        bonusMachineImage = loadAndTransparentImage(
            "res/bonus_machine.png", 
            bonusBGColor
        ); 
        depositImage = loadImage("res/deposit_button.png");

    }
    
    private BufferedImage makeColorTransparent(Image sourceImage, Color targetColor) {
        if (sourceImage == null) return null;

        BufferedImage image = new BufferedImage(
            sourceImage.getWidth(null), 
            sourceImage.getHeight(null), 
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics g = image.getGraphics();
        g.drawImage(sourceImage, 0, 0, null);
        g.dispose();

        int targetRGB = targetColor.getRGB();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);

                if (pixel == targetRGB) {
                    image.setRGB(x, y, 0x00000000); 
                }
            }
        }
        return image;
    }

    private Image loadAndTransparentImage(String path, Color targetColor) {
        Image originalImage = loadImage(path); // 기존 loadImage 메서드 사용
        if (originalImage == null) return null;
        
        return makeColorTransparent(originalImage, targetColor);
    }
    
    public Payment_Screen(Payment paymentLogic) {
        this.paymentLogic = paymentLogic;	//의존성 주입용
        setOpaque(false);
        
        loadBackgroundImage("res/back_ground.png");     
        loadScreenImages();

        setLayout(null);
        
        //마감기한 보너스 결과값 가져오기 위해 함수 호출
        Payment.get_deadline_bonus result = this.paymentLogic.deadline_bonus_count();
        
        // 마감기한 보너스 코인 영역 설정
        deadline_bonus_coin_lable = new JLabel(" 코인 : " + result.deadline_bonus_coin() + "원");
        deadline_bonus_coin_lable.setBounds(95, 275, 300, 30); // 좌표 지정
        deadline_bonus_coin_lable.setForeground(Color.WHITE);
        deadline_bonus_coin_lable.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(deadline_bonus_coin_lable);
        
        // 마감기한 보너스 티켓 영역 설정
        deadline_bonus_tiket_lable = new JLabel("티켓 : " + result.deadline_bonus_tiket() + "개");
        deadline_bonus_tiket_lable.setBounds(95, 315, 300, 30); // 좌표 지정
        deadline_bonus_tiket_lable.setForeground(Color.WHITE);
        deadline_bonus_tiket_lable.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(deadline_bonus_tiket_lable);
        
        // 현재 라운드 목표 금액
        get_round_money_lable = new JLabel("목표 금액: " + paymentLogic.get_deadline_money() + "원");
        get_round_money_lable.setBounds(425, 200, 300, 30); // 좌표 지정
        get_round_money_lable.setForeground(Color.WHITE);
        get_round_money_lable.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(get_round_money_lable);
        
        // 총 납입액 영역 설정
        total_deposit_label = new JLabel("총 납입액: " + paymentLogic.get_total_money() + "원");
        total_deposit_label.setBounds(425, 240, 300, 30); // 좌표 지정
        total_deposit_label.setForeground(Color.WHITE);
        total_deposit_label.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(total_deposit_label);
        
        // 이자 값 출력 영역 설정 ()
        paymentLogic.interest_count();
        interestLabel = new JLabel("계산된 이자:" + paymentLogic.get_interest() + "원");	//초기값
        interestLabel.setBounds(425, 280, 300, 30); // 총액 아래에 배치
        interestLabel.setForeground(Color.YELLOW);
        interestLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(interestLabel);
        paymentLogic.get_total_money(); // 초기 총액 표시
        addMouseListener(new ScreenClickListener()); 
        setPreferredSize(new Dimension(800, 600));	//기본 화면비
    }
    
    public void updatePaymentUI() {
        // 이자 업데이트
        int interest = paymentLogic.interest_count();    
        interestLabel.setText("계산된 이자: " + interest + "원");
        
        // 총 납입액 업데이트
        int total_deposit = paymentLogic.get_total_money(); 
        total_deposit_label.setText("총 납입액: " + total_deposit + "원");
        
        // 목표 금액 업데이트
        get_round_money_lable.setText("목표 금액: " + paymentLogic.get_deadline_money() + "원");
        
        revalidate();
        repaint();
    }
    
    /**------------배경 이미지 그리기 (JPanel의 paintComponent 오버라이드)---------*/
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
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        if (bonusMachineImage != null) {
            int x = 50;    // 시작 X 좌표
            int y = 200;    // 시작 Y 좌표 
            int width = 250;  // 너비
            int height = 350; // 높이
            g.drawImage(bonusMachineImage, x, y, width, height, this);
        }
        
        if (paymentMachineImage != null) {
            int x = 350;    // 시작 X 좌표
            int y = 150;    // 시작 Y 좌표 
            int width = 300;  // 너비 
            int height = 400; // 높이
            g.drawImage(paymentMachineImage, x, y, width, height, this);
        }
        if (depositImage != null) {
            int x = 570;    // 시작 X 좌표 
            int y = 270;    // 시작 Y 좌표
            int width = 50;  // 너비 
            int height = 50; // 높이
            g.drawImage(depositImage, x, y, width, height, this);
        }
    }
    
    
    /**------------ 마우스 클릭 리스너 클래스----------------*/
    private class ScreenClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point clickedPoint = e.getPoint();
            // 버튼 클릭시
            if (CLICK_AREA.contains(clickedPoint)) {
                if (paymentLogic.processPayment()) {
                	updatePaymentUI();

                } else {
                    // 납입 실패시  메시지 표시
                    JOptionPane.showMessageDialog(null, "납입 불가: 잔액이 부족하거나 목표액을 달성했습니다.");
                }
            } else {
                System.out.println("빈 영역 클릭: " + clickedPoint);//디버깅용
            }
            revalidate();
            repaint();
        }
    }
	@Override
	public void actionPerformed(ActionEvent e) {
	}
}

