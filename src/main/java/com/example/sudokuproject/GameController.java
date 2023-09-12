package com.example.sudokuproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;


public class GameController {

    @FXML
    private Label timerLabel;
    int time;
    @FXML
    private Label errorLabel;
    private int errorCount ;
    @FXML
    private Button undoBtn;
    @FXML
    private GridPane board;
    private ArrayList<ArrayList<Tile>> tileBoard;
    private ArrayDeque<Tile> undoStack;
    private Tile selectedTile;
    private int size;

    private ArrayList<Integer> startingNumbers;

    private ArrayList<ArrayList<ArrayList<Integer>>> invalidOptions;


    private ArrayList<ArrayList<Integer>> startingBoard;
    private ArrayList<ArrayList<Integer>> rowBoard;
    private ArrayList<ArrayList<Integer>> columnBoard;
    private ArrayList<ArrayList<Integer>> quadrantBoard;
    private ArrayList<ArrayList<Integer>> diagonalBoard;

    public GameController() {

    }

    @FXML
    public void initialize(){
        setUndoBtn();
        setUtilities();
        createBoard();
        setDiagonals();
    }


    @FXML
    protected void onStartButtonClick(){
        setBoard();

    }

    public void setUtilities(){
        undoStack = new ArrayDeque<>();
        time = 0;
        errorCount = 0;
        setTimer();
    }
    public void setTimer(){
//Code Snippet from https://stackoverflow.com/questions/50766244/creating-a-timer-that-shows-the-time-in-hhmmss-format-javafx
//Modified to meet needs
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
                    time++;
                    if(time >= 3600) {
                        timerLabel.setText(String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, time % 60));

                    }else if(time < 600){
                        timerLabel.setText(String.format("%01d:%02d", (time % 3600) / 60, time % 60));
                    }else{
                        timerLabel.setText(String.format("%02d:%02d", (time % 3600) / 60, time % 60));
                    }
                }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
