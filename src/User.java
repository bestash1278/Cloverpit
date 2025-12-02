public class User {
    private int roulatte_money = 1000;
    private double interest = 0.1;
    private int ticket = 5;
    private int deadline = 10;
    private int round = 1;
    private int deadline_money = 75;
    private int total_money = 30;

    public int getRoulatte_money() { return roulatte_money; }
    public void setRoulatte_money(int m) { this.roulatte_money = m; }
    public double getInterest() { return interest; }
    public int getTicket() { return ticket; }
    public int getDeadline() { return deadline; }
    public int getRound() { return round; }
    public void setRound(int r) { this.round = r; }
    public int getDeadline_money() { return deadline_money; }
    public int getTotal_money() { return total_money; }
}