import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String user_name;  
    private List<String> user_call;
    private List<String> user_item = new ArrayList<>();    
    private Map<String, Integer> itemStacks = new HashMap<>();	//스택형 아이템을 위한 같은 아이템 몇번 구매했는지/Key: 아이템이름, Value: 현재스택
    private Map<String, Integer> itemDurations = new HashMap<>(); //단발형 유물을 얼마나 사용했는지 기록용
    
    private int roulatte_money = 30;
    private double interest = 0.1;  
    private int ticket = 3;
    private int deadline = 1;
    private int round = 1;               
    private int deadline_money = 75;
    private int total_money = 0;
    private int total_spin = 0;
    private int round_spin_left = 7;
    private int item_max = 7;	//최대 11까지 가능
    private int spinsPerRound = 7; // 선택한 스핀 옵션 (7 또는 3)
    
    private int roulatte_cost = 2;	//룰렛 1회 비용
    private boolean call_count = false;	//전화를 걸 수 있는 기회
    private int callReroll_count = 0; //전화 리롤 횟수
    private int itemReroll_count = 0; //유물상점 리롤 횟수
    private int freeItemReroll_count = 0; //무료 상점 리롤 횟수
    private int freeCallReroll_count = 0; //무료 전화 리롤 횟수
    private int[] symbol_original = {2,2,3,3,5,5,7};
    private int[] pattern_original = {1,2,3,1,1,4,4,7,7,8,10};
    // 문양 가격 "레몬", "체리", "클로버", "종", "다이아", "보물", "7"
    private int[] symbol_sum = {2,2,3,3,5,5,7};
    
    // 패턴 가격 "트리플", "쿼드라", "펜타", "세로", "대각선", "지그", "재그", "지상", "천상", "눈", "잭팟" 
    private int[] pattern_sum = {1,2,3,1,1,4,4,7,7,8,10};
    
    //문양 확률 변수
    private double lemon_probability = 100.0 / 7.0;
    private double cherry_probability = 100.0 / 7.0;
    private double clover_probability = 100.0 / 7.0;
    private double bell_probability = 100.0 / 7.0;
    private double diamond_probability = 100.0 / 7.0;
    private double treasure_probability = 100.0 / 7.0;
    private double seven_probability = 100.0 / 7.0;
    //문양 초기값 확률 변수
    private double lemon_probability_original = 100.0 / 7.0;
    private double cherry_probability_original = 100.0 / 7.0;
    private double clover_probability_original = 100.0 / 7.0;
    private double bell_probability_original = 100.0 / 7.0;
    private double diamond_probability_original = 100.0 / 7.0;
    private double treasure_probability_original = 100.0 / 7.0;
    private double seven_probability_original = 100.0 / 7.0;
    
    // 변형자별 적용 확률 (0.0 ~ 1.0)
    private double chainModifierProbability = 0.3;
    private double repeatModifierProbability = 0.3;
    private double tokenModifierProbability = 0.3;
    private double ticketModifierProbability = 0.3;
    
    /*--------------------------------------------------*/
    //룰렛 돌릴때 매번 초기화 되는 값(룰렛 돌릴때 일회용으로 보너스처럼 적용되는 값.)
    //문양 확률 변수 일회용으로 더해지는 변수선언
    private double lemon_probability_sumBonus = 0.0;
    private double cherry_probability_sumBonus = 0.0;
    private double clover_probability_sumBonus = 0.0;
    private double bell_probability_sumBonus = 0.0;
    private double diamond_probability_sumBonus = 0.0;
    private double treasure_probability_sumBonus = 0.0;
    private double seven_probability_sumBonus = 0.0;
    //문양 확률 변수 일회용으로 곱해지는 변수 선언
    private double lemon_probability_multipBonus = 1.0;
    private double cherry_probability_multipBonus = 1.0;
    private double clover_probability_multipBonus = 1.0;
    private double bell_probability_multipBonus = 1.0;
    private double diamond_probability_multipBonus = 1.0;
    private double treasure_probability_multipBonus = 1.0;
    private double seven_probability_multipBonus = 1.0;
    
    //보너스 문양, 패턴 넣을 공간
    private double[] tempSymbolBonus; // SymbolOriginal의 길이만큼 (기본값 1)
    private double[] tempPatternBonus; // PatternOriginal의 길이만큼 (기본값 1.0)
   
    private double doubleChanceMultiplier = 1.0;
    
    // 하이 리스크 유물용: 다음 스핀에 적용할 배율
    private boolean highRiskActive = false;
    private int originalRoulatteCost = 2; // 원래 룰렛 비용 저장용
    
    public boolean isHighRiskActive() {
        return highRiskActive;
    }
    
    public void setHighRiskActive(boolean active) {
        this.highRiskActive = active;
        if (active) {
            // 활성화 시 원래 비용 저장
            originalRoulatteCost = roulatte_cost;
        }
    }
    
    public int getOriginalRoulatteCost() {
        return originalRoulatteCost;
    }

    public User() {
        // tempSymbolBonus와 tempPatternBonus 초기화
        this.tempSymbolBonus = new double[symbol_original.length];
        this.tempPatternBonus = new double[pattern_original.length];
        java.util.Arrays.fill(this.tempSymbolBonus, 1.0);
        java.util.Arrays.fill(this.tempPatternBonus, 1.0);
    }
    
    public void resetTemporarySpinBonuses() {
        // 1. 심볼/패턴 배열 보너스 초기화
        if (this.tempSymbolBonus != null) {
            java.util.Arrays.fill(this.tempSymbolBonus, 1);
        }
        if (this.tempPatternBonus != null) {
            java.util.Arrays.fill(this.tempPatternBonus, 1.0);
        }
        
        // 보너스 확률 초기화 코드(덧셈용) / 기본값
        this.lemon_probability_sumBonus = 0.0;
        this.cherry_probability_sumBonus = 0.0;
        this.clover_probability_sumBonus = 0.0;
        this.bell_probability_sumBonus = 0.0;
        this.diamond_probability_sumBonus = 0.0;
        this.treasure_probability_sumBonus = 0.0;
        this.seven_probability_sumBonus = 0.0;
        // 보너스 확률 초기화 코드(곱셈용) / 기본값
        this.lemon_probability_multipBonus = 1.0;
        this.cherry_probability_multipBonus = 1.0;
        this.clover_probability_multipBonus = 1.0;
        this.bell_probability_multipBonus = 1.0;
        this.diamond_probability_multipBonus = 1.0;
        this.treasure_probability_multipBonus = 1.0;
        this.seven_probability_multipBonus = 1.0;
        
        // 더블 찬스 배율 초기화
        this.doubleChanceMultiplier = 1.0;
        
        // 하이 리스크 플래그 초기화
        this.highRiskActive = false;
        
        System.out.println("DEBUG: 단발성 스핀 보너스 초기화 완료.");
    }
    
    //문양 '가격'계산 보너스 계산식 geter/seter
    public double getTempSymbolBonus(int index) { return tempSymbolBonus[index]; }//곱셈식으로 넘어갈 친구
    public double setTempSymbolBonus(int index, double d) { return this.tempSymbolBonus[index] = d; }//요기도 곱셈식
    
    //패턴 '가격'계산 보너스 계산식 getter/setter
    public double getTempPatternBonus(int index) { 
        if (tempPatternBonus == null || index < 0 || index >= tempPatternBonus.length) {
            return 1.0;
        }
        return tempPatternBonus[index];
    }
    
    public double setTempPatternBonus(int index, double value) { 
        if (tempPatternBonus == null) {
            tempPatternBonus = new double[pattern_original.length];
            java.util.Arrays.fill(tempPatternBonus, 1.0);
        }
        if (index >= 0 && index < tempPatternBonus.length) {
            this.tempPatternBonus[index] = value;
        }
        return this.tempPatternBonus[index];
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public List<String> getUser_call() {
        if (user_call == null) {
            user_call = new ArrayList<>();
        }
        return user_call;
    }

    public void setUser_call(List<String> user_call) {
        this.user_call = user_call;
    }
    
    public void addUser_call(String callItem) {
        if (user_call == null) {
            user_call = new ArrayList<>();
        }
        user_call.add(callItem);
    }

    public int getRoulatte_money() {
        return roulatte_money;
    }

    public void setRoulatte_money(int roulatte_money) {
        this.roulatte_money = roulatte_money;
    }

    public void addRoulatte_money(int delta) {
        this.roulatte_money += delta;
        if (this.roulatte_money < 0) this.roulatte_money = 0;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public void addTicket(int delta) {
        this.ticket += delta;
        if (this.ticket < 0) this.ticket = 0;
    }
    
    public boolean minusTicket(int cost) {
        if (this.ticket >= cost) {
            this.ticket -= cost;
            return true; 
        } else {
            return false; 
        }
    }

    

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
        // 기한별 데드라인 머니 계산: 75 * deadline * deadline * deadline
        this.deadline_money = 75 * deadline * deadline * deadline;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getDeadline_money() {
        return deadline_money;
    }

    public void setDeadline_money(int deadline_money) {
        this.deadline_money = deadline_money;
    }

    public int getTotal_money() {
        return total_money;
    }

    public void setTotal_money(int total_money) {
        this.total_money = total_money;
    }

    public void addTotal_money(int delta) {
        this.total_money += delta;
        if (this.total_money < 0) this.total_money = 0;
    }

    public int getRound_spin_left() {
        return round_spin_left;
    }

    public void setRound_spin_left(int round_spin_left) {
        this.round_spin_left = round_spin_left;
    }

    public int getItem_max() {
        return item_max;
    }

    public void setItem_max(int item_max) {
        this.item_max = item_max;
    }
    
    public int getSpinsPerRound() {
        return spinsPerRound;
    }
    
    public void setSpinsPerRound(int spinsPerRound) {
        this.spinsPerRound = spinsPerRound;
    }
    
    // 전화 관련 메서드-------------------------------------
    public boolean getCall_count() {
    	return call_count;
    }
    
    public void setCall_count(boolean set) {
    	if(set == true) {
    		this.call_count = true;
    	}
    	else {
    		this.call_count = false;
    	}
    }
    //-------------------------------------------------
    
    public int getRoulatte_cost() {
        return roulatte_cost;
    }
    
    public void setRoulatte_cost(int cost) {
        this.roulatte_cost = cost;
    }
    
    public void increaseInterestRate(double rate) {
        this.interest += rate;
        System.out.println(
            "DEBUG: 이자율이 " + (rate * 100) + "%p 증가했습니다. " + 
            "현재 이자율: " + (this.interest * 100) + "%p"
        );
    }
    
    public int getCallReroll_count() {
    	return callReroll_count;
    }
    public void setCallReroll_count(int set) {
    	this.callReroll_count = set;
    }
    
    public int addCallReroll_count() {
    	return callReroll_count += 1;
    }
    
    public int getItemReroll_count() {
    	return itemReroll_count;
    }
    
    public int addItemReroll_count() {
    	return itemReroll_count += 1;
    }
    public int getTotal_spin() {
    	return total_spin;
    }
    public void setTotal_spin(int total_spin) {
    	this.total_spin = total_spin;
    }
    
    // 개별 확률 getter/setter 메서드들--------------보너스 확률 계산식 추가--------------
    public double getLemonProbability() { return (lemon_probability * lemon_probability_multipBonus) + lemon_probability_sumBonus; }
    public double getCherryProbability() { return (cherry_probability * cherry_probability_multipBonus) + cherry_probability_sumBonus; }
    public double getCloverProbability() { return (clover_probability * clover_probability_multipBonus) + clover_probability_sumBonus; }
    public double getBellProbability() { return (bell_probability * bell_probability_multipBonus) + bell_probability_sumBonus; }
    public double getDiamondProbability() { return (diamond_probability * diamond_probability_multipBonus) + diamond_probability_sumBonus; }
    public double getTreasureProbability() { return (treasure_probability * treasure_probability_multipBonus) + treasure_probability_sumBonus; }
    public double getSevenProbability() { return (seven_probability * seven_probability_multipBonus) + seven_probability_sumBonus; }
    
    public double getLemonProbability_original() { return lemon_probability_original; }
    public double getCherryProbability_original() { return cherry_probability_original; }
    public double getCloverProbability_original() { return clover_probability_original; }
    public double getBellProbability_original() { return bell_probability_original; }
    public double getDiamondProbability_original() { return diamond_probability_original; }
    public double getTreasureProbability_original() { return treasure_probability_original; }
    public double getSevenProbability_original() { return seven_probability_original; }
    
    public void setLemonProbability(double prob) { this.lemon_probability = prob; }
    public void setCherryProbability(double prob) { this.cherry_probability = prob; }
    public void setCloverProbability(double prob) { this.clover_probability = prob; }
    public void setBellProbability(double prob) { this.bell_probability = prob; }
    public void setDiamondProbability(double prob) { this.diamond_probability = prob; }
    public void setTreasureProbability(double prob) { this.treasure_probability = prob; }
    public void setSevenProbability(double prob) { this.seven_probability = prob; }
    
    //문양 등장 '확률' 보너스 계산식(덧셈용) getter/setter
    public double getLemon_probability_sumBonus() { return lemon_probability_sumBonus; }
    public void setLemon_probability_sumBonus(double tempLemonBonus) { 
        this.lemon_probability_sumBonus = tempLemonBonus; 
    }

    public double getCherry_probability_sumBonus() { return cherry_probability_sumBonus; }
    public void setCherry_probability_sumBonus(double tempCherryBonus) { 
        this.cherry_probability_sumBonus = tempCherryBonus; 
    }

    public double getClover_probability_sumBonus() { return clover_probability_sumBonus; }
    public void setClover_probability_sumBonus(double tempCloverBonus) { 
        this.clover_probability_sumBonus = tempCloverBonus; 
    }

    public double getBell_probability_sumBonus() { return bell_probability_sumBonus; }
    public void setBell_probability_sumBonus(double tempBellBonus) { 
        this.bell_probability_sumBonus = tempBellBonus; 
    }

    public double getDiamond_probability_sumBonus() { return diamond_probability_sumBonus; }
    public void setDiamond_probability_sumBonus(double tempDiamondBonus) { 
        this.diamond_probability_sumBonus = tempDiamondBonus; 
    }

    public double getTreasure_probability_sumBonus() { return treasure_probability_sumBonus; }
    public void setTreasure_probability_sumBonus(double tempTreasureBonus) { 
        this.treasure_probability_sumBonus = tempTreasureBonus; 
    }

    public double getSeven_probability_sumBonus() { return seven_probability_sumBonus; }
    public void setSeven_probability_sumBonus(double tempSevenBonus) { 
        this.seven_probability_sumBonus = tempSevenBonus; 
    }
    
    //문양 등장 '확률' 보너스 계산식(곱셈용) getter/setter
    public double getLemon_probability_multipBonus() { return lemon_probability_multipBonus; }
    public void setLemon_probability_multipBonus(double bonus) { this.lemon_probability_multipBonus = bonus; }
    
    public double getCherry_probability_multipBonus() { return cherry_probability_multipBonus; }
    public void setCherry_probability_multipBonus(double bonus) { this.cherry_probability_multipBonus = bonus; }

    public double getClover_probability_multipBonus() { return clover_probability_multipBonus; }
    public void setClover_probability_multipBonus(double bonus) { this.clover_probability_multipBonus = bonus; }

    public double getBell_probability_multipBonus() { return bell_probability_multipBonus; }
    public void setBell_probability_multipBonus(double bonus) { this.bell_probability_multipBonus = bonus; }

    public double getDiamond_probability_multipBonus() { return diamond_probability_multipBonus; }
    public void setDiamond_probability_multipBonus(double bonus) { this.diamond_probability_multipBonus = bonus; }

    public double getTreasure_probability_multipBonus() { return treasure_probability_multipBonus; }
    public void setTreasure_probability_multipBonus(double bonus) { this.treasure_probability_multipBonus = bonus; }

    public double getSeven_probability_multipBonus() { return seven_probability_multipBonus; }
    public void setSeven_probability_multipBonus(double bonus) { this.seven_probability_multipBonus = bonus; }
    
    // 변형자 확률 getter/setter 메서드들
    public double getChainModifierProbability() { return chainModifierProbability; }
    public double getRepeatModifierProbability() { return repeatModifierProbability; }
    public double getTokenModifierProbability() { return tokenModifierProbability; }
    public double getTicketModifierProbability() { return ticketModifierProbability; }
    
    public void setChainModifierProbability(double prob) { 
        this.chainModifierProbability = Math.max(0.0, Math.min(1.0, prob)); 
    }
    public void setRepeatModifierProbability(double prob) { 
        this.repeatModifierProbability = Math.max(0.0, Math.min(1.0, prob)); 
    }
    public void setTokenModifierProbability(double prob) { 
        this.tokenModifierProbability = Math.max(0.0, Math.min(1.0, prob)); 
    }
    public void setTicketModifierProbability(double prob) { 
        this.ticketModifierProbability = Math.max(0.0, Math.min(1.0, prob)); 
    }
    
    public int[] getSymbolOriginal() {
        return symbol_original.clone();
    }
    public int getSymbolOriginal(int index) {
        if (index >= 0 && index < symbol_original.length) {
            return symbol_original[index];
        }
        return 0;
    }
    public void setSymbolOriginal(int[] symbolOriginal) {
        if (symbolOriginal != null && symbolOriginal.length == symbol_original.length) {
            this.symbol_original = symbolOriginal.clone();
        }
    }
    public void setSymbolOriginal(int index, int value) {
        if (index >= 0 && index < symbol_original.length) {
            this.symbol_original[index] = value;
        }
    }
    public int[] getPatternOriginal() {
        return pattern_original.clone();
    }
    public int getPatternOriginal(int index) {
        if (index >= 0 && index < pattern_original.length) {
            return pattern_original[index];
        }
        return 0;
    }
    public void setPatternOriginal(int[] patternOriginal) {
        if (patternOriginal != null && patternOriginal.length == pattern_original.length) {
            this.pattern_original = patternOriginal.clone();
        }
    }
    public void setPatternOriginal(int index, int value) {
        if (index >= 0 && index < pattern_original.length) {
            this.pattern_original[index] = value;
        }
    }
    // symbol_sum getter/setter
    public int[] getSymbolSum() {
        return symbol_sum.clone(); // 배열 복사본 반환 (캡슐화 유지)
    }
    
    public int getSymbolSum(int index) {
        if (index >= 0 && index < symbol_sum.length) {
            double finalValue = symbol_sum[index] * this.tempSymbolBonus[index];	//기본 계산식 + 단발성 유물 계산식--------------------------
            return (int) finalValue;
        }
        return 0;
    }
    
    public void setSymbolSum(int[] symbolSum) {
        if (symbolSum != null && symbolSum.length == symbol_sum.length) {
            this.symbol_sum = symbolSum.clone();
        }
    }
    
    public void setSymbolSum(int index, int value) {
        if (index >= 0 && index < symbol_sum.length) {
            this.symbol_sum[index] = value;
        }
    }
    
    // pattern_sum getter/setter
    public int[] getPatternSum() {
        return pattern_sum.clone(); // 배열 복사본 반환 (캡슐화 유지)
    }
    
    public int getPatternSum(int index) {
        if (index >= 0 && index < pattern_sum.length) {
            // 더블 찬스 체크 (한 번만 체크되도록 플래그 사용)
            if (doubleChanceMultiplier == 1.0) {
                checkDoubleChance();
            }
            double finalValue = (pattern_sum[index] * this.tempPatternBonus[index] * this.doubleChanceMultiplier);	
            return (int) finalValue;
        }
        return 0;
    }
    
    /**
     * 더블 찬스 유물 효과를 체크하고 배율을 설정합니다.
     * 15% 확률로 패턴 가격이 2배가 됩니다.
     */
    private void checkDoubleChance() {
        if (user_item != null && user_item.contains("더블 찬스(영구형)")) {
            if (Math.random() < 0.15) { // 15% 확률
                doubleChanceMultiplier = 2.0;
                System.out.println("더블 찬스 발동! 패턴 가격이 2배가 되었습니다.");
            }
        }
    }
    
    public void setPatternSum(int[] patternSum) {
        if (patternSum != null && patternSum.length == pattern_sum.length) {
            this.pattern_sum = patternSum.clone();
        }
    }
    
    public void setPatternSum(int index, int value) {
        if (index >= 0 && index < pattern_sum.length) {
            this.pattern_sum[index] = value;
        }
    }
    //이거 쓰던가
    public void addOwnItem_List(String itemName) {
        this.user_item.add(itemName);
    }

    public List<String> getOwnItem_List() {
        return user_item;
    }
    
    public void setItemReroll_count(int itemReroll_count) {
    	this.itemReroll_count = itemReroll_count;
    }

    public int getFreeItemReroll_count() {
    	return freeItemReroll_count;
    }
    
    public void setFreeItemReroll_count(int freeItemReroll_count) {
    	this.freeItemReroll_count = freeItemReroll_count;
    }
    
    public int addFreeItemReroll_count(int addFreeItemReroll_count) {
    	this.freeItemReroll_count += addFreeItemReroll_count;
    	return freeItemReroll_count;
    }

    public int getFreeCallReroll_count() {
    	return freeCallReroll_count;
    }
    
    public void setFreeCallReroll_count(int freeItemReroll_count) {
    	this.freeCallReroll_count = freeItemReroll_count;
    }
    
    public int addFreeCallReroll_count(int addFreeItemReroll_count) {
    	this.freeCallReroll_count += addFreeItemReroll_count;
    	return freeCallReroll_count;
    }

    public void addUserItem_List(String itemName) {
            if (this.user_item.size() < this.item_max) { // item_max 제한 확인 (선택 사항)
                this.user_item.add(itemName);
                System.out.println("DEBUG: 유물 [" + itemName + "] 획득. 현재 " + this.user_item.size() + "개 소유.");
            } else {
                System.out.println("DEBUG: 유물 최대 소유 개수를 초과하여 획득 실패.");
            }
        }
    
    public List<String> getUserItem_List() {
        return this.user_item;
    }
    
    public boolean removeUserItem_List(String itemName) {
        // List의 remove(Object) 메서드는 첫 번째로 일치하는 항목을 제거하고 성공 여부를 반환합니다.
        if (itemName == null) {
            return false;
        }
        return this.user_item.remove(itemName);
    }
    
    /**
     * 소유 유물 이름 리스트를 "읽을 때" 사용하는 메서드.
     * 내부 리스트를 그대로 노출하지 않기 위해 복사본을 반환합니다.
     */
    public List<String> getOwnedItemNames() {
        if (user_item == null) {
            user_item = new ArrayList<>();
        }
        return new ArrayList<>(user_item);
    }

    /**
     * 소유 유물 이름 리스트를 통째로 설정합니다.
     * CSV 로드 등에서 사용됩니다.
     */
    public void setOwnedItemNames(List<String> ownedNames) {
        if (user_item == null) {
            user_item = new ArrayList<>();
        } else {
            user_item.clear();
        }
        if (ownedNames != null) {
            user_item.addAll(ownedNames);
        }
    }

    /**
     * 소유 유물에 이름 하나를 추가합니다.
     */
    public void addOwnedItemName(String itemName) {
        if (itemName == null || itemName.isEmpty()) return;
        if (user_item == null) {
            user_item = new ArrayList<>();
        }
        user_item.add(itemName);
    }
    

  //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    public int getItemStackCount(String itemName) {
        return itemStacks.getOrDefault(itemName, 0);
    }
    
    public void addItemStack(String itemName) {
        int current = getItemStackCount(itemName);
        itemStacks.put(itemName, current + 1);
        

    }
    
    /**
     * itemStacks 맵을 반환합니다. (저장/로드용)
     */
    public Map<String, Integer> getItemStacks() {
        return new HashMap<>(itemStacks);
    }
    
    /**
     * itemStacks 맵을 설정합니다. (저장/로드용)
     */
    public void setItemStacks(Map<String, Integer> stacks) {
        if (stacks != null) {
            this.itemStacks = new HashMap<>(stacks);
        }
    }
    /**
     * [추가] 아이템의 지속 횟수를 설정합니다. (구매 시 호출)
     */
    public void setItemDuration(String itemName, int count) {
        itemDurations.put(itemName, count);
    }

    /**
     * [추가] 아이템의 남은 횟수를 가져옵니다.
     */
    public int getItemDuration(String itemName) {
        return itemDurations.getOrDefault(itemName, 0);
    }

    /**
     * [추가] 아이템의 횟수를 1 차감하고, 남은 횟수를 반환합니다.
     */
    public int decreaseItemDuration(String itemName) {
        if (itemDurations.containsKey(itemName)) {
            int current = itemDurations.get(itemName);
            int next = current - 1;
            itemDurations.put(itemName, next);
            return next;
        }
        return 0;
    }
    
    // 아이템 삭제 시 카운터도 같이 지워주는 게 좋습니다.
    public boolean removeOwnedItemName(String itemName) {
        if (user_item == null || itemName == null) return false;
        itemDurations.remove(itemName); // ⭐ 횟수 정보도 삭제
        return user_item.remove(itemName);
    }
    
    //--------------------------
}
