// 유물 파트, 밑에 유물 추가 하면 됩니다.
import java.util.HashMap;
import java.util.Map;

public abstract class ItemInfo {
    private final String name;
    private final int ticketCost; 
    private final String imagePath;	//이미지가 저장되는 경로 필드
    private final String description; // 유물설명
    private final ItemEffect rouletteEffect;	//룻렛에 영향을 주는 효과필드 저장용
    private int maxStack = 1;	//스택형 유물이 기본으로 상점에서 등장하는 갯수
    private int activeTurns = 1; // 단발형유물 기본 지속 횟수

    private static final Map<String, ItemInfo> ARTIFACT_TEMPLATES = new HashMap<>();	//구매한 유물 저장
    private static final Map<String, String> ITEM_PATH_MAP = new HashMap<>();	//스택형 유물 저장용

    public ItemInfo(String name, int ticketCost, String imagePath, String description, ItemEffect rouletteEffect,int activeTurns) {
        this.name = name;
        this.ticketCost = ticketCost;
        this.imagePath = imagePath;
        this.description = description;
        this.rouletteEffect = rouletteEffect;
        this.activeTurns = activeTurns; // 횟수 저장
        ARTIFACT_TEMPLATES.put(name, this); 
        ITEM_PATH_MAP.put(name, imagePath);
    }
    // 모든 유물이 반드시 구현해야 하는 고유 기능
    public abstract void applyEffect(User userInfo); 
    public String getName() { return name; }
    public int getTicketCost() { return ticketCost; }
    public String getImagePath() { return imagePath; }
    public static String getImagePathByName(String name) {	//이름으로 유물을 찾을 때
        return ITEM_PATH_MAP.getOrDefault(name, "res/dummy.png"); //못 찾으면 더미 이미지 반환
    }
    public String getDescription() { return description; }
    
    public ItemEffect getRouletteEffect() {
    	return rouletteEffect;
    }
    public static ItemInfo getArtifactTemplateByName(String name) {
        return ARTIFACT_TEMPLATES.get(name);
    }
    public DurationType getDurationType() {	//유물 타입 미지정이면 기본으로 즉발형 유물로 취급
        if (this.rouletteEffect == null) {
            return DurationType.INSTANT;
        }
        return this.rouletteEffect.getDuration();
    }
    public void setMaxStack(int max) { 
    	this.maxStack = max; 
    	}
    public int getMaxStack() { 
    	return maxStack; 
    	}
    public int getActiveTurns() {
        return activeTurns;
    }
    
    
    /**---------------유물 추가 파트---------------------**/
    /*
     * [유물 종류 설명]
     * 1. 즉발형 유물 : 상점에서 구매 당시에만 동작하는 유물	/ 구매즉시 소유유물에 추가되지 않고 능력만 발동
     * 2. 단발형 유물 : 설정한 횟수만큼 능력 발동후 자동으로 삭제되는 유물
     * 3. 영구형 유물 : 룰렛을 돌릴때마다 반복하여 능력을 사용합니다.
     * 4. 스택형 유물 : 선언당시 설정한 만큼 상점에서 유물이 등장함, 중첩해서 효과 상승
     * 		
     */
    //즉발형 유물
    public static class golden_compass extends ItemInfo {
        private final double rateIncrease = 0.05;	//유물 내부에서 선언시킬 변수
        
        //유물의 기본 정보가 담긴 함수	: 유물이름, 가격, 유물이미지 주소, 설명, 타입지정, 선언한 횟수만큼 리롤후 삭제(단발형만 적용)
        public golden_compass() {
            super("황금 나침반(즉발형)", 3, "res/golden_compass.png", "이자율을 영구히 5%p 증가시킵니다.", null, 1);
        }
        
