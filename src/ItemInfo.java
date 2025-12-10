// 유물 파트, 밑에 유물 추가 하면 됩니다.
import java.util.HashMap;
import java.util.Map;

public abstract class ItemInfo {
    private final String name;
    private final int ticketCost; 
    private final String imagePath;	//이미지가 저장되는 경로 필드
    private final String description; // 유물설명
    
    private static final Map<String, ItemInfo> ARTIFACT_TEMPLATES = new HashMap<>();
    private static final Map<String, String> ITEM_PATH_MAP = new HashMap<>();

    public ItemInfo(String name, int ticketCost, String imagePath, String description) {
        this.name = name;
        this.ticketCost = ticketCost;
        this.imagePath = imagePath;
        this.description = description;
        ARTIFACT_TEMPLATES.put(name, this); 
        ITEM_PATH_MAP.put(name, imagePath);
    }
    
    // 모든 유물이 반드시 구현해야 하는 고유 기능
    public abstract void applyEffect(User userInfo); 

    // 공통 Getter (UI/Shop 로직에서 사용)
    public String getName() { return name; }
    public int getTicketCost() { return ticketCost; }
    public String getImagePath() { return imagePath; }
    public static String getImagePathByName(String name) {	//이름으로 유물을 찾을 때
        return ITEM_PATH_MAP.getOrDefault(name, "res/dummy.png"); 
    }
    public String getDescription() { return description; }
    
    /**
     * 유물 이름으로 해당 유물의 템플릿 인스턴스를 가져옵니다.
     * @param name 유물 이름
     * @return ItemInfo 템플릿 인스턴스
     */
    public static ItemInfo getArtifactTemplateByName(String name) {
        return ARTIFACT_TEMPLATES.get(name);
    }
    
    
    
    
    
    
    /**---------------유물 추가 파트---------------------**/
 // IncreaseInterestRateArtifact.java (이자율 증가 유물)
    public static class golden_compass extends ItemInfo {
        private final double rateIncrease = 0.05;

        public golden_compass() {
            super("황금 나침반", 3, "res/golden_compass.png", "이자율을 영구히 5%p 증가시킵니다.");
        }

        @Override
        public void applyEffect(User userInfo) {
        	userInfo.increaseInterestRate(rateIncrease);
        	System.out.println(getName() + " 유물 효과 적용: 이자율이 " + rateIncrease * 100 + "%p 증가했습니다.");
        }
    }

    // HealthPotionArtifact.java (돈/체력 회복 유물)
    public static class HealthPotionArtifact extends ItemInfo {
        private final int moneyRestore = 50000;

        public HealthPotionArtifact() {
            super("신비한 물약", 2, "res/dummy.png", "소지금 50,000원을 즉시 회복합니다.");
        }

        @Override
        public void applyEffect(User userInfo) {
            // 💡 UserInfo 클래스에 돈을 추가하는 메소드가 필요합니다.
            userInfo.setRoulatte_money(userInfo.getRoulatte_money() + moneyRestore);
            System.out.println(getName() + " 유물 효과 적용: " + moneyRestore + "원이 추가되었습니다.");
        }
    }
    
 // ItemInfo.class 내부에 새로운 내부 클래스 추가 (Java Source Code Snippet)
    public static class SoldArtifact extends ItemInfo {
        public SoldArtifact() {
            // 이름: 판매 완료, 티켓 가격: 0, 이미지: 판매 완료 이미지 (예시 경로), 설명: 이미 팔린 유물입니다.
            super("판매 완료", 0, "res/dummy.png", "이 유물은 이미 판매되었습니다."); 
        }

        @Override
        public void applyEffect(User userInfo) {
            // 팔린 유물은 아무 효과도 적용하지 않습니다.
        }
    }
    
    public static class symbol_train extends ItemInfo {
        public symbol_train() {
            // 이름, 가격, 이미지 경로, 설명 (필요에 따라 변경)
            super("무늬 기차", 0, "res/symbol_train.png", "1번 패턴이 안나오면 무늬 가격이 원래 가격만큼 상승합니다"); 
        }
        @Override
        public void applyEffect(User userInfo) {
            for (int i = 0; i < 7; i++) {
                userInfo.setSymbolSum(i,userInfo.getSymbolSum(i) + userInfo.getSymbolOriginal(i));
            }    
        }
    }
    public static class pattern_train extends ItemInfo {
        public pattern_train() {
            super("패턴 기차", 0, "res/pattern_train.png", "1번 패턴이 안나오면 패턴 가격이 원래 가격만큼 상승합니다"); 
        }
        @Override
        public void applyEffect(User userInfo) {
            for (int i = 0; i < 11; i++) {
                userInfo.setPatternSum(i,userInfo.getPatternSum(i) + userInfo.getPatternOriginal(i));
            }
        }
    }
    public static class symbol_chain extends ItemInfo {
        public symbol_chain() {
            super("사슬 변형자 ", 0, "res/symbol_chain.png", "사슬 변형자가 포함된 무늬의 패턴이 나오면 패턴 가격이 증가합니다."); 
        }
        @Override
        public void applyEffect(User userInfo) {
            // 효과 없음
        }
    }
}

