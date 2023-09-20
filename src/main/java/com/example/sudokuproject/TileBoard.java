package com.example.sudokuproject;

import java.util.ArrayList;
import java.util.Random;

public class TileBoard extends ArrayList<ArrayList<Tile>>{

    private ArrayList<ArrayList<ArrayList<Integer>>> invalidOptions;

    private ArrayList<Integer> startingNumbers;


    public TileBoard(int size){
        invalidOptions = new ArrayList<>();

        for(int x = 0; x <size; x++) {
            ArrayList<Tile> columns = new ArrayList<>();
            ArrayList<ArrayList<Integer>> square = new ArrayList<>();
            this.add(columns);
            invalidOptions.add(square);
            for (int y = 0; y < size; y++) {
                Tile tile = new Tile(0, 0, x, y,size);
                this.get(x).add(tile);
                ArrayList<Integer> numbers = new ArrayList<>();
                square.add(numbers);
            }
        }
    }

    public TileBoard(ArrayList<ArrayList<String>> data){
        invalidOptions = new ArrayList<>();
        int size = data.size();

        for(int x = 0; x <size; x++) {
            ArrayList<Tile> columns = new ArrayList<>();
            ArrayList<ArrayList<Integer>> square = new ArrayList<>();
            this.add(columns);
            invalidOptions.add(square);
            for (int y = 0; y < size; y++) {
                String valueString = data.get(x).get(y);
                String current  = String.valueOf(valueString.charAt(0));
                String correct  = String.valueOf(valueString.charAt(2));
                Tile tile = new Tile(Integer.valueOf(current), Integer.valueOf(correct), x, y, size);
                this.get(x).add(tile);
                ArrayList<Integer> numbers = new ArrayList<>();
                square.add(numbers);
            }
        }
    }

    public ArrayList<Integer> getRowNumbers(int y){
        ArrayList<Integer> row = new ArrayList<>();
        for(int x = 0; x<9; x++){
            int number = this.get(x).get(y).getCorrectNumber();
            row.add(number);
        }

        return row;
    }

    public ArrayList<Integer> getColumnNumbers(int x){
        ArrayList<Integer> column = new ArrayList<>();

        for(int y = 0; y<9; y++){
            int number = this.get(x).get(y).getCorrectNumber();
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

                quadrant.add(this.get(j).get(i).getCurrentNumber());

            }

        }

        return quadrant;

    }

    public ArrayList<Integer> getStartingNumbers() {
        return startingNumbers;
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
        int number;
        if(startingNumbers.size() > 1){
            number = random.nextInt(startingNumbers.size());
           return startingNumbers.get(number);
        }
        return startingNumbers.get(0);
    }

    public void refreshNumbers(){
        startingNumbers = new ArrayList<>();
        for(int x = 1; x <= 9; x++){
            startingNumbers.add(x);
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

            for(Integer invalidOptions : getSquareInvalidOptions(x,y)){
                if(number.equals(invalidOptions)){
                    removeNumbers.add(number);
                }
            }
        }

        for(Integer removeNumber: removeNumbers){
            startingNumbers.remove(removeNumber);
        }

    }

    public ArrayList<Integer> getSquareInvalidOptions(int x, int y){
        return invalidOptions.get(x).get(y);
    }


}
