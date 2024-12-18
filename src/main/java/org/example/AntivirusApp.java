package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

//C:\Users\m4lyi\OneDrive\Рабочий стол\virus.txt\

public class AntivirusApp extends Application {
    private VBox centerPanel;
    private final AntivirusCore core = new AntivirusCore();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Левое меню
        VBox menu = createLeftMenu(primaryStage);

        // Центральная панель по умолчанию
        centerPanel = createDashboard();

        root.setLeft(menu);
        root.setCenter(centerPanel);

        root.setBackground(new Background(new BackgroundFill(Color.rgb(52, 115, 93), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Antivirus Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLeftMenu(Stage stage) {
        VBox menu = new VBox(20);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(200);
        menu.setAlignment(Pos.TOP_LEFT);

        Label protectionLabel = new Label("PROTECTION");
        protectionLabel.setTextFill(Color.WHITE);
        protectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button scanButton = createMenuButton("Virus Scan", () -> startScan(stage));

        Label quarantineLabel = new Label("QUARANTINE");
        quarantineLabel.setTextFill(Color.WHITE);
        quarantineLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button quarantineButton = createMenuButton("Show Quarantine", this::showQuarantine);
        Button clearQuarantineButton = createMenuButton("Clear Quarantine", this::clearQuarantine);

        Label settingsLabel = new Label("SETTINGS");
        settingsLabel.setTextFill(Color.WHITE);
        settingsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button settingsButton = createMenuButton("Settings", this::showSettings);

        Label reportLabel = new Label("REPORTS");
        reportLabel.setTextFill(Color.WHITE);
        reportLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button reportButton = createMenuButton("Scan Report", this::showScanReport);

        menu.getChildren().addAll(protectionLabel, scanButton,
                quarantineLabel, quarantineButton, clearQuarantineButton,
                settingsLabel, settingsButton,
                reportLabel, reportButton);

        menu.setBackground(new Background(new BackgroundFill(Color.rgb(40, 85, 72), CornerRadii.EMPTY, Insets.EMPTY)));
        return menu;
    }

    private VBox createDashboard() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(30));
        dashboard.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);

        dashboard.getChildren().add(title);
        return dashboard;
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(160);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #35685F; -fx-background-radius: 5;");
        button.setOnAction(e -> {
            centerPanel.getChildren().clear();
            action.run();
        });
        return button;
    }

    private void startScan(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Scan");
        File directory = directoryChooser.showDialog(stage);

        if (directory != null) {
            Label scanningLabel = new Label("Scanning directory: " + directory.getPath());
            scanningLabel.setTextFill(Color.WHITE);
            centerPanel.getChildren().add(scanningLabel);

            // Получаем угрозы
            List<String> threats = core.scanDirectory(directory);

            Label resultLabel = new Label("Scan complete. Threats found: " + threats.size());
            resultLabel.setTextFill(Color.WHITE);
            centerPanel.getChildren().add(resultLabel);

            for (String file : threats) {
                centerPanel.getChildren().add(new Label("Threat: " + file));
            }
        }
    }

    private void showQuarantine() {
        centerPanel.getChildren().clear();
        List<String> quarantinedFiles = core.getQuarantinedFiles();
        Label quarantineLabel = new Label("Quarantined Files:");
        quarantineLabel.setTextFill(Color.WHITE);
        centerPanel.getChildren().add(quarantineLabel);

        for (String file : quarantinedFiles) {
            Label fileLabel = new Label(file);
            fileLabel.setTextFill(Color.LIGHTGRAY);
            centerPanel.getChildren().add(fileLabel);
        }
    }


    public boolean clearQuarantine() {
        File quarantineDir = new File("D:\\carantin"); // Директория карантина
        if (!quarantineDir.exists() || !quarantineDir.isDirectory()) {
            System.out.println("Quarantine directory does not exist or is not a directory.");
            return false;
        }

        boolean success = true;
        File[] files = quarantineDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.delete()) { // Пытаемся удалить файл
                    success = false;
                    System.out.println("Failed to delete file: " + file.getAbsolutePath());
                }
            }
        }
        System.out.println("Quarantine cleared");
        return success;
    }


    private void showSettings() {
        Label settingsTitle = new Label("Settings");
        settingsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        settingsTitle.setTextFill(Color.WHITE);

        Button changeQuarantineFolder = new Button("Change Quarantine Folder");
        changeQuarantineFolder.setOnAction(e -> changeQuarantineFolder());

        Label extensionLabel = new Label("Suspicious File Extensions (comma-separated):");
        extensionLabel.setTextFill(Color.WHITE);

        TextField extensionsField = new TextField(String.join(", ", core.getSuspiciousExtensions()));
        Button saveButton = new Button("Save Extensions");
        saveButton.setOnAction(e -> core.setSuspiciousExtensions(extensionsField.getText()));

        VBox settingsPanel = new VBox(10, settingsTitle, changeQuarantineFolder, extensionLabel, extensionsField, saveButton);
        settingsPanel.setAlignment(Pos.CENTER_LEFT);
        settingsPanel.setPadding(new Insets(20));

        centerPanel.getChildren().add(settingsPanel);
    }

    private void showScanReport() {
        String report = core.getScanReport();
        TextArea reportArea = new TextArea(report);
        reportArea.setEditable(false);
        centerPanel.getChildren().add(reportArea);
    }

    private void changeQuarantineFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select New Quarantine Folder");
        File directory = directoryChooser.showDialog(null);

        if (directory != null) {
            core.changeQuarantineFolder(directory.getPath());
            Label newFolderLabel = new Label("New quarantine folder set: " + directory.getPath());
            newFolderLabel.setTextFill(Color.WHITE);
            centerPanel.getChildren().add(newFolderLabel);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
