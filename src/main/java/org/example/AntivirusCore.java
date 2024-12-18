package org.example;

import java.io.*;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class AntivirusCore {
    private static final String DB_URL;
    private final Connection connection;
    private boolean testMode = false;

    static {
        DB_URL = "jdbc:sqlite:identifier.sqlite";
    }

    public AntivirusCore() {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error.", e);
        }
    }

    public List<String> scanDirectory(File directory) {
        List<String> threats = new ArrayList<>();
        QuarantineManager quarantineManager = new QuarantineManager();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (isFileMalicious(file)) {
                        threats.add(file.getAbsolutePath());
                        if (quarantineManager.moveFileToQuarantine(file)) {
                            System.out.println("File quarantined: " + file.getAbsolutePath());
                        } else {
                            System.out.println("Failed to quarantine file: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }
        return threats;
    }

    public boolean isFileMalicious(File file) {
        try {
            List<String> signatures = getSignaturesFromDatabase();

            String fileHash = getFileHash(file);
            System.out.println("File hash: " + fileHash);

            if (fileHash == null) {
                return false;
            }

            fileHash = fileHash.toUpperCase();

            for (String signature : signatures) {
                System.out.println("Checking against signature: " + signature);

                if (fileHash.equals(signature.toUpperCase())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getFileHash(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesRead = 0;

            while ((bytesRead = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesRead);
            }
            fis.close();

            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> getSignaturesFromDatabase() throws SQLException {
        List<String> signatures = new ArrayList<>();
        String sql = "SELECT hash FROM signatures";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String hash = rs.getString("hash");
                System.out.println("Signature from DB: " + hash);
                signatures.add(hash);
            }
        }
        return signatures;
    }

    public List<String> getQuarantinedFiles() {
        List<String> quarantinedFiles = new ArrayList<>();
        File quarantineDir = new File("D:\\carantin");

        if (quarantineDir.exists() && quarantineDir.isDirectory()) {
            File[] files = quarantineDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    quarantinedFiles.add(file.getAbsolutePath());
                }
            }
        }
        return quarantinedFiles;
    }

    public boolean clearQuarantine() {
        QuarantineManager quarantineManager = new QuarantineManager();
        return quarantineManager.clearQuarantine();
    }

    public boolean changeQuarantineFolder(String path) {
        QuarantineManager quarantineManager = new QuarantineManager();
        return quarantineManager.changeQuarantineFolder(path);
    }

    public List<String> getSuspiciousExtensions() {
        return new ArrayList<>();
    }

    public void setSuspiciousExtensions(String extensions) {
    }

    public String getScanReport() {
        StringBuilder report = new StringBuilder();

        List<String> threats = getQuarantinedFiles();

        if (threats.isEmpty()) {
            report.append("No threats detected.\n");
        } else {
            report.append("Threats detected:\n");
            for (String threat : threats) {
                report.append(threat).append("\n");
            }
        }

        report.append("\nScan completed at: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        return report.toString();
    }

    public void toggleTestMode() {
        testMode = !testMode;
    }

    public boolean isTestMode() {
        return testMode;
    }
}
