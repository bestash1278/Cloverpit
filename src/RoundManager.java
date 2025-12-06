public class RoundManager {

    private final User user;

    private final int baseSpinsPerRound = 7;
    private final int roundsPerDeadLine = 3;

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
}
