import java.io.*;

public class SaveManagerCsv {

    private static final String SAVE_FILE = "data/user_save.csv";

    public SaveManagerCsv() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                // 헤더 라인
                bw.write("user_name,user_call,round,round_spin_left,deadline,");
                bw.write("roulatte_money,total_money,ticket,interest,item_max,deadline_money");
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** User 전체 상태를 CSV 한 줄로 저장 */
    public void save(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            // 헤더 다시 쓰기
            bw.write("user_name,user_call,round,round_spin_left,deadline,");
            bw.write("roulatte_money,total_money,ticket,interest,item_max,deadline_money");
            bw.newLine();

            StringBuilder sb = new StringBuilder();
            sb.append(nullToEmpty(user.getUser_name())).append(",");
            sb.append(nullToEmpty(user.getUser_call())).append(",");
            sb.append(user.getRound()).append(",");
            sb.append(user.getRound_spin_left()).append(",");
            sb.append(user.getDeadline()).append(",");
            sb.append(user.getRoulatte_money()).append(",");
            sb.append(user.getTotal_money()).append(",");
            sb.append(user.getTicket()).append(",");
            sb.append(user.getInterest()).append(",");
            sb.append(user.getItem_max()).append(",");
            sb.append(user.getDeadline_money());

            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** CSV에서 User 한 명 로드. 없으면 null 리턴 */
    public User load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine(); // 헤더 스킵
            String line = br.readLine();
            if (line == null || line.isEmpty()) {
                return null;
            }

            String[] arr = line.split(",", -1); // 빈 문자열 허용
            if (arr.length < 11) {
                return null;
            }

            User user = new User();
            user.setUser_name(emptyToNull(arr[0]));
            user.setUser_call(emptyToNull(arr[1]));
            user.setRound(Integer.parseInt(arr[2]));
            user.setRound_spin_left(Integer.parseInt(arr[3]));
            user.setDeadline(Integer.parseInt(arr[4]));
            user.setRoulatte_money(Integer.parseInt(arr[5]));
            user.setTotal_money(Integer.parseInt(arr[6]));
            user.setTicket(Integer.parseInt(arr[7]));
            user.setInterest(Double.parseDouble(arr[8]));
            user.setItem_max(Integer.parseInt(arr[9]));
            user.setDeadline_money(Integer.parseInt(arr[10]));

            return user;

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }

    private String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}
