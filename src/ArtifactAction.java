// ArtifactAction.java (새 파일)

@FunctionalInterface
public interface ArtifactAction {
    /**
     * 유저 객체를 받아 유물 효과 로직(계산식)을 실행합니다.
     * 이 함수 내부에서 User의 필드를 직접 변경해야 합니다.
     * @param user 유물 효과를 적용할 사용자 객체
     */
    void execute(User user);
}

