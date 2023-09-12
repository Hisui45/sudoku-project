package com.example.sudokuproject;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Tile extends StackPane {
    private int currentNumber;
    private int correctNumber;
    private Text number;

    private boolean diagonalTile = false;
    private int x,y;

    private boolean correct;

    public Tile(int currentNumber, int x, int y){
        this.currentNumber = currentNumber;
        this.x = x;
        this.y = y;
    }
    public Tile(int currentNumber, int correctNumber, int x, int y, int boardSize){
        //Set Numbers
        this.currentNumber = currentNumber;
        this.correctNumber = correctNumber;

        //Create Tile Components
        int tileSize = (540 / boardSize);
        this.setMinSize(tileSize,tileSize);

        if(currentNumber == 0){
            this.setStyle("-fx-background-color: white");
            number = new Text("0");
            correct = false;
        }else{
            this.setStyle("-fx-background-color: gray");
            number = new Text(String.valueOf(correctNumber));
            correct = true;
        }
        number.disableProperty().setValue(true);
        this.getChildren().addAll(number);

        //Set Tile Location
        this.x = x;
        this.y = y;

        //Add Appropriate Borders to Tiles
        addBorders(x,y,boardSize);
    }

    public int getCurrentNumber() {
        return currentNumber;
    }

    public boolean setCurrentNumber(int currentNumber) {
        this.currentNumber = currentNumber;
        if (currentNumber == 0) {
            number.setText("0");
            this.setStyle("-fx-background-color: white");
        }else{
            number.setText(String.valueOf(currentNumber));
        }

        return checkTile();

    }

    public void setCorrectNumber(int correctNumber) {
        this.correctNumber = correctNumber;
    }

    public int getCorrectNumber() {
        return correctNumber;
    }


    public boolean checkTile(){
        if(currentNumber == 0){
            return false;
        }else if (currentNumber != correctNumber){
        //    this.setStyle("-fx-background-color: red");
            return true;
        }else if(currentNumber == correctNumber){
         //   this.setStyle("-fx-background-color: darkgray");
            correct = true;
        }

        return false;

    }

    //Optimized for size changes
    public void addBorders(int x, int y, int boardSize){
        boolean borderAdded = false;

        int startingSize = boardSize;

        int edge1 = (startingSize / 3) - 1;
        int edge2 = (startingSize / 3) + ((startingSize / 3) - 1);

        if(x == edge1 || x == edge2){
            this.getStyleClass().add("edgeXBorder");
            borderAdded = true;
        }

        if(y == edge1 || y == edge2){
            this.getStyleClass().add("edgeYBorder");
            borderAdded = true;
        }

        if(y == edge1 & (x == edge1 || x == edge2)){
            this.getStyleClass().add("innerCornerBorder");
            borderAdded = true;
        }

        if(y == edge2 & (x == edge1 || x == edge2)){
            this.getStyleClass().add("innerCornerBorder");
            borderAdded = true;
        }

        if(!borderAdded){
            this.getStyleClass().add("defaultBorder");
        }

//        if(x == 2 || x == 5){
//            this.getStyleClass().add("edgeXBorder");
//            borderAdded = true;
//        }
//
//        if(y == 2 || y == 5){
//            this.getStyleClass().add("edgeYBorder");
//            borderAdded = true;
//        }
//
//        if(y == 2 & (x == 2 || x == 5)){
//            this.getStyleClass().add("innerCornerBorder");
//            borderAdded = true;
//        }
//
//        if(y == 5 & (x == 2 || x == 5)){
//            this.getStyleClass().add("innerCornerBorder");
//            borderAdded = true;
//        }
//
//        if(!borderAdded){
//            this.getStyleClass().add("defaultBorder");
//        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isDiagonalTile() {
        return diagonalTile;
    }

    public void setDiagonalTile(boolean diagonalTile) {
        this.diagonalTile = diagonalTile;
    }
}
