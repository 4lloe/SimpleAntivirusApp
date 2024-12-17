package org.example;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AntivirusCore {
    private String quarantineDir = "quarantine"; // Папка карантина по умолчанию
    private List<String> suspiciousExtensions = List.of(".exe", ".bat", ".js"); // Пример подозрительных расширений
    private List<String> scanReport = new ArrayList<>();
    private List<String> quarantinedFiles = new ArrayList<>();

    // Метод для сканирования директории
    public List<String> scanDirectory(File directory) {
        scanReport.clear();
        quarantinedFiles.clear();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file);  // Рекурсивный вызов для сканирования вложенных директорий
                } else {
                    scanFile(file);
                }
            }
        }

        return scanReport;
    }

    // Метод для сканирования отдельного файла
    private void scanFile(File file) {
        String fileName = file.getName();
        if (isSuspicious(fileName)) {
            quarantineFile(file);
        } else {
            scanReport.add("Safe: " + fileName);
        }
    }

    // Проверка, является ли файл подозрительным
    private boolean isSuspicious(String fileName) {
        for (String ext : suspiciousExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    // Метод для помещения файла в карантин
    private void quarantineFile(File file) {
        try {
            // Создание новой директории для карантина, если её нет
            File quarantineFolder = new File(quarantineDir);
            if (!quarantineFolder.exists()) {
                quarantineFolder.mkdirs();
            }

            File quarantineFile = new File(quarantineFolder, file.getName());
            Files.copy(file.toPath(), quarantineFile.toPath()); // Копирование файла в карантин
            quarantinedFiles.add(quarantineFile.getAbsolutePath());
            scanReport.add("Quarantined: " + file.getName());
            System.out.println("File quarantined: " + file.getName());
        } catch (IOException e) {
            scanReport.add("Error quarantining file: " + file.getName());
        }
    }

    // Метод для получения списка файлов в карантине
    public List<String> getQuarantinedFiles() {
        return quarantinedFiles;
    }

    // Метод для очистки карантина
    public void clearQuarantine() {
        for (String filePath : quarantinedFiles) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
        quarantinedFiles.clear();
        System.out.println("Quarantine cleared.");
    }

    // Метод для получения отчета о сканировании
    public String getScanReport() {
        StringBuilder report = new StringBuilder();
        for (String entry : scanReport) {
            report.append(entry).append("\n");
        }
        return report.toString();
    }

    // Метод для изменения папки карантина
    public void changeQuarantineFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Разрешаем выбор только папок
        fileChooser.setDialogTitle("Select Quarantine Folder");

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            if (selectedFolder != null && selectedFolder.exists() && selectedFolder.isDirectory()) {
                quarantineDir = selectedFolder.getAbsolutePath();
                System.out.println("New quarantine folder: " + quarantineDir);
            } else {
                System.out.println("Invalid folder selected.");
            }
        }
    }

    // Метод для получения текущих подозрительных расширений
    public List<String> getSuspiciousExtensions() {
        return suspiciousExtensions;
    }

    // Метод для обновления списка подозрительных расширений
    public void setSuspiciousExtensions(String extensions) {
        suspiciousExtensions = List.of(extensions.split(",\\s*"));
    }
}
