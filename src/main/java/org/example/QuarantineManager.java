package org.example;

import java.io.*;
import java.nio.file.*;

public class QuarantineManager {
    private static final String QUARANTINE_DIR = "D:\\carantin";

    public boolean clearQuarantine() {
        try {
            Files.walk(Paths.get(QUARANTINE_DIR))
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                            System.out.println("File deleted from quarantine: " + file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean moveFileToQuarantine(File file) {
        try {
            Path quarantinePath = Paths.get(QUARANTINE_DIR);
            if (!Files.exists(quarantinePath)) {
                Files.createDirectories(quarantinePath);
            }

            String quarantinedFileName = System.currentTimeMillis() + "_" + file.getName();
            Path destination = Paths.get(QUARANTINE_DIR, quarantinedFileName);

            Files.move(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            recordFileInQuarantine(file, quarantinedFileName);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void recordFileInQuarantine(File file, String quarantinedFileName) {
        System.out.println("File moved to quarantine: " + quarantinedFileName);
    }
}
