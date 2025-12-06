import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 데이터 관리 클래스
 * 게임 상태 정보 저장 및 CSV 파일 입출력 담당
 */
public class User {
    private String userId = "";
    private int roulatte_money = 10000;
    private double interest = 0.1;
    private int ticket = 3;
    private int deadline = 1;
    private int round = 1;
    private int deadline_money = 75;
    private int total_money = 30;

    public User() {
    }
    
    public User(String userId) {
        this.userId = userId;
    }

    public int getRoulatte_money() {
        return roulatte_money;
    }

    public void setRoulatte_money(int roulatte_money) {
        this.roulatte_money = roulatte_money;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getDeadline_money() {
        return deadline_money;
    }

    public void setDeadline_money(int deadline_money) {
        this.deadline_money = deadline_money;
    }

    public int getTotal_money() {
        return total_money;
    }

    public void setTotal_money(int total_money) {
        this.total_money = total_money;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    /**
     * 사용자 데이터를 CSV 파일에 저장
     * 기존 데이터가 있으면 업데이트, 없으면 추가
     */
    public void saveToCSV() {
        if (userId == null || userId.isEmpty()) {
            return;
        }
        
        String filename = "user.csv";
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(userId + ",")) {
                    lines.add(toCSVLine());
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (!found) {
            lines.add(toCSVLine());
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String toCSVLine() {
        return String.format("%s,%d,%.2f,%d,%d,%d,%d,%d",
            userId, roulatte_money, interest, ticket, deadline, round, deadline_money, total_money);
    }
    
    /**
     * CSV 파일에서 사용자 데이터 로드
     * @param userId 찾을 사용자 ID
     * @return User 객체 또는 null (찾지 못한 경우)
     */
    public static User loadFromCSV(String userId) {
        String filename = "user.csv";
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(userId + ",")) {
                    return parseCSVLine(line);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static User parseCSVLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 8) {
            return null;
        }
        
        try {
            User user = new User(parts[0]);
            user.setRoulatte_money(Integer.parseInt(parts[1]));
            user.setInterest(Double.parseDouble(parts[2]));
            user.setTicket(Integer.parseInt(parts[3]));
            user.setDeadline(Integer.parseInt(parts[4]));
            user.setRound(Integer.parseInt(parts[5]));
            user.setDeadline_money(Integer.parseInt(parts[6]));
            user.setTotal_money(Integer.parseInt(parts[7]));
            return user;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}