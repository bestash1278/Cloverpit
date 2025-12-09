// OwnItem_Screen.java

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OwnItem_Screen extends JPanel {
    private final OwnItem ownItemLogic; // ⭐ 로직 클래스
    private List<ItemInfo> displayedItems = new ArrayList<>(); // 현재 소유한 유물 목록 (템플릿 인스턴스)

    // UI Components for Detail Panel (유물상점과 동일)
    private JPanel itemDetailPanel; 
    private JLabel nameLabel;       
    private JTextArea descriptionArea; 
    private JButton sellButton;       // ⭐ 판매 버튼
    private JButton cancelButton;    
    
    private ItemInfo selectedItem; // 현재 선택된 유물
    private int selectedItemIndex;  // displayedItems 리스트 내의 인덱스 (사용하지 않음, 호환성 목적)

    // UI Constants and Geometry (3-4-4 Layout)
    private static final int TOTAL_SLOTS = 11;
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int ITEM_SIZE = 100;
    private static final int H_GAP = 30; // Horizontal gap
    private static final int V_GAP = 30; // Vertical gap
    private static final int START_Y = 100;
    
    private static final Rectangle[] ITEM_AREAS = new Rectangle[TOTAL_SLOTS];
    // TODO: 배경 이미지 경로를 실제 프로젝트 경로로 변경하세요.
    private static final String BACKGROUND_PATH = "res/shop_background.png"; 
    private Image backgroundImage;

    // Static initializer for layout geometry (3-4-4)
    static {
        int panelWidth = PANEL_WIDTH;
        int startY = START_Y;
        
        // --- Row 1 (3 items) ---
        int row1Width = 3 * ITEM_SIZE + 2 * H_GAP;
        int startX1 = (panelWidth - row1Width) / 2;
        for (int i = 0; i < 3; i++) {
            ITEM_AREAS[i] = new Rectangle(startX1 + i * (ITEM_SIZE + H_GAP), startY, ITEM_SIZE, ITEM_SIZE);
        }
        startY += ITEM_SIZE + V_GAP;

        // --- Row 2 (4 items) ---
        int row2Width = 4 * ITEM_SIZE + 3 * H_GAP;
        int startX2 = (panelWidth - row2Width) / 2;
        for (int i = 0; i < 4; i++) {
            ITEM_AREAS[i + 3] = new Rectangle(startX2 + i * (ITEM_SIZE + H_GAP), startY, ITEM_SIZE, ITEM_SIZE);
        }
        startY += ITEM_SIZE + V_GAP;

        // --- Row 3 (4 items) ---
        int row3Width = 4 * ITEM_SIZE + 3 * H_GAP;
        int startX3 = (panelWidth - row3Width) / 2;
        for (int i = 0; i < 4; i++) {
            ITEM_AREAS[i + 7] = new Rectangle(startX3 + i * (ITEM_SIZE + H_GAP), startY, ITEM_SIZE, ITEM_SIZE);
        }
    }
    
    public OwnItem_Screen(OwnItem ownItemLogic) {
        this.ownItemLogic = ownItemLogic;
        setLayout(null); // Custom layout for drawing
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        loadBackgroundImage(BACKGROUND_PATH); // 배경 이미지 로드

        // 마우스 리스너 추가
        addMouseListener(new ItemMouseAdapter());

        // 상세 정보 패널 초기화
        initializeItemDetailPanel();
    }
    
    // ----------- UI 그리기 및 갱신 -----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. 배경 이미지 그리기
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
             g2d.setColor(Color.DARK_GRAY);
             g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // 2. 소유 유물 표시
        for (int i = 0; i < displayedItems.size() && i < TOTAL_SLOTS; i++) {
            ItemInfo item = displayedItems.get(i);
            Rectangle area = ITEM_AREAS[i];
            
            // 유물 이미지가 너무 작게 보이지 않도록 중앙에 여백을 둡니다.
            int imagePadding = 5; 
            int imageSize = ITEM_SIZE - 2 * imagePadding;
            
            // 유물 이미지 로드 및 그리기
            Image itemImage = loadImage(item.getImagePath());
            if (itemImage != null) {
                g2d.drawImage(itemImage, area.x + imagePadding, area.y + imagePadding, imageSize, imageSize, this);
            } else {
                // 이미지가 없으면 임시 박스 표시
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(area.x, area.y, area.width, area.height);
                g2d.setColor(Color.BLACK);
                g2d.drawString(item.getName(), area.x + 5, area.y + 15);
            }

            // 테두리
            g2d.setColor(Color.WHITE);
            g2d.drawRect(area.x, area.y, area.width, area.height);
        }
    }

    /**
     * SlotMachinePanel에서 명시적으로 호출하여 UI를 갱신합니다.
     */
    @Override
    public void updateUI() {
        super.updateUI();
        updateOwnedItemsUI();
        
        if (itemDetailPanel != null) {
            itemDetailPanel.revalidate();
            itemDetailPanel.repaint();
        }
    }
    
    public void updateOwnedItemsUI() {
        // ⭐ 방어 코드: 필드가 null이면 초기화 중이므로 건너뜁니다.
        if (ownItemLogic == null) return;
        
        // 1. 유저의 소유 유물 이름 리스트를 가져옵니다.
        List<String> ownedNames = ownItemLogic.getOwnedItemNames();
        
        // 2. ItemInfo 템플릿 인스턴스로 변환
        displayedItems.clear();
        for (String name : ownedNames) {
             ItemInfo itemTemplate = ownItemLogic.getItemInfoByName(name);
             if (itemTemplate != null) {
                 displayedItems.add(itemTemplate);
             }
        }
        
        revalidate(); 
        repaint(); // 화면 다시 그리기 요청
    }
    
    // ----------- 상세 정보 패널 및 이벤트 처리 -----------

    private void initializeItemDetailPanel() {
        itemDetailPanel = new JPanel();
        itemDetailPanel.setLayout(new BorderLayout(10, 10));
        itemDetailPanel.setBackground(new Color(50, 50, 50, 230)); // 반투명 배경
        itemDetailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        itemDetailPanel.setVisible(false);
        
        int panelW = 400;
        int panelH = 300;
        itemDetailPanel.setBounds((PANEL_WIDTH - panelW) / 2, (PANEL_HEIGHT - panelH) / 2, panelW, panelH);
        
        // 1. 이름 (NORTH)
        nameLabel = new JLabel("", SwingConstants.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        itemDetailPanel.add(nameLabel, BorderLayout.NORTH);

        // 2. 설명 (CENTER)
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(new Color(50, 50, 50, 0));
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        itemDetailPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        // 3. 버튼 패널 (SOUTH)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(50, 50, 50, 0));
        
        // ⭐ 판매 버튼 추가
        sellButton = new JButton("유물 판매");
        sellButton.setForeground(Color.RED);
        sellButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        sellButton.addActionListener(e -> handleSellAction());
        
        cancelButton = new JButton("취소");
        cancelButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        cancelButton.addActionListener(e -> hideItemDetails());

        buttonPanel.add(sellButton);
        buttonPanel.add(cancelButton);
        itemDetailPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(itemDetailPanel);
        setComponentZOrder(itemDetailPanel, 0); // 상세 패널을 항상 최상위로
    }
    
    private void showItemDetails(ItemInfo item) {
        if (itemDetailPanel.isVisible()) {
            return;
        }
        
        selectedItem = item;

        // UI 업데이트
        nameLabel.setText(item.getName());
        descriptionArea.setText("가격: " + item.getTicketCost() + " 티켓\n\n설명: " + item.getDescription());
        
        // 판매 버튼 텍스트를 환급 금액으로 업데이트 (예시)
        int ticketCost = item.getTicketCost();
        int refundTickets = ticketCost / 2;
        sellButton.setText("유물 판매 (" + refundTickets + " 티켓 반환)"); // 티켓으로 표시
        
        itemDetailPanel.setVisible(true);
        repaint();
    }
    
    private void hideItemDetails() {
        itemDetailPanel.setVisible(false);
        repaint();
    }
    
    private void handleSellAction() {
        if (selectedItem == null) return;
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            "정말로 [" + selectedItem.getName() + "] 유물을 판매하시겠습니까?",
            "유물 판매 확인",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            // 로직 클래스에 판매 요청
            if (ownItemLogic.sell_item(selectedItem.getName())) { 
                JOptionPane.showMessageDialog(this, selectedItem.getName() + " 유물을 판매했습니다.");
                
                hideItemDetails();
                updateOwnedItemsUI(); // 유물 목록 갱신 (삭제된 항목이 사라짐)
            } else {
                JOptionPane.showMessageDialog(this, "유물 판매에 실패했습니다. (내부 오류)", "판매 실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ----------- 유틸리티 메서드 (이미지 로딩) -----------
    private Image loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            // System.err.println("이미지 로드 실패: " + path);
            return null;
        }
    }
    private void loadBackgroundImage(String path) {
        try {
            backgroundImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("배경 이미지 로드 실패: " + path);
            backgroundImage = null;
        }
    }

    // ----------- 마우스 이벤트 리스너 -----------
    private class ItemMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point clickedPoint = e.getPoint();

            if (itemDetailPanel.isVisible()) {
                return;
            }

            for (int i = 0; i < ITEM_AREAS.length; i++) {
                if (ITEM_AREAS[i].contains(clickedPoint)) {
                    if (displayedItems != null && i < displayedItems.size()) {
                        ItemInfo clickedItem = displayedItems.get(i);
                        showItemDetails(clickedItem); 
                    }
                    return;
                }
            }
        }
    }
}