//
    }
    public void setUndoBtn(){
        SVGPath undoSVG = new SVGPath();
        undoSVG.setContent("M8 15H3.5M3 10V14.5C3 14.7762 3.22386 15 3.5 15M3.5 15L5 12.7695C10 7.19335 17.5747 9.11768 21 15");
        undoSVG.setStroke(Color.web("#1B1D1F"));
        undoSVG.setStrokeWidth(2);
        undoSVG.setStrokeLineCap(StrokeLineCap.ROUND);
        undoSVG.setStrokeLineJoin(StrokeLineJoin.ROUND);
        undoSVG.setFill(Paint.valueOf("transparent"));
        undoBtn.setGraphic(undoSVG);
        undoBtn.getStyleClass().add("undoButton");
    }
    public void createBoard(){
        tileBoard = new ArrayList<>();
        invalidOptions = new ArrayList<>();
        board.setOnMouseClicked(onTileClicked);

        size = 9;
        for(int x = 0; x <size; x++) {
            ArrayList<Tile> columns = new ArrayList<>();

            ArrayList<ArrayList<Integer>> square = new ArrayList<>();
            tileBoard.add(columns);
            invalidOptions.add(square);
            for (int y = 0; y < size; y++) {
                Tile tile = new Tile(0, 0, x, y,size);
                board.add(tile,x,y);
                tileBoard.get(x).add(tile);
                ArrayList<Integer> numbers = new ArrayList<>();
                square.add(numbers);
            }
        }

    }


    public void clearSolutions(){
        tileBoard = new ArrayList<>();
        invalidOptions = new ArrayList<>();
        size = 9;
        for(int x = 0; x <size; x++) {
            ArrayList<Tile> columns = new ArrayList<>();

            ArrayList<ArrayList<Integer>> square = new ArrayList<>();
            tileBoard.add(columns);
            invalidOptions.add(square);
            for (int y = 0; y < size; y++) {
                Tile tile = new Tile(0, 0, x, y,size);
                board.add(tile,x,y);
                tileBoard.get(x).add(tile);
                ArrayList<Integer> numbers = new ArrayList<>();
                square.add(numbers);
            }
        }
    }
    public void printBoard(){
        size = 9;
        for(int y = 0; y <size; y++) {
            ArrayList<Tile> columns = new ArrayList<>();
            tileBoard.add(columns);
                System.out.println();
            for (int x = 0; x < size; x++) {
                System.out.print(tileBoard.get(x).get(y).getCurrentNumber());
            }
        }
    }

    public ArrayList<Integer> getRowNumbers(int y){
        ArrayList<Integer> row = new ArrayList<>();
        for(int x = 0; x<9; x++){
            int number = tileBoard.get(x).get(y).getCorrectNumber();
            row.add(number);
        }

        return row;
    }

    public ArrayList<Integer> getColumnNumbers(int x){
        ArrayList<Integer> column = new ArrayList<>();

        for(int y = 0; y<9; y++){
            int number = tileBoard.get(x).get(y).getCorrectNumber();
            column.add(number);
        }

        return column;
    }

    public  ArrayList<Integer> getQuadrantNumbers(int x, int y){
        ArrayList<Integer> quadrant = new ArrayList<>();
        int quadrantCol = x - (x % 3);
        int quadrantRow = y - (y % 3);

        for(int i = quadrantRow; i < quadrantRow+3; i++){

            for(int j = quadrantCol; j < quadrantCol+3; j++){

                quadrant.add(tileBoard.get(j).get(i).getCurrentNumber());

            }

        }

        return quadrant;

    }

    public boolean removeNumber(int number){
        int indexRow = 0;
        boolean matchRow = false;
        for (int i = 0; i < startingNumbers.size(); i++) {
            if(startingNumbers.get(i) == number){
                matchRow = true;
                indexRow = i;
            }
        }
        if(matchRow){
            startingNumbers.remove(indexRow);
            return true;
        }

        return false;
    }

    public int selectRandomNumber(){
        Random random = new Random();
        if(startingNumbers.size() > 1){
            return random.nextInt(startingNumbers.size());
        }
        return 0;
    }

    public void refreshNumbers(){
        startingNumbers = new ArrayList<>();
        for(int x = 1; x <= 9; x++){
            startingNumbers.add(x);
        }
    }

    public void refreshNumbers(ArrayList<Integer> compare, ArrayList<Integer> triedNumbers){
        refreshNumbers();
        ArrayList<Integer> removeNumbers = new ArrayList<>();



        for(Integer removeNumber: removeNumbers){
            startingNumbers.remove(removeNumber);
        }

        for (Integer number: startingNumbers ) {

            for(Integer triedNumber : triedNumbers){
                if(number.equals(triedNumber)){
                    removeNumbers.add(number);
                }
            }
        }

        for(Integer removeNumber: removeNumbers){
            startingNumbers.remove(removeNumber);
        }
    }

    public void refreshNumbers(int x, int y){
        refreshNumbers();
        ArrayList<Integer> removeNumbers = new ArrayList<>();
        for (Integer number: startingNumbers ) {

            for(Integer columnNumber : getColumnNumbers(x)){

                if(number.equals(columnNumber)){

                    removeNumbers.add(number);
                }
            }

            for(Integer rowNumber : getRowNumbers(y)){
                if(number.equals(rowNumber)){
                    removeNumbers.add(number);
                }
            }

            for(Integer quadrantNumber : getQuadrantNumbers(x,y)){
                if(number.equals(quadrantNumber)){
                    removeNumbers.add(number);
                }
            }

            for(Integer invalidOptions : invalidOptions.get(x).get(y)){
                if(number.equals(invalidOptions)){
                    removeNumbers.add(number);
                }
            }
        }

        for(Integer removeNumber: removeNumbers){
            startingNumbers.remove(removeNumber);
        }

    }

    public void setDiagonals() {
        //Set initial coordinates
        int x = 0;
        int y = 0;

        //Fill in number choices
        refreshNumbers();

        //Set numbers for left to right diagonal
        while(x<9){
            boolean duplicate = true;
            while(duplicate){
                //Generate random number
                int number = startingNumbers.get(selectRandomNumber());
                //Check if number exists on board already; if not set on board
                if(removeNumber(number)){
                    tileBoard.get(x).get(y).setCorrectNumber(number);
                    tileBoard.get(x).get(y).setCurrentNumber(number);
                    tileBoard.get(x).get(y).setDiagonalTile(true);
                    x++;
                    y++;
                    duplicate = false;

                }else{
//                    System.out.println(startingNumbers);
//                    System.out.println("This number: "+ number + " is a duplicate in diagonal at "+x+","+y);
                }
            }

        }



    }


    public void setBoard(){
        int restarted = -1;
        boolean unsolved = true;
        while(unsolved){
            clearSolutions();
            setDiagonals();
            boolean solution = generateSolution();
            if(solution){
                unsolved = false;
            }else{
                restarted++;
            }
        }

    }

    public boolean generateSolution(){
        int y = 0;
        int x;
        boolean valid = true;
        boolean movingForward = true;
        int restarted = -1;

        while(y < 9 && valid){

            x = 0;

            while(x < 9 && valid){
                Tile currentTile = tileBoard.get(x).get(y);
                refreshNumbers(x,y);

                if(x == 0 && y == 0){
                    if(restarted > 1000){
                        valid = false;
                    }
                    restarted++;
                }

                if(currentTile.isDiagonalTile()){
                    //Square is a diagonal tile

                    //Move forward or backwards?
                    if(movingForward){
                        //We're currently moving forwards
                        x++;
                    }else{
                        //We're currently moving backwards

                        //If we're back at the start
                        if(x == 0){
                           movingForward = true;
                           x++;
                        }else{
                            x--;
                        }

                    }
                }else{
                    //Square is not a diagonal
                    refreshNumbers(x, y);

                    if (startingNumbers.size() > 0) {

                        //Options to Choose From
                        if (tileBoard.get(x).get(y).getCorrectNumber() == 0) {

                            //Square is already blank
                            //All numbers should be valid
                            int number = startingNumbers.get(selectRandomNumber());

                            currentTile.setCorrectNumber(number);
                            currentTile.setCurrentNumber(number);

                        }else{

                            //Square is not blank, and it's not a diagonal, means we've moved back but there are other options

                            //Value should be stored in invalid options
                            invalidOptions.get(x).get(y).add(currentTile.getCurrentNumber());

                            refreshNumbers(x,y);

                            int number = startingNumbers.get(selectRandomNumber());

                            currentTile.setCorrectNumber(number);
                            currentTile.setCurrentNumber(number);
                        }

                        x++;

                        movingForward = true;

                    }else{
                        //No Options
                        //If x is already 0, just reset x back to 8 and move back a row.

                        if(x == 0){
                            y--;
                            x = 8;
                        }else{
                            x--;
                        }
//                        System.out.println(x+","+y);
                        movingForward = false;
                    }
                }
            }

            y++;

        }

        if(valid){
            return true;
        }else{
            return false;
        }

    }
