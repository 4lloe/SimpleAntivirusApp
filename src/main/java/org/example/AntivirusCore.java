package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
        QuarantineManager quarantineManager = new QuarantineManager();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (isFileMalicious(file)) {
                        threats.add(file.getAbsolutePath());
                        // Перемещаем файл в карантин
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
        List<String> quarantinedFiles = new ArrayList<>();
        File quarantineDir = new File("D:\\carantin"); // Убедитесь, что путь совпадает с QuarantineManager

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
        // Реализуем метод для очистки карантина
        return false;
    }

    public boolean changeQuarantineFolder(String path) {
        // Реализуем метод для изменения папки карантина
        return false;
    }

    public List<String> getSuspiciousExtensions() {
        // Реализуем метод для получения подозрительных расширений
        return new ArrayList<>();
    }

    public void setSuspiciousExtensions(String extensions) {
        // Реализуем метод для изменения подозрительных расширений
    }

    public String getScanReport() {
        StringBuilder report = new StringBuilder();

        // Здесь генерируем отчёт. Например, если были угрозы, добавляем их в отчёт.
        List<String> threats = getQuarantinedFiles(); // Получаем список угроз (можно использовать уже полученные данные)

        if (threats.isEmpty()) {
            report.append("No threats detected.\n");
        } else {
            report.append("Threats detected:\n");
            for (String threat : threats) {
                report.append(threat).append("\n");
            }
        }

        // Можно добавить дату сканирования или другие данные
        report.append("\nScan completed at: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        return report.toString();
    }

}
