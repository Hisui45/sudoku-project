package com.example.sudokuproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;


public class GameController {

    @FXML
    private Label timerLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Button undoBtn,homeBtn;
    @FXML
    private GridPane board;
    @FXML
    private HBox utilBox;
    @FXML
    private HBox numbersBox;
    @FXML
    private ProgressBar loadBar;
    @FXML
    private Label generatingLabel;

    private TileBoard tileBoard;
    private int time;
    private int errorCount;
    private ArrayDeque<Tile> undoStack;
    private Tile selectedTile;
    private final int SIZE = 9;

    @FXML
    public void initialize() {

        loadBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        changeVisibility(false);
        setUndoBtn();
        setHomeBtn();

        ArrayList<ArrayList<String>> boardData = null;

        try {
            FileInputStream fileIn = new FileInputStream("data");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();
            if (obj instanceof ArrayList) {
                boardData = (ArrayList<ArrayList<String>>) obj;
                objectIn.close();
            }

        } catch (FileNotFoundException e) {
            boardData = null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (boardData != null) {
            ArrayList<String> gameDetails = boardData.get(boardData.size()-1);
            time = Integer.parseInt(gameDetails.get(0));
            errorCount = Integer.parseInt(gameDetails.get(1));
            boardData.remove(boardData.size()-1);
            tileBoard = new TileBoard(boardData);
            ContinueThread continueThread = new ContinueThread();
            continueThread.start();
        }else{
            SolutionThread solutionThread = new SolutionThread();
            solutionThread.start();
        }



    }

    public class SolutionThread extends Thread {
        public void run() {

            setDiagonals();
            setBoard();
            Platform.runLater(
                    () -> {
                        createBoard();
                        createPuzzle();
                        time = 0;
                        errorCount = 0;
                        setUtilities();
                        changeVisibility(true);
                        allowKeyUse();
                    }
            );

        }
    }

    public class ContinueThread extends Thread {
        public void run() {

            Platform.runLater(
                    () -> {
                        createBoard();
                        setUtilities();
                        changeVisibility(true);
                        allowKeyUse();
                    }
            );

        }
    }



    public void allowKeyUse(){
        Stage stage = (Stage) board.getScene().getWindow();

        stage.getScene().setOnKeyPressed(event -> {
            int buttonValue = 0;
            switch (event.getCode()) {
                case DIGIT1:    buttonValue = 1; break;
                case DIGIT2:    buttonValue = 2; break;
                case DIGIT3:    buttonValue = 3; break;
                case DIGIT4:    buttonValue = 4; break;
                case DIGIT5:    buttonValue = 5; break;
                case DIGIT6:    buttonValue = 6; break;
                case DIGIT7:    buttonValue = 7; break;
                case DIGIT8:    buttonValue = 8; break;
                case DIGIT9:    buttonValue = 9; break;
            }

            try {
                buttonOperations(buttonValue);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }
    public void changeVisibility(boolean visibility){
        board.setVisible(visibility);
        undoBtn.setVisible(visibility);
        homeBtn.setVisible(visibility);
        utilBox.setVisible(visibility);
        numbersBox.setVisible(visibility);

        loadBar.setVisible(!visibility);
        generatingLabel.setVisible(!visibility);
    }
    public void setUtilities(){
        undoStack = new ArrayDeque<>();
        errorLabel.setText(String.valueOf(errorCount));
        timerLabel.setText(formatTime(time));
        setTimer();
    }

    public String formatTime(int time){
        if(time >= 3600) {
            return String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, time % 60);

        }else if(time < 600){
            return String.format("%01d:%02d", (time % 3600) / 60, time % 60);
        }else{
            return String.format("%02d:%02d", (time % 3600) / 60, time % 60);
        }

    }
    public void setTimer(){
    /**Code Snippet from https://stackoverflow.com/questions/50766244/creating-a-timer-that-shows-the-time-in-hhmmss-format-javafx
    Modified to meet needs */
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
                    time++;
                    timerLabel.setText(formatTime(time));
                }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

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
    public void setHomeBtn(){
        SVGPath homeSVG = new SVGPath();
        homeSVG.setContent("M4.16138 10.2151C4.05852 10.3098 4 10.4432 4 10.583V19.3636C4 19.3636 4 19.3636 4 19.3636C4 19.3636 4 21 5.6 21C7.2 21 18.4 21 18.4 21C18.4 21 18.4 21 18.4 21C18.4 21 20 21 20 19.3636C20 17.8942 20 11.8062 20 10.5796C20 10.4398 19.9415 10.3098 19.8386 10.2151L12.3386 3.31168C12.1472 3.13553 11.8528 3.13553 11.6614 3.31168L4.16138 10.2151Z");
        homeSVG.setStroke(Color.web("#1B1D1F"));
        homeSVG.setStrokeWidth(2);
        homeSVG.setFillRule(FillRule.EVEN_ODD);
        homeSVG.setFill(Paint.valueOf("transparent"));
        homeBtn.setGraphic(homeSVG);


    }
    /** Method: createBoard()
     *  Sets up and fills solution into board  */
    public void createBoard(){
        for(int x = 0; x < 9; x++){

            ArrayList<Tile> column = tileBoard.get(x);

            for(int y = 0; y < 9; y++){
                Tile currentTile = column.get(y);
                board.add(currentTile, x, y);
            }

        }

        board.setOnMouseClicked(onTileClicked);

    }
    public void clearSolutions(){
            tileBoard = new TileBoard(SIZE);
    }
    public void setDiagonals() {

        tileBoard = new TileBoard(SIZE);

        //Set initial coordinates
        int x = 0;
        int y = 0;

        //Fill in number choices
        tileBoard.refreshNumbers();

        //Set numbers for left to right diagonal
        while(x<9){
            boolean duplicate = true;
            while(duplicate){
                //Generate random number
                int number = tileBoard.selectRandomNumber();
                //Check if number exists on board already; if not set on board
                if(tileBoard.removeNumber(number)){
                    tileBoard.get(x).get(y).setCorrectNumber(number);
                    tileBoard.get(x).get(y).setCurrentNumber(number);
                    tileBoard.get(x).get(y).setDiagonalTile(true);
                    x++;
                    y++;
                    duplicate = false;

                }
            }

        }

    }
    public void setBoard(){
        boolean unsolved = true;
        while(unsolved){
            clearSolutions();
            setDiagonals();
            boolean solution = generateSolution();
            if(solution){
                unsolved = false;
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
                tileBoard.refreshNumbers(x,y);

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
                    tileBoard.refreshNumbers(x, y);

                    if (tileBoard.getStartingNumbers().size() > 0) {

                        //Options to Choose From
                        if (tileBoard.get(x).get(y).getCorrectNumber() == 0) {

                            //Square is already blank
                            //All numbers should be valid
                            int number = tileBoard.selectRandomNumber();

                            currentTile.setCorrectNumber(number);
                            currentTile.setCurrentNumber(number);

                        }else{

                            //Square is not blank, and it's not a diagonal, means we've moved back but there are other options

                            //Value should be stored in invalid options
                            tileBoard.getSquareInvalidOptions(x,y).add(currentTile.getCurrentNumber());

                            tileBoard.refreshNumbers(x,y);

                            int number = tileBoard.selectRandomNumber();

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
    public void createPuzzle(){
        //EASY 36 - 45 clues
        Random random = new Random();

        int randomRange = random.nextInt(8)+1;

//        int clues = 36 + randomRange;

        int clues = 65 + randomRange;

        int removeAmount = 81 - clues;

        int x;
        int y;

        for(int i = 0; i < removeAmount; i++){
            x = random.nextInt(8)+1;
            y = random.nextInt(8)+1;
            tileBoard.get(x).get(y).setCurrentNumber(0);
        }

    }

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

    @FXML
    protected  void onHomeButtonClick() throws IOException {

        SaveTileBoard();
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("home-view.fxml"));

        stage.getScene().setRoot(fxmlLoader);

        stage.show();
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

    public void checkBoard() throws IOException {
        boolean solvedBoard = true;
        for(int x = 0; x < 9; x++){

            ArrayList<Tile> column = tileBoard.get(x);

            for(int y = 0; y < 9; y++){
                Tile currentTile = column.get(y);

                if(!currentTile.isCorrect()){
                    solvedBoard = false;
                }
            }

        }

        if(solvedBoard){
            File file = new File("data");
            file.delete();
            Stage stage = (Stage) board.getScene().getWindow();
            Parent fxmlLoader = FXMLLoader.load(getClass().getResource("end-view.fxml"));
            stage.getScene().setRoot(fxmlLoader);
            stage.show();
        }
    }


    public void buttonOperations(int btnNum) throws IOException {
        addToUndoStack();
        updateTile(btnNum);
        checkBoard();
    }

    @FXML
    protected void onOneButtonClick() throws IOException {
        buttonOperations(1);
    }
    @FXML
    protected void onTwoButtonClick() throws IOException {
        buttonOperations(2);
    }
    @FXML
    protected void onThreeButtonClick() throws IOException {
        buttonOperations(3);

    }
    @FXML
    protected void onFourButtonClick() throws IOException {
        buttonOperations(4);

    }
    @FXML
    protected void onFiveButtonClick() throws IOException {
        buttonOperations(5);
    }
    @FXML
    protected void onSixButtonClick() throws IOException {
        buttonOperations(6);
    }
    @FXML
    protected void onSevenButtonClick() throws IOException {
        buttonOperations(7);
    }
    @FXML
    protected void onEightButtonClick() throws IOException {
        buttonOperations(8);
    }
    @FXML
    protected void onNineButtonClick() throws IOException {
        buttonOperations(9);
    }

    public void SaveTileBoard() {

        ArrayList<ArrayList<String>> data = new ArrayList<>();

        for(int x = 0; x < 9; x++){

            ArrayList<Tile> column = tileBoard.get(x);
            ArrayList<String> dataColumn = new ArrayList<>();
            data.add(dataColumn);
            for(int y = 0; y < 9; y++){
                Tile currentTile = column.get(y);
                dataColumn.add(currentTile.getCurrentNumber()+" "+ currentTile.getCorrectNumber());

            }

        }

        ArrayList<String> gameDetails = new ArrayList<>();

        gameDetails.add(String.valueOf(time));
        System.out.println(String.valueOf(time));
        gameDetails.add(String.valueOf(errorCount));
        data.add(gameDetails);
        try {

            FileOutputStream fileOut = new FileOutputStream("data");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(data);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");
            System.out.println(data);
        } catch (Exception ex) {
            ex.printStackTrace();
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




}