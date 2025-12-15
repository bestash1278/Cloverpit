
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ItemShop_Screen extends JPanel {
    private final ItemShop itemShopLogic; // ItemShop 객체에 의존성 주입
    private Image backgroundImage;
    private List<ItemInfo> displayedItems;
    
    //유물 설명용 패널
    private javax.swing.JPanel itemDetailPanel; // 유물 상세 정보를 담을 메인 패널
    private javax.swing.JLabel nameLabel;       // 유물 이름
    private javax.swing.JTextArea descriptionArea; // 유물 설명 (JTextArea를 사용하여 줄 바꿈 처리)
    private javax.swing.JLabel priceLabel;      // 유물 가격
    private javax.swing.JButton buyButton;       // 구매 버튼
    private javax.swing.JButton cancelButton;    // 취소 버튼
    private ItemInfo selectedItem;
    private int selectedItemIndex = -1;
    
    //5개의 아이템 박스 영역 좌표
    private static final Rectangle[] ITEM_AREAS = new Rectangle[5]; 
    //리롤 버튼 영역 좌표
    private static final Rectangle REROLL_AREA = new Rectangle(325, 450, 150, 100); 
    
    public ItemShop_Screen(ItemShop itemShopLogic) {
        this.itemShopLogic = itemShopLogic;
        // 초기 아이템 목록 가져오기
        this.displayedItems = itemShopLogic.getCurrentItems();

        loadBackgroundImage("res/back_ground.png"); 
        setLayout(null); 
        
        //아이템 박스 좌표 설정
        setupItemAreas();
        setupItemDetailPanel();	//초기화
        
        addMouseListener(new ShopClickListener());
        setPreferredSize(new Dimension(800, 600));
    }
    
    
    // 아이템 박스 셋팅 함수
    private void setupItemAreas() {
        int itemWidth = 150;
        int itemHeight = 150;
        int gap = 80;
        int center_x = 400;

        // 위에 2개 (0, 1)
        ITEM_AREAS[0] = new Rectangle(center_x - itemWidth - gap/2, 100, itemWidth, itemHeight);
        ITEM_AREAS[1] = new Rectangle(center_x + gap/2, 100, itemWidth, itemHeight);

        // 밑에 3개 (2, 3, 4)
        ITEM_AREAS[2] = new Rectangle(center_x - itemWidth - gap - itemWidth/2, 250, itemWidth, itemHeight);
        ITEM_AREAS[3] = new Rectangle(center_x - itemWidth/2, 250, itemWidth, itemHeight);
        ITEM_AREAS[4] = new Rectangle(center_x + gap + itemWidth/2, 250, itemWidth, itemHeight);
    }
    
    public void updateShopUI(List<ItemInfo> newItems) {
        this.displayedItems = newItems;
        revalidate();
        repaint();
        
    }


    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        //배경 이미지
        if (backgroundImage != null) { 
        	g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (displayedItems != null) {
            for (int i = 0; i < displayedItems.size(); i++) {
                java.awt.Rectangle area = ITEM_AREAS[i]; // 유물 표시 영역
                ItemInfo item = (ItemInfo) displayedItems.get(i); 
                java.awt.Image realItemImage = null;
                try {
                    java.io.File imageFile = new java.io.File(item.getImagePath());
                    realItemImage = javax.imageio.ImageIO.read(imageFile);
                } catch (java.io.IOException e) {
                    System.err.println("유물 이미지 로드 실패: " + item.getImagePath());
                }
                
                if (realItemImage != null) {
                    // 실제 이미지가 성공적으로 로드된 경우
                    g.drawImage(
                        realItemImage, 
                        area.x, 
                        area.y, 
                        area.width, 
                        area.height, 
                        this
                    );
                } else {
                    // 이미지 로드 실패 시 대체 사각형
                    g.setColor(java.awt.Color.LIGHT_GRAY); 
                    g.fillRect(area.x, area.y, area.width, area.height); 
                }

                //유물 가격
                g.setColor(java.awt.Color.YELLOW);
                g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                java.lang.String costText = item.getTicketCost() + " Tickets";
                
                int textWidth = g.getFontMetrics().stringWidth(costText);
                int textX = area.x + (area.width - textWidth) / 2;
                int textY = area.y + area.height + 20; // 유물 영역 아래쪽에 위치
                
                g.drawString(costText, textX, textY);
            }
        }
        
        //리롤영역
        if (REROLL_AREA != null) {
            g.setColor(java.awt.Color.YELLOW);
            g.drawRect(REROLL_AREA.x, REROLL_AREA.y, REROLL_AREA.width, REROLL_AREA.height); 
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
            String rerollText = "REROLL";
            int textWidth = g.getFontMetrics().stringWidth(rerollText);
            int textX = REROLL_AREA.x + (REROLL_AREA.width - textWidth) / 2;
            int textY = REROLL_AREA.y + REROLL_AREA.height / 2 + 7;
            g.drawString(rerollText, textX, textY);
        }
        
    }
    
    

    //유물 설명 화면 초기화 함수
    private void setupItemDetailPanel() {
        itemDetailPanel = new javax.swing.JPanel();
        itemDetailPanel.setLayout(null);
        itemDetailPanel.setBackground(new java.awt.Color(0, 0, 0, 180)); 
        itemDetailPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.WHITE));

        int windowWidth = 800;
        int panelW = 500;
        int panelH = 350;
        int panelX = (windowWidth - panelW) / 2;
        int panelY = 50;
        itemDetailPanel.setBounds(panelX, panelY, panelW, panelH);
        
        //처음에는 유물 상세 설명이 안보이도록 설정했다가 유물 클릭하면 보이도록 설정
        itemDetailPanel.setVisible(false);
        
        // 이름 표시
        nameLabel = new javax.swing.JLabel("유물이름", javax.swing.SwingConstants.LEFT);
        nameLabel.setForeground(java.awt.Color.YELLOW);
        nameLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 22));
        nameLabel.setBounds(10, 10, panelW - 20, 30);
        itemDetailPanel.add(nameLabel);

        // 설명 표시
        descriptionArea = new javax.swing.JTextArea();
        descriptionArea.setWrapStyleWord(true); // 단어 단위 줄 바꿈
        descriptionArea.setLineWrap(true);       // 줄 바꿈 활성화
        descriptionArea.setEditable(false);      // 수정 불가능
        descriptionArea.setBackground(java.awt.Color.BLACK); 
        descriptionArea.setOpaque(true); // 불투명하도록 설정
        descriptionArea.setForeground(java.awt.Color.WHITE);
        descriptionArea.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 16));
        descriptionArea.setBounds(10, 50, panelW - 20, 200);
        itemDetailPanel.add(descriptionArea);
        
        // 가격 표시
        priceLabel = new javax.swing.JLabel("", javax.swing.SwingConstants.RIGHT);
        priceLabel.setForeground(java.awt.Color.GREEN);
        priceLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
        priceLabel.setBounds(10, panelH - 70, panelW - 20, 20);
        itemDetailPanel.add(priceLabel);
        // 구매버튼 설정
        buyButton = new javax.swing.JButton("구매 (BUY)");
        buyButton.setBounds(panelW / 2 - 165, panelH - 40, 150, 40);
        buyButton.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
        itemDetailPanel.add(buyButton);
        //취소버튼 설정
        cancelButton = new javax.swing.JButton("취소 (CANCEL)");
        cancelButton.setBounds(panelW / 2 + 15, panelH - 40, 150, 40);
        cancelButton.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
        itemDetailPanel.add(cancelButton);

        this.add(itemDetailPanel); 
        //버튼 함수
        setupButtonListeners();
    }

    //상세정보 보여주기 함수
    public void showItemDetails(ItemInfo item, int index) {
        this.selectedItem = item;		//선택한 유물이 무엇인지
        this.selectedItemIndex = index;	//선택한 유물의 상점번호가 몇인지
        nameLabel.setText(item.getName());	//선택한 유물 이름 가져오기
        descriptionArea.setText(item.getDescription());
        priceLabel.setText("가격: " + item.getTicketCost() + " Tickets");
        
        itemDetailPanel.setVisible(true);
        itemDetailPanel.requestFocusInWindow();
        
        repaint();
    }
    
    //구매버튼
    private void setupButtonListeners() {
    	
    	// 취소 버튼
        cancelButton.addActionListener(e -> {
            itemDetailPanel.setVisible(false);
            selectedItem = null;
            selectedItemIndex = -1;
            repaint();
        });
        
        //구매 버튼
        buyButton.addActionListener(e -> {
            if (selectedItem != null) {	//유물 선택을 했다면
            	ItemShop.PurchaseResult result = itemShopLogic.buy_item(selectedItemIndex);	//유물 구매 함수 실행
            	//결과에 따라서 처리
                switch (result) {
                    case SUCCESS:
                    	updateShopUI(itemShopLogic.getCurrentItems());
                        javax.swing.JOptionPane.showMessageDialog(this, selectedItem.getName() + " 구매 성공!", "알림", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        break;
                        
                    case INSUFFICIENT_TICKETS:
                        // 티켓 부족으로 인한 구매 실패
                        javax.swing.JOptionPane.showMessageDialog(this, "티켓이 부족합니다!", "구매 실패", javax.swing.JOptionPane.WARNING_MESSAGE);
                        break;
                        
                    case ALREADY_SOLD:
                        // 이미 판매된 유물을 재구매 시도
                        javax.swing.JOptionPane.showMessageDialog(this, "이미 판매된 유물입니다!", "구매 실패", javax.swing.JOptionPane.WARNING_MESSAGE);
                        break;
                        
                    case INVENTORY_FULL: //인벤토리 가득 찼을 경우
                        JOptionPane.showMessageDialog(this, 
                            "유물 소유 개수가 최대치에 도달하여 더 이상 구매할 수 없습니다.", 
                            "구매 실패", 
                            JOptionPane.WARNING_MESSAGE
                        );
                        break;
                }
            }
            //작업 처리후 유물 상세정보 화면 감추기
            itemDetailPanel.setVisible(false);
            selectedItem = null;
            selectedItemIndex = -1;
            repaint();
        });
    }
    
    
    
    /**----------------------마우스 클릭 리스너--------------------------*/
    //마우스 클릭시
    private class ShopClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            Point clickedPoint = e.getPoint();
            //유물 상세 정보가 보인다면
            if (itemDetailPanel.isVisible()) {
                return;
            }
            //리롤 버튼을 눌렀다면
            if (REROLL_AREA.contains(clickedPoint)) {
                List<ItemInfo> newItems = itemShopLogic.rerollItems(); //리롤해서 새로운 유물 채워넣기
                if (newItems != null) {	//채운걸로 ui업데이트
                    updateShopUI(newItems);
                }  
            }
            //유물 클릭했다면
            else {
                for (int i = 0; i < ITEM_AREAS.length; i++) {
                    if (ITEM_AREAS[i].contains(clickedPoint)) {
                    	// 유물 클릭 감지!
                        if (displayedItems != null && i < displayedItems.size()) {
                            ItemInfo clickedItem = (ItemInfo) displayedItems.get(i);
                            //유물에 맞는 상세정보 보여주기
                            showItemDetails(clickedItem, i); 
                        }
                        return;
                    }
                }
            }
        }
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
}

