package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntivirusCore {
    private static final String DB_URL;
    private final Connection connection;

    static {
        // Путь к вашей базе данных SQLite
        DB_URL = "jdbc:sqlite:identifier.sqlite";  // Обновите путь к вашей базе
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

        // Получаем список всех файлов в директории
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Проверяем каждый файл на наличие угроз
                    if (isFileMalicious(file)) {
                        threats.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return threats;
    }

    public boolean isFileMalicious(File file) {
        try {
            // Загружаем сигнатуры из базы данных
            List<String> signatures = getSignaturesFromDatabase();

            // Получаем хэш файла
            String fileHash = getFileHash(file);
            System.out.println("File hash: " + fileHash);  // Логирование хэша файла

            if (fileHash == null) {
                return false;
            }

            // Приводим хэш файла к верхнему регистру
            fileHash = fileHash.toUpperCase();

            // Проверяем хэш файла на наличие совпадений с хэшами в базе данных
            for (String signature : signatures) {
                System.out.println("Checking against signature: " + signature);  // Логирование хэшей в базе данных

                // Приводим хэш из базы данных к верхнему регистру
                if (fileHash.equals(signature.toUpperCase())) {
                    return true; // Файл является вредоносным
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

            // Читаем файл и обновляем хэш
            while ((bytesRead = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesRead);
            }
            fis.close();

            // Получаем хэш в виде строки
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
        String sql = "SELECT hash FROM signatures";  // Используем колонку 'hash' для сигнатур

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String hash = rs.getString("hash");
                System.out.println("Signature from DB: " + hash);  // Логирование хэша из базы
                signatures.add(hash);
            }
        }
        return signatures;
    }


    public List<String> getQuarantinedFiles() {
        // Реализуем метод для получения списка файлов в карантине
        return new ArrayList<>();
    }

    public void clearQuarantine() {
        // Реализуем метод для очистки карантина
    }

    public void changeQuarantineFolder() {
        // Реализуем метод для изменения папки карантина
    }

    public List<String> getSuspiciousExtensions() {
        // Реализуем метод для получения подозрительных расширений
        return new ArrayList<>();
    }

    public void setSuspiciousExtensions(String extensions) {
        // Реализуем метод для изменения подозрительных расширений
    }

    public String getScanReport() {
        // Реализуем метод для получения отчета о сканировании
        return "Scan Report";
    }
}
