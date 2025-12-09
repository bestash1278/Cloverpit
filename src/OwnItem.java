// OwnedItems.java (새 파일 생성)

import java.util.List;
import java.util.stream.Collectors;
import java.awt.Image;
import javax.swing.ImageIcon;

public class OwnItem {
    private final User user;

    public OwnItem(User user) {
        this.user = user;
    }

    /**
     * User 객체에서 소유한 모든 유물의 이름을 가져옵니다.
     * @return 소유 유물 이름 (String) 리스트
     */
    public List<String> getOwnedItemNames() {
        return user.getOwnItem_List(); 
    }

    /**
     * 소유한 유물 이름 리스트를 기반으로 이미지 경로를 찾습니다.
     * ItemInfo 클래스의 정적 맵을 사용합니다.
     * @return 이미지 경로 (String) 리스트
     */
    public List<String> getOwnedItemImagePaths() {
        List<String> itemNames = getOwnedItemNames();
        
        // ItemInfo 클래스에 구현된 정적 메서드를 사용
        return itemNames.stream()
                .map(ItemInfo::getImagePathByName) 
                .collect(Collectors.toList());
    }
}