package org.example;
import java.security.*;
import java.sql.*;
import java.util.*;

public class ProcessScanner {
    private final String dbPath;

    public ProcessScanner(String dbPath) {
        this.dbPath = dbPath;
    }

    // Сканирование процессов
    public List<String> scanProcesses() {
        List<String> infectedProcesses = new ArrayList<>();
        try {
            List<String> processes = getProcessList();

            for (String processName : processes) {
                String processHash = getProcessHash(processName);
                if (isInfected(processHash)) {
                    infectedProcesses.add(processName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infectedProcesses;
    }

    private List<String> getProcessList() {
        // Получение списка процессов (используйте стороннюю библиотеку или системные команды)
        return Arrays.asList("process1", "process2");
    }

    private String getProcessHash(String processName) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(processName.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    private boolean isInfected(String processHash) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT 1 FROM signatures WHERE hash = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, processHash);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
