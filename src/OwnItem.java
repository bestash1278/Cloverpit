// OwnItem.java (OwnedItems 클래스를 사용하셨다면 파일명을 맞춰주세요)

import java.util.List;
import java.util.stream.Collectors;

public class OwnItem {
    private final User user;
    private final Runnable updateMainStatus; // 상태바 갱신용 Runnable

    public OwnItem(User user, Runnable updateMainStatus) {
        this.user = user;
        this.updateMainStatus = updateMainStatus;
    }

    public List<String> getOwnedItemNames() {
        return user.getUserItem_List(); 
    }

    // ⭐ 유물 이름으로 ItemInfo 템플릿을 가져옵니다.
    public ItemInfo getItemInfoByName(String itemName) {
        return ItemInfo.getArtifactTemplateByName(itemName);
    }

    /**
     * 유물을 판매 처리하고 사용자에게 돈을 환급합니다.
     * @param itemName 판매할 유물의 이름
     * @return 판매 성공 시 true
     */
    public boolean sell_item(String itemName) {
        ItemInfo item = getItemInfoByName(itemName);
        if (item == null || !(item instanceof ItemInfo)) {
            return false;
        }

        // 1. 유물 효과 제거 (⭐ 중요: 지속 효과 유물이라면 여기서 효과를 되돌려야 합니다.)
        // 현재는 즉발 효과 유물만 있다고 가정하고 이 부분은 생략합니다.

        // 2. 환급 금액 계산 (티켓 가격의 절반을 1티켓 = 100000 돈으로 가정하고 환급)
        int ticketCost = item.getTicketCost();
        int refundTickets = ticketCost / 2; // 요청하신 절반 티켓 환급

        // 3. User 데이터에서 유물 제거 및 돈 환급
        if (user.removeUserItem_List(itemName)) {
            user.addTicket(refundTickets);
            
            // 4. 상태바 갱신
            if (updateMainStatus != null) {
                updateMainStatus.run();
                System.out.println("OwnItem: 유물 판매 완료. " + refundTickets + " 티켓 환급.");
            }
            return true;
        }

        return false;
    }
}