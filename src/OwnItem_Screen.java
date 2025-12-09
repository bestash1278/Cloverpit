// OwnedItems_Screen.java (새 파일 생성)

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OwnItem_Screen extends JPanel {
    private final OwnItem ownItemLogic;
    private JPanel displayPanel; 

    public OwnItem_Screen(OwnItem ownItemLogic) {
        this.ownItemLogic = ownItemLogic;
        setLayout(new BorderLayout());
        
        initializeUI();
        loadAndDisplayItems();
    }

    private void initializeUI() {
        JLabel titleLabel = new JLabel("현재 소유한 유물", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 유물 이미지를 담을 패널 (FlowLayout으로 가로 나열)
        displayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        JScrollPane scrollPane = new JScrollPane(displayPanel);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    
    /**
     * OwnedItems 로직에서 목록을 가져와 UI를 갱신하고 유물을 표시합니다.
     */
    public void loadAndDisplayItems() {
    	if (displayPanel == null) {
            return; 
        }
        displayPanel.removeAll(); 
        
        // 1. 이미지 경로 리스트를 가져옵니다.
        List<String> imagePaths = ownItemLogic.getOwnedItemImagePaths();
        List<String> itemNames = ownItemLogic.getOwnedItemNames();
        
        if (imagePaths.isEmpty()) {
            displayPanel.add(new JLabel("소유한 유물이 없습니다."));
        } else {
            for (int i = 0; i < imagePaths.size(); i++) {
                String path = imagePaths.get(i);
                String name = itemNames.get(i);
                
                try {
                    // 2. 경로를 사용하여 이미지를 로드하고 JComponent에 담습니다.
                    ImageIcon originalIcon = new ImageIcon(path);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    
                    // 3. 이미지와 이름을 포함하는 컴포넌트 생성
                    JPanel itemTile = createItemTile(icon, name);
                    displayPanel.add(itemTile);
                    
                } catch (Exception e) {
                    // 이미지 로드 실패 시 대체 라벨
                    displayPanel.add(new JLabel("이미지 로드 실패: " + name));
                }
            }
        }

        revalidate(); // ⭐ 필수
        repaint();    // ⭐ 필수   
    }
    
    // 유물 이미지와 이름을 표시하는 작은 타일 생성
    private JPanel createItemTile(ImageIcon icon, String name) {
        JPanel tile = new JPanel(new BorderLayout());
        tile.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        // 이미지 라벨
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // 이름 라벨
        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        
        tile.add(imageLabel, BorderLayout.CENTER);
        tile.add(nameLabel, BorderLayout.SOUTH);
        
        return tile;
    }
    
    // 외부에서 갱신을 요청할 때 사용 (예: SlotMachinePanel에서)
    public void updateUI() {
    	super.updateUI();
        loadAndDisplayItems();
    }
}