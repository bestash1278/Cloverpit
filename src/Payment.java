//보낼정보 : 이자계산, 현재 이자율, 사용자 돈 변화, 라운드 정보
//받아올 정보 : 사용자 납입액(total_money), 라운드 정보(round)

//납입 => 라운드변화, 티켓, 돈, 납입당 금액, 

//이자 시스템 및 납입

public class Payment {
	private int interest;	//이자
	private int roulatte_money; //소지액
	private int total_money; //총 납입액	: total_deposit => total_money
	private int round_money; //이번 라운드 목표액
	private int payment_amount_clik; //납입버튼 클릭당 돈
	private boolean chack = true; //납입가능한 상태인지
	
	//TODO : interest_Rate(이자율 변수)도 얘들한테 알려줘야할듯, 이자율 건드리는 유물 나올 수 있기 때문에
//	private static double interest_rate = 0.07; //이자율	//이제 userinfo에서 값을 받아오기 때문에 지워두 됨.
	//private static int payment_amount;	//납입당 마이너스 비율(어라? 값이 바뀌어야됨 final이면 안됨)
	
	//TODO : 유저클래스에서 roulatte_money가져오는 함수찾아서 수정하기(
	//1.유저클래스 자체를 받아와서 필요한거 빼먹는 방법(이자 계산식에서 매개변수 빼두됨)
	private final User userInfo;	//UserInfo 객체에 의존
	private final RoundManager roundInfo;
	private final RoulatteInfo roulatteInfo;
	private double interest_rate;
	public Payment(User userInfo, RoundManager roundInfo, RoulatteInfo roulatteInfo) {
        // 주입받은 UserInfo 객체를 내부 필드에 저장합니다.
        this.userInfo = userInfo; 
        this.roundInfo = roundInfo;
        this.roulatteInfo = roulatteInfo;
        this.interest_rate = userInfo.getInterest();
    }
	

	/**------------------이자파트---------------------**/
	/**이자 계산함수**/
	public int interest_count() {
		int total_deposit = this.userInfo.getTotal_money(); // 내부에서 직접 가져옴
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
		//int current_round_money = this.roundInfo.get_round_money(); //라운드 클래스에서 현재 라운드에서 지불할 금액값 가져오기
		int current_round_money = this.userInfo.getDeadline_money(); //라운드 클래스에서 현재 라운드에서 지불할 금액값 가져오기

		//계산식
		int payment_amount = (int)(current_round_money / 20); //1회당 지불액 계산식 = (int) 라운드에 납입해야하는 총 금액 / 20

		this.payment_amount_clik = payment_amount;
		//계산값 반환
		return payment_amount_clik;
	}
	
	//납입 가능 상태인지 확인하는 함수
	public boolean get_chack() {
		return chack;
	}

//	//납입버튼 클릭당 지불해서 소지금 변화(지난 과거용 삭제 요망)
//	public int payment_cost_clik_roulatte_money_test() {	
//		//필요한값 가져오기
//		int current_money = this.userInfo.getTotalMoney();	//유저데이터의 소지액 찾기
//    	int deposit_count = this.roundInfo.get_total_deposit();	//얼만큼 납입 했었는지 값 가져오기
//    	int payment_cost = payment_amount_clik();	//1회당 얼마 납입하는지
//    	int roulatte_cost = this.roulatteInfo.get_roulatte_cost();	//룰렛1회비용
//		int current_round_money = this.roundInfo.get_round_money(); //현재 라운드에서 지불해야할 금액값 가져오기
//		
//    	int new_roulatte_money;
//    	int new_total_deposit;
//    	
//    	//모든 돈을 지불했을때긴 한데 이미 이후에 똑같은게 선언되있어서 지워두 되지 않나?
//    	if(current_round_money <= deposit_count ) {
//    		chack = false;
//    		this.roundInfo.set_round();
//    	}
//    	else {
//	    	//돈낸후 룻렛도 돌릴수있는지
//	    	if (current_money < (roulatte_cost + payment_cost) ) {	
//	    		//룻렛 돌릴 돈도 없는 상태
//	    		if(current_money < roulatte_cost) {	
//	    			chack = false;	//납입 불가상태
//	    		}
//	    		//일부 납입 가능 상태
//	    		chack = true;	
//	    		new_roulatte_money = current_money - (payment_cost - roulatte_cost);	//룻렛 돌릴비용 남기고 납입
//	    		new_total_deposit = deposit_count + (payment_cost - roulatte_cost);
//	    		
//	    	}
//	    	
//	    	else {
//	    		chack = true;
//	    		//계산식 : (라운드 지불 금액 - 납입한 금액) < (룰렛1회 비용 + 버튼1회 비용)
//	    		if((current_round_money - deposit_count) < (roulatte_cost + payment_cost)) {
//	    			new_roulatte_money = current_money - (current_round_money - deposit_count);
//	    			this.roundInfo.set_round();
//	    		}
//	    		new_roulatte_money = current_money - payment_cost; //유저돈 - 납입후 소비된 금액
//	    		new_total_deposit = deposit_count + payment_cost;	//총 납입금액 계산식
//	    	}
//	    	
//	        //최신화
//	    	this.userInfo.setTotalMoney(new_roulatte_money);	//유저돈 변경
//	    	this.userInfo.set_total_deposit(new_total_deposit);	//총 납입액 업데이트
//	    	this.roulatte_money = new_total_deposit; //지역변수 만들어서 가독성 올렸습니다.
//    	}
//		//유저돈 반환
//		return roulatte_money;
//	}
	
	
	// Payment.java (수정된 메서드)

