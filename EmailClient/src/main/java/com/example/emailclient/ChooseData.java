package com.example.emailclient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ChooseData {

    @FXML
    private Button cancelButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordTextField;

    @FXML
    private ChoiceBox<String> chooseData;


    // Initialisiert die ChoiceBox mit den vorhandenen Benutzerkonten
    @FXML
    private void initialize() throws IOException {
        String[] userAccounts = ChooseData.userAccounts();

        // Füllt die ChoiceBox mit den Benutzernamen (ohne Dateiendung)
        for (String account : userAccounts) {
            String accountName = account.substring(0, account.length() - 4); // Entfernt .txt
            chooseData.getItems().add(accountName);
        }

        // Setzt den ersten Benutzer als Standardauswahl
        if (userAccounts.length > 0) {
            String firstAcc = userAccounts[0];
            chooseData.setValue(firstAcc.substring(0, firstAcc.length() - 4)); // Entfernt .txt
        }
    }

    // Schließt das aktuelle Fenster, wenn der Abbrechen-Button gedrückt wird
    @FXML
    void cancelButtonClicked() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Überprüft das eingegebene Passwort und öffnet das LoginScreen-Fenster, wenn es korrekt ist
    @FXML
    void loginButtonClicked() {
        ReadWrite readWrite = new ReadWrite();
        try {
            String username = chooseData.getValue();
            String password = readWrite.readPassword(username);

            // Überprüft, ob das eingegebene Passwort korrekt ist
            if (passwordTextField.getText().equals(password)) {
                try {
                    // Lädt das LoginScreen-FXML und zeigt es an
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
                    Parent root1 = fxmlLoader.load();

                    LoginScreen loginScreen = fxmlLoader.getController();
                    loginScreen.showUserData(username);

                    Stage stage = new Stage();
                    stage.setTitle("Login");
                    stage.setScene(new Scene(root1));
                    stage.show();

                    // Schließt das aktuelle Fenster
                    Stage window = (Stage) loginButton.getScene().getWindow();
                    window.close();
                } catch (Exception ignored) {

                }
            } else {
                // Zeigt einen Fehler an, wenn das Passwort falsch ist
                showError("Fehler", "Falsches Passwort");
            }
        } catch (IOException ignored) {

        }
    }

    // gibt Array mit Nutzernamen zurück
    private static String[] userAccounts() {
        File folder = new File("C:\\files");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return new String[0]; // Gibt ein leeres Array zurück, falls keine Dateien vorhanden sind
        }

        String[] nameOfFiles = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                nameOfFiles[i] = listOfFiles[i].getName();
            }
        }
        return nameOfFiles;
    }

    // Fehlermeldungsfenster
    private static void showError(String title, String message) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChooseData.class.getResource("AlertBox.fxml"));
        Parent root1 = fxmlLoader.load();

        AlertBox alertBox = fxmlLoader.getController();
        alertBox.display(message);

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root1));
        stage.show();
    }
}
