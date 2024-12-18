package org.example;

import java.io.IOException;
import java.nio.file.*;

public class SettingsManager {
    private static String quarantineDir = "path/to/quarantine";

    public boolean updateQuarantineDirectory(String newDirectory) {
        try {
            Path newPath = Paths.get(newDirectory);
            if (!Files.exists(newPath)) {
                Files.createDirectories(newPath);
            }
            quarantineDir = newDirectory;
            System.out.println("Quarantine directory updated to: " + quarantineDir);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getQuarantineDirectory() {
        return quarantineDir;
    }
}
