//ArtifactAction(유물효과)클래스와 DurationType(유물지속시간)클래스의 정보를 받아와서 처리하는곳
//슬룻머신패널 클래스에서 ItemEffect파일을 이용해서 한번에 계산하게 도와줌
public class ItemEffect {
    private final ArtifactAction action;
    private final DurationType duration;

    public ItemEffect(ArtifactAction action, DurationType duration) {
        this.action = action;
        this.duration = duration;
    }

    public ArtifactAction getAction() { return action; }
    public DurationType getDuration() { return duration; }
}

