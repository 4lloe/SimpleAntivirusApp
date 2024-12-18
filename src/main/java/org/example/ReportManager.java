package org.example;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportManager {

    private static final String REPORT_DIR = "path/to/reports";

    public void generateReport(String fileName, String action) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String report = "Timestamp: " + timestamp + "\nFile: " + fileName + "\nAction: " + action + "\n\n";

            Path reportPath = Paths.get(REPORT_DIR, "virus_report.txt");
            Files.write(reportPath, report.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Report generated: " + reportPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