/** Third Try */
//    public void setBoard(){
//
//
//        int tries = 0;
//        boolean unsolved = true;
//        while(tries < 1000 && unsolved ) {
//
//            int y = 0;
//            int x = 0;
//            boolean valid = true;
//
//            while (y < 9 && valid) {
//
//
//                while (x < 9 && valid) {
//
//                    ArrayList<Integer> triedNumbers = new ArrayList<>();
//
//                    boolean duplicate = true;
//
//
//                    int previousValue = 0;
//
//                    while (duplicate) {
//
//                        boolean noOptions = false;
//                        invalidOptions.get(x).get(y).add(previousValue);
//
//                        refreshNumbers(x, y);
//
//                        if (startingNumbers.size() > 0) {
//
//                            if (tileBoard.get(x).get(y).getCorrectNumber() == 0) {
//
//                                refreshNumbers(x, y);
//
//                                int number = startingNumbers.get(selectRandomNumber());
//
//                                tileBoard.get(x).get(y).setCorrectNumber(number);
//                                tileBoard.get(x).get(y).setCurrentNumber(number);
//
//                            }
//
//                            duplicate = false;
//                        } else {
//
//                            noOptions = true;
//
//                        }
//
//                        if (noOptions) {
//
//                            if (x == 0) {
//                                duplicate = false;
//                                valid = false;
//
//                            } else {
//                                if(tileBoard.get(x).get(y).isDiagonalTile()) {
//                                    if(x > 1){
//                                        x=-2;
//                                    }else{
//                                        duplicate = false;
//                                        valid = false;
//                                    }
//
//                                }else{
//                                    invalidOptions.get(x).get(y).add(tileBoard.get(x).get(y).getCurrentNumber());
//                                    tileBoard.get(x).get(y).setCurrentNumber(0);
//                                    tileBoard.get(x).get(y).setCorrectNumber(0);
//                                    x--;
//                                    previousValue = tileBoard.get(x).get(y).getCurrentNumber();
//                                    triedNumbers = new ArrayList<>();
//                                    triedNumbers.add(previousValue);
//
//                                    if(tileBoard.get(x).get(y).isDiagonalTile()){
//                                        if(x > 1){
//                                            x=-2;
//                                        }else{
//                                            duplicate = false;
//                                            valid = false;
//                                        }
//                                    }else{
//                                        tileBoard.get(x).get(y).setCurrentNumber(0);
//                                        tileBoard.get(x).get(y).setCorrectNumber(0);
//                                    }
//                                }
//
//
//                            }
//
//                        }
//                    }
//                    x++;
//
////                    System.out.println("X: "+x);
//
//                }
//
//                if (valid) {
//                    y++;
//                    x = 0;
//                    System.out.println(y);
//                } else {
//                    if (y > 0) {
//                        y--;
//                        x = 8;
//                        valid = true;
//                    }
//                }
//
////                System.out.println("Y: "+y);
//
//            }
//
//            if(valid) {
//                unsolved = false;
//            }else {
//                tries++;
//            }
//
////            System.out.println(tries);
//        }
//    }

