public class User {
    private int roulatte_money = 10000;
    private double interest = 0.1;
    private int ticket = 3;
    private int deadline = 1;
    private int round = 1;
    private int deadline_money = 75;
    private int total_money = 30;

    public User() {
        // 초기 설정
    }

    public int getRoulatte_money() {
        return roulatte_money;
    }

    public void setRoulatte_money(int roulatte_money) {
        this.roulatte_money = roulatte_money;
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
}