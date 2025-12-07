public class User {

    private String user_name;  
    private String user_call;   

    private int roulatte_money = 10000;  
    private double interest = 0.1;  
    private int ticket = 3;
    private int deadline = 1;
    private int round = 1;               
    private int deadline_money = 75;
    private int total_money = 30;

    private int round_spin_left = 7;
    private int item_max = 6;
    
    private int roulatte_cost = 2;	//룰렛 1회 비용

    public User() { }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_call() {
        return user_call;
    }

    public void setUser_call(String user_call) {
        this.user_call = user_call;
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
    
    /*-----진성--------*/
    public int getRoulatte_cost() {
        return roulatte_cost;
    }
    
    public void increaseInterestRate(double rate) {
        this.interest += rate;
        
        // 디버깅/확인용 출력 (선택 사항)
        java.lang.System.out.println(
            "DEBUG: 이자율이 " + (rate * 100) + "%p 증가했습니다. " + 
            "현재 이자율: " + (this.interest * 100) + "%p"
        );
    }
    
    /*---------------*/
}
