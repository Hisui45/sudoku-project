package com.example.sudokuproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class EndController {

    @FXML
    private Button newBtn, homeBtn;

    @FXML
    protected void onNewButtonClick() throws IOException {
        Stage stage = (Stage) newBtn.getScene().getWindow();
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("game-view.fxml"));

        stage.getScene().setRoot(fxmlLoader);

        stage.show();

    }

    @FXML
    protected void onHomeButtonClick() throws IOException {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("home-view.fxml"));

        stage.getScene().setRoot(fxmlLoader);

        stage.show();

    }
}