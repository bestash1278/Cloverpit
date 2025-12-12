import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaveManagerCsv {

    private static final String SAVE_FILE = "data/user_save.csv";

    // 새 헤더 (기존 + 추가 필드)
    private static final String HEADER =
            "round,round_spin_left,deadline,roulatte_money,"
          + "total_money,ticket,interest,item_max,deadline_money,"
          + "total_spin,call_count,callReroll_count,itemReroll_count,freeItemReroll_count,"
          + "lemon_prob,cherry_prob,clover_prob,bell_prob,diamond_prob,treasure_prob,seven_prob,"
          + "symbol_sum,pattern_sum,user_items,user_call";

    public SaveManagerCsv() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(HEADER);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ========== SAVE ==========

    public void save(User user) {
        File file = new File(SAVE_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // 한 번 저장할 때마다 헤더 + 한 줄 갱신
            bw.write(HEADER);
            bw.newLine();

            // 🔹 새 아이템 게터 사용
            String userItemsStr = joinStringList(user.getOwnedItemNames());
            String userCallStr  = joinStringList(user.getUser_call());

            String symbolSumStr  = joinIntArray(user.getSymbolSum());
            String patternSumStr = joinIntArray(user.getPatternSum());

            StringBuilder sb = new StringBuilder();

            sb.append(user.getRound()).append(",");
            sb.append(user.getRound_spin_left()).append(",");
            sb.append(user.getDeadline()).append(",");
            sb.append(user.getRoulatte_money()).append(",");
            sb.append(user.getTotal_money()).append(",");
            sb.append(user.getTicket()).append(",");
            sb.append(user.getInterest()).append(",");
            sb.append(user.getItem_max()).append(",");
            sb.append(user.getDeadline_money()).append(",");

            sb.append(user.getTotal_spin()).append(",");
            sb.append(user.getCall_count()).append(",");
            sb.append(user.getCallReroll_count()).append(",");
            sb.append(user.getItemReroll_count()).append(",");
            sb.append(user.getFreeItemReroll_count()).append(",");

            sb.append(user.getLemonProbability()).append(",");
            sb.append(user.getCherryProbability()).append(",");
            sb.append(user.getCloverProbability()).append(",");
            sb.append(user.getBellProbability()).append(",");
            sb.append(user.getDiamondProbability()).append(",");
            sb.append(user.getTreasureProbability()).append(",");
            sb.append(user.getSevenProbability()).append(",");

            sb.append(symbolSumStr).append(",");
            sb.append(patternSumStr).append(",");
            sb.append(userItemsStr).append(",");
            sb.append(userCallStr);

            bw.write(sb.toString());
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== LOAD ==========

    public User load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header == null) return null;

            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                return null;
            }

            String[] arr = line.split(",", -1);
            User user = new User();

            int idx = 0;

            // 기본 진행/돈/티켓/이자/목표
            user.setRound(getInt(arr, idx++, user.getRound()));
            user.setRound_spin_left(getInt(arr, idx++, user.getRound_spin_left()));
            user.setDeadline(getInt(arr, idx++, user.getDeadline()));
            user.setRoulatte_money(getInt(arr, idx++, user.getRoulatte_money()));
            user.setTotal_money(getInt(arr, idx++, user.getTotal_money()));
            user.setTicket(getInt(arr, idx++, user.getTicket()));
            user.setInterest(getDouble(arr, idx++, user.getInterest()));
            user.setItem_max(getInt(arr, idx++, user.getItem_max()));
            user.setDeadline_money(getInt(arr, idx++, user.getDeadline_money()));

            // 스핀 / 전화 / 리롤
            user.setTotal_spin(getInt(arr, idx++, user.getTotal_spin()));
            user.setCall_count(getInt(arr, idx++, user.getCall_count()));

            int savedCallReroll = getInt(arr, idx++, user.getCallReroll_count());
            if (savedCallReroll >= 0) {
                for (int i = 0; i < savedCallReroll; i++) {
                    user.addCallReroll_count();
                }
            }

            int savedItemReroll = getInt(arr, idx++, user.getItemReroll_count());
            user.setItemReroll_count(savedItemReroll);

            int savedFreeItemReroll = getInt(arr, idx++, user.getFreeItemReroll_count());
            user.setFreeItemReroll_count(savedFreeItemReroll);

            // 문양 확률
            double lemonProb    = getDouble(arr, idx++, user.getLemonProbability());
            double cherryProb   = getDouble(arr, idx++, user.getCherryProbability());
            double cloverProb   = getDouble(arr, idx++, user.getCloverProbability());
            double bellProb     = getDouble(arr, idx++, user.getBellProbability());
            double diamondProb  = getDouble(arr, idx++, user.getDiamondProbability());
            double treasureProb = getDouble(arr, idx++, user.getTreasureProbability());
            double sevenProb    = getDouble(arr, idx++, user.getSevenProbability());

            user.setLemonProbability(lemonProb);
            user.setCherryProbability(cherryProb);
            user.setCloverProbability(cloverProb);
            user.setBellProbability(bellProb);
            user.setDiamondProbability(diamondProb);
            user.setTreasureProbability(treasureProb);
            user.setSevenProbability(sevenProb);

            // symbol_sum
            String symbolSumStr = getString(arr, idx++, "");
            if (!symbolSumStr.isEmpty()) {
                int[] savedSymbol = parseIntArray(symbolSumStr, user.getSymbolSum().length);
                user.setSymbolSum(savedSymbol);
            }

            // pattern_sum
            String patternSumStr = getString(arr, idx++, "");
            if (!patternSumStr.isEmpty()) {
                int[] savedPattern = parseIntArray(patternSumStr, user.getPatternSum().length);
                user.setPatternSum(savedPattern);
            }

            // user_items (소유 유물 이름들) - 🔹 새 세터 사용
            String userItemsStr = getString(arr, idx++, "");
            if (!userItemsStr.isEmpty()) {
                List<String> items = parseStringList(userItemsStr);
                user.setOwnedItemNames(items);
            }

            // user_call (전화 내역)
            String userCallStr = getString(arr, idx++, "");
            if (!userCallStr.isEmpty()) {
                List<String> calls = parseStringList(userCallStr);
                for (String c : calls) {
                    user.addUser_call(c);
                }
            }

            return user;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void reset() {
        File f = new File(SAVE_FILE);
        if (f.exists()) {
            f.delete();
        }
    }

    // ========== 유틸 함수들 ==========

    private int getInt(String[] arr, int index, int defaultValue) {
        if (index < arr.length) {
            String v = arr[index];
            if (v != null && !v.isEmpty()) {
                try {
                    return Integer.parseInt(v);
                } catch (NumberFormatException e) {
                    // 무시하고 기본값 사용
                }
            }
        }
        return defaultValue;
    }

    private double getDouble(String[] arr, int index, double defaultValue) {
        if (index < arr.length) {
            String v = arr[index];
            if (v != null && !v.isEmpty()) {
                try {
                    return Double.parseDouble(v);
                } catch (NumberFormatException e) {
                    // 무시하고 기본값 사용
                }
            }
        }
        return defaultValue;
    }

    private String getString(String[] arr, int index, String defaultValue) {
        if (index < arr.length) {
            String v = arr[index];
            return (v == null) ? defaultValue : v;
        }
        return defaultValue;
    }

    private String joinIntArray(int[] arr) {
        if (arr == null || arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append("|");
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    private int[] parseIntArray(String value, int length) {
        int[] result = new int[length];
        if (value == null || value.isEmpty()) return result;

        String[] parts = value.split("\\|");
        for (int i = 0; i < length && i < parts.length; i++) {
            try {
                result[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }

    private String joinStringList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : list) {
            if (s == null) continue;
            if (!first) sb.append("|");
            sb.append(s.replace("\n", "\\\\n")); // 개행 보호
            first = false;
        }
        return sb.toString();
    }

    private List<String> parseStringList(String value) {
        List<String> list = new ArrayList<>();
        if (value == null || value.isEmpty()) return list;

        String[] parts = value.split("\\|");
        for (String p : parts) {
            if (p != null && !p.isEmpty()) {
                list.add(p.replace("\\\\n", "\n"));
            }
        }
        return list;
    }
}
