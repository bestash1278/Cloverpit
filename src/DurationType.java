/**
 * 유물 효과의 지속 기간을 나타내는 Enum
 */
public enum DurationType {
    TEMPORARY,  // Type 2: 다음 스핀에만 적용되고 스핀 후 리셋됨
    PERSISTENT  // Type 3: 스핀 후에도 영구적으로 유지됨
    // Type 1 (즉발/영구) 효과는 ItemInfo.applyEffect()에서 처리합니다.
}

