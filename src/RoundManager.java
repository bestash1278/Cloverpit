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

        // 스핀 보너스 유물 효과 적용
        if (user.getUserItem_List() != null && user.getUserItem_List().contains("스핀 보너스(영구형)")) {
            int bonusSpins = 2; // 스핀 보너스 유물이 추가하는 스핀 횟수
            user.setRound_spin_left(user.getRound_spin_left() + bonusSpins);
            System.out.println("스핀 보너스 발동! 스핀 횟수가 " + bonusSpins + "회 추가되었습니다.");
        }

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
        int spinLeftBefore = user.getRound_spin_left();
        user.setRound_spin_left(user.getRound_spin_left() - 1);
        
        // 하이 리스크 유물 체크: 스핀 횟수가 0이 되었고, 하이 리스크 유물이 있으면 활성화
        if (user.getRound_spin_left() == 0 && user.getUserItem_List() != null 
            && user.getUserItem_List().contains("하이 리스크(영구형)") 
            && spinLeftBefore == 1) {
            user.setHighRiskActive(true);
            System.out.println("하이 리스크 활성화! 다음 스핀에 비용 2배, 무늬 가격 2배, 패턴 가격 2배가 적용됩니다.");
        }
        
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
        user.addRoulatte_money(interestAmount);
        
        // 복리 계산기 유물 효과 적용
        if (user.getUserItem_List() != null && user.getUserItem_List().contains("복리 계산기(영구형)")) {
            double rateIncrease = 0.01; // 1%p
            user.increaseInterestRate(rateIncrease);
            System.out.println("복리 계산기 발동! 이자율이 " + (rateIncrease * 100) + "%p 증가했습니다. 현재 이자율: " + (user.getInterest() * 100) + "%");
        }
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
