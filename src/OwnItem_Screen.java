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
    private final OwnItem ownItemLogic;
    private List<ItemInfo> displayedItems = new ArrayList<>(); // 현재 소유한 유물 목록

    private JPanel itemDetailPanel; 
    private JLabel nameLabel;       
    private JTextArea descriptionArea; 
    private JButton sellButton;    
    private JButton cancelButton;    
    
    private ItemInfo selectedItem; // 현재 선택된 유물

    private static final int TOTAL_SLOTS = 11;
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int ITEM_SIZE = 100;
    private static final int H_GAP = 30; // Horizontal gap
    private static final int V_GAP = 30; // Vertical gap
    private static final int START_Y = 100;
    
    private static final Rectangle[] ITEM_AREAS = new Rectangle[TOTAL_SLOTS];
    
    private Image centralBackgroundImage;
    private static final String BACKGROUND_PATH = "res/back_ground.png"; 
    private static final String CENTRAL_BG_PATH = "res/shop_bg.png"; // 유물 전체 뒤 배경 경로
    private Image backgroundImage;
    
    private static final Rectangle CENTRAL_ARTIFACT_AREA;	//유물 전체 뒷 배경

    static {
        int panelWidth = PANEL_WIDTH;
        int startY = START_Y;
        int row1Width = 3 * ITEM_SIZE + 2 * H_GAP;
        int startX1 = (panelWidth - row1Width) / 2;
        for (int i = 0; i < 3; i++) {
            ITEM_AREAS[i] = new Rectangle(startX1 + i * (ITEM_SIZE + H_GAP), startY - 60, ITEM_SIZE, ITEM_SIZE);
        }
        startY += ITEM_SIZE + V_GAP;

        int row2Width = 4 * ITEM_SIZE + 3 * H_GAP;
        int startX2 = (panelWidth - row2Width) / 2;
        for (int i = 0; i < 4; i++) {
            ITEM_AREAS[i + 3] = new Rectangle(startX2 + i * (ITEM_SIZE + H_GAP), startY, ITEM_SIZE, ITEM_SIZE);
        }
        startY += ITEM_SIZE + V_GAP;

        int row3Width = 4 * ITEM_SIZE + 3 * H_GAP;
        int startX3 = (panelWidth - row3Width) / 2;
        for (int i = 0; i < 4; i++) {
            ITEM_AREAS[i + 7] = new Rectangle(startX3 + i * (ITEM_SIZE + H_GAP), startY + 60, ITEM_SIZE, ITEM_SIZE);
        }
        
        final int FIXED_X = 100;
        final int FIXED_Y = 50;
        final int FIXED_WIDTH = 600;
        final int FIXED_HEIGHT = 450;
        
        CENTRAL_ARTIFACT_AREA = new Rectangle(
            FIXED_X,
            FIXED_Y,
            FIXED_WIDTH,
            FIXED_HEIGHT
        );
        
    }
    
    public OwnItem_Screen(OwnItem ownItemLogic) {
        this.ownItemLogic = ownItemLogic;
        setLayout(null); 
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        loadBackgroundImage(BACKGROUND_PATH); // 배경 이미지 로드
        loadCentralBackgroundImage(CENTRAL_BG_PATH);
        // 마우스 리스너 추가
        addMouseListener(new ItemMouseAdapter());
        // 상세 정보 패널 초기화
        initializeItemDetailPanel();
    }
    //유물 관물대 이미지 불러오기
    private void loadCentralBackgroundImage(String path) {
        try {
            centralBackgroundImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            centralBackgroundImage = null;
        }
    }
    
    // ----------- UI 그리기 및 갱신 -----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
             g2d.setColor(Color.DARK_GRAY);
             g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        //유물 관물대그리기
        if (centralBackgroundImage != null) {
            g2d.drawImage(
                centralBackgroundImage,
                CENTRAL_ARTIFACT_AREA.x	- 50,
                CENTRAL_ARTIFACT_AREA.y + 5,
                CENTRAL_ARTIFACT_AREA.width + 100,
                CENTRAL_ARTIFACT_AREA.height + 100,
                this
            );
        } else {
            g2d.setColor(new Color(50, 50, 50, 150)); 
            g2d.fillRect(
                CENTRAL_ARTIFACT_AREA.x,
                CENTRAL_ARTIFACT_AREA.y,
                CENTRAL_ARTIFACT_AREA.width,
                CENTRAL_ARTIFACT_AREA.height
            );
        }
        //소유 유물 표시하기
        for (int i = 0; i < displayedItems.size() && i < TOTAL_SLOTS; i++) {
            ItemInfo item = displayedItems.get(i);
            Rectangle area = ITEM_AREAS[i];

            int imagePadding = 5; 
            int imageSize = ITEM_SIZE - 2 * imagePadding;
            // 유물 이미지 로드 및 그리기
            Image itemImage = loadImage(item.getImagePath());
            if (itemImage != null) {
                g2d.drawImage(itemImage, area.x + imagePadding, area.y + imagePadding, imageSize, imageSize, this);
            } else {
            	// 이미지가 없으면 임시 박스 표시
                int itemAreaX = area.x + imagePadding;
                int itemAreaY = area.y + imagePadding;
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(itemAreaX, itemAreaY, imageSize, imageSize); 
                g2d.setColor(Color.BLACK);
                g2d.drawString(item.getName(), itemAreaX + 5, itemAreaY + 15);
            }
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
    //소유 유물 ui업데이트
    public void updateOwnedItemsUI() {
        if (ownItemLogic == null) return;
        //소유 유물 리스트 가져오기
        List<String> ownedNames = ownItemLogic.getOwnedItemNames();
        
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
        
        //이름
        nameLabel = new JLabel("", SwingConstants.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        itemDetailPanel.add(nameLabel, BorderLayout.NORTH);

        //설명
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(new Color(50, 50, 50, 0));
        descriptionArea.setForeground(Color.BLACK);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        itemDetailPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        //버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(50, 50, 50, 0));
        
        //판매 버튼 추가
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
    
    //유물 상세정보 설정 함수
    private void showItemDetails(ItemInfo item) {
        if (itemDetailPanel.isVisible()) {
            return;
        }
        selectedItem = item;
        // UI 업데이트
        nameLabel.setText(item.getName());
        descriptionArea.setText("가격: " + item.getTicketCost() + " 티켓\n\n설명: " + item.getDescription());
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
            "정말로 [" + selectedItem.getName() + "] 유물을 판매하시겠습니까?","유물 판매 확인",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
        	//판매선택 누르면
            if (ownItemLogic.sell_item(selectedItem.getName())) { 
                JOptionPane.showMessageDialog(this, selectedItem.getName() + " 유물을 판매했습니다.");
                
                hideItemDetails();
                updateOwnedItemsUI(); // 유물 목록 갱신
            } else {
                JOptionPane.showMessageDialog(this, "유물 판매에 실패했습니다. (내부 오류)", "판매 실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //이미지 불러오기 함수
    private Image loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
    }
    //배경 이미지 불러오기 함수
    private void loadBackgroundImage(String path) {
        try {
            backgroundImage = ImageIO.read(new File(path));
        } catch (IOException e) {
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

