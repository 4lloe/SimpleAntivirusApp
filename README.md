# Antivirus Application

This is a simple antivirus application built with JavaFX. It allows users to scan directories for potential threats, view quarantined files, clear the quarantine, and change the background color of the application through the settings menu.

## Features
- **Virus Scan**: Select a directory to scan for potential threats. The application will list any detected threats.
- **Quarantine**: View quarantined files and clear the quarantine if necessary.
- **Settings**: Change the background color of the application.
- **Scan Report**: View a detailed report of the scan results.

## Getting Started

To get started with the Antivirus Application, follow these steps:

### Prerequisites

- Java Development Kit (JDK) 11 or higher.
- Maven for dependency management (if you're using Maven).
- JavaFX library (included in the project).

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/antivirus-app.git
    ```

2. Navigate to the project directory:
    ```bash
    cd antivirus-app
    ```

3. If using Maven, build the project with:
    ```bash
    mvn clean install
    ```

4. Run the application:
    ```bash
    mvn javafx:run
    ```

### Usage

1. When the application starts, the main dashboard will appear with the following options in the left menu:
   - **Virus Scan**: Click this button to scan a directory for threats.
   - **Show Quarantine**: View quarantined files.
   - **Clear Quarantine**: Remove files from quarantine.
   - **Settings**: Change the background color of the application.
   - **Scan Report**: View a detailed report of the scan results.

2. In the **Settings** section, you can change the background color to a darker shade by clicking the button.

### Example

- **Virus Scan**: After selecting a directory, the application will display any detected threats.
- **Quarantine**: Files found during scans are added to quarantine and can be cleared if needed.

## Technologies Used
- Java
- JavaFX
- Maven (optional, if using)

## License


This project is licensed under the MIT License - see the [LICENSE.md](SimpleAntivirusApp/LICENSE.md) file for details.


