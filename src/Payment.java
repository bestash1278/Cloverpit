//이자 시스템 및 납입

public class Payment {
	private int interest;	//이자
	private int roulatte_money; //소지액
	private int total_money; //총 납입액
	private int round_money; //이번 라운드 목표액
	private int payment_amount_clik; //납입버튼 클릭당 돈
	private boolean chack = true; //납입가능한 상태인지
	
	private final User userInfo;	//UserInfo 객체에 의존
	private final RoundManager roundInfo;
	private final RoulatteInfo roulatteInfo;
	private double interest_rate;
	private final ItemShop itemShop;
    private Runnable updateMainStatus; // 상태바 업데이트를 위한 Runnable 인터페이스
    private Runnable updateShopScreen;
    private	final Call call;
    private final Runnable updateCallScreen;
    
	public Payment(User userInfo, RoundManager roundInfo, RoulatteInfo roulatteInfo, ItemShop itemShop,Runnable updateMainStatus, Runnable updateShopScreen, Call call, Runnable updateCallScreen) {
        this.userInfo = userInfo; 
        this.roundInfo = roundInfo;
        this.roulatteInfo = roulatteInfo;
        this.interest_rate = userInfo.getInterest();
        this.itemShop = itemShop;
        this.updateMainStatus = updateMainStatus;
        this.updateShopScreen = updateShopScreen;
        this.call = call;
        this.updateCallScreen = updateCallScreen;

    }
	

	/**------------------이자파트---------------------**/
	/**이자 계산함수**/
	public int interest_count() {
		int total_deposit = this.userInfo.getTotal_money(); //총 납입금
		double currentRate = this.interest_rate;
		int interest_count = (int) (total_deposit * currentRate); //계산식 : 납입총액 * 이자율
		this.interest = interest_count;	//지역변수 만들어서 가독성 올렸습니다.
		return this.interest;	//계산된 이자
	}
	
	/**현재 이자**/
	public int get_interest() {
        return this.interest;
    }
	
	/**-------------납입버튼 파트 --------------**/
	//납입버튼 클릭당 지불액 계산 함수
	private int payment_amount_clik() {	
		//필요한값 가져오기
		int current_round_money = this.userInfo.getDeadline_money(); //라운드 클래스에서 현재 라운드에서 지불할 금액값 가져오기
		//계산식
		int payment_amount = (int)(current_round_money / 20); //1회당 지불액 계산식 = (int) 라운드에 납입해야하는 총 금액 / 20
		this.payment_amount_clik = payment_amount;
		return payment_amount_clik;
	}
	
	//납입 가능 상태인지 확인하는 함수
	public boolean get_chack() {
		return chack;
	}

	// 납입 처리를 수행하고 성공 여부(true/false)를 반환하는 메서드
	public boolean processPayment() {
	    //필요한 값 가져오기
	    int current_money = this.userInfo.getRoulatte_money(); // 유저 소지액
	    int deposit_count = this.userInfo.getTotal_money();    // 현재까지 총 납입액
	    int payment_cost = payment_amount_clik();                  // 버튼 1회 클릭당 납입 기준 금액
	    int current_round_money = this.userInfo.getDeadline_money(); // 현재 라운드 목표 금액
	    int roulatte_cost = this.roulatteInfo.get_roulatte_cost(); // 룰렛 1회 비용
	    //납입 가능 여부 (룰렛 비용을 제외한 금액)
	    int spendable_money = current_money - roulatte_cost; 
	    //납입 목표 달성 여부 확인
	    int remaining_target = current_round_money - deposit_count;
	    //목표 달성 불가 판단
	    if (remaining_target <= 0) {
	        return false; 
	    }
	    //룰렛 돌릴 돈을 남기면 납입할 돈이 없는 경우
	    if (spendable_money <= 0) {
	        return false; // 납입 불가
	    }
	    //실제 납입할 금액 결정: 
	    // (1회 기준 금액) vs (남은 목표 금액) vs (룰렛 비용을 제외한 소지액) 중 가장 작은 값
	    int actual_payment = Math.min(payment_cost, remaining_target);
	    actual_payment = Math.min(actual_payment, spendable_money); 
	    //실제 납입액이 0이면 납입 불가
	    if (actual_payment <= 0) {
	        return false; 
	    }
	    
	    //실제 납입 계산
	    int new_total_money = current_money - actual_payment;	//계산식 : 소지액 - 실제 납입금
	    int new_total_deposit = deposit_count + actual_payment;	//계산식 : 총 납입액 + 실제 납입금
	    this.userInfo.setRoulatte_money(new_total_money);    //유저돈 업데이트
	    this.userInfo.setTotal_money(new_total_deposit); 	//유저 총 납입액 업데이트
	    //라운드 종료 여부 확인 (업데이트 후)
	    if (new_total_deposit >= current_round_money) {
	    	this.userInfo.getDeadline_money();
	        this.itemShop.rerollItems();	//상점리롤
	        this.call.addCallOpportunity();	//전화 기회 부여
	        if (this.updateShopScreen != null) {
	            this.updateShopScreen.run(); 
	        }
		    if (this.updateMainStatus != null) {
		        this.updateMainStatus.run(); // 메인 화면 갱신 요청!
		    }
		    if (this.updateCallScreen != null) { 
		        this.updateCallScreen.run(); 
		    }
	    }

	    return true; // 납입 성공
	}

	
	/**-----------------------------------------**/
	//총 금액(소지액)가져오기
	public int get_roulatte_money() {
		int get_total_money = userInfo.getRoulatte_money();
		this.roulatte_money = get_total_money;
		return roulatte_money;
	}
	
    // 총 납입액 가져오기
    public int get_total_money() {
    	//필요한값 가져오기
    	int get_total_deposit = this.userInfo.getTotal_money();	//얼만큼 납입 했었는지 값 가져오기
    	this.total_money = get_total_deposit;
    	return total_money;	//업데이트한 총 납입앱 가격을 다시 가져오기
    }
    
    
    /**-----------마감기한 보너스 파트----------------------**/
    // 마감기한 보너스 가져오는 함수
    public record get_deadline_bonus(int deadline_bonus_coin, int deadline_bonus_tiket) {}	//return값 2개 보내려고 새로운 공간 만듦
    public get_deadline_bonus deadline_bonus_count() {
    	// 임시 디버깅 코드 추가
        if (this.roundInfo == null) {
            System.err.println("🚨 Payment 클래스: roundInfo가 null입니다! 초기화 문제를 확인하세요.");
        }
        int this_deadline_bonus_coin = this.roundInfo.get_deadline_bonus_coin();
    	int this_deadline_bonus_tiket = this.roundInfo.get_deadline_bonus_tiket();
    	
    	return new get_deadline_bonus(this_deadline_bonus_coin, this_deadline_bonus_tiket);
    }
    
    /**------------목표 납입액---------------**/
    //이번 라운드 납입해야할 금액 가져오는함수
public int get_deadline_money() {
    	int this_round_money = userInfo.getDeadline_money();
    	this.round_money = this_round_money;
    	return round_money;
    }

	
	
}

