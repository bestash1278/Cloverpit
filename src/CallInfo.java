import java.util.ArrayList;
import java.util.List;

public class CallInfo {
    private final String name;	//전화능력 이름
    private final String description;	//전화 설명
    // 이 능력의 효과를 적용할 Runnable 또는 Consumer 등의 함수형 인터페이스
    private final Runnable effect; //전화 효과

    // 생성자
    public CallInfo(String name, String description, Runnable effect) {
        this.name = name;
        this.description = description;
        this.effect = effect;
    }

    // ⭐ 능력 적용 함수 (핵심)
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
        
        // --- 💡 능력 정의 예시 ---
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
        
        // ... 다른 능력들을 여기에 추가 ...
        
        return abilities;
    }
}

