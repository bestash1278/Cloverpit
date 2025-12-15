
import java.util.List;
import java.util.ArrayList;

public class ItemShop {
    private final User userInfo; // 사용자 티켓/돈 정보를 위해 의존성 주입
    private List<ItemInfo> currentItems; // 현재 상점에 표시되는 5개 아이템 목록
    private Runnable updateMainStatus; // 상태바 업데이트를 위한 Runnable 인터페이스
    private final Runnable updateOwnItemScreen;
    private List<ItemInfo> availableItems;//등장 가능한 유물 목록
    private SaveManagerCsv saveManager;
    
    public void setSaveManager(SaveManagerCsv saveManager) {
        this.saveManager = saveManager;
    }

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
            
            //테스트 유물
            new ItemInfo.TestPersistentArtifact(), 
            new ItemInfo.TestTemporaryArtifact(),
            new ItemInfo.pattern_train(),

            new ItemInfo.symbol_chain(),
            new ItemInfo.symbol_repeat(),
            new ItemInfo.symbol_ticket(),
            new ItemInfo.symbol_token(),
            new ItemInfo.LemonStackArtifact(),
            new ItemInfo.RefreshingCherryArtifact(),
            new ItemInfo.CherryStackArtifact(),
            new ItemInfo.RefreshingCloverArtifact(),
            new ItemInfo.CloverStackArtifact(),
            new ItemInfo.RefreshingBellArtifact(),
            new ItemInfo.BellStackArtifact(),
            new ItemInfo.RefreshingDiamondArtifact(),
            new ItemInfo.DiamondStackArtifact(),
            new ItemInfo.RefreshingTreasureArtifact(),
            new ItemInfo.TreasureStackArtifact(),
            new ItemInfo.RefreshingSevenArtifact(),
            new ItemInfo.SevenStackArtifact(),
            new ItemInfo.GoldenLemon(),
            new ItemInfo.GoldenCherry(),
            new ItemInfo.GoldenClover(),
            new ItemInfo.GoldenBell(),
            new ItemInfo.GoldenDiamond(),
            new ItemInfo.GoldenTreasure(),
            new ItemInfo.GoldenSeven(),
            new ItemInfo.DoubleChanceArtifact(),
            new ItemInfo.TicketSavingsBox(),
            new ItemInfo.LuckyCoin(),
            new ItemInfo.RerollCoupon(),
            new ItemInfo.InventoryExpansion(),
            new ItemInfo.SpinBonusArtifact(),
            new ItemInfo.HighRiskArtifact(),
            new ItemInfo.ModifierAmplifier(),
            new ItemInfo.CompoundCalculator()

        );
    }
    
    
    // 가중치 기반 유물 선택 (rerollItems에서도 사용)
    private ItemInfo selectByWeight(java.util.Random random, 
                                     java.util.List<ItemInfo> common, 
                                     java.util.List<ItemInfo> rare, 
                                     java.util.List<ItemInfo> epic, 
                                     java.util.List<ItemInfo> legendary) {
        int totalWeight = WEIGHT_COMMON + WEIGHT_RARE + WEIGHT_EPIC + WEIGHT_LEGENDARY;
        int randomValue = random.nextInt(totalWeight);
        
        int cumulativeWeight = 0;
        
        // 커먼
        cumulativeWeight += WEIGHT_COMMON;
        if (randomValue < cumulativeWeight && !common.isEmpty()) {
            return common.get(random.nextInt(common.size()));
        }
        
        // 레어
        cumulativeWeight += WEIGHT_RARE;
        if (randomValue < cumulativeWeight && !rare.isEmpty()) {
            return rare.get(random.nextInt(rare.size()));
        }
        
        // 에픽
        cumulativeWeight += WEIGHT_EPIC;
        if (randomValue < cumulativeWeight && !epic.isEmpty()) {
            return epic.get(random.nextInt(epic.size()));
        }
        
        // 레전더리
        if (!legendary.isEmpty()) {
            return legendary.get(random.nextInt(legendary.size()));
        }
        
        // 선택 가능한 유물이 없으면 null 반환
        return null;
    }
    
    // 초기 상점 아이템을 설정하거나 리롤하는 함수
    private void initializeShop() {
    	if (this.currentItems == null) {
            this.currentItems = createRandomItems();
            System.out.println("ItemShop: 상점 목록 최초 생성 완료.");
    	}
    }
    
    // 레어리티별 가중치 (확률 조정용)
    private static final int WEIGHT_COMMON = 50;    // 커먼 50%
    private static final int WEIGHT_RARE = 30;      // 레어 30%
    private static final int WEIGHT_EPIC = 15;      // 에픽 15%
    private static final int WEIGHT_LEGENDARY = 5;  // 레전더리 5%
    
    // 무작위로 5개의 유물을 뽑아 반환하는 함수 (레어리티 가중치 적용)
    private java.util.List<ItemInfo> createRandomItems() {
        List<String> ownedItemNames = userInfo.getOwnedItemNames();
        java.util.List<ItemInfo> availableArtifacts = new java.util.ArrayList<>();
        
        // 구매 가능한 유물 목록 생성 (소유하지 않았거나 스택형 유물인 경우)
        for (ItemInfo artifact : ALL_ARTIFACTS) {
            String name = artifact.getName();
            boolean isOwned = ownedItemNames.contains(name);
            if (!isOwned) {
                availableArtifacts.add(artifact);
            } else if (artifact.getDurationType() == DurationType.STACKABLE) {
                int currentStack = userInfo.getItemStackCount(name);
                if (currentStack < artifact.getMaxStack()) {
                    availableArtifacts.add(artifact);
                }
            }
        }
        
        // 레어리티별로 분류
        java.util.List<ItemInfo> commonItems = new java.util.ArrayList<>();
        java.util.List<ItemInfo> rareItems = new java.util.ArrayList<>();
        java.util.List<ItemInfo> epicItems = new java.util.ArrayList<>();
        java.util.List<ItemInfo> legendaryItems = new java.util.ArrayList<>();
        
        for (ItemInfo artifact : availableArtifacts) {
            switch (artifact.getRarity()) {
                case COMMON:
                    commonItems.add(artifact);
                    break;
                case RARE:
                    rareItems.add(artifact);
                    break;
                case EPIC:
                    epicItems.add(artifact);
                    break;
                case LEGENDARY:
                    legendaryItems.add(artifact);
                    break;
            }
        }
        
        // 가중치 기반으로 5개 선택
        java.util.List<ItemInfo> selectedItems = new java.util.ArrayList<>();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < 5; i++) {
            ItemInfo selected = selectByWeight(random, commonItems, rareItems, epicItems, legendaryItems);
            if (selected != null) {
                selectedItems.add(selected);
            } else if (!availableArtifacts.isEmpty()) {
                // 가중치 선택 실패 시 랜덤으로 선택
                selectedItems.add(availableArtifacts.get(random.nextInt(availableArtifacts.size())));
            } else {
                // 선택 가능한 유물이 없으면 SoldArtifact 추가
                selectedItems.add(new ItemInfo.SoldArtifact());
            }
        }
        
        return selectedItems;
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
        
        // 레어리티별로 분류
        java.util.List<ItemInfo> commonItems = new ArrayList<>();
        java.util.List<ItemInfo> rareItems = new ArrayList<>();
        java.util.List<ItemInfo> epicItems = new ArrayList<>();
        java.util.List<ItemInfo> legendaryItems = new ArrayList<>();
        
        for (ItemInfo artifact : availableArtifacts) {
            switch (artifact.getRarity()) {
                case COMMON:
                    commonItems.add(artifact);
                    break;
                case RARE:
                    rareItems.add(artifact);
                    break;
                case EPIC:
                    epicItems.add(artifact);
                    break;
                case LEGENDARY:
                    legendaryItems.add(artifact);
                    break;
            }
        }
        
        // 가중치 기반으로 5개 선택
        java.util.Random random = new java.util.Random();
        List<ItemInfo> newItems = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            ItemInfo selected = selectByWeight(random, commonItems, rareItems, epicItems, legendaryItems);
            if (selected != null) {
                newItems.add(selected);
            } else if (!availableArtifacts.isEmpty()) {
                // 가중치 선택 실패 시 랜덤으로 선택
                newItems.add(availableArtifacts.get(random.nextInt(availableArtifacts.size())));
            }
        }
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
        boolean isConsumable = (item.getDurationType() == DurationType.CONSUMABLE);	//단발형 유물인지
        boolean isStackable = (item.getDurationType() == DurationType.STACKABLE);	//스택형 유물인지
        boolean hasItem = userInfo.getItemStackCount(item.getName()) > 0;	//스택이 있는지
        boolean isInstant = (item.getDurationType() == DurationType.INSTANT);	//지속형 유물인지
        
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
                //즉발형 유물 즉시 효과 발동
                item.applyEffect(userInfo); 
                //즉발형은 인벤토리 추가 안됨
        	}
        	else {
                // 영구형 유물도 구매 시 applyEffect 호출 (인벤토리 확장 등)
                if (item.getDurationType() == DurationType.PASSIVE) {
                    item.applyEffect(userInfo);
                }
	            //해당 아이템 스택 +1
	            userInfo.addItemStack(item.getName());
	            
                if (!isStackable || !hasItem) {	//스택형 유물이 아니거나 스택이 0보다 크다면
                    userInfo.addOwnedItemName(item.getName());	//아이템 추가
                }

                //단발형이라면 지속 횟수 설정
                if (isConsumable) {
                    userInfo.setItemDuration(item.getName(), item.getActiveTurns());
                }

	            //기본 유물은 구매하면 더 이상 상점에서 안나옴
	            boolean shouldRemoveFromPool = true; //구매하면 목록에서 제거
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
	            if (shouldRemoveFromPool) { //유물 제거
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
            
            // 유물 구매 직후 자동 저장
            if (saveManager != null) {
                saveManager.save(userInfo);
                System.out.println("[SAVE] 유물 구매 저장 완료: " + item.getName());
            }
            
            return PurchaseResult.SUCCESS; // 구매 성공
        } else {
            return PurchaseResult.INSUFFICIENT_TICKETS;// 티켓 차감 실패
        }
    }
    //유물제거용 함수
    private void removeItemFromPool(String targetName) {
        availableItems.removeIf(info -> info.getName().equals(targetName));
    }
   
    public List<ItemInfo> getCurrentItems() {
        return this.currentItems;
    }
}

