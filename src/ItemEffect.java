// ItemEffect.java (새 파일)

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

