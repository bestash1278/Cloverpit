import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 무늬 가격 표시 화면
 * User 객체로부터 무늬의 현재 가격을 표시합니다.
 */
public class SymbolPrice_Screen extends JPanel {
    private final User user;
    private Image backgroundImage;
    
    // 무늬 이름 배열
    private static final String[] SYMBOL_NAMES = {"레몬", "체리", "클로버", "종", "다이아", "보물", "7"};
    
    // 무늬 이미지 경로 배열
    private static final String[] SYMBOL_IMAGE_PATHS = {
        "res/sybols_lemon.png",
        "res/sybols_cherry.png",
        "res/sybols_clover.png",
        "res/sybols_bell.png",
        "res/sybols_diamond.png",
        "res/sybols_treasure.png",
        "res/sybols_seven.png"
    };
    
    // 가격 정보를 표시할 라벨들
    private JLabel[] symbolImageLabels;
    private JLabel[] symbolNameLabels;
    private JLabel[] currentPriceLabels;
    
    public SymbolPrice_Screen(User user) {
        this.user = user;
        
        loadBackgroundImage("res/back_ground.png");
        setLayout(null);
        
        setupPriceLabels();
        setPreferredSize(new Dimension(1200, 800));
    }
    
    /**
     * 가격 정보 라벨들을 초기화하고 배치합니다.
     */
    private void setupPriceLabels() {
        symbolImageLabels = new JLabel[SYMBOL_NAMES.length];
        symbolNameLabels = new JLabel[SYMBOL_NAMES.length];
        currentPriceLabels = new JLabel[SYMBOL_NAMES.length];
        
        int panelWidth = 1200; // 패널 너비
        int startY = 150;
        int spacing = 80;
        int imageSize = 80;
        int nameWidth = 120;
        int priceWidth = 200;
        int gap1 = 30; // 이미지와 이름 사이 간격
        int gap2 = 40; // 이름과 가격 사이 간격
        
        // 전체 컨텐츠 너비 계산
        int totalContentWidth = imageSize + gap1 + nameWidth + gap2 + priceWidth;
        int leftMargin = (panelWidth - totalContentWidth) / 2; // 가운데 정렬을 위한 왼쪽 마진
        
        // 제목 라벨 (완전히 가운데)
        JLabel titleLabel = new JLabel("무늬 가격 정보", SwingConstants.CENTER);
        titleLabel.setBounds((panelWidth - 300) / 2, 50, 300, 40);
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        add(titleLabel);
        
        // 헤더 라벨 (가운데 정렬)
        JLabel headerImage = new JLabel("무늬", SwingConstants.CENTER);
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
        
        // 각 무늬별 라벨 생성 및 배치
        for (int i = 0; i < SYMBOL_NAMES.length; i++) {
            int y = startY + (i * spacing);
            
            // 무늬 이미지 라벨
            try {
                String resPath = "/" + SYMBOL_IMAGE_PATHS[i].replace("res/", "");
                java.net.URL imgUrl = getClass().getResource(resPath);
                if (imgUrl != null) {
                    // 2. URL을 사용하여 이미지 로드 (File.exists()는 이제 필요 없음)
                    Image symbolImage = ImageIO.read(imgUrl);
                    if (symbolImage != null) {
                        Image scaledImage = symbolImage.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
                        symbolImageLabels[i] = new JLabel(new ImageIcon(scaledImage));
                        symbolImageLabels[i].setBounds(leftMargin, y, imageSize, imageSize);
                        symbolImageLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
                        symbolImageLabels[i].setVerticalAlignment(SwingConstants.CENTER);
                        add(symbolImageLabels[i]);
                    } else {
                        // 이미지 데이터가 깨졌거나 읽을 수 없는 경우
                        symbolImageLabels[i] = createPlaceholderLabel(imageSize);
                        symbolImageLabels[i].setBounds(leftMargin, y, imageSize, imageSize);
                        add(symbolImageLabels[i]);
                    }
                } else {
                    // 3. 리소스 URL을 찾지 못한 경우 (경로 문제 또는 파일 누락)
                    System.err.println("무늬 리소스를 찾을 수 없습니다: " + resPath);
                    symbolImageLabels[i] = createPlaceholderLabel(imageSize);
                    symbolImageLabels[i].setBounds(leftMargin, y, imageSize, imageSize);
                    add(symbolImageLabels[i]);
                }
            } catch (IOException e) {
                System.err.println("무늬 이미지 로드 에러: " + SYMBOL_IMAGE_PATHS[i]);
                symbolImageLabels[i] = createPlaceholderLabel(imageSize);
                symbolImageLabels[i].setBounds(leftMargin, y, imageSize, imageSize);
                add(symbolImageLabels[i]);
            }
            
            // 무늬 이름 라벨 (이하 동일)
            symbolNameLabels[i] = new JLabel(SYMBOL_NAMES[i], SwingConstants.LEFT);
            symbolNameLabels[i].setBounds(leftMargin + imageSize + gap1, y + (imageSize - 30) / 2, nameWidth, 30);
            symbolNameLabels[i].setForeground(Color.CYAN);
            symbolNameLabels[i].setFont(new Font("맑은 고딕", Font.BOLD, 16));
            symbolNameLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            add(symbolNameLabels[i]);
            
            // 현재 가격 라벨 (이하 동일)
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
        
        for (int i = 0; i < SYMBOL_NAMES.length; i++) {
            int originalPrice = user.getSymbolOriginal(i);
            int currentPrice = user.getSymbolSum(i);
            
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

