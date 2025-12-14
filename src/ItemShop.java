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
    
 // ItemShop.class (필드 및 초기화 블록)

 // 1. 모든 유물을 정의하는 마스터 풀을 선언
    private static final java.util.List<ItemInfo> ALL_ARTIFACTS;

    static {
        // ItemInfo.class에 정의된 '인수가 없는 기본 생성자'를 사용하여 유물을 생성합니다.
        ALL_ARTIFACTS = java.util.List.of(
            // ⭐ 인수를 전달하지 않고 기본 생성자만 호출합니다.
            new ItemInfo.HealthPotionArtifact(),
            new ItemInfo.golden_compass(),  // 6번째 슬롯
            new ItemInfo.symbol_train(),  // 6번째 슬롯
            new ItemInfo.pattern_train(),  // 6번째 슬롯
            new ItemInfo.PersistentBonusArtifact(),
            new ItemInfo.NextSpinOnlyArtifact(),     // <--- 새로 만든 단발성 유물 (Type 2)
            new ItemInfo.HealthPotionArtifact(),
            
            //테스트 유물
            new ItemInfo.TestPersistentArtifact(), 
            new ItemInfo.TestTemporaryArtifact(),
            new ItemInfo.pattern_train(),

            // 여기에 다른 유물 클래스들의 기본 생성자 호출을 추가합니다.
            new ItemInfo.symbol_chain(),
            new ItemInfo.symbol_repeat(),
            new ItemInfo.symbol_ticket(),
            new ItemInfo.symbol_token(),
            new ItemInfo.LemonStackArtifact()
            // ... (모든 유물 정의)
        );
    }
    
    

    // 초기 상점 아이템을 설정하거나 리롤하는 함수
    private void initializeShop() {
    	if (this.currentItems == null) { // ⭐ 최초 1회만 실행되도록 조건 추가
            this.currentItems = createRandomItems();
            System.out.println("ItemShop: 상점 목록 최초 생성 완료.");
    	}
    }
    
    
 // 무작위로 5개의 유물을 뽑아 반환하는 함수
    private java.util.List<ItemInfo> createRandomItems() {
        // 1. ALL_ARTIFACTS 리스트를 복사 (원본 보호)
        java.util.List<ItemInfo> itemsToShuffle = new java.util.ArrayList<>(ALL_ARTIFACTS);
        
        // 2. 리스트를 무작위로 섞습니다.
        java.util.Collections.shuffle(itemsToShuffle);
        
        // 3. 섞인 리스트에서 최대 5개의 유물을 선택하여 반환합니다.
        int count = java.lang.Math.min(5, itemsToShuffle.size());
        
        return itemsToShuffle.subList(0, count);
    }
    
    private boolean tryRerollCost() {
        int Reroll_cost = userInfo.getItemReroll_count() * 2 * userInfo.getRound();
        
        // 1. 무료 리롤 횟수 확인 및 사용
        if (userInfo.getFreeItemReroll_count() > 0) {
            // 무료 리롤 사용: 무료 횟수 1 차감
            userInfo.addFreeItemReroll_count(-1); 
            System.out.println("무료 리롤 사용 성공. 남은 무료 리롤횟수: " + userInfo.getFreeItemReroll_count());
            
            // 상태바 갱신은 메인 로직(rerollItems)에서 한 번에 처리합니다.
            return true;
        }
        
        // 2. 돈으로 리롤 시도
        else {
            if (userInfo.getRoulatte_money() >= Reroll_cost) { // 금액 비교 >=로 변경
                userInfo.addRoulatte_money(-Reroll_cost);    // 계산식: (유물 리롤 횟수 * 2 * 라운드수)
                userInfo.addItemReroll_count();                // 유물 리롤 카운트 증가
                
                // 갱신은 rerollItems에서 처리되므로 여기서는 제거합니다.
                
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
        // 1. 리롤 비용 확인 및 차감
        if (!tryRerollCost()) {
            return null; 
        }
        // 2. 소유 유물 목록 가져오기 (ItemInfo 이름 목록)
        List<String> ownedItemNames = userInfo.getOwnedItemNames();
        
        // 3. 새로운 상점 목록 후보 (구매 가능 유물) 생성
        List<ItemInfo> availableArtifacts = new ArrayList<>();
        
        for (ItemInfo artifact : ALL_ARTIFACTS) {
        	String name = artifact.getName();
            boolean isOwned = ownedItemNames.contains(name);
            if (!isOwned) {
                availableArtifacts.add(artifact);
            }
            else if (artifact.getDurationType() == DurationType.STACKABLE) {
                // 현재 몇 개 가지고 있는지 확인 (User.java에 추가했던 메서드 활용)
                int currentStack = userInfo.getItemStackCount(name);
                
                // 최대 스택보다 적게 가지고 있다면 다시 상점에 나올 수 있음
                if (currentStack < artifact.getMaxStack()) {
                    availableArtifacts.add(artifact);
                    System.out.println("DEBUG: 스택형 유물 [" + name + "] 재등장 가능 (" + currentStack + "/" + artifact.getMaxStack() + ")");
                }
            }
        
            
        }
        // 리스트를 섞고, 앞에서 itemsToSelect 개를 선택
        Collections.shuffle(availableArtifacts, new Random());
        // 4. 새로운 5개 아이템을 무작위로 선택
        int itemsToSelect = Math.min(5, availableArtifacts.size());
        
        List<ItemInfo> newItems = new ArrayList<>(availableArtifacts.subList(0, itemsToSelect));
        
        // 5. 상점 목록을 5개로 채우기 (5개 미만인 경우 SoldArtifact로 채움)
        while (newItems.size() < 5) {
            newItems.add(new ItemInfo.SoldArtifact());
        }
        
        // 6. 현재 상점 목록 업데이트
        this.currentItems = newItems;
        
        // 상태바 갱신 요청 (리롤 횟수 차감 반영)
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

        // 1. 판매된 유물인지 확인 (SoldArtifact는 재구매 불가)
        if (item instanceof ItemInfo.SoldArtifact) {
            return PurchaseResult.ALREADY_SOLD;
        }

        // 최대유물 갯수를 넘지 않는지
        boolean isStackable = (item.getDurationType() == DurationType.STACKABLE);
        boolean hasItem = userInfo.getItemStackCount(item.getName()) > 0;
        
        // "스택형이면서 이미 가지고 있는 경우"가 아니라면, 인벤토리 공간을 확인해야 함
        if (!(isStackable && hasItem)) {
            int currentItemsCount = userInfo.getUserItem_List().size();
            if (currentItemsCount >= userInfo.getItem_max()) {
                return PurchaseResult.INVENTORY_FULL;
            }
        }
        //아이템 가격 가져오기
        int cost = item.getTicketCost();
        
        // 2. UserInfo에서 티켓 차감 시도
        if (userInfo.minusTicket(cost)) {

            //해당 아이템 스택 +1
            userInfo.addItemStack(item.getName());
            // 3. 티켓 차감 성공: 유물 효과 적용
            item.applyEffect(userInfo);
            System.out.println("DEBUG: 스택 증가 완료 -> " + userInfo.getItemStackCount(item.getName()));
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
                    System.out.println("스택형 유물: 리롤 목록에 유지됨 (" + currentStack + "/" + maxStack + ")");
                } else {
                    // 최대 스택 도달 -> 제거 진행
                    System.out.println("스택형 유물: 최대 스택 도달. 리롤 목록에서 제거.");
                }
            }

            if (shouldRemoveFromPool) {
                removeItemFromPool(item.getName());
            }
            // 4. 상점 UI 업데이트: '판매된 유물' 객체로 교체
            ItemInfo soldItem = new ItemInfo.SoldArtifact();
            currentItems.set(itemIndex, soldItem);
            
            updateMainStatus.run();
            if (this.updateOwnItemScreen != null) {
                this.updateOwnItemScreen.run(); 
                System.out.println("ItemShop: 소유 유물 화면 갱신 요청 완료.");
            }
            
            return PurchaseResult.SUCCESS; // 구매 성공
        } else {
            // 티켓 차감 실패 (티켓 부족)
            return PurchaseResult.INSUFFICIENT_TICKETS; 
        }
    }
 // 유물 목록에서 이름을 찾아 안전하게 제거하는 헬퍼 함수
    private void removeItemFromPool(String targetName) {
        // availableItems 리스트를 순회하며 이름이 같은 객체 제거
        availableItems.removeIf(info -> info.getName().equals(targetName));
    }
    
    // ItemShopScreen이 현재 아이템 목록을 가져가서 화면을 그릴 때 사용
    public List<ItemInfo> getCurrentItems() {
        return this.currentItems;
    }
    
    // ... 기타 UI 표시를 위한 Getter (예: 리롤 비용 등) ...
}

