/*------------유물 상점 화면 클래스 -----------
 * 기능 : 유물상점 GUI 담당
 * 간단설명 : ItemShop클래스에서 선언된 유물리스트를 받아와서 보여주는 기능과 유물 구매과정 시각화.
 * 제작자 : Jinsung
 * 마지막 리팩토링 일자 : 2025-12-09
 */
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ItemShop_Screen extends JPanel {
    private final ItemShop itemShopLogic;

    private Image backgroundImage;
    private List<ItemInfo> displayedItems;
    
    //유물 설명용 패널
    private javax.swing.JPanel itemDetailPanel; // 유물 상세 정보를 담을 메인 패널
    private javax.swing.JLabel nameLabel;       // 유물 이름
    private javax.swing.JTextArea descriptionArea; // 유물 설명 (JTextArea를 사용하여 줄 바꿈 처리)
    private javax.swing.JLabel priceLabel;      // 유물 가격
    private javax.swing.JButton buyButton;       // 구매 버튼
    private javax.swing.JButton cancelButton;    // 취소 버튼
    // 현재 선택된 유물의 ItemInfo 객체와, 그 유물의 ITEM_AREAS 인덱스를 저장
    private ItemInfo selectedItem;
    private int selectedItemIndex = -1;
    
    //5개의 아이템 박스 영역 좌표
    private static final Rectangle[] ITEM_AREAS = new Rectangle[5]; 
    //리롤 버튼 영역 좌표
    private static final Rectangle REROLL_AREA = new Rectangle(700, 300, 80, 50); 
    
    public ItemShop_Screen(ItemShop itemShopLogic) {
        this.itemShopLogic = itemShopLogic;
        
        // 초기 아이템 목록 가져오기
        this.displayedItems = itemShopLogic.getCurrentItems();

        loadBackgroundImage("res/back_ground.png"); 
        setLayout(null); 
        
        //아이템 박스 좌표 설정
        setupItemAreas();
        setupItemDetailPanel();	//초기화 로직
//        loadItemImage();	//유물더미데이터 이미지 불러오는 함수
        
        addMouseListener(new ShopClickListener());
        setPreferredSize(new Dimension(800, 600));
    }
    
    
    // 아이템 박스 셋팅 함수
    private void setupItemAreas() {
        int itemWidth = 150;
        int itemHeight = 150;
        int gap = 80;
        int center_x = 400;

        //유물 상점 위에 2개 위치지정
        ITEM_AREAS[0] = new Rectangle(center_x - itemWidth - gap/2, 100, itemWidth, itemHeight);
        ITEM_AREAS[1] = new Rectangle(center_x + gap/2, 100, itemWidth, itemHeight);
        //유물 상점 밑에 3개 위치지정
        ITEM_AREAS[2] = new Rectangle(center_x - itemWidth - gap - itemWidth/2, 250, itemWidth, itemHeight);
        ITEM_AREAS[3] = new Rectangle(center_x - itemWidth/2, 250, itemWidth, itemHeight);
        ITEM_AREAS[4] = new Rectangle(center_x + gap + itemWidth/2, 250, itemWidth, itemHeight);
    }
    
    // UI 업데이트 (아이템 목록 새로고침)
    public void updateShopUI(List<ItemInfo> newItems) {
        this.displayedItems = newItems;
        revalidate();
        repaint();
        
    }


    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        // 1. 배경 이미지 그리기
        if (backgroundImage != null) {
            g.drawImage(
                backgroundImage, 
                0, 
                0, 
                getWidth(), 
                getHeight(), 
                this
            );
        }

        // 2. 유물 목록 순회 및 렌더링
        if (displayedItems != null) {
            for (int i = 0; i < displayedItems.size(); i++) {
                java.awt.Rectangle area = ITEM_AREAS[i]; // 유물 표시 영역
                ItemInfo item = (ItemInfo) displayedItems.get(i); 


                 // --- A. 유물 이미지 동적 로드 및 그리기 (수정된 부분) ---
                    java.awt.Image realItemImage = null;
                    try {
                        java.io.File imageFile = new java.io.File(item.getImagePath());
                        realItemImage = javax.imageio.ImageIO.read(imageFile);
                    } catch (java.io.IOException e) {
                        System.err.println("유물 이미지 로드 실패: " + item.getImagePath());
                        // 에러 발생 시 realItemImage는 null 상태로 유지됩니다.
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

                // --- B. 유물 비용 텍스트 그리기 ---
                g.setColor(java.awt.Color.YELLOW);
                g.setFont(new java.awt.Font("맑은 고딕", java.awt.Font.BOLD, 14));
                
                // 티켓 비용 텍스트 준비
                java.lang.String costText = item.getTicketCost() + " Tickets";
                
                // 텍스트 중앙 정렬을 위한 X 좌표 계산
                int textWidth = g.getFontMetrics().stringWidth(costText);
                int textX = area.x + (area.width - textWidth) / 2;
                int textY = area.y + area.height + 20; // 유물 영역 아래쪽에 위치
                
                g.drawString(costText, textX, textY);
            }
        }
        
        // 3. REROLL 영역 그리기 (클릭 영역 표시)
        if (REROLL_AREA != null) {
            g.setColor(java.awt.Color.YELLOW);
            // 사각형 테두리를 그려 REROLL 버튼 영역을 시각적으로 표시합니다.
            g.drawRect(REROLL_AREA.x, REROLL_AREA.y, REROLL_AREA.width, REROLL_AREA.height); 
            
            // REROLL 텍스트 표시 (예시)
            g.setColor(java.awt.Color.WHITE);
            if(itemShopLogic.getFreeReroll_count() > 0) {
                g.drawString("무료 리롤", REROLL_AREA.x + 10, REROLL_AREA.y + REROLL_AREA.height / 2 + 5);

            }
            else {
                g.drawString("리롤", REROLL_AREA.x + 10, REROLL_AREA.y + REROLL_AREA.height / 2 + 5);
            }
        }
        
    }
    
    

    //유물 설명 화면 초기화 메서드
    private void setupItemDetailPanel() {
        // 1. 상세 패널 생성 및 기본 설정
        itemDetailPanel = new javax.swing.JPanel();
        itemDetailPanel.setLayout(null); // 내부 컴포넌트 위치를 직접 지정하기 위해 null로 설정
        itemDetailPanel.setBackground(new java.awt.Color(0, 0, 0, 180)); // 반투명 검은색 배경 (RGB + Alpha)
        itemDetailPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.WHITE));

        // 화면 중앙에 위치하도록 setBounds를 사용하여 크기와 위치를 지정합니다.
        // (예시: 화면의 1/3 크기, 중앙 배치)
        int windowWidth = 800;	//설정한 윈도우창 너비
        int panelW = 500;
        int panelH = 350;
        int panelX = (windowWidth - panelW) / 2; // 예: (800 - 500) / 2 = 150
        int panelY = 50;
        itemDetailPanel.setBounds(panelX, panelY, panelW, panelH);
        
        // 초기에는 숨김 처리
        itemDetailPanel.setVisible(false);

        // 2. 내부 컴포넌트 설정 및 추가
        
        // 이름 라벨 (왼쪽 위)
        nameLabel = new javax.swing.JLabel("유물이름", javax.swing.SwingConstants.LEFT);
        nameLabel.setForeground(java.awt.Color.YELLOW);
        nameLabel.setFont(new java.awt.Font("맑은 고딕", java.awt.Font.BOLD, 22));
        nameLabel.setBounds(10, 10, panelW - 20, 30);
        itemDetailPanel.add(nameLabel);

        // 설명 텍스트 영역 (이름 아래)
        descriptionArea = new javax.swing.JTextArea();
        descriptionArea.setWrapStyleWord(true); // 단어 단위 줄 바꿈
        descriptionArea.setLineWrap(true);       // 줄 바꿈 활성화
        descriptionArea.setEditable(false);      // 수정 불가능
        descriptionArea.setBackground(java.awt.Color.BLACK); 
        descriptionArea.setOpaque(true); // 불투명하도록 설정
        descriptionArea.setForeground(java.awt.Color.WHITE);
        descriptionArea.setFont(new java.awt.Font("맑은 고딕", java.awt.Font.PLAIN, 16));
        descriptionArea.setBounds(10, 50, panelW - 20, 200);
        itemDetailPanel.add(descriptionArea);
        
        // 가격 라벨 (오른쪽 아래)
        priceLabel = new javax.swing.JLabel("", javax.swing.SwingConstants.RIGHT);
        priceLabel.setForeground(java.awt.Color.GREEN);
        priceLabel.setFont(new java.awt.Font("맑은 고딕", java.awt.Font.BOLD, 18));
        priceLabel.setBounds(10, panelH - 70, panelW - 20, 20);
        itemDetailPanel.add(priceLabel);

        // 구매 버튼 (하단)
        buyButton = new javax.swing.JButton("구매 (BUY)");
        buyButton.setBounds(panelW / 2 - 110, panelH - 40, 100, 30);
        itemDetailPanel.add(buyButton);
        
        // 취소 버튼 (하단)
        cancelButton = new javax.swing.JButton("취소 (CANCEL)");
        cancelButton.setBounds(panelW / 2 + 10, panelH - 40, 100, 30);
        itemDetailPanel.add(cancelButton);

        // 메인 패널에 상세 정보 패널 추가
        this.add(itemDetailPanel);
        
        // 3. 버튼 리스너 연결
        setupButtonListeners();
    }

    //유물 정보 자세히 보기
    public void showItemDetails(ItemInfo item, int index) {
        this.selectedItem = item;
        this.selectedItemIndex = index;

     // 1. 컴포넌트에 유물 정보 설정
        nameLabel.setText(item.getName());
        // getDescription() 메서드는 ItemInfo.class에 정의되어 있습니다.
        descriptionArea.setText(item.getDescription()); 
        priceLabel.setText("가격: " + item.getTicketCost() + " Tickets");
        
        // 2. 패널 보이기
        itemDetailPanel.setVisible(true);
        itemDetailPanel.requestFocusInWindow();
        
        // 3. UI 갱신 요청
        repaint();
    }
    
    public enum PurchaseResult {
        SUCCESS,       // 구매 성공
        INSUFFICIENT_TICKETS, // 티켓 부족
        ALREADY_SOLD   // 이미 판매된 유물
    }
    
    //구매버튼 리스너
    private void setupButtonListeners() {
    	
    	// 취소 버튼 로직
        cancelButton.addActionListener(e -> {
            itemDetailPanel.setVisible(false);
            selectedItem = null;
            selectedItemIndex = -1;
            repaint();
        });
        
        //구매 버튼 로직
        buyButton.addActionListener(e -> {
            if (selectedItem != null) {
                // ItemShop의 buy_item 로직 호출 (ItemShop.class의 buy_item(I)Z 메서드 사용)
            	ItemShop.PurchaseResult result = itemShopLogic.buy_item(selectedItemIndex);
            	
            	// 2. 결과에 따라 처리 분기
                switch (result) {
                    case SUCCESS:
                        // 구매 성공
                        // UI 갱신 (구매된 슬롯을 '판매 완료'로 표시)
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
                }
            }
            // 상세 패널 숨기기
            itemDetailPanel.setVisible(false);
            selectedItem = null;
            selectedItemIndex = -1;
            repaint(); // 화면 갱신
        });

        // 취소 버튼 리스너
        cancelButton.addActionListener(e -> {
            // 상세 패널 숨기기
            itemDetailPanel.setVisible(false);
            selectedItem = null;
            selectedItemIndex = -1;
            repaint(); // 화면 갱신
        });
    }
    
    
    
    /**----------------------마우스 클릭 리스너--------------------------*/
	//마우스 눌렀을때
    private class ShopClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            Point clickedPoint = e.getPoint();
            //상세 패널이 이미 보이는 중이라면, 다른 이벤트는 무시
            if (itemDetailPanel.isVisible()) {
                return;
            }

            //리롤 버튼 클릭 처리
            if (REROLL_AREA.contains(clickedPoint)) {
                List<ItemInfo> newItems = itemShopLogic.rerollItems();
                updateShopUI(newItems);
                // TODO: 리롤 결과에 따른 알림 메시지 (예: 비용 부족) 처리
                
            } 
            
            
            // 2. 아이템 박스 클릭 처리
            else {
                for (int i = 0; i < ITEM_AREAS.length; i++) {
                    if (ITEM_AREAS[i].contains(clickedPoint)) {
                    	// 유물 클릭 감지!
                        if (displayedItems != null && i < displayedItems.size()) {
                            ItemInfo clickedItem = (ItemInfo) displayedItems.get(i);
                            // 3. 상세 정보 표시 메서드 호출
                            showItemDetails(clickedItem, i); 
                        }
                        return; // 클릭 처리 완료 후 종료
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


    
    // ... (loadBackgroundImage 메소드, Dimension 설정 등 생략) ...
}