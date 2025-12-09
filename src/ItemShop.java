// ItemShop.java
import java.util.List;

public class ItemShop {
    private final User userInfo; // 사용자 티켓/돈 정보를 위해 의존성 주입
    private List<ItemInfo> currentItems; // 현재 상점에 표시되는 5개 아이템 목록
    private final Runnable updateMainStatus;
    private List<ItemInfo> displayedItems;


    public ItemShop(User userInfo, Runnable updateMainStatus) {
        this.userInfo = userInfo;
        this.updateMainStatus = updateMainStatus; //메인슬룻패널 스테이터스바 업데이트
        this.displayedItems = getCurrentItems();

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
            new ItemInfo.IncreaseInterestRateArtifact(),  
            new ItemInfo.PlaceholderArtifact4(), // 4번째 슬롯
            new ItemInfo.PlaceholderArtifact5(),  // 5번째 슬롯
            new ItemInfo.PlaceholderArtifact6(),  // 5번째 슬롯

            // 여기에 다른 유물 클래스들의 기본 생성자 호출을 추가합니다.
            // 예: new ItemInfo.MoneyTreeArtifact(),
            new ItemInfo.HealthPotionArtifact() 
            // ... (모든 유물 정의)
        );
    }
 // ... 나머지 ItemShop 필드 (userInfo, currentItems 등)
    
    
    

    // 초기 상점 아이템을 설정하는 함수
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
    
    // ⭐ 새로 추가: 리롤만 할 때 티켓을 차감하는 함수 (핵심)
    public boolean useItemForReroll() {
    	int Reroll_cost = userInfo.getItemReroll_count() * 2 * userInfo.getRound();
    	if(userInfo.getFreeItemReroll_count() > 0) {
			System.out.println("무료 리롤 사용 성공. 남은 무료 리롤횟수: " + userInfo.getFreeItemReroll_count());
    		return true;
    	}
    	else {
    		if (userInfo.getRoulatte_money() > Reroll_cost) {
    			userInfo.addRoulatte_money(- Reroll_cost);	//계산식 : (유물 리롤 횟수 * 2 * 라운드수)
    			userInfo.addItemReroll_count();	//전화 리롤 카운트 증가
    		    if (this.updateMainStatus != null) {
    		        this.updateMainStatus.run(); // 메인 화면 갱신 요청!
    		        System.out.println("Payment: 메인 스테이터스 바 갱신 요청 완료.");
    		    }
    			System.out.println("리롤 사용 성공. 남은 금액: " + userInfo.getRoulatte_money());
    			return true;
        }
        return false;
    	}
    }
    
    public int getFreeReroll_count() {
    	return userInfo.getFreeItemReroll_count();
    }
    
    // 리롤 버튼 클릭 시 호출될 함수
    public List<ItemInfo> rerollItems() {
        // TODO: 리롤 비용(예: 1 티켓)을 차감하는 로직 구현
        useItemForReroll();
        // 새로운 아이템 목록으로 갱신
    	this.currentItems = createRandomItems(); // 새로운 아이템 생성
    	this.userInfo.addItemReroll_count();
    	System.out.println("ItemShop: 리롤 성공. 새 목록 크기: " + this.currentItems.size()); //디버깅용
    	
        // 갱신된 아이템 목록을 ItemShopScreen에 반환
        return this.currentItems;
    }
    
    
    public enum PurchaseResult {
        SUCCESS,       // 구매 성공
        INSUFFICIENT_TICKETS, // 티켓 부족
        ALREADY_SOLD   // 이미 판매된 유물
    }

    /**
     * 유물 구매를 시도합니다.
     * @param itemIndex 구매할 유물의 currentItems 내 인덱스
     * @return 구매 성공 시 true, 실패 시 (티켓 부족 등) false
     */
    public PurchaseResult buy_item(int itemIndex) {
        if (currentItems == null || itemIndex < 0 || itemIndex >= currentItems.size()) {
            return PurchaseResult.ALREADY_SOLD; // 유효하지 않은 인덱스
        }

        ItemInfo item = (ItemInfo) currentItems.get(itemIndex);

        // 1. 판매된 유물인지 확인 (SoldArtifact는 재구매 불가)
        if (item instanceof ItemInfo.SoldArtifact) {
            return PurchaseResult.ALREADY_SOLD;
        }
        
        int cost = item.getTicketCost();
        
        if (userInfo.minusTicket(cost)) {
            // 3. 티켓 차감 성공: 유물 효과 적용
            item.applyEffect(userInfo);
            
            // 4. 상점 UI 업데이트: '판매된 유물' 객체로 교체
            ItemInfo soldItem = new ItemInfo.SoldArtifact();
            currentItems.set(itemIndex, soldItem);
            
            if (this.updateMainStatus != null) {
                this.updateMainStatus.run(); 
                System.out.println("ItemShop: 스테이터스 바 갱신 요청 완료.");
            }
            
            return PurchaseResult.SUCCESS; // 구매 성공
        } else {
            return PurchaseResult.INSUFFICIENT_TICKETS; 
        }
    }

    // ItemShopScreen이 현재 아이템 목록을 가져가서 화면을 그릴 때 사용
    public List<ItemInfo> getCurrentItems() {
        return this.currentItems;
    }
    
}