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
 
    
    // 문양 가격 "레몬", "체리", "클로버", "종", "다이아", "보물", "7"
    private int[] symbol_sum = {2,2,3,3,5,5,7};
    
    // 패턴 가격 "트리플", "쿼드라", "펜타", "세로", "대각선", "지그", "재그", "지상", "천상", "눈", "잭팟" 
    private int[] pattern_sum = {1,2,3,1,1,4,4,7,7,8,10};
    
    /*
    private int what_symbol(String symbol) {
    	int index=0;
    	switch(symbol) {
    	case "레몬": index=0;
    	case "체리": index=1;
    	case "클로버": index=2;
    	case "종": index=3;
    	case "다이아": index=4;
    	case "보물": index=5;
    	case "7": index=6;
    	}
    	return symbol_sum[index];
    }
    */
    
    private int what_pattern(String pattern) {
    	int index=0;
    	switch(pattern) {
    	case "triple": index=0; break;
    	case "quadra": index=1; break;
    	case "penta": index=2; break;
    	case "vertical": index=3;break;
    	case "diagonal": index=4; break;
    	case "zig": index=5; break;
    	case "zag": index=6; break;
    	case "ground": index=7;break;
    	case "heaven": index=8; break;
    	case "eye": index=9;break;
    	case "jackpot": index=10; break;
    	}
    	return pattern_sum[index];
    }
   
    // 게임 상태 변수
    private int round = 1;
    private int round_spin_left = 0;
    int roulette_money = 0;
    private int total_money = 0;
    private int item_max = 0;
    private String user_name = "";
    private String user_call = "";
    private int roulatte_cost = 0;
    private int item_reroll_cost = 0;
    private int call_reroll_cost = 0;
    private int item_free_reroll_cost = 0;
    private int symbol_mul=1;
    private int pattern_mul=1;
    
    private Random random;
    
    // 문양별 확률 변수 (전체 합계 100)
    private double lemon_probability = 100.0 / 7.0;
    private double cherry_probability = 100.0 / 7.0;
    private double clover_probability = 100.0 / 7.0;
    private double bell_probability = 100.0 / 7.0;
    private double diamond_probability = 100.0 / 7.0;
    private double treasure_probability = 100.0 / 7.0;
    private double seven_probability = 100.0 / 7.0;
    
    public Roulette() {
        random = new Random();
        // 기본 확률 설정 (모두 동일하게)
        initializeProbabilities();
    }
    
    private void initializeProbabilities() {
        double defaultProb = 100.0 / SYMBOL_TYPES.length;
        lemon_probability = defaultProb;
        cherry_probability = defaultProb;
        clover_probability = defaultProb;
        bell_probability = defaultProb;
        diamond_probability = defaultProb;
        treasure_probability = defaultProb;
        seven_probability = defaultProb;
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
    
    public int generateRandomSymbol() {
        double randomValue = random.nextDouble() * 100.0;
        double cumulative = 0.0;
        
        // 레몬
        cumulative += lemon_probability;
        if (randomValue < cumulative) return 0;
        
        // 체리
        cumulative += cherry_probability;
        if (randomValue < cumulative) return 1;
        
        // 클로버
        cumulative += clover_probability;
        if (randomValue < cumulative) return 2;
        
        // 종
        cumulative += bell_probability;
        if (randomValue < cumulative) return 3;
        
        // 다이아
        cumulative += diamond_probability;
        if (randomValue < cumulative) return 4;
        
        // 보물
        cumulative += treasure_probability;
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
            case 0: lemon_probability = newProbability; break;
            case 1: cherry_probability = newProbability; break;
            case 2: clover_probability = newProbability; break;
            case 3: bell_probability = newProbability; break;
            case 4: diamond_probability = newProbability; break;
            case 5: treasure_probability = newProbability; break;
            case 6: seven_probability = newProbability; break;
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
        if (excludeIndex != 0) total += lemon_probability;
        if (excludeIndex != 1) total += cherry_probability;
        if (excludeIndex != 2) total += clover_probability;
        if (excludeIndex != 3) total += bell_probability;
        if (excludeIndex != 4) total += diamond_probability;
        if (excludeIndex != 5) total += treasure_probability;
        if (excludeIndex != 6) total += seven_probability;
        return total;
    }
    
    /**
     * 특정 문양을 제외한 나머지 문양들의 확률을 비율로 조정합니다.
     */
    private void adjustOtherProbabilities(int excludeIndex, double ratio) {
        if (excludeIndex != 0) lemon_probability *= ratio;
        if (excludeIndex != 1) cherry_probability *= ratio;
        if (excludeIndex != 2) clover_probability *= ratio;
        if (excludeIndex != 3) bell_probability *= ratio;
        if (excludeIndex != 4) diamond_probability *= ratio;
        if (excludeIndex != 5) treasure_probability *= ratio;
        if (excludeIndex != 6) seven_probability *= ratio;
    }
    
    /**
     * 특정 문양을 제외한 나머지 문양들의 확률을 동일한 값으로 설정합니다.
     */
    private void setEqualProbabilitiesExcept(int excludeIndex, double prob) {
        if (excludeIndex != 0) lemon_probability = prob;
        if (excludeIndex != 1) cherry_probability = prob;
        if (excludeIndex != 2) clover_probability = prob;
        if (excludeIndex != 3) bell_probability = prob;
        if (excludeIndex != 4) diamond_probability = prob;
        if (excludeIndex != 5) treasure_probability = prob;
        if (excludeIndex != 6) seven_probability = prob;
    }
    
    /**
     * 확률을 정규화하여 전체 합이 정확히 100이 되도록 합니다.
     */
    private void normalizeProbabilities() {
        double total = lemon_probability + cherry_probability + clover_probability + 
                      bell_probability + diamond_probability + treasure_probability + 
                      seven_probability;
        
        if (total > 0) {
            double ratio = 100.0 / total;
            lemon_probability *= ratio;
            cherry_probability *= ratio;
            clover_probability *= ratio;
            bell_probability *= ratio;
            diamond_probability *= ratio;
            treasure_probability *= ratio;
            seven_probability *= ratio;
        }
    }
    
    /**
     * 현재 문양별 확률을 반환합니다.
     * @return 확률 배열 (전체 합계 100)
     */
    public double[] getSymbolProbabilities() {
        return new double[] {
            lemon_probability,
            cherry_probability,
            clover_probability,
            bell_probability,
            diamond_probability,
            treasure_probability,
            seven_probability
        };
    }
    
    /**
     * 특정 문양의 확률을 반환합니다.
     * @param symbolIndex 문양 인덱스 (0~6)
     * @return 확률 값
     */
    public double getSymbolProbability(int symbolIndex) {
        switch (symbolIndex) {
            case 0: return lemon_probability;
            case 1: return cherry_probability;
            case 2: return clover_probability;
            case 3: return bell_probability;
            case 4: return diamond_probability;
            case 5: return treasure_probability;
            case 6: return seven_probability;
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
        
        lemon_probability = probabilities[0];
        cherry_probability = probabilities[1];
        clover_probability = probabilities[2];
        bell_probability = probabilities[3];
        diamond_probability = probabilities[4];
        treasure_probability = probabilities[5];
        seven_probability = probabilities[6];
        
        normalizeProbabilities();
    }
    
    // 개별 확률 getter/setter 메서드들
    public double getLemonProbability() { return lemon_probability; }
    public double getCherryProbability() { return cherry_probability; }
    public double getCloverProbability() { return clover_probability; }
    public double getBellProbability() { return bell_probability; }
    public double getDiamondProbability() { return diamond_probability; }
    public double getTreasureProbability() { return treasure_probability; }
    public double getSevenProbability() { return seven_probability; }
    
    public void setLemonProbability(double prob) { setSymbolProbability(0, prob); }
    public void setCherryProbability(double prob) { setSymbolProbability(1, prob); }
    public void setCloverProbability(double prob) { setSymbolProbability(2, prob); }
    public void setBellProbability(double prob) { setSymbolProbability(3, prob); }
    public void setDiamondProbability(double prob) { setSymbolProbability(4, prob); }
    public void setTreasureProbability(double prob) { setSymbolProbability(5, prob); }
    public void setSevenProbability(double prob) { setSymbolProbability(6, prob); }
    
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
                roulette_money += symbol_sum[results[0][2]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            } else if (isZig) {
                detectedPattern = ZIG;
                winMessage.append(String.format("%s 패턴! (%s)\n", 
                    ZIG, SYMBOL_NAMES[zigSymbol]));
                roulette_money += symbol_sum[results[0][2]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
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
                roulette_money += symbol_sum[results[0][0]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            } else if (isZag) {
                detectedPattern = ZAG;
                winMessage.append(String.format("%s 패턴! (%s)\n", 
                    ZAG, SYMBOL_NAMES[zagSymbol]));
                roulette_money += symbol_sum[results[0][0]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
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
                roulette_money += symbol_sum[results[2][1]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
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
                    roulette_money += symbol_sum[results[0][j]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                    
                }
                
                // 대각선 체크 - LEFT_DIAGONAL (오른쪽 위 -> 왼쪽 아래)
                if (results[2][j] == results[1][j+1] && results[1][j+1] == results[0][j+2]) {
                   
                    detectedPattern = LEFT_DIAGONAL;
                    winMessage.append(String.format("위로 대각선 패턴! (%s)\n", SYMBOL_NAMES[results[2][j]]));
                    roulette_money += symbol_sum[results[0][j]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
                    
                }
            
        }
            
            boolean secPenta = (results[1][0] == results[1][1] && 
                                results[1][1] == results[1][2] && 
                                results[1][2] == results[1][3] && 
                                results[1][3] == results[1][4]);

            if (secPenta) {
                    detectedPattern = SECPENTA;
                    winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                        2, SECPENTA, SYMBOL_NAMES[results[1][0]]));
                    roulette_money += symbol_sum[results[1][0]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                    hasWin = true;
            }else if (results[1][0] == results[1][1] && results[1][1] == results[1][2] && results[1][2] == results[1][3]) {
                detectedPattern = QUADRA;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    2, QUADRA, SYMBOL_NAMES[results[1][0]]));
                roulette_money += symbol_sum[results[1][0]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            }
            else if (results[1][1] == results[1][2] && results[1][2] == results[1][3] && results[1][3] == results[1][4]) {
                detectedPattern = QUADRA;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    2, QUADRA, SYMBOL_NAMES[results[1][1]]));
                roulette_money += symbol_sum[results[1][1]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            }
            // TRIPLE (3개 일치)
            else if (results[1][0] == results[1][1] && results[1][1] == results[1][2]) {
                detectedPattern = TRIPLE;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    2, TRIPLE, SYMBOL_NAMES[results[1][0]]));
                roulette_money += symbol_sum[results[1][0]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            }
            else if (results[1][1] == results[1][2] && results[1][2] == results[1][3]) {
                detectedPattern = TRIPLE;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    2, TRIPLE, SYMBOL_NAMES[results[1][1]]));
                roulette_money += symbol_sum[results[1][1]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            }
            else if (results[1][2] == results[1][3] && results[1][3] == results[1][4]) {
                detectedPattern = TRIPLE;
                winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                    2, TRIPLE, SYMBOL_NAMES[results[1][2]]));
                roulette_money += symbol_sum[results[1][2]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                hasWin = true;
            }
        
            
            
            
            // 천상/지상 패턴이 없고 눈 패턴이 없을 때만 트리플 체크
            if (!isGround && !isHeaven && !isEye) {
                // 가로 라인 체크 - PENTA (5개 일치)
                

                for (int i = 0; i < ROWS; i++) {
                    int[] row = results[i];
                    
                    if(secPenta){
                        continue;
                    }
                    if (row[0] == row[1] && row[1] == row[2] && row[2] == row[3] && row[3] == row[4]) {
                        detectedPattern = PENTA;
                        winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                            i + 1, PENTA, SYMBOL_NAMES[row[0]]));
                        roulette_money += symbol_sum[row[0]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                        hasWin = true;
                    } 
                    // QUADRA (4개 일치)
                    if (row[0] == row[1] && row[1] == row[2] && row[2] == row[3]) {
                        detectedPattern = QUADRA;
                        winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                            i + 1, QUADRA, SYMBOL_NAMES[row[0]]));
                        roulette_money += symbol_sum[row[0]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                        hasWin = true;
                    }
                    else if (row[1] == row[2] && row[2] == row[3] && row[3] == row[4]) {
                        detectedPattern = QUADRA;
                        winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                            i + 1, QUADRA, SYMBOL_NAMES[row[1]]));
                        roulette_money += symbol_sum[row[1]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                        hasWin = true;
                    }
                    // TRIPLE (3개 일치)
                    else if (row[0] == row[1] && row[1] == row[2]) {
                        detectedPattern = TRIPLE;
                        winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                            i + 1, TRIPLE, SYMBOL_NAMES[row[0]]));
                        roulette_money += symbol_sum[row[0]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                        hasWin = true;
                    }
                    else if (row[1] == row[2] && row[2] == row[3]) {
                        detectedPattern = TRIPLE;
                        winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                            i + 1, TRIPLE, SYMBOL_NAMES[row[1]]));
                        roulette_money += symbol_sum[row[1]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                        hasWin = true;
                    }
                    else if (row[2] == row[3] && row[3] == row[4]) {
                        detectedPattern = TRIPLE;
                        winMessage.append(String.format("%d번째 행 %s 패턴! (%s)\n", 
                            i + 1, TRIPLE, SYMBOL_NAMES[row[2]]));
                        roulette_money += symbol_sum[row[2]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
                        hasWin = true;
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
                    roulette_money += symbol_sum[results[1][j]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
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
                roulette_money += symbol_sum[results[1][2]] * what_pattern(detectedPattern)*symbol_mul*pattern_mul;
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
