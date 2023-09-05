package com.example.sudokuproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class GameController {
    @FXML
    private Label welcomeText;

    @FXML
    private SVGPath timerSVG;



    public GameController() {
    }

    @FXML
    protected void onHelloButtonClick() {

    }



    @FXML
    protected void onOneButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}