import java.util.Random;

public class Roulette {
    private static final int[] SYMBOL_TYPES = {SymbolIcon.LEMON, SymbolIcon.CHERRY, SymbolIcon.CLOVER, 
                                               SymbolIcon.BELL, SymbolIcon.DIAMOND, SymbolIcon.TREASURE, SymbolIcon.SEVEN};
    private static final String[] SYMBOL_NAMES = {"레몬", "체리", "클로버", "종", "다이아", "보물", "7"};
    private static final int ROWS = 3;
    private static final int COLS = 5;
    
    // 패턴 상수
    private static final String TRIPLE = "triple";
    private static final String QUADRA = "quadra";
    private static final String PENTA = "penta";
    private static final String SECPENTA = "penta";
    private static final String VERTICAL = "vertical";
    private static final String RIGHT_DIAGONAL = "diagonal";
    private static final String LEFT_DIAGONAL = "diagonal";
    private static final String ZIG = "zig";
    private static final String ZAG = "zag";
    private static final String GROUND = "ground";
    private static final String HEAVEN = "heaven";
    private static final String EYE = "eye";
    private static final String JACKPOT = "jackpot";
    
    // 변형자 상수
    private static final String MODIFIER_CHAIN = "사슬";
    private static final String MODIFIER_REPEAT = "반복";
    private static final String MODIFIER_TOKEN = "토큰";
    private static final String MODIFIER_TICKET = "티켓";
    private static final String[] MODIFIERS = {MODIFIER_CHAIN, MODIFIER_REPEAT, MODIFIER_TOKEN, MODIFIER_TICKET};
    
    // 문양 정보를 담는 내부 클래스
    public static class SymbolInfo {
        private int symbolIndex; // 문양 인덱스 (항상 유효한 값)
        private String modifier; // 변형자 이름 (null이면 변형자 없음)
        
        public SymbolInfo(int symbolIndex, String modifier) {
            this.symbolIndex = symbolIndex;
            this.modifier = modifier;
        }
        
        public int getSymbolIndex() {
            return symbolIndex;
        }
        
        public String getModifier() {
            return modifier;
        }
        
        public void setModifier(String modifier) {
            this.modifier = modifier;
        }
        
        /**
         * 이 위치에 변형자가 적용되었는지 확인합니다.
         * @return 변형자가 있으면 true, 없으면 false
         */
        public boolean hasModifier() {
            return modifier != null && !modifier.isEmpty();
        }
    }
 

    
    
    
    private int what_pattern(String pattern) {
    	switch(pattern) {
    	case "triple": return user.getPatternSum(0);
    	case "quadra": return user.getPatternSum(1);
    	case "penta": return user.getPatternSum(2);
    	case "vertical": return user.getPatternSum(3);
    	case "diagonal": return user.getPatternSum(4);
    	case "zig": return user.getPatternSum(5);
    	case "zag": return user.getPatternSum(6);
    	case "ground": return user.getPatternSum(7);
    	case "heaven": return user.getPatternSum(8);
    	case "eye": return user.getPatternSum(9);
    	case "jackpot": return user.getPatternSum(10);
    	default: return 0;
        }
    }
   
    int roulette_money = 0;
    private int symbol_mul=1;
    private int pattern_mul=1;
    
    private Random random;
    public User user;
    
    // 문양별 확률 변수 (전체 합계 100)

    
    public Roulette() {
        random = new Random();
        // 기본 확률 설정 (모두 동일하게)
        initializeProbabilities();
    }
    
    private void initializeProbabilities() {
        if (user == null) {
            return; // User not set yet, skip initialization
        }
        user.setLemonProbability(user.getLemonProbability());
        user.setCherryProbability(user.getCherryProbability());
        user.setCloverProbability(user.getCloverProbability());
        user.setBellProbability(user.getBellProbability());
        user.setDiamondProbability(user.getDiamondProbability());
        user.setTreasureProbability(user.getTreasureProbability());
        user.setSevenProbability(user.getSevenProbability());
    }

    public void setUser(User user) {
        this.user = user;
        initializeProbabilities();
    }
    
    public int getRows() {
        return ROWS;
    }
    
    public int getCols() {
        return COLS;
    }
    
    public int[] getSymbolTypes() {
        return SYMBOL_TYPES;
    }
    
    public String[] getSymbolNames() {
        return SYMBOL_NAMES;
    }
    
    /**
     * 변형자 이름 배열을 반환합니다.
     * @return 변형자 이름 배열
     */
    public String[] getModifiers() {
        return MODIFIERS.clone();
    }
    
    /**
     * 변형자 상수들을 반환합니다.
     */
    public static String getModifierChain() { return MODIFIER_CHAIN; }
    public static String getModifierRepeat() { return MODIFIER_REPEAT; }
    public static String getModifierToken() { return MODIFIER_TOKEN; }
    public static String getModifierTicket() { return MODIFIER_TICKET; }
    
    public int generateRandomSymbol() {
        double randomValue = random.nextDouble() * 100.0;
        double cumulative = 0.0;
        
        // 레몬
        cumulative += user.getLemonProbability();
        if (randomValue < cumulative) return 0;
        
        // 체리
        cumulative += user.getCherryProbability();
        if (randomValue < cumulative) return 1;
        
        // 클로버
        cumulative += user.getCloverProbability();
        if (randomValue < cumulative) return 2;
        
        // 종
        cumulative += user.getBellProbability();
        if (randomValue < cumulative) return 3;
        
        // 다이아
        cumulative += user.getDiamondProbability();
        if (randomValue < cumulative) return 4;
        
        // 보물
        cumulative += user.getTreasureProbability();
        if (randomValue < cumulative) return 5;
        
        // 7
        return 6;
    }
    
    /**
     * 특정 문양의 확률을 설정하고, 나머지 문양들의 확률을 자동으로 조정합니다.
     * @param symbolIndex 문양 인덱스 (0~6)
     * @param newProbability 새로운 확률 (0~100)
     */
    public void setSymbolProbability(int symbolIndex, double newProbability) {
        if (symbolIndex < 0 || symbolIndex >= SYMBOL_TYPES.length) {
            return;
        }
        
        // 확률 범위 제한
        newProbability = Math.max(0.0, Math.min(100.0, newProbability));
        
        // 나머지 문양들의 현재 확률 합계 계산
        double remainingTotal = getTotalProbabilityExcept(symbolIndex);
        double targetRemaining = 100.0 - newProbability;
        
        // 변경된 확률 설정
        switch (symbolIndex) {
            case 0: user.setLemonProbability(newProbability); break;
            case 1: user.setCherryProbability(newProbability); break;
            case 2: user.setCloverProbability(newProbability); break;
            case 3: user.setBellProbability(newProbability); break;
            case 4: user.setDiamondProbability(newProbability); break;
            case 5: user.setTreasureProbability(newProbability); break;
            case 6: user.setSevenProbability(newProbability); break;
        }
        
        // 나머지 문양들의 확률 조정
        if (remainingTotal > 0 && targetRemaining > 0) {
            // 비례적으로 조정
            double ratio = targetRemaining / remainingTotal;
            adjustOtherProbabilities(symbolIndex, ratio);
        } else if (remainingTotal == 0) {
            // 다른 문양이 없으면 균등 분배
            double equalProb = targetRemaining / (SYMBOL_TYPES.length - 1);
            setEqualProbabilitiesExcept(symbolIndex, equalProb);
        }
        
        // 정밀도 보정: 전체 합이 정확히 100이 되도록
        normalizeProbabilities();
    }
    
