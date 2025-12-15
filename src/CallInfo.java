import java.util.ArrayList;
import java.util.List;

public class CallInfo {
    private final String name;	//전화능력 이름
    private final String description;	//전화 설명
    private final Runnable effect; //전화 효과

    // 생성자
    public CallInfo(String name, String description, Runnable effect) {
        this.name = name;
        this.description = description;
        this.effect = effect;
    }

    //능력 적용 함수 (핵심)
    public void applyEffect() {
        if (effect != null) {
            effect.run();
        }
    }

    // Getter 메서드
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    // 이 클래스 내부에 모든 능력의 정적 리스트를 선언해야 합니다.
    public static List<CallInfo> getAllAbilities(User user, RoundManager roundManager) {
        List<CallInfo> abilities = new ArrayList<>();
        
        abilities.add(new CallInfo("골든 찬스", "현재 소지 금액을 두 배로 만듭니다.", () -> {
            int currentMoney = user.getRoulatte_money();
            user.setRoulatte_money(currentMoney * 2);
            System.out.println("골든 찬스 발동: 소지액이 두 배가 되었습니다.");
        }));
        
        abilities.add(new CallInfo("납입 면제", "이번 라운드의 납입 목표액을 절반으로 줄입니다.", () -> {
            int currentTarget = user.getDeadline_money();
            user.setDeadline_money(currentTarget / 2);
            System.out.println("납입 면제 발동: 목표액이 절반이 되었습니다.");
        }));
        abilities.add(new CallInfo("행운 가득", "티켓을 4개 획득합니다.", () -> {
            user.addTicket(4);
            System.out.println("티켓 4개를 획득합니다.");
        }));
        abilities.add(new CallInfo("소길", "티켓을 2개 획득합니다.", () -> {
            user.addTicket(4);
            System.out.println("티켓 4개를 획득합니다.");
        }));
        abilities.add(new CallInfo("중길", "티켓을 4개와 소지액의 1/4의 금액을 획득합니다.", () -> {
            user.addTicket(4);
            int money = user.getRoulatte_money();
            user.setRoulatte_money(money / 4);
            System.out.println("중길 : 티켓을 4개와 소지액의 1/4의 금액을 획득합니다.");
        }));
        abilities.add(new CallInfo("대길", "티켓을 8개와 소지액의 절반의 금액을 획득합니다.", () -> {
            user.addTicket(8);
            int money = user.getRoulatte_money();
            user.setRoulatte_money(money);
            System.out.println("티켓 8개 + 소지금의 절반금액 획득");
        }));
        abilities.add(new CallInfo("정체모를 신호", "... . . . ...", () -> {
            user.addRoulatte_money(1000000);
            System.out.println("누군가의 간절한 목소리");
        }));
        abilities.add(new CallInfo("리롤 중독", "무료 상점 리롤 기회를 3회 얻습니다.", () -> {
            user.addFreeItemReroll_count(3);
            System.out.println("리롤중독 : 무료 상점 리롤 + 3");
        }));
        abilities.add(new CallInfo("통신비 납부", "무료 상점 리롤 기회를 3회 얻습니다.", () -> {
            user.addFreeItemReroll_count(2);
            System.out.println("리롤중독 : 무료 상점 리롤 + 3");
        }));
        abilities.add(new CallInfo("과분한 기회", "전화 리롤 횟수를 초기화 합니다.", () -> {
            user.setCallReroll_count(0);
            System.out.println("과분한 기회 : 전화 리롤 횟수 초기화");
        }));
        abilities.add(new CallInfo("만기일", "납입액의 절반을 획득합니다.", () -> {
            int money = user.getTotal_money();
            user.addRoulatte_money(money / 2);
            System.out.println("납입액 절반 획득");
        }));
        abilities.add(new CallInfo("버블 경제", "이자율이 2배 증가합니다.", () -> {
        	double rate = user.getInterest();
            user.increaseInterestRate(rate);
            System.out.println("현재 이자율 * 2");
        }));
        abilities.add(new CallInfo("완벽한 정리", "소유할수 있는 유물의 갯수가 1개 증가합니다.", () -> {
        	int item_max = user.getItem_max();
        	user.setItem_max(item_max + 1);
            System.out.println("소유 유물 +1");
        }));
        
        abilities.add(new CallInfo("레몬 사랑", "레몬 등장확률을 2배 높이고 체리 등장확률을 2배 낮춥니다.", () -> {
            double save = user.getLemonProbability();
            user.setLemonProbability(save * 2);
            save = user.getCherryProbability();
            user.setCherryProbability(save / 2);
            System.out.println("레몬 등장확률을 2배 업, 체리 등장확률을 2배 다운.");
        }));
        abilities.add(new CallInfo("체리 사랑", "체리 등장확률을 2배 높이고 레몬 등장확률을 2배 낮춥니다.", () -> {
            double save = user.getLemonProbability();
            user.setLemonProbability(save / 2);
            save = user.getCherryProbability();
            user.setCherryProbability(save * 2);
            System.out.println("체리 등장확률을 2배 업, 레몬 등장확률을 2배 다운.");
        }));
        
        abilities.add(new CallInfo("클로버 사랑", "클로버 등장확률을 2배 높이고 다이아 등장확률을 2배 낮춥니다.", () -> {
            double save = user.getCloverProbability();
            user.setCloverProbability(save *2);
            save = user.getDiamondProbability();
            user.setDiamondProbability(save / 2);
            System.out.println("클로버 등장확률을 2배 업, 다이아 등장확률을 2배 다운.");
        }));
        abilities.add(new CallInfo("다이아 러버", "다이아 등장확률을 2배 높이고 클로버 등장확률을 2배 낮춥니다.", () -> {
            double save = user.getCloverProbability();
            user.setCloverProbability(save /2);
            save = user.getDiamondProbability();
            user.setDiamondProbability(save * 2);
            System.out.println("다이아 등장확률을 2배 업, 클로버 등장확률을 2배 다운.");
        }));
        abilities.add(new CallInfo("과제", "산더미 처럼 쌓여버린 과제로 인해 모아둔 돈과 티켓을 잃지만 해결하면서 보물의 등장 확률이 2배 증가합니다.", () -> {
        	user.setTicket(0);
        	user.setRoulatte_money(0);
            double TreasureProbability = user.getTreasureProbability();
            user.setTreasureProbability(TreasureProbability * 2);
            System.out.println("기간내에 끝내기에는 무리였다.");
        }));
        abilities.add(new CallInfo("절 반", "50%확률로 현재 소지액의 50%가 증가하거나, 50%확률로 현재 소지액의 50%를 잃습니다.", () -> {
        	int roulatte_money = user.getRoulatte_money();
        	user.setRoulatte_money(roulatte_money * 2 );
            System.out.println("절반 : 소지액의 절반 추가.");
        }));
        abilities.add(new CallInfo("절 반", "50%확률로 소지할수 있는 유물의 갯수가 2개 증가하거나, 50%확률로 현재 소지액의 50%를 잃습니다.", () -> {
        	int roulatte_money = user.getRoulatte_money();
        	user.setRoulatte_money(roulatte_money / 2 );
            System.out.println("절반 : 소지액의 절반 삭제");
        }));
        abilities.add(new CallInfo("시험", "고생끝엔 낙원이 있기를 바랍니다. 모든 금액을 지불하고 7의 확률의 등장 확률이 2배 증가합니다.", () -> {
        	user.setRoulatte_money(0);
            double sevenProbability = user.getSevenProbability();
            user.setLemonProbability(sevenProbability * 2);
            System.out.println("시험 : 아쉽게도 시험이 끝나도 프로젝트가 남아있습니다.");
        }));
        abilities.add(new CallInfo("도박사", "25% 확률로 랜덤한 기능이 동작됩니다. --인생은 한방이야--", () -> {
        	int item_max = user.getItem_max();
        	user.setItem_max(item_max + 2);
            System.out.println("소유 유물 + 2");
        }));
        abilities.add(new CallInfo("도박사", "25% 확률로 랜덤한 기능이 동작됩니다. --인생은 한방이야--", () -> {
        	user.setRoulatte_money(0);
            System.out.println("도박사 : 소지금액 전체 증발");
        }));
        abilities.add(new CallInfo("도박사", "25% 확률로 랜덤한 기능이 동작됩니다. --인생은 한방이야--", () -> {
        	user.setTicket(0);
            System.out.println("도박사 : 소지 티켓 전체 증발");
        }));
        abilities.add(new CallInfo("도박사", "25% 확률로 랜덤한 기능이 동작됩니다. --인생은 한방이야--", () -> {
        	int item_max = user.getItem_max();
        	user.setItem_max(item_max - 1);
            System.out.println("도박사 : 소유 유물 -1");
        }));
        abilities.add(new CallInfo("한방", "잭팟 배수가  3배 증가합니다.", () -> {
        	int save[] = user.getPatternSum();
        	int index = 10;
            System.out.println("잭팟 배수가  3배, 현재 잭팟 배수 : " + user.getPatternSum(index));
        	save[index] = save[index] * 3;
        	user.setPatternSum(index, save[index]);
            System.out.println("잭팟 배수가  3배, 현재 잭팟 배수 : " + user.getPatternSum(index));
        }));
        abilities.add(new CallInfo("345", " 연속으로 3개, 4개, 5개 이어진 패턴들의 배수가  3배 증가합니다.", () -> {
        	int save[] = user.getPatternSum();
        	int index_tr = 1;
            System.out.println("트리플 배수가  3배, 현재 잭팟 배수 : " + user.getPatternSum(index_tr));
        	save[index_tr] = save[index_tr] * 3;
        	user.setPatternSum(index_tr, save[index_tr]);
            System.out.println("쿼드라 배수가  3배, 현재 잭팟 배수 : " + user.getPatternSum(index_tr));
        	int index_q = 2;
            System.out.println("트리플 배수가  3배, 현재 잭팟 배수 : " + user.getPatternSum(index_q));
        	save[index_q] = save[index_q] * 3;
        	user.setPatternSum(index_q, save[index_q]);
            System.out.println("쿼드라 배수가  3배, 현재 잭팟 배수 : " + user.getPatternSum(index_q));
        	int index_p = 3;
            System.out.println("트리플 배수가  3배, 현재 잭팟 배수 : " + user.getPatternSum(index_p));
        	save[index_p] = save[index_p] * 3;
        	user.setPatternSum(index_p, save[index_p]);
            System.out.println("쿼드라 배수가  3배, 현재 잭팟 배수 : " + user.getPatternSum(index_p));
        }));
        
 
        
        
        // ... 다른 능력들을 여기에 추가 ...
        
        return abilities;
    }
}

