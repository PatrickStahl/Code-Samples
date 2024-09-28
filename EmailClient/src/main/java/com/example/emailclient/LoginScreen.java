package com.example.emailclient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;

public class LoginScreen {

    @FXML
    private ChoiceBox<String> chooseServer;

    @FXML
    private Button confirmButton;

    @FXML
    private TextField email;

    @FXML
    private TextField inputPort;

    @FXML
    private TextField inputServer;

    @FXML
    private TextField outputPort;

    @FXML
    private TextField outputServer;

    @FXML
    private TextField password;

    @FXML
    private CheckBox passwordCheckBox;

    @FXML
    private TextField username;

    /**
     * Wird ausgeführt, wenn der Bestätigungsbutton gedrückt wird.
     * Überprüft, ob alle Felder ausgefüllt sind und speichert die Daten optional ab.
     */
    @FXML
    void confirmButtonClicked(ActionEvent event) throws IOException, MessagingException {
        // Überprüft, ob eines der erforderlichen Felder leer ist
        if (username.getText().isEmpty() || password.getText().isEmpty() || inputServer.getText().isEmpty() ||
                inputPort.getText().isEmpty() || outputPort.getText().isEmpty() || outputServer.getText().isEmpty() ||
                email.getText().isEmpty()) {

            showError("Fehler", "Füllen Sie alle Felder aus!");
        } else {
            // Wenn die Checkbox "Speichern" aktiviert ist, speichere die Nutzerdaten in einer Datei
            if (passwordCheckBox.isSelected()) {
                String data = username.getText() + "," + password.getText() + "," + inputServer.getText() + "," +
                        inputPort.getText() + "," + outputServer.getText() + "," + outputPort.getText() + "," +
                        chooseServer.getValue() + "," + email.getText();

                ReadWrite readWrite = new ReadWrite();
                readWrite.write(data);
            }

            // Lädt die MainScreen FXML-Datei und zeigt das Hauptfenster an
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent root2 = fxmlLoader.load();
            MainScreen mainScreen = fxmlLoader.getController();

            // Setzt die Nutzerdaten in MainScreen
            mainScreen.setUserData(username.getText(), email.getText(), password.getText(), inputServer.getText(),
                    Integer.parseInt(inputPort.getText()), outputServer.getText(),
                    Integer.parseInt(outputPort.getText()));
            mainScreen.setDirectoryName(username.getText());
            mainScreen.initialize();

            // Zeigt das neue Fenster an
            Stage stage = new Stage();
            stage.setScene(new Scene(root2));
            stage.show();

            // Schließt das aktuelle Login-Fenster
            Stage window = (Stage) confirmButton.getScene().getWindow();
            window.close();
        }
    }

    //Initialisiere Loginscreen, setze für die FSU-Jena passende Standardwerte ein
    @FXML
    private void initialize() {
        // Erstellt Verzeichnisse, falls sie nicht existieren
        createDirectoryIfNotExists("C:\\files");
        createDirectoryIfNotExists("C:\\mails");

        // Setzt die Server-Optionen für das ChoiceBox und Standardwerte für die Felder
        chooseServer.getItems().addAll("POP3", "SMTP");
        chooseServer.setValue("POP3");
        inputServer.setText("smtp.uni-jena.de");
        inputPort.setText("465");
        outputServer.setText("pop3.uni-jena.de");
        outputPort.setText("995");

        // Fügt Listener hinzu, um sicherzustellen, dass nur Zahlen in die Port-Felder eingegeben werden
        addNumericValidation(inputPort);
        addNumericValidation(outputPort);
    }

    //Zeige Nutzerdaten an
    public void showUserData(String usernameData) throws IOException {
        ReadWrite write = new ReadWrite();
        username.setText(write.readUsername(usernameData));
        password.setText(write.readPassword(usernameData));
        inputServer.setText(write.readInputAddress(usernameData));
        inputPort.setText(write.readInputPort(usernameData));
        outputServer.setText(write.readOutputAddress(usernameData));
        outputPort.setText(write.readOutputPort(usernameData));
        chooseServer.setValue(write.readServer(usernameData));
        email.setText(write.readEmail(usernameData));
    }

    // Fenster für Fehlermeldungen
    private void showError(String title, String message) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AlertBox.fxml"));
        Parent root1 = fxmlLoader.load();

        // Holt den Controller der AlertBox und zeigt die Fehlermeldung an
        AlertBox alertBox = fxmlLoader.getController();
        alertBox.display(message);

        // Zeigt das Fehlermeldungsfenster an
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root1));
        stage.show();
    }

    // Erstelle Verzeichnis, falls noch keins existiert
    private void createDirectoryIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Validierung, sodass in den Ports nur Zahlen verwendet werden können
    private void addNumericValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
}
