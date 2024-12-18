package org.example;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class QuarantineManager {

    private static final String QUARANTINE_PATH = "D:\\carantin";

    public boolean moveFileToQuarantine(File file) {
        try {
            Files.move(file.toPath(), Paths.get(QUARANTINE_PATH, file.getName()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearQuarantine() {
        File quarantineDir = new File(QUARANTINE_PATH);
        if (quarantineDir.exists() && quarantineDir.isDirectory()) {
            File[] files = quarantineDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
                return true;
            }
        }
        return false;
    }

    public boolean changeQuarantineFolder(String newPath) {
        File newDir = new File(newPath);
        if (newDir.exists() && newDir.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }
}
