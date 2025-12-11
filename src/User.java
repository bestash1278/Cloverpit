import java.util.ArrayList;
import java.util.List;

public class User {

    private String user_name;  
    private List<String> user_call;
    private List<String> user_item = new ArrayList<>();     

    private int roulatte_money = 0;  
    private double interest = 0.1;  
    private int ticket = 3;
    private int deadline = 1;
    private int round = 1;               
    private int deadline_money = 75;
    private int total_money = 30;
    private int total_spin = 0;
    private int round_spin_left = 7;
    private int item_max = 6;
    
    private int roulatte_cost = 2;	//룰렛 1회 비용

    private int call_count = 0;	//전화를 걸 수 있는 기회
    private int callReroll_count = 0; //전화 리롤 횟수
    private int itemReroll_count = 0; //유물상점 리롤 횟수
    private int freeItemReroll_count = 0; //무료 상점 리롤 횟수
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

    public User() { }

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
    

    // 전화 관련 메서드
    public int getCall_count() {
    	return call_count;
    }
    
    public void setCall_count(int call_count) {
    	this.call_count = call_count;
    }
    
    public void addCall_count() {	//전화 기회 추가
    	this.call_count += 1;
    }
    
    public void minusCall_count() {	//전화 기회 빼기
    	this.call_count -= 1;
    }
    
    public int getRoulatte_cost() {
        return roulatte_cost;
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
    
    // 개별 확률 getter/setter 메서드들
    public double getLemonProbability() { return lemon_probability; }
    public double getCherryProbability() { return cherry_probability; }
    public double getCloverProbability() { return clover_probability; }
    public double getBellProbability() { return bell_probability; }
    public double getDiamondProbability() { return diamond_probability; }
    public double getTreasureProbability() { return treasure_probability; }
    public double getSevenProbability() { return seven_probability; }
    
    public void setLemonProbability(double prob) { this.lemon_probability = prob; }
    public void setCherryProbability(double prob) { this.cherry_probability = prob; }
    public void setCloverProbability(double prob) { this.clover_probability = prob; }
    public void setBellProbability(double prob) { this.bell_probability = prob; }
    public void setDiamondProbability(double prob) { this.diamond_probability = prob; }
    public void setTreasureProbability(double prob) { this.treasure_probability = prob; }
    public void setSevenProbability(double prob) { this.seven_probability = prob; }
    
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
            return symbol_sum[index];
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
            return pattern_sum[index];
        }
        return 0;
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

    
    //--------유저 유물-----------
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
    
    public void addUserItem_List(String itemName) {
    	this.user_item.add(itemName);
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
    //--------------------------
}

