import java.util.List;

public class OwnItem {
    private final User user;
    private final Runnable updateMainStatus; 

    public OwnItem(User user, Runnable updateMainStatus) {
        this.user = user;
        this.updateMainStatus = updateMainStatus;
    }

    public List<String> getOwnedItemNames() {
        return user.getUserItem_List(); 
    }

    public ItemInfo getItemInfoByName(String itemName) {
        return ItemInfo.getArtifactTemplateByName(itemName);
    }

    /**
     * 특정 유물의 스택 개수를 반환합니다.
     * @param itemName 유물 이름
     * @return 스택 개수 (스택형이 아니거나 없으면 0)
     */
    public int getItemStackCount(String itemName) {
        return user.getItemStackCount(itemName);
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

        int ticketCost = item.getTicketCost();
        int refundTickets = ticketCost / 2; //절반 티켓 환급
        //유물제거
        if (user.removeUserItem_List(itemName)) {
            user.addTicket(refundTickets);//판매티켓 추가
            
            if (updateMainStatus != null) {
                updateMainStatus.run();
                System.out.println("OwnItem: 유물 판매 완료. " + refundTickets + " 티켓 환급.");
            }
            return true;
        }

        return false;
    }
}

