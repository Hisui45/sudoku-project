package com.example.sudokuproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private Button newBtn, contButton;

    @FXML
    protected void onNewButtonClick() throws IOException {
        Stage stage = (Stage) newBtn.getScene().getWindow();
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("game-view.fxml"));
        stage.getScene().setRoot(fxmlLoader);
        stage.show();

    }

    @FXML
    protected void onContButtonClick() {

    }
}
