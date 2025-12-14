// ItemShop.java
import java.util.List;
import java.util.ArrayList; 
import java.util.Collections;
import java.util.Random;

public class ItemShop {
    private final User userInfo; // 사용자 티켓/돈 정보를 위해 의존성 주입
    private List<ItemInfo> currentItems; // 현재 상점에 표시되는 5개 아이템 목록
    private Runnable updateMainStatus; // 상태바 업데이트를 위한 Runnable 인터페이스
    private final Runnable updateOwnItemScreen;
    private List<ItemInfo> availableItems;//등장 가능한 유물 목록

    public ItemShop(User userInfo,Runnable updateMainStatus, Runnable updateOwnItemScreen) {
        this.userInfo = userInfo;
        this.updateMainStatus = updateMainStatus;
        this.updateOwnItemScreen = updateOwnItemScreen;
        this.availableItems = new ArrayList<>(ALL_ARTIFACTS);//등장 가능한 유물 초기화
        initializeShop(); // 초기 아이템 목록 설정
    }

    private static final java.util.List<ItemInfo> ALL_ARTIFACTS;

    static {
        ALL_ARTIFACTS = java.util.List.of(
            //***************추가된 유물이 상점에 나오도록 생성자 추가해줘야 합니다. ****************
            new ItemInfo.HealthPotionArtifact(),
            new ItemInfo.golden_compass(),  
            new ItemInfo.symbol_train(),  
            new ItemInfo.pattern_train(), 
            new ItemInfo.PersistentBonusArtifact(),
            new ItemInfo.NextSpinOnlyArtifact(),  
            new ItemInfo.HealthPotionArtifact(),
            
            //테스트 유물
            new ItemInfo.TestPersistentArtifact(), 
            new ItemInfo.TestTemporaryArtifact(),
            new ItemInfo.pattern_train(),

            new ItemInfo.symbol_chain(),
            new ItemInfo.symbol_repeat(),
            new ItemInfo.symbol_ticket(),
            new ItemInfo.symbol_token(),
            new ItemInfo.LemonStackArtifact()

        );
    }
    
    
    // 초기 상점 아이템을 설정하거나 리롤하는 함수
    private void initializeShop() {
    	if (this.currentItems == null) {
            this.currentItems = createRandomItems();
            System.out.println("ItemShop: 상점 목록 최초 생성 완료.");
    	}
    }
    
    
    // 무작위로 5개의 유물을 뽑아 반환하는 함수
    private java.util.List<ItemInfo> createRandomItems() {
        java.util.List<ItemInfo> itemsToShuffle = new java.util.ArrayList<>(ALL_ARTIFACTS);
        java.util.Collections.shuffle(itemsToShuffle);	//무작위로 섞기
        int count = java.lang.Math.min(5, itemsToShuffle.size());	//섞인 유물에서 5개 뽑습니다.
        return itemsToShuffle.subList(0, count);
    }
    //리롤 비용 계산 함수
    private boolean tryRerollCost() {
        int Reroll_cost = userInfo.getItemReroll_count() * 2 * userInfo.getRound();

        if (userInfo.getFreeItemReroll_count() > 0) {	//무료 리롤있는지
            userInfo.addFreeItemReroll_count(-1); 
            System.out.println("무료 리롤 사용 성공. 남은 무료 리롤횟수: " + userInfo.getFreeItemReroll_count());
            
            return true;
        }
        
        //돈으로 리롤 시도
        else {
            if (userInfo.getRoulatte_money() >= Reroll_cost) { 
                userInfo.addRoulatte_money(-Reroll_cost);    // 계산식: (유물 리롤 횟수 * 2 * 라운드수)
                userInfo.addItemReroll_count();                // 유물 리롤 카운트 증가
                System.out.println("리롤 사용 성공. 차감 금액: " + Reroll_cost + " 남은 금액: " + userInfo.getRoulatte_money());
                return true;
            }
            System.out.println("리롤 실패. 비용 (" + Reroll_cost + ")이 부족합니다. 남은 금액: " + userInfo.getRoulatte_money());
            return false;
        }
    }
    
    
    /**
     * 현재 상점 유물 목록을 리롤하고 새로운 목록을 반환합니다.
     * @return 새로 리롤된 5개의 유물 목록. (리롤 비용 부족 시 null)
     */
    public List<ItemInfo> rerollItems() {
        if (!tryRerollCost()) {
            return null; 
        }
        List<String> ownedItemNames = userInfo.getOwnedItemNames();
        List<ItemInfo> availableArtifacts = new ArrayList<>();
        
        for (ItemInfo artifact : ALL_ARTIFACTS) {
        	String name = artifact.getName();
            boolean isOwned = ownedItemNames.contains(name);
            if (!isOwned) {
                availableArtifacts.add(artifact);
            }
            else if (artifact.getDurationType() == DurationType.STACKABLE) {
                int currentStack = userInfo.getItemStackCount(name);
                
                if (currentStack < artifact.getMaxStack()) { //스택형 유물, 풀스택이 아니면 상점에 재등장
                    availableArtifacts.add(artifact);	//상점에 재등장
                }
            }
        
            
        }
        Collections.shuffle(availableArtifacts, new Random());
        int itemsToSelect = Math.min(5, availableArtifacts.size());
        List<ItemInfo> newItems = new ArrayList<>(availableArtifacts.subList(0, itemsToSelect));
        //상점목록이 5개보다 적으면
        while (newItems.size() < 5) {
            newItems.add(new ItemInfo.SoldArtifact());
        }
        this.currentItems = newItems;

        if (this.updateMainStatus != null) {
            this.updateMainStatus.run();
        }

        return this.currentItems;
    }
    
    
    public enum PurchaseResult {
        SUCCESS,       // 구매 성공
        INSUFFICIENT_TICKETS, // 티켓 부족
        ALREADY_SOLD,  // 이미 판매된 유물
        INVENTORY_FULL	//인벤토리 가득참
    }