    /**
     * 특정 문양을 제외한 나머지 문양들의 확률 합계를 반환합니다.
     */
    private double getTotalProbabilityExcept(int excludeIndex) {
        double total = 0.0;
        if (excludeIndex != 0) total += user.getLemonProbability();
        if (excludeIndex != 1) total += user.getCherryProbability();
        if (excludeIndex != 2) total += user.getCloverProbability();
        if (excludeIndex != 3) total += user.getBellProbability();
        if (excludeIndex != 4) total += user.getDiamondProbability();
        if (excludeIndex != 5) total += user.getTreasureProbability();
        if (excludeIndex != 6) total += user.getSevenProbability();
        return total;
    }
    
    /**
     * 특정 문양을 제외한 나머지 문양들의 확률을 비율로 조정합니다.
     */
    private void adjustOtherProbabilities(int excludeIndex, double ratio) {
        if (excludeIndex != 0) user.setLemonProbability(user.getLemonProbability() * ratio);
        if (excludeIndex != 1) user.setCherryProbability(user.getCherryProbability() * ratio);
        if (excludeIndex != 2) user.setCloverProbability(user.getCloverProbability() * ratio);
        if (excludeIndex != 3) user.setBellProbability(user.getBellProbability() * ratio);
        if (excludeIndex != 4) user.setDiamondProbability(user.getDiamondProbability() * ratio);
        if (excludeIndex != 5) user.setTreasureProbability(user.getTreasureProbability() * ratio);
        if (excludeIndex != 6) user.setSevenProbability(user.getSevenProbability() * ratio);
    }
    
    /**
     * 특정 문양을 제외한 나머지 문양들의 확률을 동일한 값으로 설정합니다.
     */
    private void setEqualProbabilitiesExcept(int excludeIndex, double prob) {
        if (excludeIndex != 0) user.setLemonProbability(prob);
        if (excludeIndex != 1) user.setCherryProbability(prob);
        if (excludeIndex != 2) user.setCloverProbability(prob);
        if (excludeIndex != 3) user.setBellProbability(prob);
        if (excludeIndex != 4) user.setDiamondProbability(prob);
        if (excludeIndex != 5) user.setTreasureProbability(prob);
        if (excludeIndex != 6) user.setSevenProbability(prob);
    }
    
    /**
     * 확률을 정규화하여 전체 합이 정확히 100이 되도록 합니다.
     */
    private void normalizeProbabilities() {
        double total = user.getLemonProbability() + user.getCherryProbability() + user.getCloverProbability() + 
                      user.getBellProbability() + user.getDiamondProbability() + user.getTreasureProbability() + 
                      user.getSevenProbability();
        
        if (total > 0) {
            double ratio = 100.0 / total;
            user.setLemonProbability(user.getLemonProbability() * ratio);
            user.setCherryProbability(user.getCherryProbability() * ratio);
            user.setCloverProbability(user.getCloverProbability() * ratio);
            user.setBellProbability(user.getBellProbability() * ratio);
            user.setDiamondProbability(user.getDiamondProbability() * ratio);
            user.setTreasureProbability(user.getTreasureProbability() * ratio);
            user.setSevenProbability(user.getSevenProbability() * ratio);
        }
    }
    
    /**
     * 현재 문양별 확률을 반환합니다.
     * @return 확률 배열 (전체 합계 100)
     */
    public double[] getSymbolProbabilities() {
        return new double[] {
            user.getLemonProbability(),
            user.getCherryProbability(),
            user.getCloverProbability(),
            user.getBellProbability(),
            user.getDiamondProbability(),
            user.getTreasureProbability(),
            user.getSevenProbability()
        };
    }
    
    /**
     * 특정 문양의 확률을 반환합니다.
     * @param symbolIndex 문양 인덱스 (0~6)
     * @return 확률 값
     */
    public double getSymbolProbability(int symbolIndex) {
        switch (symbolIndex) {
            case 0: return user.getLemonProbability();
            case 1: return user.getCherryProbability();
            case 2: return user.getCloverProbability();
            case 3: return user.getBellProbability();
            case 4: return user.getDiamondProbability();
            case 5: return user.getTreasureProbability();
            case 6: return user.getSevenProbability();
            default: return 0.0;
        }
    }
    
    /**
     * 모든 문양의 확률을 한 번에 설정합니다.
     * @param probabilities 확률 배열 (전체 합계가 100이어야 함)
     */
    public void setAllProbabilities(double[] probabilities) {
        if (probabilities == null || probabilities.length != SYMBOL_TYPES.length) {
            return;
        }
        
        user.setLemonProbability(probabilities[0]);
        user.setCherryProbability(probabilities[1]);
        user.setCloverProbability(probabilities[2]);
        user.setBellProbability(probabilities[3]);
        user.setDiamondProbability(probabilities[4]);
        user.setTreasureProbability(probabilities[5]);
        user.setSevenProbability(probabilities[6]);
        
        normalizeProbabilities();
    }

    /**
     * 보유한 아이템에 해당하는 변형자 목록을 반환합니다.
     * @return 사용 가능한 변형자 배열
     */
    private java.util.ArrayList<String> getAvailableModifiers() {
        java.util.ArrayList<String> availableModifiers = new java.util.ArrayList<>();
        
        if (user == null || user.getUserItem_List() == null) {
            return availableModifiers;
        }
        
        for (String itemName : user.getUserItem_List()) {
            // 아이템 이름에서 변형자 이름 추출 (예: "사슬 변형자 " -> "사슬")
            String trimmedName = itemName != null ? itemName.trim() : "";
            
            // "변형자"가 포함된 경우 변형자 이름만 추출
            if (trimmedName.contains("변형자")) {
                String modifierName = trimmedName.replace(" 변형자", "").replace("변형자", "").trim();
                
                if (modifierName.equals("사슬")) {
                    availableModifiers.add(MODIFIER_CHAIN);
                } else if (modifierName.equals("반복")) {
                    availableModifiers.add(MODIFIER_REPEAT);
                } else if (modifierName.equals("토큰")) {
                    availableModifiers.add(MODIFIER_TOKEN);
                } else if (modifierName.equals("티켓")) {
                    availableModifiers.add(MODIFIER_TICKET);
                }
            } else {
                // 직접 변형자 이름과 비교 (기존 호환성)
                if (trimmedName.equals(MODIFIER_CHAIN)) {
                    availableModifiers.add(MODIFIER_CHAIN);
                } else if (trimmedName.equals(MODIFIER_REPEAT)) {
                    availableModifiers.add(MODIFIER_REPEAT);
                } else if (trimmedName.equals(MODIFIER_TOKEN)) {
                    availableModifiers.add(MODIFIER_TOKEN);
                } else if (trimmedName.equals(MODIFIER_TICKET)) {
                    availableModifiers.add(MODIFIER_TICKET);
                }
            }
        }
        
        return availableModifiers;
    }
    
    /**
     * 변형자 조건을 체크합니다.
     * @param modifier 변형자 이름
     * @return 조건을 만족하면 true, 아니면 false
     */
    public boolean checkModifierCondition(String modifier) {
        if (modifier == null || user == null || user.getUserItem_List() == null) {
            return false;
        }
        
        return user.getUserItem_List().contains(modifier);
    }
    
