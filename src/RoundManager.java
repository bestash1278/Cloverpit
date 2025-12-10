public class RoundManager {

    private final User user;

    private final int baseSpinsPerRound = 7;
    private final int roundsPerDeadLine = 3;

    private int deadline_bonus_coin = 8;	//마감기한 보너스 코인
    private int deadline_bonus_tiket = 4;	//마감기한 보너스 티켓

    public RoundManager(User user) {
        this.user = user;
        if (user.getRound_spin_left() == 0) {
            user.setRound_spin_left(baseSpinsPerRound);
        }
    }

    public void startNewRound() {
        user.setRound(user.getRound() + 1);
        user.setRound_spin_left(baseSpinsPerRound);

        if (user.getDeadline() > 0) {
            user.setDeadline(user.getDeadline() - 1);
        }
    }
    
    public boolean isDeadlineReached() {
    	return user.getDeadline() <= 0;
    }
    
    public void resetDeadline() {
    	user.setDeadline(roundsPerDeadLine);
    }

    public boolean consumeSpin() {
        if (user.getRound_spin_left() <= 0) {
            return false;
        }
        user.setRound_spin_left(user.getRound_spin_left() - 1);
        return true;
    }

    public boolean useTicketForExtraSpin() {
        if (user.getTicket() <= 0) {
            return false;
        }
        user.addTicket(-1);
        user.setRound_spin_left(user.getRound_spin_left() + 1);
        return true;
    }

    public void applyInterestAfterRound() {
        double interest = user.getInterest();
        if (interest <= 0) return;

        int money = user.getTotal_money();
        int interestAmount = (int) Math.floor(money * interest);
        user.addTotal_money(interestAmount);
    }

    public boolean isDeadlineOver() {
        return user.getDeadline() <= 0;
    }
    
    //마감기한 보너스 값 반환 함수
    public int get_deadline_bonus_coin() {
    	return this.deadline_bonus_coin;
    }
    public int get_deadline_bonus_tiket() {
    	return this.deadline_bonus_tiket;
    }
}
