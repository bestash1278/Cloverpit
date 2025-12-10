import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SymbolIcon implements Icon {
    // 심볼 타입 상수
    public static final int LEMON = 0;
    public static final int CHERRY = 1;
    public static final int CLOVER = 2;
    public static final int BELL = 3;
    public static final int DIAMOND = 4;
    public static final int TREASURE = 5;
    public static final int SEVEN = 6;
    
    // 각 문양별 이미지 파일 경로
    private static final String[] SYMBOL_IMAGE_PATHS = {
        "res/sybols_lemon.png",
        "res/sybols_cherry.png",
        "res/sybols_clover.png",
        "res/sybols_bell.png",
        "res/sybols_diamond.png",
        "res/sybols_treasure.png",
        "res/sybols_seven.png"
    };
    
    // 문양 이름 배열 (파일명 생성용)
    private static final String[] SYMBOL_NAMES = {
        "lemon", "cherry", "clover", "bell", "diamond", "treasure", "seven"
    };
    
    // 변형자 이름 한글-영어 매핑 (이미지 파일명 생성용)
    private static final Map<String, String> MODIFIER_NAME_MAP = new HashMap<>();
    static {
        MODIFIER_NAME_MAP.put("사슬", "chain");
        MODIFIER_NAME_MAP.put("반복", "repeat");
        MODIFIER_NAME_MAP.put("토큰", "token");
        MODIFIER_NAME_MAP.put("티켓", "ticket");
        // 영어 이름도 지원 (대소문자 무시)
        MODIFIER_NAME_MAP.put("chain", "chain");
        MODIFIER_NAME_MAP.put("repeat", "repeat");
        MODIFIER_NAME_MAP.put("token", "token");
        MODIFIER_NAME_MAP.put("ticket", "ticket");
    }
    
    // 이미지 캐시 (한 번 로드한 이미지를 저장)
    private static final Map<Integer, BufferedImage> imageCache = new HashMap<>();
    private static final Map<String, BufferedImage> modifierImageCache = new HashMap<>();
    
    private int symbolType;
    private String modifier; // 변형자 이름 (null이면 일반 문양)
    private int size;
    
    public SymbolIcon(int symbolType, int size) {
        this.symbolType = symbolType;
        this.modifier = null;
        this.size = size;
    }
    
    public SymbolIcon(int symbolType, String modifier, int size) {
        this.symbolType = symbolType;
        this.modifier = modifier;
        this.size = size;
    }
    
    /**
     * 특정 문양 타입의 이미지를 로드합니다.
     * 이미지가 캐시에 있으면 캐시에서 반환하고, 없으면 파일에서 로드합니다.
     */
    private BufferedImage loadSymbolImage(int symbolType) {
        // 변형자가 있는 경우 문양+변형자 조합 이미지 로드
        if (modifier != null && !modifier.isEmpty() && symbolType >= 0 && symbolType < SYMBOL_NAMES.length) {
            String cacheKey = symbolType + "_" + modifier;
            if (modifierImageCache.containsKey(cacheKey)) {
                return modifierImageCache.get(cacheKey);
            }
            
            // 변형자 이름을 영어로 변환 (한글 -> 영어)
            String modifierEnglish = MODIFIER_NAME_MAP.getOrDefault(modifier, modifier.toLowerCase());
            
            // 변형자 이미지 파일 경로 생성 (예: res/sybols_lemon_ticket.png)
            String imagePath = "res/sybols_" + SYMBOL_NAMES[symbolType] + "_" + modifierEnglish + ".png";
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    BufferedImage image = ImageIO.read(imageFile);
                    if (image != null) {
                        modifierImageCache.put(cacheKey, image);
                        return image;
                    }
                } else {
                    // 변형자 이미지가 없으면 일반 문양 이미지로 대체
                    System.err.println("경고: 변형자 이미지 파일을 찾을 수 없습니다. 경로: " + imagePath + " - 일반 문양 이미지 사용");
                }
            } catch (IOException e) {
                System.err.println("변형자 이미지 로드 중 오류 발생: " + imagePath + " - " + e.getMessage());
            }
        }
        
        // 일반 문양 이미지 로드 (변형자가 없거나 변형자 이미지를 찾을 수 없는 경우)
        if (imageCache.containsKey(symbolType)) {
            return imageCache.get(symbolType);
        }
        
        // 파일에서 로드
        if (symbolType >= 0 && symbolType < SYMBOL_IMAGE_PATHS.length) {
            try {
                File imageFile = new File(SYMBOL_IMAGE_PATHS[symbolType]);
                if (imageFile.exists()) {
                    BufferedImage image = ImageIO.read(imageFile);
                    if (image != null) {
                        // 캐시에 저장
                        imageCache.put(symbolType, image);
                        return image;
                    }
                } else {
                    System.err.println("경고: 이미지 파일을 찾을 수 없습니다. 경로: " + SYMBOL_IMAGE_PATHS[symbolType]);
                }
            } catch (IOException e) {
                System.err.println("이미지 로드 중 오류 발생: " + SYMBOL_IMAGE_PATHS[symbolType] + " - " + e.getMessage());
            }
        }
        
        return null;
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        BufferedImage symbolImage = loadSymbolImage(symbolType);
        
        if (symbolImage == null) {
            // 이미지가 로드되지 않았을 경우 기본 도형으로 그리기
            drawFallback(g, x, y);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 이미지를 크기에 맞게 그리기
        g2d.drawImage(symbolImage, 
                     x, y, x + size, y + size,
                     0, 0, symbolImage.getWidth(), symbolImage.getHeight(),
                     null);
        
        g2d.dispose();
    }
    
    // 이미지 로드 실패 시 대체 그리기
    private void drawFallback(Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int centerX = x + size / 2;
        int centerY = y + size / 2;
        
        // 변형자만 있는 경우 (symbolType == -1)
        if (symbolType == -1 && modifier != null) {
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillRect(x, y, size, size);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, size / 3));
            FontMetrics fm = g2d.getFontMetrics();
            String text = modifier;
            int textX = centerX - fm.stringWidth(text) / 2;
            int textY = centerY + fm.getAscent() / 2;
            g2d.drawString(text, textX, textY);
            g2d.dispose();
            return;
        }
        
        switch (symbolType) {
            case LEMON:
                g2d.setColor(new Color(255, 255, 0));
                g2d.fillOval(x + size/6, y + size/6, size*2/3, size*2/3);
                break;
            case CHERRY:
                g2d.setColor(new Color(255, 0, 0));
                g2d.fillOval(x + size/4, y + size/6, size/2, size/2);
                g2d.fillOval(x + size/2, y + size/6, size/2, size/2);
                break;
            case CLOVER:
                g2d.setColor(new Color(0, 150, 0));
                int radius = size / 4;
                g2d.fillOval(centerX - radius, centerY - size/3, radius*2, radius*2);
                g2d.fillOval(centerX - size/3, centerY, radius*2, radius*2);
                g2d.fillOval(centerX + size/3 - radius, centerY, radius*2, radius*2);
                g2d.fillOval(centerX - radius, centerY + size/3 - radius, radius*2, radius*2);
                break;
            case BELL:
                g2d.setColor(new Color(255, 215, 0));
                int[] xPoints = {centerX, centerX - size/3, centerX - size/4, centerX - size/4, centerX + size/4, centerX + size/4, centerX + size/3};
                int[] yPoints = {centerY - size/2, centerY - size/3, centerY, centerY + size/3, centerY + size/3, centerY, centerY - size/3};
                g2d.fillPolygon(xPoints, yPoints, 7);
                break;
            case DIAMOND:
                g2d.setColor(new Color(0, 191, 255));
                int[] xDiamond = {centerX, centerX - size/3, centerX, centerX + size/3};
                int[] yDiamond = {centerY - size/3, centerY, centerY + size/3, centerY};
                g2d.fillPolygon(xDiamond, yDiamond, 4);
                break;
            case TREASURE:
                g2d.setColor(new Color(255, 215, 0));
                g2d.fillRect(x + size/6, y + size/6, size*2/3, size*2/3);
                break;
            case SEVEN:
                g2d.setColor(new Color(255, 0, 0));
                g2d.setFont(new Font("Arial", Font.BOLD, size));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "7";
                int textX = centerX - fm.stringWidth(text) / 2;
                int textY = centerY + fm.getAscent() / 2;
                g2d.drawString(text, textX, textY);
                break;
        }
        
        g2d.dispose();
    }
    
    @Override
    public int getIconWidth() {
        return size;
    }
    
    @Override
    public int getIconHeight() {
        return size;
    }
}