    /**
     * 유물 구매를 시도합니다.
     * @param itemIndex 구매할 유물의 currentItems 내 인덱스
     * @return 구매 성공 시 true, 실패 시 (티켓 부족 등) false
     */
    public PurchaseResult buy_item(int itemIndex) {
    	//유물 상점이 비어있지 않고 보여줄수있는 만큼 보여주기
        if (currentItems == null || itemIndex < 0 || itemIndex >= currentItems.size()) {
            return PurchaseResult.ALREADY_SOLD;
        }
        //상점에 보여주기 위해 생성한 아이템들 목록
        ItemInfo item = (ItemInfo) currentItems.get(itemIndex);

        //판매된 유물인지 확인
        if (item instanceof ItemInfo.SoldArtifact) {
            return PurchaseResult.ALREADY_SOLD;
        }

        // 최대유물 갯수를 넘지 않는지
        boolean isConsumable = (item.getDurationType() == DurationType.CONSUMABLE);
        boolean isStackable = (item.getDurationType() == DurationType.STACKABLE);
        boolean hasItem = userInfo.getItemStackCount(item.getName()) > 0;
        boolean isInstant = (item.getDurationType() == DurationType.INSTANT);
        
     // 인벤토리 체크: "즉발형도 아니고" AND "(스택형이면서 이미 가진 경우)도 아니라면" -> 공간 필요
        if (!isInstant && !(isStackable && hasItem)) {
            int currentItemsCount = userInfo.getUserItem_List().size();
            if (currentItemsCount >= userInfo.getItem_max()) {
                return PurchaseResult.INVENTORY_FULL;
            }
        }

        int cost = item.getTicketCost(); //아이템 가격 가져오기
        
        if (userInfo.minusTicket(cost)) {
        	if (item.getDurationType() == DurationType.INSTANT) {
                // 1. 즉발형: 구매 즉시 효과 발동
                item.applyEffect(userInfo); 
                System.out.println("즉발형 아이템 사용됨: " + item.getName());
                // 2. 인벤토리에 추가하지 않음 (addOwnedItemName 호출 X)
        	}
        	else {
	            //해당 아이템 스택 +1
	            userInfo.addItemStack(item.getName());
	            
                if (!isStackable || !hasItem) {
                    userInfo.addOwnedItemName(item.getName());
                }

                //단발형(CONSUMABLE)이라면 지속 횟수 설정
                if (isConsumable) {
                    userInfo.setItemDuration(item.getName(), item.getActiveTurns());
                    System.out.println("단발형 아이템 구매: " + item.getName() + " (횟수: " + item.getActiveTurns() + ")");
                }

	            //기본아이템들은 구매하면 더 이상 상점에서 안나옴
	            boolean shouldRemoveFromPool = true; // 기본적으로는 구매하면 목록에서 제거
	            //스택형 유물이라면
	            if (item.getDurationType() == DurationType.STACKABLE) {
	                // 현재 스택 확인
	                int currentStack = userInfo.getItemStackCount(item.getName());
	                int maxStack = item.getMaxStack();
	                //스택 남았으면
	                if (currentStack < maxStack) {
	                    shouldRemoveFromPool = false; 
	                }
	            }
	            
	
	            if (shouldRemoveFromPool) {
	                removeItemFromPool(item.getName());
	            }
        	}
            //'판매된 유물' 객체로 교체
            ItemInfo soldItem = new ItemInfo.SoldArtifact();
            currentItems.set(itemIndex, soldItem);
            
            updateMainStatus.run();
            if (this.updateOwnItemScreen != null) {
                this.updateOwnItemScreen.run(); 
                System.out.println("ItemShop: 소유 유물 화면 갱신 요청 완료.");
            }
            
            return PurchaseResult.SUCCESS; // 구매 성공
        } else {
            // 티켓 차감 실패
            return PurchaseResult.INSUFFICIENT_TICKETS; 
        }
    }
    
    private void removeItemFromPool(String targetName) {
        availableItems.removeIf(info -> info.getName().equals(targetName));
    }
    
    public List<ItemInfo> getCurrentItems() {
        return this.currentItems;
    }
}

