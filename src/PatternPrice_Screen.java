import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 패턴 가격 표시 화면
 * User 객체로부터 패턴의 현재 가격을 표시합니다.
 */
public class PatternPrice_Screen extends JPanel {
    private final User user;
    private Image backgroundImage;
    
    // 패턴 이름 배열
    private static final String[] PATTERN_NAMES = {
        "트리플", "쿼드라", "펜타", "세로", "대각선", 
        "지그", "재그", "지상", "천상", "눈", "잭팟"
    };
    
    // 패턴 이미지 경로 배열
    private static final String[] PATTERN_IMAGE_PATHS = {
        "res/triple.png",
        "res/quadra.png",
        "res/penta.png",
        "res/vertical.png",
        "res/diagonal.png",
        "res/zig.png",
        "res/zag.png",
        "res/ground.png",
        "res/heaven.png",
        "res/eye.png",
        "res/jakpot.png"  // 파일명이 jakpot으로 되어 있음
    };
    
    // 가격 정보를 표시할 라벨들
    private JLabel[] patternImageLabels;
    private JLabel[] patternNameLabels;
    private JLabel[] currentPriceLabels;
    
    public PatternPrice_Screen(User user) {
        this.user = user;
        
        loadBackgroundImage("/back_ground.png");
        setLayout(null);
        
        setupPriceLabels();
        setPreferredSize(new Dimension(1200, 800));
    }
    
    /**
     * 가격 정보 라벨들을 초기화하고 배치합니다.
     */
    private void setupPriceLabels() {
        patternImageLabels = new JLabel[PATTERN_NAMES.length];
        patternNameLabels = new JLabel[PATTERN_NAMES.length];
        currentPriceLabels = new JLabel[PATTERN_NAMES.length];
        
        int panelWidth = 1200; // 패널 너비 (무늬 화면과 동일하게)
        int startY = 150;
        int spacing = 50;
        int imageSize = 60;
        int nameWidth = 150;
        int priceWidth = 200;
        int gap1 = 30; // 이미지와 이름 사이 간격
        int gap2 = 40; // 이름과 가격 사이 간격
        
        // 전체 컨텐츠 너비 계산
        int totalContentWidth = imageSize + gap1 + nameWidth + gap2 + priceWidth;
        int leftMargin = (panelWidth - totalContentWidth) / 2; // 가운데 정렬을 위한 왼쪽 마진
        
        // 제목 라벨 (완전히 가운데)
        JLabel titleLabel = new JLabel("패턴 가격 정보", SwingConstants.CENTER);
        titleLabel.setBounds((panelWidth - 300) / 2, 50, 300, 40);
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        add(titleLabel);
        
        // 헤더 라벨 (가운데 정렬)
        JLabel headerImage = new JLabel("패턴", SwingConstants.CENTER);
        headerImage.setBounds(leftMargin, startY - 30, imageSize, 30);
        headerImage.setForeground(Color.WHITE);
        headerImage.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(headerImage);
        
        JLabel headerName = new JLabel("이름", SwingConstants.CENTER);
        headerName.setBounds(leftMargin + imageSize + gap1, startY - 30, nameWidth, 30);
        headerName.setForeground(Color.WHITE);
        headerName.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(headerName);
        
        JLabel headerCurrent = new JLabel("현재 가격", SwingConstants.CENTER);
        headerCurrent.setBounds(leftMargin + imageSize + gap1 + nameWidth + gap2, startY - 30, priceWidth, 30);
        headerCurrent.setForeground(Color.WHITE);
        headerCurrent.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(headerCurrent);
        
        // 각 패턴별 라벨 생성 및 배치
        for (int i = 0; i < PATTERN_NAMES.length; i++) {
            int y = startY + (i * spacing);
            
            // 패턴 이미지 라벨
            try {
            	String resPath = "/" + PATTERN_IMAGE_PATHS[i].replace("res/", "");
            	java.net.URL imgUrl = getClass().getResource(resPath);
            	if (imgUrl != null) {
                    // 2. URL을 통해 이미지를 직접 읽음 (File 객체 사용 안 함)
                    Image patternImage = javax.imageio.ImageIO.read(imgUrl);
                if (patternImage != null) {
                	Image scaledImage = patternImage.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
                    patternImageLabels[i] = new JLabel(new ImageIcon(scaledImage));
                    patternImageLabels[i].setBounds(leftMargin, y, imageSize, imageSize);
                    patternImageLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
                    patternImageLabels[i].setVerticalAlignment(SwingConstants.CENTER);
                    add(patternImageLabels[i]);
                    } else {
                        // 이미지가 null인 경우
                        patternImageLabels[i] = createPlaceholderLabel(imageSize);
                        patternImageLabels[i].setBounds(leftMargin, y, imageSize, imageSize);
                        add(patternImageLabels[i]);
                    }
                } else {
                    // 파일이 없는 경우
                    System.err.println("패턴 이미지 파일을 찾을 수 없습니다: " + PATTERN_IMAGE_PATHS[i]);
                    patternImageLabels[i] = createPlaceholderLabel(imageSize);
                    patternImageLabels[i].setBounds(leftMargin, y, imageSize, imageSize);
                    add(patternImageLabels[i]);
                }
            } catch (IOException e) {
                System.err.println("패턴 이미지 로드 실패: " + PATTERN_IMAGE_PATHS[i] + " - " + e.getMessage());
                // 이미지 로드 실패 시 플레이스홀더 라벨 생성
                patternImageLabels[i] = createPlaceholderLabel(imageSize);
                patternImageLabels[i].setBounds(leftMargin, y, imageSize, imageSize);
                add(patternImageLabels[i]);
            }
            
            // 패턴 이름 라벨 (수직 가운데 정렬)
            patternNameLabels[i] = new JLabel(PATTERN_NAMES[i], SwingConstants.LEFT);
            patternNameLabels[i].setBounds(leftMargin + imageSize + gap1, y + (imageSize - 30) / 2, nameWidth, 30);
            patternNameLabels[i].setForeground(Color.CYAN);
            patternNameLabels[i].setFont(new Font("맑은 고딕", Font.BOLD, 16));
            patternNameLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            add(patternNameLabels[i]);
            
            // 현재 가격 라벨 (수직 가운데 정렬)
            currentPriceLabels[i] = new JLabel("", SwingConstants.CENTER);
            currentPriceLabels[i].setBounds(leftMargin + imageSize + gap1 + nameWidth + gap2, y + (imageSize - 30) / 2, priceWidth, 30);
            currentPriceLabels[i].setForeground(Color.GREEN);
            currentPriceLabels[i].setFont(new Font("맑은 고딕", Font.BOLD, 16));
            currentPriceLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            add(currentPriceLabels[i]);
        }
        
        // 가격 정보 업데이트
        updatePriceInfo();
    }
    
