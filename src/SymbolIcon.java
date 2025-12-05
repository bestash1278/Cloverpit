import javax.swing.*;
import java.awt.*;

public class SymbolIcon implements Icon {
    // 심볼 타입 상수
    public static final int LEMON = 0;
    public static final int CHERRY = 1;
    public static final int CLOVER = 2;
    public static final int BELL = 3;
    public static final int DIAMOND = 4;
    public static final int TREASURE = 5;
    public static final int SEVEN = 6;
    
    private int symbolType;
    private int size;
    
    public SymbolIcon(int symbolType, int size) {
        this.symbolType = symbolType;
        this.size = size;
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int centerX = x + size / 2;
        int centerY = y + size / 2;
        
        switch (symbolType) {
            case LEMON:
                drawLemon(g2d, centerX, centerY);
                break;
            case CHERRY:
                drawCherry(g2d, centerX, centerY);
                break;
            case CLOVER:
                drawClover(g2d, centerX, centerY);
                break;
            case BELL:
                drawBell(g2d, centerX, centerY);
                break;
            case DIAMOND:
                drawDiamond(g2d, centerX, centerY);
                break;
            case TREASURE:
                drawTreasure(g2d, centerX, centerY);
                break;
            case SEVEN:
                drawSeven(g2d, centerX, centerY);
                break;
        }
        
        g2d.dispose();
    }
    
    private void drawLemon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 255, 0));
        g.fillOval(x - size/3, y - size/3, size*2/3, size*2/3);
        g.setColor(new Color(200, 200, 0));
        g.drawOval(x - size/3, y - size/3, size*2/3, size*2/3);
    }
    
    private void drawCherry(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 0, 0));
        g.fillOval(x - size/4, y - size/3, size/2, size/2);
        g.fillOval(x, y - size/3, size/2, size/2);
        g.setColor(new Color(0, 100, 0));
        g.setStroke(new BasicStroke(2));
        g.drawLine(x - size/4, y - size/3, x + size/4, y - size/2);
    }
    
    private void drawClover(Graphics2D g, int x, int y) {
        g.setColor(new Color(0, 150, 0));
        int radius = size / 4;
        g.fillOval(x - radius, y - size/3, radius*2, radius*2);
        g.fillOval(x - size/3, y, radius*2, radius*2);
        g.fillOval(x + size/3 - radius, y, radius*2, radius*2);
        g.fillOval(x - radius, y + size/3 - radius, radius*2, radius*2);
        g.setColor(new Color(0, 100, 0));
        g.setStroke(new BasicStroke(2));
        g.drawLine(x, y + size/3, x, y + size/2);
    }
    
    private void drawBell(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 215, 0));
        int[] xPoints = {x, x - size/3, x - size/4, x - size/4, x + size/4, x + size/4, x + size/3};
        int[] yPoints = {y - size/2, y - size/3, y, y + size/3, y + size/3, y, y - size/3};
        g.fillPolygon(xPoints, yPoints, 7);
        g.setColor(new Color(200, 150, 0));
        g.drawPolygon(xPoints, yPoints, 7);
        g.setColor(new Color(139, 69, 19));
        g.fillOval(x - size/8, y + size/3, size/4, size/8);
    }
    
    private void drawDiamond(Graphics2D g, int x, int y) {
        g.setColor(new Color(0, 191, 255));
        int[] xPoints = {x, x - size/3, x, x + size/3};
        int[] yPoints = {y - size/3, y, y + size/3, y};
        g.fillPolygon(xPoints, yPoints, 4);
        g.setColor(new Color(0, 100, 200));
        g.drawPolygon(xPoints, yPoints, 4);
    }
    
    private void drawTreasure(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 215, 0));
        g.fillRect(x - size/3, y - size/3, size*2/3, size*2/3);
        g.setColor(new Color(200, 150, 0));
        g.setStroke(new BasicStroke(2));
        g.drawRect(x - size/3, y - size/3, size*2/3, size*2/3);
        g.setColor(new Color(139, 69, 19));
        g.fillOval(x - size/6, y - size/6, size/3, size/3);
    }
    
    private void drawSeven(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 0, 0));
        g.setFont(new Font("Arial", Font.BOLD, size));
        FontMetrics fm = g.getFontMetrics();
        String text = "7";
        int textX = x - fm.stringWidth(text) / 2;
        int textY = y + fm.getAscent() / 2;
        g.drawString(text, textX, textY);
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

