// 유물 파트, 밑에 유물 추가 하면 됩니다.
import java.util.HashMap;
import java.util.Map;

public abstract class ItemInfo {
    private final String name;
    private final int ticketCost; 
    private final String imagePath;	//이미지가 저장되는 경로 필드
    private final String description; // 유물설명
    private final ItemEffect rouletteEffect;	//룻렛에 영향을 주는 효과필드 저장용

    
    private static final Map<String, ItemInfo> ARTIFACT_TEMPLATES = new HashMap<>();
    private static final Map<String, String> ITEM_PATH_MAP = new HashMap<>();

    public ItemInfo(String name, int ticketCost, String imagePath, String description, ItemEffect rouletteEffect) {
        this.name = name;
        this.ticketCost = ticketCost;
        this.imagePath = imagePath;
        this.description = description;
        this.rouletteEffect = rouletteEffect;
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
        // 맵에서 찾거나 없는 경우 기본 경로 반환
        return ITEM_PATH_MAP.getOrDefault(name, "res/dummy.png"); 
    }
    public String getDescription() { return description; }
    
    
    public ItemEffect getRouletteEffect() {
    	return rouletteEffect;
    }
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
            super("황금 나침반", 3, "res/golden_compass.png", "이자율을 영구히 5%p 증가시킵니다.", null);
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
            super("신비한 물약", 2, "res/dummy.png", "소지금 50,000원을 즉시 회복합니다.", null);
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
            super("판매 완료", 0, "res/dummy.png", "이 유물은 이미 판매되었습니다.", null); 
        }

        @Override
        public void applyEffect(User userInfo) {
            // 팔린 유물은 아무 효과도 적용하지 않습니다.
        }
    }
    
    public static class symbol_train extends ItemInfo {
        public symbol_train() {
            // 이름, 가격, 이미지 경로, 설명 (필요에 따라 변경)
            super("무늬 기차", 0, "res/symbol_train.png", "1번 패턴이 안나오면 무늬 가격이 원래 가격만큼 상승합니다", null); 
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
            super(
                "패턴 기차 (단발형)", // 이름 변경 (선택 사항)
                0, 
                "res/pattern_train.png", 
                "다음 스핀에 한해 패턴 가격이 원래 가격만큼 상승합니다.",
                
                // ⭐ ItemEffect 정의 (핵심)
                new ItemEffect(
                    (user) -> { 
                        // Type 2 로직: tempPatternBonus를 수정합니다.
                        for (int i = 0; i < 11; i++) {
                            // 현재 임시 보너스 + 오리지널 패턴 값만큼 보너스 추가
                            user.setPatternSum(
                                i, 
                                (int)user.getPatternSum(i) + user.getPatternOriginal(i)
                            );
                        }
                    },
                    DurationType.TEMPORARY // 단발성 선언
                ) 
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {
            // 이 유물은 룰렛 효과를 사용하므로, 즉발 효과는 없습니다.
            // 또는 상점에서 구매 시 발생하는 다른 일회성 효과를 여기에 구현할 수 있습니다.
        }
    }
    public static class symbol_chain extends ItemInfo {
        public symbol_chain() {
            super("사슬 변형자 ", 0, "res/symbol_chain.png", "사슬 변형자가 포함된 무늬의 패턴이 나오면 패턴 가격이 증가합니다.", null); 
        }
        @Override
        public void applyEffect(User userInfo) {
            // 효과 없음
        }
    }

    public static class symbol_repeat extends ItemInfo {
        public symbol_repeat() {
            super("반복 변형자 ", 0, "res/symbol_repeat.png", "반복 변형자가 포함된 무늬의 패턴이 나오면 패턴 가격이 증가합니다.", null); 
        }
        @Override
        public void applyEffect(User userInfo) {
            // 효과 없음
        }
    }

    public static class symbol_ticket extends ItemInfo {
        public symbol_ticket() {
            super("티켓 변형자 ", 0, "res/symbol_ticket.png", "티켓 변형자가 포함된 무늬의 패턴이 나오면 패턴 가격이 증가합니다.", null); 
        }
        @Override
        public void applyEffect(User userInfo) {
            // 효과 없음
        }
    }

    public static class symbol_token extends ItemInfo {
        public symbol_token() {
            super("토큰 변형자 ", 0, "res/symbol_token.png", "토큰 변형자가 포함된 무늬의 패턴이 나오면 패턴 가격이 증가합니다.", null); 
        }
        @Override
        public void applyEffect(User userInfo) {
            // 효과 없음
        }
    }
    
    //일회성 유물
    public static class NextSpinOnlyArtifact extends ItemInfo {
        public NextSpinOnlyArtifact() {
            super(
                "레몬 1000배", 2, "res/dummy.png", "레몬 가격 1000배 상승.",
                new ItemEffect(
                	    (user) -> {
                	    	int targetIndex = 0; // 레몬 인덱스
                	    	//스핀 레몬배율 +1000.0배
                	    	user.setTempSymbolBonus(targetIndex, user.getTempSymbolBonus(targetIndex) + 1000.0);},
                	    DurationType.TEMPORARY
                	)
            );
        }
        @Override
        public void applyEffect(User userInfo) {
            // 구매 시 즉발 효과는 없으므로 비워둡니다.
        }
    }
    //유지형 유물
    public static class PersistentBonusArtifact extends ItemInfo {
        public PersistentBonusArtifact() {
            super(
                "지속 보너스", 10, "res/dummy.png", "모든 스핀의 심볼 당첨금을 1.1배 증가시킵니다.",
                new ItemEffect(
                    (user) -> { // ⭐ ArtifactAction: 로직을 ItemInfo에서 캡슐화
                        // User의 지속 필드 (persistent)를 변경하는 계산식 정의
                        // 🚨 User 클래스에 increasePersistentSymbolBonus(double)이 필요
                        //user.increasePersistentSymbolBonus(1.1);
                        System.out.println("DEBUG: [PersistentBonusArtifact] 지속 심볼 배율 1.1x 누적");
                    },
                    DurationType.PERSISTENT // ⭐ Type 3 지정: 스핀 종료 후 리셋 안 됨
                )
            );
        }
        @Override
        public void applyEffect(User userInfo) {
            // 구매 시 즉발 효과는 없으므로 비워둡니다.
        }
    }
    
    public static class TestPersistentArtifact extends ItemInfo {
        public TestPersistentArtifact() {
            super(
                "TEST-지속형(x2)", 1, "res/dummy.png", 
                "구매 시, 레몬(0번 심볼)의 기본 상금을 영구적으로 2배로 만듭니다.", 
                null // 룰렛 스핀 효과는 없으므로 null
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {
            int targetIndex = 0; // 레몬 인덱스
            
            // 🚨 Type 3 로직: 영구적으로 원본(Base) 값을 2000배 증가시킵니다.
            int currentOriginal = userInfo.getSymbolOriginal(targetIndex); 
            userInfo.setSymbolOriginal(targetIndex, currentOriginal * 2000); 
            
            // ⭐ 디버깅 로그
            System.out.println("DEBUG ARTIFACT: [지속형 적용] 원본 레몬가격(" + targetIndex + ") 값: " + userInfo.getSymbolOriginal(targetIndex));
            System.out.println("DEBUG ARTIFACT: [지속형 적용] 변화된 계산값(" + targetIndex + ") 값: " + userInfo.getSymbolSum(targetIndex));
        }
    }
    
    public static class TestTemporaryArtifact extends ItemInfo {
        public TestTemporaryArtifact() {
            super(
                "TEST-단발성(x3)", 1, "res/dummy.png", 
                "다음 스핀에 한해 레몬(0번 심볼)의 상금 배율을 33333배로 증가시킵니다.",
                
                new ItemEffect(
                    (user) -> { 
                        int targetIndex = 0; // 레몬 인덱스
                        double currentBonus = user.getTempSymbolBonus(targetIndex); 
                        
                        // 🚨 Type 2 로직: 임시 보너스 필드를 3.0으로 설정 (또는 기존 값에 곱하기)
                        // 현재는 '3배 증가'이므로, 덮어쓰기 대신 누적 곱셈을 가정합니다.
                        user.setTempSymbolBonus(targetIndex, currentBonus * 33333.0); 
                        
                        // ⭐ 디버깅 로그
                        System.out.println("DEBUG ARTIFACT: [단발성 실행] 단발성 뻥튀기 배율(" + targetIndex + ") 값: " + user.getTempSymbolBonus(targetIndex));
                        System.out.println("DEBUG ARTIFACT: [단발성 실행] 합계 배율(" + targetIndex + ") 값: " + user.getSymbolSum(targetIndex));
                    },
                    DurationType.TEMPORARY // 다음 스핀 종료 시 1.0으로 리셋됩니다.
                ) 
            ); 
        }
        @Override
        public void applyEffect(User userInfo) { /* 구매 시 즉발 효과 없음 */ }
    }
    
    
}