    /**
     * 문양 정보 배열을 생성합니다. (변형자 포함)
     * 아이템이 있는 경우 일정 확률로 문양에 변형자를 적용합니다.
     * 변형자가 적용되어도 원래 문양 정보는 유지됩니다.
     * @return SymbolInfo 2차원 배열
     */
    public SymbolInfo[][] generateResultsWithModifiers() {
        SymbolInfo[][] results = new SymbolInfo[ROWS][COLS];
        java.util.ArrayList<String> availableModifiers = getAvailableModifiers();
        
        // 변형자 적용 확률 (70% 확률로 변형자 적용)
        double modifierApplyProbability = 0.7;
        
        // 디버깅: 사용 가능한 변형자 확인
        if (availableModifiers.isEmpty()) {
            System.out.println("DEBUG: 사용 가능한 변형자가 없습니다. 보유 아이템: " + 
                (user != null && user.getUserItem_List() != null ? user.getUserItem_List() : "null"));
        } else {
            System.out.println("DEBUG: 사용 가능한 변형자: " + availableModifiers);
        }
        
        int modifierCount = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                // 먼저 확률 기반으로 문양 생성
                int symbolIndex = generateRandomSymbol();
                String modifier = null;
                
                // 사용 가능한 변형자가 있고, 확률에 따라 변형자 적용
                if (!availableModifiers.isEmpty() && random.nextDouble() < modifierApplyProbability) {
                    // 사용 가능한 변형자 중 랜덤 선택
                    int modifierIndex = random.nextInt(availableModifiers.size());
                    modifier = availableModifiers.get(modifierIndex);
                    modifierCount++;
                    // 원래 문양 정보는 유지 (symbolIndex는 그대로)
                }
                
                results[i][j] = new SymbolInfo(symbolIndex, modifier);
            }
        }
        
        System.out.println("DEBUG: 총 " + modifierCount + "개의 변형자가 적용되었습니다.");
        return results;
    }
    
    /**
     * 기존 호환성을 위한 메서드 (변형자 없이 문양 인덱스만 반환)
     * @return 문양 인덱스 2차원 배열
     */
    public int[][] generateResults() {
        int[][] results = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                // 확률 기반으로 문양 생성
                results[i][j] = generateRandomSymbol();
            }
        }
        return results;
    }
    
    /**
     * 변형자 정보를 포함한 패턴 체크 (변형자 효과 적용)
     * 각 패턴이 발동될 때마다 그 패턴의 변형자를 즉시 발동합니다.
     * @param results 문양 인덱스 배열
     * @param symbolInfos 변형자 정보 배열
     * @return 패턴 결과
     */
    public PatternResult checkResults(int[][] results, SymbolInfo[][] symbolInfos) {
        // 변형자 정보를 포함하여 패턴 체크 (패턴 감지 시점에 변형자 발동)
        return checkResultsWithModifiers(results, symbolInfos);
    }
    
    /**
     * 변형자 정보를 포함한 패턴 체크 (내부 메서드)
     * @param results 문양 인덱스 배열
     * @param symbolInfos 변형자 정보 배열
     * @return 패턴 결과
     */
    private PatternResult checkResultsWithModifiers(int[][] results, SymbolInfo[][] symbolInfos) {
        StringBuilder winMessage = new StringBuilder();
        boolean hasWin = false;
        String detectedPattern = "";
        
        // 지그재그 패턴 먼저 체크 (대각선과 중복 방지)
        // 지그 패턴 체크 - ZIG (위로 뾰족한 패턴)
        int zigSymbol = results[0][2];
        boolean isZig = (results[0][2] == zigSymbol && 
                        results[1][1] == zigSymbol && results[1][3] == zigSymbol && 
                        results[2][0] == zigSymbol && results[2][4] == zigSymbol);
        
        // 지상 패턴 체크 - GROUND (지그 패턴 + 아래 행이 모두 같은 문양)
        boolean isGround = false;
        if (isZig && results[2][0] == zigSymbol && results[2][1] == zigSymbol &&
            results[2][2] == zigSymbol && results[2][3] == zigSymbol && 
            results[2][4] == zigSymbol) {
            isGround = true;
            detectedPattern = GROUND;
            winMessage.append(String.format("%s 패턴! (%s)\n", 
                GROUND, SYMBOL_NAMES[zigSymbol]));
            roulette_money += user.getSymbolSum(results[0][2]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
            hasWin = true;
            // 패턴 발동 시 변형자 발동
            if (symbolInfos != null) {
                applyModifierEffectsForPattern(GROUND, symbolInfos, results);
            }
        } else if (isZig) {
            detectedPattern = ZIG;
            winMessage.append(String.format("%s 패턴! (%s)\n", 
                ZIG, SYMBOL_NAMES[zigSymbol]));
            roulette_money += user.getSymbolSum(results[0][2]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
            hasWin = true;
            // 패턴 발동 시 변형자 발동
            if (symbolInfos != null) {
                applyModifierEffectsForPattern(ZIG, symbolInfos, results);
            }
        }
        
        // 재그 패턴 체크 - ZAG (아래로 뾰족한 패턴)
        int zagSymbol = results[0][0];
        boolean isZag = (results[0][0] == zagSymbol && results[0][4] == zagSymbol &&
                        results[1][1] == zagSymbol && results[1][3] == zagSymbol && 
                        results[2][2] == zagSymbol);
        
        // 천상 패턴 체크 - HEAVEN (재그 패턴 + 윗 행이 모두 같은 문양)
        boolean isHeaven = false;
        if (isZag && results[0][0] == zagSymbol && results[0][1] == zagSymbol &&
            results[0][2] == zagSymbol && results[0][3] == zagSymbol && 
            results[0][4] == zagSymbol) {
            isHeaven = true;
            detectedPattern = HEAVEN;
            winMessage.append(String.format("%s 패턴! (%s)\n", 
                HEAVEN, SYMBOL_NAMES[zagSymbol]));
            roulette_money += user.getSymbolSum(results[0][0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
            hasWin = true;
            // 패턴 발동 시 변형자 발동
            if (symbolInfos != null) {
                applyModifierEffectsForPattern(HEAVEN, symbolInfos, results);
            }
        } else if (isZag) {
            detectedPattern = ZAG;
            winMessage.append(String.format("%s 패턴! (%s)\n", 
                ZAG, SYMBOL_NAMES[zagSymbol]));
            roulette_money += user.getSymbolSum(results[0][0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
            hasWin = true;
            // 패턴 발동 시 변형자 발동
            if (symbolInfos != null) {
                applyModifierEffectsForPattern(ZAG, symbolInfos, results);
            }
        }
        
        // 눈 패턴 체크 - EYE
        int eyeSymbol = results[0][1];
        boolean isEye = (results[0][1] == eyeSymbol && results[0][2] == eyeSymbol && 
                        results[0][3] == eyeSymbol &&
                        results[1][0] == eyeSymbol && results[1][4] == eyeSymbol &&
                        results[1][1] == eyeSymbol && results[1][3] == eyeSymbol &&
                        results[2][1] == eyeSymbol && results[2][2] == eyeSymbol && 
                        results[2][3] == eyeSymbol);
        
        if (isEye) {
            detectedPattern = EYE;
            winMessage.append(String.format("%s 패턴! (%s)\n", 
                EYE, SYMBOL_NAMES[eyeSymbol]));
            roulette_money += user.getSymbolSum(results[2][1]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
            hasWin = true;
            // 패턴 발동 시 변형자 발동
            if (symbolInfos != null) {
                applyModifierEffectsForPattern(EYE, symbolInfos, results);
            }
        }
        
        // 지그재그 패턴이 없을 때만 대각선 체크
        for(int j = 0; j < COLS-2; j++) {
            // 대각선 체크 - RIGHT_DIAGONAL (왼쪽 위 -> 오른쪽 아래)
            if((j==0 && isZag) || (j==0 && isHeaven)) {
                continue;
            }
            if((j==2 && isZig) || (j==2 && isGround)) {
                continue;
            }
            if((j==0 && isZig) || (j==0 && isGround)) {
                continue;
            }
            if((j==2 && isZag) || (j==2 && isHeaven)) {
                continue;
            }
            
            if (results[0][j] == results[1][j+1] && results[1][j+1] == results[2][j+2]) {
                detectedPattern = RIGHT_DIAGONAL;
                winMessage.append(String.format("아래로 대각선패턴! (%s)\n", SYMBOL_NAMES[results[0][j]]));
                roulette_money += user.getSymbolSum(results[0][j]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
                // 패턴 발동 시 변형자 발동
                if (symbolInfos != null) {
                    applyModifierEffectsForPattern(RIGHT_DIAGONAL, symbolInfos, results);
                }
            }
            
            // 대각선 체크 - LEFT_DIAGONAL (오른쪽 위 -> 왼쪽 아래)
            if (results[2][j] == results[1][j+1] && results[1][j+1] == results[0][j+2]) {
                detectedPattern = LEFT_DIAGONAL;
                winMessage.append(String.format("위로 대각선 패턴! (%s)\n", SYMBOL_NAMES[results[2][j]]));
                roulette_money += user.getSymbolSum(results[0][j]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
                // 패턴 발동 시 변형자 발동
                if (symbolInfos != null) {
                    applyModifierEffectsForPattern(LEFT_DIAGONAL, symbolInfos, results);
                }
            }
        }
        
        // 모든 행에서 트리플/쿼드라/펜타 패턴 체크 (겹치는 패턴 제외)
        for (int i = 0; i < ROWS; i++) {
            int[] row = results[i];
            
            // PENTA (5개 일치)
            if (row[0] == row[1] && row[1] == row[2] && row[2] == row[3] && row[3] == row[4]) {
                // 천상 패턴이 있으면 0행의 펜타 제외
                if (isHeaven && i == 0) {
                    continue;
                }
                // 지상 패턴이 있으면 2행의 펜타 제외
                if (isGround && i == 2) {
                    continue;
                }
                // 1행의 펜타는 SECPENTA로 처리
                if (i == 1) {
                    detectedPattern = SECPENTA;
                    winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                        i + 1, SECPENTA, SYMBOL_NAMES[row[0]]));
                    roulette_money += user.getSymbolSum(row[0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                    // 패턴 발동 시 변형자 발동
                    if (symbolInfos != null) {
                        applyModifierEffectsForPattern(SECPENTA, symbolInfos, results);
                    }
                } else {
                    detectedPattern = PENTA;
                    winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                        i + 1, PENTA, SYMBOL_NAMES[row[0]]));
                    roulette_money += user.getSymbolSum(row[0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                    // 패턴 발동 시 변형자 발동
                    if (symbolInfos != null) {
                        applyModifierEffectsForPattern(PENTA, symbolInfos, results);
                    }
                }
            } 
            // QUADRA (4개 일치)
            else if (row[0] == row[1] && row[1] == row[2] && row[2] == row[3]) {
                // 천상 패턴이 있으면 0행의 쿼드라 제외
                if (isHeaven && i == 0) {
                    continue;
                }
                // 지상 패턴이 있으면 2행의 쿼드라 제외
                if (isGround && i == 2) {
                    continue;
                }
                detectedPattern = QUADRA;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    i + 1, QUADRA, SYMBOL_NAMES[row[0]]));
                roulette_money += user.getSymbolSum(row[0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
                // 패턴 발동 시 변형자 발동
                if (symbolInfos != null) {
                    applyModifierEffectsForPattern(QUADRA, symbolInfos, results);
                }
            }
            else if (row[1] == row[2] && row[2] == row[3] && row[3] == row[4]) {
                // 천상 패턴이 있으면 0행의 쿼드라 제외
                if (isHeaven && i == 0) {
                    continue;
                }
                // 지상 패턴이 있으면 2행의 쿼드라 제외
                if (isGround && i == 2) {
                    continue;
                }
                detectedPattern = QUADRA;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    i + 1, QUADRA, SYMBOL_NAMES[row[1]]));
                roulette_money += user.getSymbolSum(row[1]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
                // 패턴 발동 시 변형자 발동
                if (symbolInfos != null) {
                    applyModifierEffectsForPattern(QUADRA, symbolInfos, results);
                }
            }
            // TRIPLE (3개 일치)
            else if (row[0] == row[1] && row[1] == row[2]) {
                // 천상 패턴이 있으면 0행의 트리플 제외
                if (isHeaven && i == 0) {
                    continue;
                }
                // 지상 패턴이 있으면 2행의 트리플 제외
                if (isGround && i == 2) {
                    continue;
                }
                detectedPattern = TRIPLE;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    i + 1, TRIPLE, SYMBOL_NAMES[row[0]]));
                roulette_money += user.getSymbolSum(row[0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
                // 패턴 발동 시 변형자 발동
                if (symbolInfos != null) {
                    applyModifierEffectsForPattern(TRIPLE, symbolInfos, results);
                }
            }
            else if (row[1] == row[2] && row[2] == row[3]) {
                // 천상 패턴이 있으면 0행의 트리플 제외
                if (isHeaven && i == 0) {
                    continue;
                }
                // 지상 패턴이 있으면 2행의 트리플 제외
                if (isGround && i == 2) {
                    continue;
                }
                // 눈 패턴이 있을 때 0행과 2행의 [1,2,3] 열 트리플 제외 (눈 패턴과 겹침)
                if (isEye && (i == 0 || i == 2)) {
                    continue;
                }
                detectedPattern = TRIPLE;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    i + 1, TRIPLE, SYMBOL_NAMES[row[1]]));
                roulette_money += user.getSymbolSum(row[1]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
                // 패턴 발동 시 변형자 발동
                if (symbolInfos != null) {
                    applyModifierEffectsForPattern(TRIPLE, symbolInfos, results);
                }
            }
            else if (row[2] == row[3] && row[3] == row[4]) {
                // 천상 패턴이 있으면 0행의 트리플 제외
                if (isHeaven && i == 0) {
                    continue;
                }
                // 지상 패턴이 있으면 2행의 트리플 제외
                if (isGround && i == 2) {
                    continue;
                }
                detectedPattern = TRIPLE;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    i + 1, TRIPLE, SYMBOL_NAMES[row[2]]));
                roulette_money += user.getSymbolSum(row[2]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
                // 패턴 발동 시 변형자 발동
                if (symbolInfos != null) {
                    applyModifierEffectsForPattern(TRIPLE, symbolInfos, results);
                }
            }
        }
        
        // 세로 라인 체크 - VERTICAL
        for (int j = 0; j < COLS; j++) {
            if((isEye && j==1) || (isEye && j==3)) {
                continue;
            }
            if (results[0][j] == results[1][j] && results[1][j] == results[2][j]) {
                detectedPattern = VERTICAL;
                winMessage.append(String.format("%d번째 열 %s 패턴! (%s)\n", 
                    j + 1, VERTICAL, SYMBOL_NAMES[results[0][j]]));
                roulette_money += user.getSymbolSum(results[1][j]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
                // 패턴 발동 시 변형자 발동
                if (symbolInfos != null) {
                    applyModifierEffectsForPattern(VERTICAL, symbolInfos, results);
                }
            }
        }
        
        boolean isJackpot = true;
        int jackpotSymbol = results[0][0];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (results[i][j] != jackpotSymbol) {
                    isJackpot = false;
                    break;
                }
            }
            if (!isJackpot) break;
        }
        
        if (isJackpot) {
            detectedPattern = JACKPOT;
            winMessage.append(String.format("%s 패턴! (%s)\n", 
                JACKPOT, SYMBOL_NAMES[jackpotSymbol]));
            roulette_money += user.getSymbolSum(results[1][2]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
            hasWin = true;
            // 패턴 발동 시 변형자 발동
            if (symbolInfos != null) {
                applyModifierEffectsForPattern(JACKPOT, symbolInfos, results);
            }
        }
        
        return new PatternResult(hasWin, winMessage.toString(), detectedPattern);
    }
    
    /**
     * 특정 패턴에 대한 변형자 효과를 적용합니다.
     * @param pattern 패턴 타입
     * @param symbolInfos 변형자 정보 배열
     * @param results 문양 인덱스 배열
     */
    private void applyModifierEffectsForPattern(String pattern, SymbolInfo[][] symbolInfos, int[][] results) {
        java.util.ArrayList<int[]> patternPositions = getPatternPositions(pattern, results);
        
        if (patternPositions.isEmpty()) {
            return;
        }
        
        // 패턴에 포함된 위치들의 변형자 확인 (중복 허용 - 같은 변형자가 여러 개 있으면 모두 발동)
        java.util.ArrayList<String> modifiersInPattern = new java.util.ArrayList<>();
        for (int[] pos : patternPositions) {
            int i = pos[0];
            int j = pos[1];
            if (symbolInfos[i][j].hasModifier()) {
                modifiersInPattern.add(symbolInfos[i][j].getModifier());
            }
        }
        
        if (modifiersInPattern.isEmpty()) {
            return;
        }
        
        // 반복 변형자의 개수 세기
        int repeatCount = 0;
        for (String modifier : modifiersInPattern) {
            if (modifier.equals(MODIFIER_REPEAT)) {
                repeatCount += 1;
            }
        }
        
        // 패턴 결과 생성 (변형자 효과 적용을 위해)
        PatternResult patternResult = new PatternResult(true, "", pattern);
        
        // 각 변형자 효과 적용
        for (String modifier : modifiersInPattern) {
            if (modifier.equals(MODIFIER_REPEAT)) {
                // 반복 변형자 자체는 원래 개수만큼만 발동 (패턴 보상 추가)
                applyModifierEffect(modifier, pattern, patternResult, results, patternPositions);
            } else {
                // 다른 변형자는 원래 개수만큼 발동
                applyModifierEffect(modifier, pattern, patternResult, results, patternPositions);
            }
        }
        
        // 반복 변형자가 있으면 다른 변형자들의 효과를 반복 변형자 개수만큼 추가 발동
        if (repeatCount > 0) {
            java.util.Set<String> otherModifiers = new java.util.HashSet<>();
            for (String modifier : modifiersInPattern) {
                if (!modifier.equals(MODIFIER_REPEAT)) {
                    otherModifiers.add(modifier);
                }
            }
            // 각 다른 변형자를 반복 변형자 개수만큼 추가 발동
            for (int i = 0; i < repeatCount; i++) {
                for (String modifier : otherModifiers) {
                    applyModifierEffect(modifier, pattern, patternResult, results, patternPositions);
                }
            }
        }
    }
    
    /**
     * 패턴 완성 시 변형자 효과를 적용합니다.
     * @param patternResult 패턴 결과
     * @param symbolInfos 변형자 정보 배열
     * @param results 문양 인덱스 배열
     */
    private void applyModifierEffects(PatternResult patternResult, SymbolInfo[][] symbolInfos, int[][] results) {
        if (!patternResult.hasWin() || symbolInfos == null) {
            return;
        }
        
        String pattern = patternResult.getPattern();
        java.util.ArrayList<int[]> patternPositions = getPatternPositions(pattern, results);
        
        // 패턴에 포함된 위치들의 변형자 확인 (중복 허용 - 같은 변형자가 여러 개 있으면 모두 발동)
        java.util.ArrayList<String> modifiersInPattern = new java.util.ArrayList<>();
        for (int[] pos : patternPositions) {
            int i = pos[0];
            int j = pos[1];
            if (symbolInfos[i][j].hasModifier()) {
                modifiersInPattern.add(symbolInfos[i][j].getModifier());
            }
        }
        
        // 반복 변형자의 개수 세기
        int repeatCount = 0;
        for (String modifier : modifiersInPattern) {
            if (modifier.equals(MODIFIER_REPEAT)) {
                repeatCount+=1;
            }
        }
        
        // 각 변형자 효과 적용
        // 반복 변형자가 있으면 다른 변형자들의 효과도 반복 변형자 개수만큼 추가 발동
        for (String modifier : modifiersInPattern) {
            if (modifier.equals(MODIFIER_REPEAT)) {
                // 반복 변형자 자체는 원래 개수만큼만 발동 (패턴 보상 추가)
                applyModifierEffect(modifier, pattern, patternResult, results, patternPositions);
            } else {
                // 다른 변형자는 원래 개수만큼 발동
                applyModifierEffect(modifier, pattern, patternResult, results, patternPositions);
            }
        }
        
        // 반복 변형자가 있으면 다른 변형자들의 효과를 반복 변형자 개수만큼 추가 발동
        if (repeatCount > 0) {
            java.util.Set<String> otherModifiers = new java.util.HashSet<>();
            for (String modifier : modifiersInPattern) {
                if (!modifier.equals(MODIFIER_REPEAT)) {
                    otherModifiers.add(modifier);
                }
            }
            // 각 다른 변형자를 반복 변형자 개수만큼 추가 발동
            for (int i = 0; i < repeatCount; i++) {
                for (String modifier : otherModifiers) {
                    applyModifierEffect(modifier, pattern, patternResult, results, patternPositions);
                }
            }
        }
    }
    
    /**
     * 패턴 타입에 따라 패턴에 포함된 위치들을 반환합니다.
     * @param pattern 패턴 타입
     * @param results 문양 인덱스 배열
     * @return 패턴에 포함된 위치들의 리스트 (각 위치는 [i, j] 형태)
     */
    public java.util.ArrayList<int[]> getPatternPositions(String pattern, int[][] results) {
        java.util.ArrayList<int[]> positions = new java.util.ArrayList<>();
        
        if (pattern == null || pattern.isEmpty()) {
            return positions;
        }
        
        // 먼저 겹치는 패턴들을 확인
        boolean hasEye = checkEyePattern(results);
        boolean hasHeaven = checkHeavenPattern(results);
        boolean hasGround = checkGroundPattern(results);
        boolean hasZig = checkZigPattern(results);
        boolean hasZag = checkZagPattern(results);
        
        switch (pattern) {
            case TRIPLE:
                // 모든 행에서 트리플 패턴 위치 찾기 (겹치는 패턴 제외)
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j <= COLS - 3; j++) {
                        if (results[i][j] == results[i][j+1] && results[i][j+1] == results[i][j+2]) {
                            // 겹치는 패턴 체크
                            boolean shouldExclude = false;
                            if (hasEye && i == 0 && j == 1) shouldExclude = true; // 눈 패턴의 [0,1],[0,2],[0,3]
                            if (hasEye && i == 2 && j == 1) shouldExclude = true; // 눈 패턴의 [2,1],[2,2],[2,3]
                            if (hasHeaven && i == 0) shouldExclude = true; // 천상 패턴의 윗 행
                            if (hasGround && i == 2) shouldExclude = true; // 지상 패턴의 아래 행
                            
                            if (!shouldExclude) {
                                positions.add(new int[]{i, j});
                                positions.add(new int[]{i, j+1});
                                positions.add(new int[]{i, j+2});
                                return positions; // 첫 번째 트리플만
                            }
                        }
                    }
                }
                break;
                
            case QUADRA:
                // 모든 행에서 쿼드라 패턴 위치 찾기 (겹치는 패턴 제외)
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j <= COLS - 4; j++) {
                        if (results[i][j] == results[i][j+1] && results[i][j+1] == results[i][j+2] && 
                            results[i][j+2] == results[i][j+3]) {
                            // 겹치는 패턴 체크
                            boolean shouldExclude = false;
                            if (hasHeaven && i == 0) shouldExclude = true; // 천상 패턴의 윗 행
                            if (hasGround && i == 2) shouldExclude = true; // 지상 패턴의 아래 행
                            
                            if (!shouldExclude) {
                                positions.add(new int[]{i, j});
                                positions.add(new int[]{i, j+1});
                                positions.add(new int[]{i, j+2});
                                positions.add(new int[]{i, j+3});
                                return positions; // 첫 번째 쿼드라만
                            }
                        }
                    }
                }
                break;
                
            case PENTA:
                // 모든 행에서 펜타 패턴 위치 찾기 (겹치는 패턴 제외)
                for (int i = 0; i < ROWS; i++) {
                    if (results[i][0] == results[i][1] && results[i][1] == results[i][2] && 
                        results[i][2] == results[i][3] && results[i][3] == results[i][4]) {
                        // 겹치는 패턴 체크
                        boolean shouldExclude = false;
                        if (hasHeaven && i == 0) shouldExclude = true; // 천상 패턴의 윗 행
                        if (hasGround && i == 2) shouldExclude = true; // 지상 패턴의 아래 행
                        
                        if (!shouldExclude) {
                            for (int j = 0; j < COLS; j++) {
                                positions.add(new int[]{i, j});
                            }
                            return positions; // 첫 번째 펜타만
                        }
                    }
                }
                break;
                
            case VERTICAL:
                // 세로 패턴 위치 찾기 (눈 패턴 제외)
                for (int j = 0; j < COLS; j++) {
                    if (results[0][j] == results[1][j] && results[1][j] == results[2][j]) {
                        // 눈 패턴 체크 (j==1 또는 j==3인 경우 제외)
                        if (hasEye && (j == 1 || j == 3)) {
                            continue;
                        }
                        positions.add(new int[]{0, j});
                        positions.add(new int[]{1, j});
                        positions.add(new int[]{2, j});
                        return positions; // 첫 번째 세로 패턴만
                    }
                }
                break;
                
            case RIGHT_DIAGONAL:
                // RIGHT_DIAGONAL과 LEFT_DIAGONAL은 같은 값이므로 results 배열을 확인하여 구분
                // 오른쪽 대각선 패턴 (왼쪽 위 -> 오른쪽 아래) 먼저 체크
                for (int j = 0; j <= COLS - 3; j++) {
                    if (results[0][j] == results[1][j+1] && results[1][j+1] == results[2][j+2]) {
                        // 겹치는 패턴 체크
                        boolean shouldExclude = false;
                        // 재그 패턴 체크: [0,0],[1,1],[2,2] (j=0)
                        if (hasZag && j == 0) shouldExclude = true;
                        // 천상 패턴 체크: [0,0],[1,1],[2,2] (j=0)
                        if (hasHeaven && j == 0) shouldExclude = true;
                        // 지상 패턴 체크: [0,2],[1,3],[2,4] (j=2)
                        if (hasGround && j == 2) shouldExclude = true;
                        // 지그 패턴 체크: [0,2],[1,3],[2,4] (j=2)
                        if (hasZig && j == 2) shouldExclude = true;
                        
                        if (!shouldExclude) {
                            positions.add(new int[]{0, j});
                            positions.add(new int[]{1, j+1});
                            positions.add(new int[]{2, j+2});
                            return positions; // 첫 번째 대각선만
                        }
                    }
                }
                // 왼쪽 대각선 패턴 (오른쪽 위 -> 왼쪽 아래) 체크
                for (int j = 0; j <= COLS - 3; j++) {
                    if (results[2][j] == results[1][j+1] && results[1][j+1] == results[0][j+2]) {
                        // 겹치는 패턴 체크
                        boolean shouldExclude = false;
                        // 재그 패턴 체크: [2,2],[1,3],[0,4] (j=2)
                        if (hasZag && j == 2) shouldExclude = true;
                        // 천상 패턴 체크: [2,2],[1,3],[0,4] (j=2)
                        if (hasHeaven && j == 2) shouldExclude = true;
                        // 지상 패턴 체크: [2,0],[1,1],[0,2] (j=0)
                        if (hasGround && j == 0) shouldExclude = true;
                        // 지그 패턴 체크: [2,0],[1,1],[0,2] (j=0) 또는 [0,2],[1,3],[2,4]는 RIGHT_DIAGONAL이므로 j=2는 제외하지 않음
                        if (hasZig && j == 0) shouldExclude = true;
                        
                        if (!shouldExclude) {
                            positions.add(new int[]{2, j});
                            positions.add(new int[]{1, j+1});
                            positions.add(new int[]{0, j+2});
                            return positions; // 첫 번째 대각선만
                        }
                    }
                }
                break;
                
            case ZIG:
                positions.add(new int[]{0, 2});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{1, 3});
                positions.add(new int[]{2, 0});
                positions.add(new int[]{2, 4});
                break;
                
            case ZAG:
                positions.add(new int[]{0, 0});
                positions.add(new int[]{0, 4});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{1, 3});
                positions.add(new int[]{2, 2});
                break;
                
            case GROUND:
                // 지그 패턴 + 아래 행 전체
                positions.add(new int[]{0, 2});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{1, 3});
                positions.add(new int[]{2, 0});
                positions.add(new int[]{2, 1});
                positions.add(new int[]{2, 2});
                positions.add(new int[]{2, 3});
                positions.add(new int[]{2, 4});
                break;
                
            case HEAVEN:
                // 재그 패턴 + 윗 행 전체
                positions.add(new int[]{0, 0});
                positions.add(new int[]{0, 1});
                positions.add(new int[]{0, 2});
                positions.add(new int[]{0, 3});
                positions.add(new int[]{0, 4});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{1, 3});
                positions.add(new int[]{2, 2});
                break;
                
            case EYE:
                positions.add(new int[]{0, 1});
                positions.add(new int[]{0, 2});
                positions.add(new int[]{0, 3});
                positions.add(new int[]{1, 0});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{1, 3});
                positions.add(new int[]{1, 4});
                positions.add(new int[]{2, 1});
                positions.add(new int[]{2, 2});
                positions.add(new int[]{2, 3});
                break;
                
            case JACKPOT:
                // 전체 보드
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < COLS; j++) {
                        positions.add(new int[]{i, j});
                    }
                }
                break;
        }
        
        return positions;
    }
    
    /**
     * 눈 패턴이 존재하는지 확인합니다.
     */
    private boolean checkEyePattern(int[][] results) {
        int eyeSymbol = results[0][1];
        return (results[0][1] == eyeSymbol && results[0][2] == eyeSymbol && results[0][3] == eyeSymbol &&
                results[1][0] == eyeSymbol && results[1][1] == eyeSymbol && results[1][3] == eyeSymbol && results[1][4] == eyeSymbol &&
                results[2][1] == eyeSymbol && results[2][2] == eyeSymbol && results[2][3] == eyeSymbol);
    }
    
    /**
     * 천상 패턴이 존재하는지 확인합니다.
     */
    private boolean checkHeavenPattern(int[][] results) {
        int heavenSymbol = results[0][0];
        return (results[0][0] == heavenSymbol && results[0][1] == heavenSymbol &&
                results[0][2] == heavenSymbol && results[0][3] == heavenSymbol && 
                results[0][4] == heavenSymbol &&
                results[1][1] == heavenSymbol && results[1][3] == heavenSymbol && 
                results[2][2] == heavenSymbol);
    }
    
    /**
     * 지상 패턴이 존재하는지 확인합니다.
     */
    private boolean checkGroundPattern(int[][] results) {
        int groundSymbol = results[0][2];
        return (results[0][2] == groundSymbol && results[1][1] == groundSymbol && 
                results[1][3] == groundSymbol &&
                results[2][0] == groundSymbol && results[2][1] == groundSymbol &&
                results[2][2] == groundSymbol && results[2][3] == groundSymbol && 
                results[2][4] == groundSymbol);
    }
    
    /**
     * 지그 패턴이 존재하는지 확인합니다.
     */
    private boolean checkZigPattern(int[][] results) {
        int zigSymbol = results[0][2];
        return (results[0][2] == zigSymbol && 
                results[1][1] == zigSymbol && results[1][3] == zigSymbol && 
                results[2][0] == zigSymbol && results[2][4] == zigSymbol);
    }
    
    /**
     * 재그 패턴이 존재하는지 확인합니다.
     */
    private boolean checkZagPattern(int[][] results) {
        int zagSymbol = results[0][0];
        return (results[0][0] == zagSymbol && results[0][4] == zagSymbol &&
                results[1][1] == zagSymbol && results[1][3] == zagSymbol && 
                results[2][2] == zagSymbol);
    }
    
    /**
     * 개별 변형자 효과를 적용합니다.
     * @param modifier 변형자 이름
     * @param pattern 패턴 타입
     * @param patternResult 패턴 결과
     * @param results 문양 인덱스 배열
     * @param patternPositions 패턴에 포함된 위치들
     */
    private void applyModifierEffect(String modifier, String pattern, PatternResult patternResult, 
                                     int[][] results, java.util.ArrayList<int[]> patternPositions) {
        if (modifier == null || user == null) {
            return;
        }
        
        if (modifier.equals(MODIFIER_CHAIN)) {
            // 사슬: 해당 패턴의 가치 상승 (pattern_sum)
            int patternIndex = getPatternIndex(pattern);
            if (patternIndex >= 0) {
                int currentValue = user.getPatternSum(patternIndex);
                int originalValue = user.getPatternOriginal(patternIndex);
                // 원래 가치만큼 증가
                user.setPatternSum(patternIndex, currentValue + originalValue);
                System.out.println("사슬 변형자 효과: " + pattern + " 패턴 가치가 " + originalValue + " 증가했습니다.");
            }
            
        } else if (modifier.equals(MODIFIER_REPEAT)) {
            // 반복: 해당 패턴 한번 더
            int patternIndex = getPatternIndex(pattern);
            if (patternIndex >= 0 && !patternPositions.isEmpty()) {
                // 패턴 위치에서 문양 인덱스 가져오기
                int[] firstPos = patternPositions.get(0);
                int symbolIndex = results[firstPos[0]][firstPos[1]];
                // 패턴 보상을 한 번 더 지급
                int additionalReward = user.getSymbolSum(symbolIndex) * what_pattern(pattern) * symbol_mul * pattern_mul;
                roulette_money += additionalReward;
                System.out.println("반복 변형자 효과: " + pattern + " 패턴 보상을 한 번 더 받았습니다. (+" + additionalReward + "원)");
            }
            
        } else if (modifier.equals(MODIFIER_TICKET)) {
            // 티켓: 티켓 +1
            user.addTicket(1);
            System.out.println("티켓 변형자 효과: 티켓이 1개 증가했습니다.");
            
        } else if (modifier.equals(MODIFIER_TOKEN)) {
            // 토큰: 현재 이자만큼 돈 추가
            int interestAmount = (int)(user.getTotal_money() * user.getInterest());
            roulette_money += interestAmount;
            System.out.println("토큰 변형자 효과: 현재 이자(" + (user.getInterest() * 100) + "%)만큼 돈이 추가되었습니다. (+" + interestAmount + "원)");
        }
    }
    
    /**
     * 패턴 타입을 인덱스로 변환합니다.
     * @param pattern 패턴 타입
     * @return 패턴 인덱스 (0~10)
     */
    private int getPatternIndex(String pattern) {
        if (pattern == null) return -1;
        switch (pattern) {
            case TRIPLE: return 0;
            case QUADRA: return 1;
            case PENTA: return 2;
            case VERTICAL: return 3;
            case RIGHT_DIAGONAL:
                // RIGHT_DIAGONAL과 LEFT_DIAGONAL은 같은 값이므로 하나의 case로 처리
                return 4;
            case ZIG: return 5;
            case ZAG: return 6;
            case GROUND: return 7;
            case HEAVEN: return 8;
            case EYE: return 9;
            case JACKPOT: return 10;
            default: return -1;
        }
    }
    
    /**
     * 기존 호환성을 위한 메서드 (변형자 효과 없이 패턴만 체크)
     * @param results 문양 인덱스 배열
     * @return 패턴 결과
     */
    public PatternResult checkResults(int[][] results) {
        StringBuilder winMessage = new StringBuilder();
        boolean hasWin = false;
        String detectedPattern = "";
        
            // 지그재그 패턴 먼저 체크 (대각선과 중복 방지)
            // 지그 패턴 체크 - ZIG (위로 뾰족한 패턴)
            int zigSymbol = results[0][2];
            boolean isZig = (results[0][2] == zigSymbol && 
                            results[1][1] == zigSymbol && results[1][3] == zigSymbol && 
                            results[2][0] == zigSymbol && results[2][4] == zigSymbol);
            
            // 지상 패턴 체크 - GROUND (지그 패턴 + 아래 행이 모두 같은 문양)
            boolean isGround = false;
            if (isZig && results[2][0] == zigSymbol && results[2][1] == zigSymbol &&
                results[2][2] == zigSymbol && results[2][3] == zigSymbol && 
                results[2][4] == zigSymbol) {
                isGround = true;
                detectedPattern = GROUND;
                winMessage.append(String.format("%s 패턴! (%s)\n", 
                    GROUND, SYMBOL_NAMES[zigSymbol]));
                roulette_money += user.getSymbolSum(results[0][2]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            } else if (isZig) {
                detectedPattern = ZIG;
                winMessage.append(String.format("%s 패턴! (%s)\n", 
                    ZIG, SYMBOL_NAMES[zigSymbol]));
                roulette_money += user.getSymbolSum(results[0][2]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            }
            
            // 재그 패턴 체크 - ZAG (아래로 뾰족한 패턴)
            int zagSymbol = results[0][0];
            boolean isZag = (results[0][0] == zagSymbol && results[0][4] == zagSymbol &&
                            results[1][1] == zagSymbol && results[1][3] == zagSymbol && 
                            results[2][2] == zagSymbol);
            
            // 천상 패턴 체크 - HEAVEN (재그 패턴 + 윗 행이 모두 같은 문양)
            boolean isHeaven = false;
            if (isZag && results[0][0] == zagSymbol && results[0][1] == zagSymbol &&
                results[0][2] == zagSymbol && results[0][3] == zagSymbol && 
                results[0][4] == zagSymbol) {
                isHeaven = true;
                detectedPattern = HEAVEN;
                winMessage.append(String.format("%s 패턴! (%s)\n", 
                    HEAVEN, SYMBOL_NAMES[zagSymbol]));
                roulette_money += user.getSymbolSum(results[0][0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            } else if (isZag) {
                detectedPattern = ZAG;
                winMessage.append(String.format("%s 패턴! (%s)\n", 
                    ZAG, SYMBOL_NAMES[zagSymbol]));
                roulette_money += user.getSymbolSum(results[0][0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            }
            
            // 눈 패턴 체크 - EYE
            int eyeSymbol = results[0][1];
            boolean isEye = (results[0][1] == eyeSymbol && results[0][2] == eyeSymbol && 
                            results[0][3] == eyeSymbol &&
                            results[1][0] == eyeSymbol && results[1][4] == eyeSymbol &&
                            results[1][1] == eyeSymbol && results[1][3] == eyeSymbol &&
                            results[2][1] == eyeSymbol && results[2][2] == eyeSymbol && 
                            results[2][3] == eyeSymbol);
            
            if (isEye) {
                detectedPattern = EYE;
                winMessage.append(String.format("%s 패턴! (%s)\n", 
                    EYE, SYMBOL_NAMES[eyeSymbol]));
                roulette_money += user.getSymbolSum(results[2][1]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            }
            
            //  지그재그 패턴이 없을 때만 대각선 체크
           
                for(int j = 0; j < COLS-2; j++) {
                // 대각선 체크 - RIGHT_DIAGONAL (왼쪽 위 -> 오른쪽 아래)
                if((j==0 && isZag) || (j==0 && isHeaven)) {
                    continue;
                }
                if((j==2 && isZig) || (j==2 && isGround)) {
                    continue;
                }

                if((j==0 && isZig) || (j==0 && isGround)) {
                    continue;
                }

                if((j==2 && isZag) || (j==2 && isHeaven)) {
                    continue;
                }
                
                if (results[0][j] == results[1][j+1] && results[1][j+1] == results[2][j+2]) {
                    
                    detectedPattern = RIGHT_DIAGONAL;
                    winMessage.append(String.format("아래로 대각선패턴! (%s)\n", SYMBOL_NAMES[results[0][j]]));
                    roulette_money += user.getSymbolSum(results[0][j]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                    
                }
                
                // 대각선 체크 - LEFT_DIAGONAL (오른쪽 위 -> 왼쪽 아래)
                if (results[2][j] == results[1][j+1] && results[1][j+1] == results[0][j+2]) {
                   
                    detectedPattern = LEFT_DIAGONAL;
                    winMessage.append(String.format("위로 대각선 패턴! (%s)\n", SYMBOL_NAMES[results[2][j]]));
                    roulette_money += user.getSymbolSum(results[0][j]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                    
                }
            
        }
            
            // 모든 행에서 트리플/쿼드라/펜타 패턴 체크 (겹치는 패턴 제외)
            for (int i = 0; i < ROWS; i++) {
                int[] row = results[i];
                
                // PENTA (5개 일치)
                if (row[0] == row[1] && row[1] == row[2] && row[2] == row[3] && row[3] == row[4]) {
                    // 천상 패턴이 있으면 0행의 펜타 제외
                    if (isHeaven && i == 0) {
                        continue;
                    }
                    // 지상 패턴이 있으면 2행의 펜타 제외
                    if (isGround && i == 2) {
                        continue;
                    }
                    // 1행의 펜타는 SECPENTA로 처리
                    if (i == 1) {
                        detectedPattern = SECPENTA;
                        winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                            i + 1, SECPENTA, SYMBOL_NAMES[row[0]]));
                        roulette_money += user.getSymbolSum(row[0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                        hasWin = true;
                    } else {
                        detectedPattern = PENTA;
                        winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                            i + 1, PENTA, SYMBOL_NAMES[row[0]]));
                        roulette_money += user.getSymbolSum(row[0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                        hasWin = true;
                    }
                } 
                // QUADRA (4개 일치)
                else if (row[0] == row[1] && row[1] == row[2] && row[2] == row[3]) {
                    // 천상 패턴이 있으면 0행의 쿼드라 제외
                    if (isHeaven && i == 0) {
                        continue;
                    }
                    // 지상 패턴이 있으면 2행의 쿼드라 제외
                    if (isGround && i == 2) {
                        continue;
                    }
                    detectedPattern = QUADRA;
                    winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                        i + 1, QUADRA, SYMBOL_NAMES[row[0]]));
                    roulette_money += user.getSymbolSum(row[0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                }
                else if (row[1] == row[2] && row[2] == row[3] && row[3] == row[4]) {
                    // 천상 패턴이 있으면 0행의 쿼드라 제외
                    if (isHeaven && i == 0) {
                        continue;
                    }
                    // 지상 패턴이 있으면 2행의 쿼드라 제외
                    if (isGround && i == 2) {
                        continue;
                    }
                    detectedPattern = QUADRA;
                    winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                        i + 1, QUADRA, SYMBOL_NAMES[row[1]]));
                    roulette_money += user.getSymbolSum(row[1]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                }
                // TRIPLE (3개 일치)
                else if (row[0] == row[1] && row[1] == row[2]) {
                    // 천상 패턴이 있으면 0행의 트리플 제외
                    if (isHeaven && i == 0) {
                        continue;
                    }
                    // 지상 패턴이 있으면 2행의 트리플 제외
                    if (isGround && i == 2) {
                        continue;
                    }
                    // 눈 패턴이 있을 때 0행과 2행의 [0,1,2] 열 트리플은 눈 패턴과 겹치지 않으므로 체크
                    // 하지만 [1,2,3] 열 트리플은 눈 패턴과 겹침
                    detectedPattern = TRIPLE;
                    winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                        i + 1, TRIPLE, SYMBOL_NAMES[row[0]]));
                    roulette_money += user.getSymbolSum(row[0]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                }
                else if (row[1] == row[2] && row[2] == row[3]) {
                    // 천상 패턴이 있으면 0행의 트리플 제외
                    if (isHeaven && i == 0) {
                        continue;
                    }
                    // 지상 패턴이 있으면 2행의 트리플 제외
                    if (isGround && i == 2) {
                        continue;
                    }
                    // 눈 패턴이 있을 때 0행과 2행의 [1,2,3] 열 트리플 제외 (눈 패턴과 겹침)
                    if (isEye && (i == 0 || i == 2)) {
                        continue;
                    }
                    detectedPattern = TRIPLE;
                    winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                        i + 1, TRIPLE, SYMBOL_NAMES[row[1]]));
                    roulette_money += user.getSymbolSum(row[1]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                }
                else if (row[2] == row[3] && row[3] == row[4]) {
                    // 천상 패턴이 있으면 0행의 트리플 제외
                    if (isHeaven && i == 0) {
                        continue;
                    }
                    // 지상 패턴이 있으면 2행의 트리플 제외
                    if (isGround && i == 2) {
                        continue;
                    }
                    detectedPattern = TRIPLE;
                    winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                        i + 1, TRIPLE, SYMBOL_NAMES[row[2]]));
                    roulette_money += user.getSymbolSum(row[2]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                }
            }
            
            // 세로 라인 체크 - VERTICAL
            for (int j = 0; j < COLS; j++) {
                if((isEye && j==1) || (isEye && j==3)) {
                    continue;
                }
                if (results[0][j] == results[1][j] && results[1][j] == results[2][j]) {
                    detectedPattern = VERTICAL;
                    winMessage.append(String.format("%d번째 열 %s 패턴! (%s)\n", 
                        j + 1, VERTICAL, SYMBOL_NAMES[results[0][j]]));
                    roulette_money += user.getSymbolSum(results[1][j]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                }
            }
            
            boolean isJackpot = true;
            int jackpotSymbol = results[0][0];
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (results[i][j] != jackpotSymbol) {
                        isJackpot = false;
                        break;
                    }
                }
                if (!isJackpot) break;
            }
            
            if (isJackpot) {
                detectedPattern = JACKPOT;
                winMessage.append(String.format("%s 패턴! (%s)\n", 
                    JACKPOT, SYMBOL_NAMES[jackpotSymbol]));
                roulette_money += user.getSymbolSum(results[1][2]) * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            }
        
        
        return new PatternResult(hasWin, winMessage.toString(), detectedPattern);
    }
    
    // 패턴 결과를 담는 내부 클래스
    public static class PatternResult {
        private boolean hasWin;
        private String message;
        private String pattern;
        
        public PatternResult(boolean hasWin, String message, String pattern) {
            this.hasWin = hasWin;
            this.message = message;
            this.pattern = pattern;
        }
        
        public boolean hasWin() {
            return hasWin;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getPattern() {
            return pattern;
        }
    }
}