    /**
     * 이미지 로드 실패 시 사용할 플레이스홀더 라벨을 생성합니다.
     */
    private JLabel createPlaceholderLabel(int size) {
        JLabel placeholder = new JLabel("?", SwingConstants.CENTER);
        placeholder.setOpaque(true);
        placeholder.setBackground(new Color(100, 100, 100));
        placeholder.setForeground(Color.WHITE);
        placeholder.setFont(new Font("맑은 고딕", Font.BOLD, size / 2));
        placeholder.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        return placeholder;
    }
    
    /**
     * User 객체로부터 가격 정보를 가져와 라벨을 업데이트합니다.
     */
    public void updatePriceInfo() {
        if (user == null) {
            return;
        }
        
        for (int i = 0; i < PATTERN_NAMES.length; i++) {
            int originalPrice = user.getPatternOriginal(i);
            int currentPrice = user.getPatternSum(i);
            
            currentPriceLabels[i].setText(currentPrice + "원");
            
            // 현재 가격이 원래 가격보다 높으면 강조 표시
            if (currentPrice > originalPrice) {
                currentPriceLabels[i].setForeground(Color.YELLOW);
            } else {
                currentPriceLabels[i].setForeground(Color.GREEN);
            }
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * 배경 이미지를 로드합니다.
     */
    private void loadBackgroundImage(String path) {
    	try {
            String resPath = path.startsWith("/") ? path : "/" + path.replace("res/", "");
            java.net.URL imgUrl = getClass().getResource(resPath);
            
            if (imgUrl != null) {
                backgroundImage = ImageIO.read(imgUrl);
            } else {
                System.err.println("배경 리소스를 찾을 수 없음: " + resPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

