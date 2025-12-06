public class RoundManager {

    private final User user;

    // 기본 라운드당 스핀 수 (게임 규칙에 맞게 수정 가능)
    private final int baseSpinsPerRound = 5;

    public RoundManager(User user) {
        this.user = user;
        if (user.getRound_spin_left() == 0) {
            user.setRound_spin_left(baseSpinsPerRound);
        }
    }

    /** 새로운 라운드 시작 */
    public void startNewRound() {
        user.setRound(user.getRound() + 1);
        user.setRound_spin_left(baseSpinsPerRound);

        // 라운드가 진행될 때마다 deadline 줄이고 싶으면 여기에서
        if (user.getDeadline() > 0) {
            user.setDeadline(user.getDeadline() - 1);
        }
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
