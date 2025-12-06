import java.io.*;

public class SaveManagerCsv {

    private static final String SAVE_FILE = "data/user_save.csv";

    public SaveManagerCsv() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write("round,round_spin_left,deadline,roulatte_money,"
                           + "total_money,ticket,interest,item_max,deadline_money");
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(User user) {
        File file = new File(SAVE_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("round,round_spin_left,deadline,roulatte_money,"
                   + "total_money,ticket,interest,item_max,deadline_money");
            bw.newLine();

            StringBuilder sb = new StringBuilder();
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
            if (arr.length < 9) {
                return null;
            }

            User user = new User();
            user.setRound(Integer.parseInt(arr[0]));
            user.setRound_spin_left(Integer.parseInt(arr[1]));
            user.setDeadline(Integer.parseInt(arr[2]));
            user.setRoulatte_money(Integer.parseInt(arr[3]));
            user.setTotal_money(Integer.parseInt(arr[4]));
            user.setTicket(Integer.parseInt(arr[5]));
            user.setInterest(Double.parseDouble(arr[6]));
            user.setItem_max(Integer.parseInt(arr[7]));
            user.setDeadline_money(Integer.parseInt(arr[8]));

            return user;

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