	// Payment.java

	// ... (다른 메서드들 유지) ...


	// 납입 처리를 수행하고 성공 여부(true/false)를 반환하는 메서드로 변경
	public boolean processPayment() {
	    // 1. 필요한 값 가져오기
	    int current_money = this.userInfo.getRoulatte_money();          // 유저 소지액
	    //int deposit_count = this.roundInfo.get_total_deposit();    // 현재까지 총 납입액
	    int deposit_count = this.userInfo.getTotal_money();    // 현재까지 총 납입액

	    int payment_cost = payment_amount_clik();                  // 버튼 1회 클릭당 납입 기준 금액
	    //int current_round_money = this.roundInfo.get_round_money(); // 현재 라운드 목표 금액
	    int current_round_money = this.userInfo.getDeadline_money(); // 현재 라운드 목표 금액

	    int roulatte_cost = this.roulatteInfo.get_roulatte_cost(); // 룰렛 1회 비용
	    
	    // ----------------------------------------------------
	    // ⭐ ⭐ ⭐ 디버깅 메시지 추가 시작 ⭐ ⭐ ⭐
	    // ----------------------------------------------------
	    System.out.println("--- 납입 버튼 클릭 디버그 정보 ---");
	    System.out.println("1. 현재 유저 소지액 (roulatte_money): " + current_money + "원");
	    System.out.println("2. 현재까지 총 납입액 (total_money): " + deposit_count + "원");
	    System.out.println("3. 라운드 목표 금액 (deadline_money): " + current_round_money + "원");
	    System.out.println("4. 룰렛 1회 비용 (roulatte_cost): " + roulatte_cost + "원");
	    System.out.println("5. 1회 납입 기준 금액 (payment_cost): " + payment_cost + "원");
	    System.out.println("------------------------------------");
	    // ----------------------------------------------------
	    
	    // 2. 납입 가능 여부 (룰렛 비용을 제외한 금액)
	    int spendable_money = current_money - roulatte_cost; 
	    
	    // 3. 납입 목표 달성 여부 확인
	    int remaining_target = current_round_money - deposit_count;
	    
	    // ----------------------------------------------------
	    // 🚨 납입 불가능 조건에 디버그 메시지 추가
	    // ----------------------------------------------------

	    // 3-1. 납입 목표 이미 달성 확인 -> 다음 라운드로 전환
	    if (remaining_target <= 0) {
	        System.out.println("🚨 납입 불가 사유: 이미 목표액을 모두 납입했습니다. (남은 금액: " + remaining_target + ")");
	        this.roundInfo.startNewRound(); 
	        return false; 
	    }
	    
	    // 3-2. 룰렛 돌릴 돈을 남기면 납입할 돈이 없는 경우
	    if (spendable_money <= 0) {
	        System.out.println("🚨 납입 불가 사유: 룰렛 비용(" + roulatte_cost + "원)을 제외하면 납입할 금액이 없습니다. (납입 가능 금액: " + spendable_money + ")");
	        return false; // 납입 불가
	    }
	    
	    // 4. 실제 납입할 금액 결정: 
	    // (1회 기준 금액) vs (남은 목표 금액) vs (룰렛 비용을 제외한 소지액) 중 가장 작은 값
	    int actual_payment = Math.min(payment_cost, remaining_target);
	    actual_payment = Math.min(actual_payment, spendable_money); 

	    // 5. 실제 납입액이 0이면 납입 불가
	    if (actual_payment <= 0) {
	        System.out.println("🚨 납입 불가 사유: 실제 납입할 금액이 0원입니다. (actual_payment: " + actual_payment + ")");
	        return false; 
	    }
	    
	    // ----------------------------------------------------
	    System.out.println("✅ 납입 성공 준비: 실제 납입할 금액 (actual_payment): " + actual_payment + "원");
	    // ----------------------------------------------------
	    
	    // 6. 유저 정보 업데이트 (실제 납입 실행)
	    int new_total_money = current_money - actual_payment;	//계산식 : 소지액 - 실제 납입금
	    int new_total_deposit = deposit_count + actual_payment;	//계산식 : 총 납입액 + 실제 납입금
	    
	    this.userInfo.setRoulatte_money(new_total_money);    //유저돈 업데이트
	    this.userInfo.setTotal_money(new_total_deposit); 	//유저 총 납입액 업데이트
	    
	    // 7. 라운드 종료 여부 확인 (업데이트 후)
	    if (new_total_deposit >= current_round_money) {
	        this.roundInfo.startNewRound(); 
	        System.out.println("🎉 라운드 목표 달성! 다음 라운드로 전환됩니다.");
	    }
	    
	    System.out.println("💰 납입 완료: 남은 소지액: " + new_total_money + "원, 총 납입액: " + new_total_deposit + "원");
	    System.out.println("------------------------------------");
	    
	    return true; // 납입 성공
	}

	
	