/** Second Try */
//    public void setBoard(){
//        refreshNumbers();
//
//        Random random = new Random();
//        quadrantBoard = new ArrayList<>();
//        rowBoard = new ArrayList<>();
//        columnBoard = new ArrayList<>();
//        startingBoard = new ArrayList<>();
//
//        //Initiate Rows and Columns
//        for(int x = 0; x<9;x++){
//            ArrayList<Integer> column = new ArrayList<>();
//            ArrayList<Integer> row = new ArrayList<>();
//
//            columnBoard.add(column);
//            rowBoard.add(row);
//            for(int y = 0; y<9;y++){
//
//            }
//        }
//
//     //   Initiate Board
//        for(int x = 0; x<9;x++){
//            ArrayList<Integer> column = new ArrayList<>();
//            startingBoard.add(column);
//            for(int y = 0; y<9;y++){
//                startingBoard.get(x).add(0);
//            }
//        }
//        boolean keepGoing = true;
//        int restarts = 0;
//        int fullRestarts = 0;
//        int spacesFilled = 0;
//        while(spacesFilled < 82){
//
//            System.out.println("RESTARTING: GOT THIS FAR: "+ spacesFilled);
//            spacesFilled = 0;
//            quadrantBoard.clear();
//            rowBoard.clear();
//            columnBoard.clear();
//
//            for(int x = 0; x<9;x++){
//                ArrayList<Integer> column = new ArrayList<>();
//                ArrayList<Integer> row = new ArrayList<>();
//                columnBoard.add(column);
//                rowBoard.add(row);
//            }
//
//            for(int j = 0; j < 9; j=j+3){
//                //Quadrants on the X
//                if(restarts < 100){
//                    for(int i = 0; i < 9; i=i+3){
//                        //Quadrants on the Y
//                        if(restarts < 100){
//                            ArrayList<Integer> quadrant = new ArrayList<>();
//                            for(int y = 0; y<3; y++){
//                                //1 Quadrant on the Y
//                                if(restarts < 100){
//                                    for(int x = 0; x<3; x++){
//                                        //1 Quadrant on the X
//                                        if(restarts < 100){
//                                            boolean duplicate = true;
//                                            restarts = 0;
//                                            while(duplicate && restarts < 100){
//                                                int number = random.nextInt(9)+1;
//                                                System.out.println("Current Number:"+number);
//                                                if(!quadrant.contains(number)){
//
//                                                    if(!columnBoard.get(j+x).contains(number)){
//
//                                                        if(!rowBoard.get(i+y).contains(number)){
//
//                                                            quadrant.add(number);
//                                                            tileBoard.get(j+x).get(i+y).setCurrentNumber(number);
//                                                            columnBoard.get(j+x).add(number);
//                                                            rowBoard.get(i+y).add(number);
//                                                            duplicate = false;
//                                                            spacesFilled++;
//
//                                                        }else{
//                                                            System.out.println("Already in Row");
//                                                            System.out.println("Starting Over!");
//                                                        }
//
//                                                    }else{
//                                                        System.out.println("Already in Column");
//                                                        System.out.println("Starting Over!");
//                                                    }
//
//                                                }else{
//                                                    System.out.println("Already in Quadrant");
//                                                    System.out.println("Starting Over!");
//                                                }
//                                                restarts++;
//                                            }
//                                        }
//                                    }
//                                }
//
//
//                            }
//
//                            quadrantBoard.add(quadrant);
//                        }
//
//                    }
//                }
//
//
//            }
//            restarts = 0;
//
//        }
//
//
//        System.out.println("Quadrant"+quadrantBoard);
//        System.out.println("Column"+columnBoard);
//        System.out.println("Row"+rowBoard);
//    }
/** First Try */
//    public void setBoard(){
//        startingNumbers = new ArrayList<>();
//        refreshNumbers();
////        refreshBoard();
//
//        Random random = new Random();
//        int startingNumber;
//        boolean isDuplicate = true;
//
//        startingBoard = new ArrayList<ArrayList<Integer>>();
//        int checkingColumn = 0;
//        for(int y = 0; y<9; y++){
//            refreshNumbers();
//            ArrayList<Integer> row = new ArrayList<>();
//            startingBoard.add(row);
//            for(int x = 0; x<9; x++){
//                startingNumber = random.nextInt(9)+1;
//
//                while(isDuplicate && checkingColumn < 100){
//                    int indexRow = 0;
//                    boolean matchRow = false;
//                    for (int i = 0; i < startingNumbers.size(); i++) {
//                        if(startingNumbers.get(i) == startingNumber){
//                            matchRow = true;
//                            indexRow = i;
//                        }
//                    }
//                    if(matchRow){
//                        checkingColumn++;
//                        boolean matchColumn = false;
//                        for(int j = y-1; j > 0; j--){
//                            if(startingBoard.get(j).get(x) == startingNumber){
//                                matchColumn = true;
//                            }
//                        }
//                        if(matchColumn){
//                            startingNumber = random.nextInt(9)+1;
//                        }else{
//
//                            startingNumbers.remove(indexRow);
//                            isDuplicate = false;
//                        }
//                    }else{
//                        startingNumber = random.nextInt(9)+1;
//                    }
//                }
//                if(!(checkingColumn >= 100)){
//                    tileBoard.get(x).get(y).setCurrentNumber(startingNumber);
//                    startingBoard.get(y).add(startingNumber);
//                    isDuplicate = true;
//                }else{
//                    break;
//                }
//            }
//            if((checkingColumn >= 100)){
//                break;
//            }
//            System.out.println(startingBoard);
//        }
//
//    }

