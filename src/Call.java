import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Call {
    private final User user;
    private List<CallInfo> allAbilities;        // 모든 능력 리스트 (데이터)
    private List<CallInfo> currentSelections;   // UI에 보여줄 현재 3개의 능력
    private static final int SELECTION_COUNT = 3; // 한 번에 보여줄 선택지 개수
    
    private int callRerollTiket_count;
    private final Runnable updateCallScreen;
    private SaveManagerCsv saveManager;
    
    public void setSaveManager(SaveManagerCsv saveManager) {
        this.saveManager = saveManager;
    }

    // 생성자 (필요한 로직 객체 주입)
    public Call(User user, RoundManager roundManager ,Runnable updateCallScreen) {
        this.user = user;
        // 모든 능력 리스트 초기화 (예시로 User와 RoundManager를 넘겨줌)
        this.allAbilities = CallInfo.getAllAbilities(user, roundManager); 
        this.currentSelections = new ArrayList<>();
        this.updateCallScreen = updateCallScreen;
        // 초기 3개 능력 리롤
        rerollAbilities(); 
    }

    // ⭐ 라운드 종료 시 호출되어 사용 기회를 추가하는 함수 (핵심)
    public void addCallOpportunity() {
        user.setCall_count(true);	//기회 1회 추가
        System.out.println("전화 기회 1회 추가. 총 기회: " + user.getCall_count());
    }

    // ⭐ 3가지 능력을 랜덤으로 선택하여 currentSelections에 저장
    public void rerollAbilities() {
        this.currentSelections.clear();
        Random random = new Random();
        
        if (allAbilities.size() >= SELECTION_COUNT) {
            // 중복 없이 3가지 능력 선택
            while (currentSelections.size() < SELECTION_COUNT) {
                int index = random.nextInt(allAbilities.size());
                CallInfo selected = allAbilities.get(index);
                if (!currentSelections.contains(selected)) {
                    currentSelections.add(selected);
                }
            }
        } else {
            // 능력이 3개 미만일 경우 전부 포함
            this.currentSelections.addAll(allAbilities);
        }
        System.out.println("전화 능력 리롤 완료. 현재 선택지: " + currentSelections.size() + "개");
    }
    public int CallRerollTiket_count() {
    	
    	return callRerollTiket_count;
    }

    //전화 리롤사용함수
    public boolean useCallForReroll() {
    	int Reroll_cost = getCallReroll_cost();
    	
        // 무료 리롤 횟수 확인 및 사용
        if (user.getFreeCallReroll_count() > 0) {
            user.addFreeCallReroll_count(-1); 
            System.out.println("무료 리롤 사용 성공. 남은 무료 리롤횟수: " + user.getFreeCallReroll_count());
            return true;
        }
        else {
	        if (user.getTicket() > Reroll_cost) {
	        	user.minusTicket(Reroll_cost);	//계산식 : (전화 리롤 횟수 * 2)
	        	rerollAbilities(); //리롤사용
	        	user.addCallReroll_count();	//전화 리롤 카운트 증가
	        	if (this.updateCallScreen != null) {
	                this.updateCallScreen.run(); // Call_Screen 갱신 (버튼 텍스트 변경)
	            }
	            
	            System.out.println("리롤 사용 성공. 남은 티켓: " + user.getTicket());
	            return true;
	        }
	        return false;
        }
    }
    
    public int getCallReroll_cost() {
    	int count = user.getCallReroll_count() * 2 + 2;
    	return  count; //계산식 : (전화 리롤 횟수 * 2 + 2 )
    }
    
    
    // ⭐ 선택된 능력을 적용하고 기회를 차감하는 함수 (핵심)
    public boolean useCall(int selectionIndex) {
        if (user.getCall_count()  && selectionIndex >= 0 && selectionIndex < currentSelections.size()) {
            CallInfo selectedAbility = currentSelections.get(selectionIndex);
            
            // 1. 능력 적용
            selectedAbility.applyEffect();
            
            // 2. 기회 차감
            user.setCall_count(false);
            
            // 3. 전화 내역에 추가 (능력 이름과 설명)
            String callHistory = selectedAbility.getName() + " - " + selectedAbility.getDescription();
            user.addUser_call(callHistory);
            
            if (this.updateCallScreen != null) {
                this.updateCallScreen.run(); 
           }
            
            System.out.println("전화 사용 성공: [" + selectedAbility.getName() + "]");
            
            // 전화 능력 사용 직후 자동 저장
            if (saveManager != null) {
                saveManager.save(user);
                System.out.println("[SAVE] 전화 사용 저장 완료: " + selectedAbility.getName());
            }
            
            return true;
        }
        System.out.println("전화 사용 실패: 남은 기회가 없거나 잘못된 선택입니다.");
        return false;
    }
    
    // Getter 메서드
    public List<CallInfo> getCurrentSelections() {
        return currentSelections;
    }
    
    public boolean getCall_count() {
        return user.getCall_count();
    }
    
    public int getCallReroll_count() {
    	return user.getCallReroll_count();
    }
    
    public int getTicket() {
    	return user.getTicket();
    }
    
    
}