	// 📌 기존 payment_cost_clik_total_money()는 제거하거나 위 코드로 대체합니다.
	
	//TO Do : 제거해도 될듯?
//	public int payment_cost_clik_total_money() {
//	    // 1. 필요한 값 가져오기
//	    int current_money = this.userInfo.getRoulatte_money();          // 유저 소지액
//	    int deposit_count = this.userInfo.getTotal_money();    // 현재까지 총 납입액
//	    int payment_cost = payment_amount_clik();                  // 버튼 1회 클릭당 납입 기준 금액
//	    int current_round_money = this.userInfo.getDeadline_money(); // 현재 라운드 목표 금액
//	    int roulatte_cost = this.userInfo.getRoulatte_cost(); // 룰렛 1회 비용 (추가)
//	    
//	    // 2. 납입에 사용할 수 있는 실제 금액 계산
//	    // 룰렛 비용을 제외하고 남은 돈만 납입에 사용할 수 있음
//	    int spendable_money = current_money - roulatte_cost; 
//	    
//	    // 3. 납입 가능 여부 초기 확인
//	    if (spendable_money <= 0) {
//	        // 룰렛 돌릴 돈을 남기면 납입할 돈이 없거나, 룰렛 비용보다 소지액이 적은 경우
//	        chack = false; // 납입 불가
//	        return current_money; // 현재 소지액 반환
//	    }
//	    
//	    // 4. 실제 납입할 금액 계산 (actual_payment)
//	    
//	    // 4-1. 라운드 목표 달성까지 남은 금액
//	    int remaining_target = current_round_money - deposit_count;
//	    
//	    // 4-2. 납입 목표 이미 달성 확인
//	    if (remaining_target <= 0) {
//	        chack = false;
//	        this.roundInfo.set_round(); // 다음 라운드로 전환	///////////////////////////////////////////////여기도 나중에 수정
//	        return current_money;
//	    }
//	    
//	    
//	    
//	    // 4-3. 실제 납입할 금액 결정: 
//	    // (1회 기준 금액) vs (남은 목표 금액) vs (룰렛 비용을 제외한 소지액) 중 가장 작은 값
//	    int actual_payment = Math.min(payment_cost, remaining_target);
//	    actual_payment = Math.min(actual_payment, spendable_money); // ** spendable_money 사용 **
//
//	    // 5. 납입 불가 조건 재확인
//	    if (actual_payment <= 0) {
//	        chack = false;
//	        return current_money; 
//	    }
//	    
//	    // 6. 유저 정보 업데이트
//	    int new_total_money = current_money - actual_payment;
//	    int new_total_deposit = deposit_count + actual_payment;
//	    
//	    this.userInfo.setRoulatte_money(roulatte_cost);    // 유저 돈 변경
//	    this.userInfo.setTotal_money(new_total_money); // 총 납입액 업데이트
//	    
//	    // 7. 라운드 종료 여부 확인 (업데이트 후)
//	    if (new_total_deposit >= current_round_money) {
//	        this.roundInfo.set_round(); // 다음 라운드로 전환//////////////////////////////////////////////여기도 나중에 수정
//	    }
//	    
//	    chack = true; // 납입 성공
//	    
//	    // 8. 납입 후 유저의 새로운 소지액 반환
//	    return new_total_money;
//	}
	
	
	
	
	
	
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