//    public void refreshBoard(){
//        startingBoard = new ArrayList<ArrayList<Integer>>();
//        int numberStart = 1;
//        for (int y = 0; y<9; y++){
//            int currentNumber = numberStart;
//            ArrayList<Integer> row = new ArrayList<>();
//            startingBoard.add(row);
//            for(int x = 0; x<9; x++){
//                tileBoard.get(x).get(y).setCurrentNumber(currentNumber);
//                startingBoard.get(y).add(currentNumber);
//                if(currentNumber==9){
//                    currentNumber =0;
//                }
//                currentNumber++;
//            }
//            numberStart++;
//        }
//        System.out.println(startingBoard);
//    }


    EventHandler<MouseEvent> onTileClicked = mouseEvent -> {
                if (mouseEvent.getTarget() instanceof Tile){
                    Tile tile = (Tile) mouseEvent.getTarget();
                    selectedTile = tile;
                }
    };

    public Tile getSelectedTile(){
        return selectedTile;
    }

    @FXML
    protected void onUndoButtonClick(){
        if(!undoStack.isEmpty()){
            Tile undoTile = undoStack.removeLast();
            tileBoard.get(undoTile.getX()).get(undoTile.getY()).setCurrentNumber(undoTile.getCurrentNumber());
        }

    }

    public void addToUndoStack(){
        Tile copyTile = new Tile(getSelectedTile().getCurrentNumber(), getSelectedTile().getX(), getSelectedTile().getY());
        undoStack.add(copyTile);
    }

    public void updateTile(int btnNum){
        boolean wrong = getSelectedTile().setCurrentNumber(btnNum);
        if(wrong){
            errorCount++;
            errorLabel.setText(String.valueOf(errorCount));
        }
    }
    @FXML
    protected void onOneButtonClick() {
        addToUndoStack();
        updateTile(1);
    }

    @FXML
    protected void onTwoButtonClick() {
        addToUndoStack();
        updateTile(2);
    }

    @FXML
    protected void onThreeButtonClick() {
        addToUndoStack();
        updateTile(3);

    }

    @FXML
    protected void onFourButtonClick() {
        addToUndoStack();
        updateTile(4);

    }
    @FXML
    protected void onFiveButtonClick() {
        addToUndoStack();
        updateTile(5);
    }
    @FXML
    protected void onSixButtonClick() {
        addToUndoStack();
        updateTile(6);
    }
    @FXML
    protected void onSevenButtonClick() {
        addToUndoStack();
        updateTile(7);
    }

    @FXML
    protected void onEightButtonClick() {
        addToUndoStack();
        updateTile(8);
    }
    @FXML
    protected void onNineButtonClick() {
        addToUndoStack();
        updateTile(9);
    }

}