        //즉발형 유물을 선언할때 사용되는 함수입니다. 상점에서 구매시에 동작합니다.
        @Override
        public void applyEffect(User userInfo) {
        	userInfo.increaseInterestRate(rateIncrease);
        }
    }
    //단발형
    public static class TestTemporaryArtifact extends ItemInfo {
        public TestTemporaryArtifact() {
            super(
                "상큼한 레몬(단발형)", 1, "res/fresh_lemon.png", 
                "다음 룰렛 3회에 한해 레몬의 상금 배율을 3배로 증가시킵니다.",
                
                new ItemEffect(
                    (user) -> { 
                        int targetIndex = 0; // 레몬
                        double currentBonus = user.getTempSymbolBonus(targetIndex); //레몬 보너스값 가져오기
                        user.setTempSymbolBonus(targetIndex, currentBonus * 3.0);	//레몬 상금  
                    },
                    DurationType.CONSUMABLE //단발형
                    
                ) 
            ,3	//3번 동작하고 삭제됨
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //스택형 유물
    public static class LemonStackArtifact extends ItemInfo {
        public LemonStackArtifact() {
            super(
                "신비한 레몬(스택형)",    // 이름
                2,              // 가격 (티켓)
                "res/special_lemon.png", // 이미지 경로
                "레몬 등장 확률이 증가합니다. (중첩 가능: 개당 +5%)", // 설명
                new ItemEffect(
                    (user) -> { 
                        int stacks = user.getItemStackCount("신비한 레몬(스택형)"); //스택형 유물 몇개 가지고 있는지
                        //스택에 따른 보너스 계산 (1개: 5%, 2개: 10%, 3개: 15%)
                        double bonusChance = stacks * 5.0; 

                        double lemonProbability_original = user.getLemonProbability_original();
                        double newProbability = lemonProbability_original + bonusChance;
                        user.setSymbolProbability(0, newProbability); // 레몬 인덱스: 0
                    },
                    DurationType.STACKABLE //스택형 유물
                ),1 //선언한만큼 리롤후 삭제(단발형 유물에서만 사용)
            );
            this.setMaxStack(3); 	//스택형 유물, 구매 가능 횟수
        }

        @Override
        public void applyEffect(User userInfo) {
//            if (this.getRouletteEffect() != null) {
//                this.getRouletteEffect().getAction().execute(userInfo);
//            }
        }
    }
    //즉발형
    public static class HealthPotionArtifact extends ItemInfo {	
        private final int moneyRestore = 50000;
        //image URL = https://studionamepending.itch.io/heart-pickup-animated
        public HealthPotionArtifact() {
            super("신비한 물약(즉발형)", 2, "res/Heart Pickup.png", "소지금 50,000원을 즉시 회복합니다.", null,1);
        }

        @Override
        public void applyEffect(User userInfo) {
            userInfo.setRoulatte_money(userInfo.getRoulatte_money() + moneyRestore);
        }
    }
    
    //더미상품 : 판매된 상품 표시용
    public static class SoldArtifact extends ItemInfo {
        public SoldArtifact() {
            // 이름: 판매 완료, 티켓 가격: 0, 이미지: 판매 완료 이미지 (예시 경로), 설명: 이미 팔린 유물입니다.
            super("판매 완료", 0, "res/dummy.png", "이 유물은 이미 판매되었습니다.", null, 1); 
        }

        @Override
        public void applyEffect(User userInfo) {
        }
    }
    
    //즉발형
    public static class symbol_train extends ItemInfo {
        public symbol_train() {
            super("무늬 기차(즉발형)", 0, "res/symbol_train.png", "1번 패턴이 안나오면 무늬 가격이 원래 가격만큼 상승합니다", null,1); 
        }
        
        @Override
        public void applyEffect(User userInfo) {
            for (int i = 0; i < 7; i++) {
                userInfo.setSymbolSum(i,userInfo.getSymbolSum(i) + userInfo.getSymbolOriginal(i));
            }    
        }
    }
    //단발형
    public static class pattern_train extends ItemInfo {
        public pattern_train() {
            super(
                "패턴 기차(단발형)",
                2, 
                "res/pattern_train.png", 
                "다음 7번 룰렛을 돌릴때에 한해 패턴 가격이 원래 가격만큼 상승합니다.",
                new ItemEffect(
                    (user) -> { 
                        for (int i = 0; i < 11; i++) {
                            // 현재 임시 보너스 + 오리지널 패턴 값만큼 보너스 추가
                            user.setPatternSum(
                                i, 
                                (int)user.getPatternSum(i) + user.getPatternOriginal(i)
                            );
                        }
                    },
                    DurationType.CONSUMABLE // 단발형 유물
                ) ,7	//7번 스핀후 삭제(단발형 유물)
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    public static class symbol_chain extends ItemInfo {
        public symbol_chain() {
            super(
                "사슬 변형자", 2, "res/symbol_chain.png", "30% 확률로 사슬 변형자를 적용시킵니다. (사슬변형자: 기본 패턴 값만큼 패턴 가격 증가)",
                new ItemEffect(
                	    (user) -> {
                	    	},
                	    DurationType.PASSIVE
                	),1
            );

        }
        @Override
        public void applyEffect(User userInfo) {
        }
        
    }

    public static class symbol_repeat extends ItemInfo {
        public symbol_repeat() {
            super("반복 변형자 ", 0, "res/symbol_repeat.png", "반복 변형자가 포함된 무늬의 패턴이 나오면 패턴 가격이 증가합니다.",
            new ItemEffect(
                (user) -> {
                    },
                DurationType.PASSIVE
            ),1
        ); 
        
        }
        @Override
        public void applyEffect(User userInfo) {
        }
    }

    public static class symbol_ticket extends ItemInfo {
        public symbol_ticket() {
            super("티켓 변형자 ", 0, "res/symbol_ticket.png", "티켓 변형자가 포함된 무늬의 패턴이 나오면 패턴 가격이 증가합니다.", 
            new ItemEffect(
                (user) -> {
                    },
                DurationType.PASSIVE
            ),1); 
        }
        @Override
        public void applyEffect(User userInfo) {
        }
    }

    public static class symbol_token extends ItemInfo {
        public symbol_token() {
            super("토큰 변형자 ", 0, "res/symbol_token.png", "토큰 변형자가 포함된 무늬의 패턴이 나오면 패턴 가격이 증가합니다.", 
            new ItemEffect(
                (user) -> {
                    },
                DurationType.PASSIVE
            ),1); 
        }
        @Override
        public void applyEffect(User userInfo) {
        }
    }
    
    //단발형
    public static class NextSpinOnlyArtifact extends ItemInfo {
        public NextSpinOnlyArtifact() {
        	//image URL = https://freesvg.org/lemon-128985
            super(
                "레몬 2배(단발형)", 2, "res/lemon-citrina.png", "룰렛 돌릴때, 레몬 가격 2배 보너스!.",
                new ItemEffect(
                	    (user) -> {
                	    	int targetIndex = 0; // 레몬
                	    	//룰렛 돌릴때마다 초기화 되는 값
                	    	user.setTempSymbolBonus(targetIndex, user.getTempSymbolBonus(targetIndex) + 2.0);},
                	    DurationType.CONSUMABLE
                	),3
            );
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //유지형 유물
    public static class PersistentBonusArtifact extends ItemInfo {
        public PersistentBonusArtifact() {
            super(
                "지속 보너스(유지형)", 10, "res/dummy.png", "모든 스핀의 심볼 당첨금을 1.1배 증가시킵니다.",
                new ItemEffect(
                    (user) -> { // ⭐ ArtifactAction: 로직을 ItemInfo에서 캡슐화
                        // User의 지속 필드 (persistent)를 변경하는 계산식 정의
                        // 🚨 User 클래스에 increasePersistentSymbolBonus(double)이 필요
                        //user.increasePersistentSymbolBonus(1.1);
                    },
                    DurationType.PASSIVE
                ),1
            );
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
 //즉발형 유물
    public static class TestPersistentArtifact extends ItemInfo {

        public TestPersistentArtifact() {
            super( "소화된 레몬(즉발형)", 5, "res/digested_lemon.png", 
                "구매 시, 레몬의 기본 상금의 2배가격을 더합니다. 영구적으로 2배로 만듭니다.", 
                new ItemEffect(
                    (user) -> { 
                        int targetIndex = 0; // 레몬
                        int currentOriginal = user.getSymbolOriginal(targetIndex); 
                        int newValue = currentOriginal * 2;
                        user.setSymbolOriginal(targetIndex, newValue); // 계산식: 원래값 * 2
                    },
                    DurationType.INSTANT //즉발형 유물
                ),1
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //단발형 - 체리
    public static class RefreshingCherryArtifact extends ItemInfo {
        public RefreshingCherryArtifact() {
            super(
                "상큼한 체리(단발형)", 1, "res/fresh_cherry.png", 
                "다음 룰렛 3회에 한해 체리의 상금 배율을 3배로 증가시킵니다.",
                
                new ItemEffect(
                    (user) -> { 
                        int targetIndex = 1; // 체리
                        double currentBonus = user.getTempSymbolBonus(targetIndex);
                        user.setTempSymbolBonus(targetIndex, currentBonus * 3.0);
                    },
                    DurationType.CONSUMABLE
                ) 
            ,3
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //스택형 유물 - 체리
    public static class CherryStackArtifact extends ItemInfo {
        public CherryStackArtifact() {
            super(
                "신비한 체리(스택형)",
                2,
                "res/special_cherry.png",
                "체리 등장 확률이 증가합니다. (중첩 가능: 개당 +5%)",
                new ItemEffect(
                    (user) -> { 
                        int stacks = user.getItemStackCount("신비한 체리(스택형)");
                        double bonusChance = stacks * 5.0; 

                        double cherryProbability_original = user.getCherryProbability_original();
                        double newProbability = cherryProbability_original + bonusChance;
                        user.setSymbolProbability(1, newProbability); // 체리 인덱스: 1
                    },
                    DurationType.STACKABLE
                ),1
            );
            this.setMaxStack(3);
        }

        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //단발형 - 클로버
    public static class RefreshingCloverArtifact extends ItemInfo {
        public RefreshingCloverArtifact() {
            super(
                "상큼한 클로버(단발형)", 1, "res/fresh_clover.png", 
                "다음 룰렛 3회에 한해 클로버의 상금 배율을 3배로 증가시킵니다.",
                
                new ItemEffect(
                    (user) -> { 
                        int targetIndex = 2; // 클로버
                        double currentBonus = user.getTempSymbolBonus(targetIndex);
                        user.setTempSymbolBonus(targetIndex, currentBonus * 3.0);
                    },
                    DurationType.CONSUMABLE
                ) 
            ,3
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //스택형 유물 - 클로버
    public static class CloverStackArtifact extends ItemInfo {
        public CloverStackArtifact() {
            super(
                "신비한 클로버(스택형)",
                2,
                "res/special_clover.png",
                "클로버 등장 확률이 증가합니다. (중첩 가능: 개당 +5%)",
                new ItemEffect(
                    (user) -> { 
                        int stacks = user.getItemStackCount("신비한 클로버(스택형)");
                        double bonusChance = stacks * 5.0; 

                        double cloverProbability_original = user.getCloverProbability_original();
                        double newProbability = cloverProbability_original + bonusChance;
                        user.setSymbolProbability(2, newProbability); // 클로버 인덱스: 2
                    },
                    DurationType.STACKABLE
                ),1
            );
            this.setMaxStack(3);
        }

        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //단발형 - 벨
    public static class RefreshingBellArtifact extends ItemInfo {
        public RefreshingBellArtifact() {
            super(
                "상큼한 종(단발형)", 1, "res/fresh_bell.png", 
                "다음 룰렛 3회에 한해 종의 상금 배율을 3배로 증가시킵니다.",
                
                new ItemEffect(
                    (user) -> { 
                        int targetIndex = 3; // 벨
                        double currentBonus = user.getTempSymbolBonus(targetIndex);
                        user.setTempSymbolBonus(targetIndex, currentBonus * 3.0);
                    },
                    DurationType.CONSUMABLE
                ) 
            ,3
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //스택형 유물 - 벨
    public static class BellStackArtifact extends ItemInfo {
        public BellStackArtifact() {
            super(
                "신비한 종(스택형)",
                2,
                "res/special_bell.png",
                "벨 등장 확률이 증가합니다. (중첩 가능: 개당 +5%)",
                new ItemEffect(
                    (user) -> { 
                        int stacks = user.getItemStackCount("신비한 종(스택형)");
                        double bonusChance = stacks * 5.0; 

                        double bellProbability_original = user.getBellProbability_original();
                        double newProbability = bellProbability_original + bonusChance;
                        user.setSymbolProbability(3, newProbability); // 종 인덱스: 3
                    },
                    DurationType.STACKABLE
                ),1
            );
            this.setMaxStack(3);
        }

        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //단발형 - 다이아몬드
    public static class RefreshingDiamondArtifact extends ItemInfo {
        public RefreshingDiamondArtifact() {
            super(
                "상큼한 다이아몬드(단발형)", 1, "res/fresh_diamond.png", 
                "다음 룰렛 3회에 한해 다이아몬드의 상금 배율을 3배로 증가시킵니다.",
                
                new ItemEffect(
                    (user) -> { 
                        int targetIndex = 4; // 다이아몬드
                        double currentBonus = user.getTempSymbolBonus(targetIndex);
                        user.setTempSymbolBonus(targetIndex, currentBonus * 3.0);
                    },
                    DurationType.CONSUMABLE
                ) 
            ,3
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //스택형 유물 - 다이아몬드
    public static class DiamondStackArtifact extends ItemInfo {
        public DiamondStackArtifact() {
            super(
                "신비한 다이아몬드(스택형)",
                2,
                "res/special_diamond.png",
                "다이아몬드 등장 확률이 증가합니다. (중첩 가능: 개당 +5%)",
                new ItemEffect(
                    (user) -> { 
                        int stacks = user.getItemStackCount("신비한 다이아몬드(스택형)");
                        double bonusChance = stacks * 5.0; 

                        double diamondProbability_original = user.getDiamondProbability_original();
                        double newProbability = diamondProbability_original + bonusChance;
                        user.setSymbolProbability(4, newProbability); // 다이아몬드 인덱스: 4
                    },
                    DurationType.STACKABLE
                ),1
            );
            this.setMaxStack(3);
        }

        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //단발형 - 보물
    public static class RefreshingTreasureArtifact extends ItemInfo {
        public RefreshingTreasureArtifact() {
            super(
                "상큼한 보물(단발형)", 1, "res/fresh_treasure.png", 
                "다음 룰렛 3회에 한해 보물의 상금 배율을 3배로 증가시킵니다.",
                
                new ItemEffect(
                    (user) -> { 
                        int targetIndex = 5; // 보물
                        double currentBonus = user.getTempSymbolBonus(targetIndex);
                        user.setTempSymbolBonus(targetIndex, currentBonus * 3.0);
                    },
                    DurationType.CONSUMABLE
                ) 
            ,3
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //스택형 유물 - 보물
    public static class TreasureStackArtifact extends ItemInfo {
        public TreasureStackArtifact() {
            super(
                "신비한 보물(스택형)",
                2,
                "res/special_treasure.png",
                "보물 등장 확률이 증가합니다. (중첩 가능: 개당 +5%)",
                new ItemEffect(
                    (user) -> { 
                        int stacks = user.getItemStackCount("신비한 보물(스택형)");
                        double bonusChance = stacks * 5.0; 

                        double treasureProbability_original = user.getTreasureProbability_original();
                        double newProbability = treasureProbability_original + bonusChance;
                        user.setSymbolProbability(5, newProbability); // 보물 인덱스: 5
                    },
                    DurationType.STACKABLE
                ),1
            );
            this.setMaxStack(3);
        }

        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //단발형 - 세븐
    public static class RefreshingSevenArtifact extends ItemInfo {
        public RefreshingSevenArtifact() {
            super(
                "상큼한 세븐(단발형)", 1, "res/fresh_seven.png", 
                "다음 룰렛 3회에 한해 세븐의 상금 배율을 3배로 증가시킵니다.",
                
                new ItemEffect(
                    (user) -> { 
                        int targetIndex = 6; // 세븐
                        double currentBonus = user.getTempSymbolBonus(targetIndex);
                        user.setTempSymbolBonus(targetIndex, currentBonus * 3.0);
                    },
                    DurationType.CONSUMABLE
                ) 
            ,3
            ); 
        }
        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //스택형 유물 - 세븐
    public static class SevenStackArtifact extends ItemInfo {
        public SevenStackArtifact() {
            super(
                "신비한 세븐(스택형)",
                2,
                "res/special_seven.png",
                "세븐 등장 확률이 증가합니다. (중첩 가능: 개당 +5%)",
                new ItemEffect(
                    (user) -> { 
                        int stacks = user.getItemStackCount("신비한 세븐(스택형)");
                        double bonusChance = stacks * 5.0; 

                        double sevenProbability_original = user.getSevenProbability_original();
                        double newProbability = sevenProbability_original + bonusChance;
                        user.setSymbolProbability(6, newProbability); // 세븐 인덱스: 6
                    },
                    DurationType.STACKABLE
                ),1
            );
            this.setMaxStack(3);
        }

    

        @Override
        public void applyEffect(User userInfo) {}
    }
    
    //즉발형 유물 - 레몬 골드
    public static class GoldenLemon extends ItemInfo {
        public GoldenLemon() {
            super(
                "황금레몬(즉발형)",
                2,
                "res/sybols_lemon_gold.png",
                "구매 시, 레몬 문양의 가격이 오리지널 가격만큼 영구적으로 증가합니다.",
                null,
                1
            );
        }

        @Override
        public void applyEffect(User userInfo) {
            int originalPrice = userInfo.getSymbolOriginal(0);
            int[] symbolSumArray = userInfo.getSymbolSum();
            int oldPrice = symbolSumArray[0];
            int newPrice = oldPrice + originalPrice;
            userInfo.setSymbolSum(0, newPrice);
        }
    }
    
    public static class GoldenCherry extends ItemInfo {
        public GoldenCherry() {
            super(
                "황금체리(즉발형)",
                2,
                "res/sybols_cherry_gold.png",
                "구매 시, 체리 문양의 가격이 오리지널 가격만큼 영구적으로 증가합니다.",
                null,
                1
            );
        }

        @Override
        public void applyEffect(User userInfo) {
            int originalPrice = userInfo.getSymbolOriginal(1);
            int[] symbolSumArray = userInfo.getSymbolSum();
            int oldPrice = symbolSumArray[1];
            int newPrice = oldPrice + originalPrice;
            userInfo.setSymbolSum(1, newPrice);
        }
    }
    
    public static class GoldenClover extends ItemInfo {
        public GoldenClover() {
            super(
                "황금클로버(즉발형)",
                2,
                "res/sybols_clover_gold.png",
                "구매 시, 클로버 문양의 가격이 오리지널 가격만큼 영구적으로 증가합니다.",
                null,
                1
            );
        }

        @Override
        public void applyEffect(User userInfo) {
            int originalPrice = userInfo.getSymbolOriginal(2);
            int[] symbolSumArray = userInfo.getSymbolSum();
            int oldPrice = symbolSumArray[2];
            int newPrice = oldPrice + originalPrice;
            userInfo.setSymbolSum(2, newPrice);
        }
    }
    
    public static class GoldenBell extends ItemInfo {
        public GoldenBell() {
            super(
                "황금종(즉발형)",
                2,
                "res/sybols_bell_gold.png",
                "구매 시, 종 문양의 가격이 오리지널 가격만큼 영구적으로 증가합니다.",
                null,
                1
            );
        }

        @Override
        public void applyEffect(User userInfo) {
            int originalPrice = userInfo.getSymbolOriginal(3);
            int[] symbolSumArray = userInfo.getSymbolSum();
            int oldPrice = symbolSumArray[3];
            int newPrice = oldPrice + originalPrice;
            userInfo.setSymbolSum(3, newPrice);
        }
    }
    
    public static class GoldenDiamond extends ItemInfo {
        public GoldenDiamond() {
            super(
                "황금다이아몬드(즉발형)",
                2,
                "res/sybols_diamond_gold.png",
                "구매 시, 다이아몬드 문양의 가격이 오리지널 가격만큼 영구적으로 증가합니다.",
                null,
                1
            );
        }

        @Override
        public void applyEffect(User userInfo) {
            int originalPrice = userInfo.getSymbolOriginal(4);
            int[] symbolSumArray = userInfo.getSymbolSum();
            int oldPrice = symbolSumArray[4];
            int newPrice = oldPrice + originalPrice;
            userInfo.setSymbolSum(4, newPrice);
        }
    }
    
    public static class GoldenTreasure extends ItemInfo {
        public GoldenTreasure() {
            super(
                "황금보물(즉발형)",
                2,
                "res/sybols_treasure_gold.png",
                "구매 시, 보물 문양의 가격이 오리지널 가격만큼 영구적으로 증가합니다.",
                null,
                1
            );
        }

        @Override
        public void applyEffect(User userInfo) {
            int originalPrice = userInfo.getSymbolOriginal(5);
            int[] symbolSumArray = userInfo.getSymbolSum();
            int oldPrice = symbolSumArray[5];
            int newPrice = oldPrice + originalPrice;
            userInfo.setSymbolSum(5, newPrice);
        }
    }
    
    public static class GoldenSeven extends ItemInfo {
        public GoldenSeven() {
            super(
                "황금세븐(즉발형)",
                2,
                "res/sybols_seven_gold.png",
                "구매 시, 세븐 문양의 가격이 오리지널 가격만큼 영구적으로 증가합니다.",
                null,
                1
            );
        }

        @Override
        public void applyEffect(User userInfo) {
            int originalPrice = userInfo.getSymbolOriginal(6);
            int[] symbolSumArray = userInfo.getSymbolSum();
            int oldPrice = symbolSumArray[6];
            int newPrice = oldPrice + originalPrice;
            userInfo.setSymbolSum(6, newPrice);
        }
    }

}

