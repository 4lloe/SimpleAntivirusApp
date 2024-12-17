package org.example;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileScanner {
    private final String dbPath;

    public FileScanner(String dbPath) {
        this.dbPath = dbPath;
    }

    // Сканирование файлов и каталогов
    public List<String> scanFiles(String path) {
        List<String> infectedFiles = new ArrayList<>();
        try {
            List<Path> filesToScan = Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            for (Path file : filesToScan) {
                String fileHash = hashFile(file);
                if (isInfected(fileHash)) {
                    infectedFiles.add(file.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return infectedFiles;
    }

    private String hashFile(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    private boolean isInfected(String fileHash) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT 1 FROM signatures WHERE hash = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fileHash);
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
