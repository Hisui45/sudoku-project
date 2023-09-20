package com.example.sudokuproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class HomeController {

    @FXML
    private Button newBtn, contButton;



    @FXML
    public void initialize(){
        try {
            FileInputStream fileIn = new FileInputStream("data");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();

            if (obj instanceof ArrayList) {
                objectIn.close();
                contButton.disableProperty().setValue(false);
            }

        }catch(FileNotFoundException e){
            contButton.disableProperty().setValue(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    protected void onNewButtonClick() throws IOException {

        File file = new File("data");
        file.delete();

        Stage stage = (Stage) newBtn.getScene().getWindow();
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("game-view.fxml"));
        stage.getScene().setRoot(fxmlLoader);
        stage.show();
    }

    @FXML
    protected void onContButtonClick() throws IOException {
        Stage stage = (Stage) contButton.getScene().getWindow();
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("game-view.fxml"));
        stage.getScene().setRoot(fxmlLoader);
        stage.show();
    }
}
