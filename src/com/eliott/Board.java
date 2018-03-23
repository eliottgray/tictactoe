package com.eliott;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by egray on 2/20/2018.
 */
class Board {

    private String[][] boardMatrix;
    private ArrayList<Move> availableMovesList;

    Board() {
        boardMatrix = new String[3][3];
        availableMovesList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardMatrix[i][j] = " ";
//                availableMovesList.add(new int[]{i, j});  //todo Remove
                availableMovesList.add(new Move(i, j, ""));

            }
        }
    }


    String[][] getBoardMatrix() { return boardMatrix; }

    ArrayList<Move> getAvailableMovesList() { return availableMovesList; }

    void setBoardMatrix(String[][] newBoardMatrix) {
        boardMatrix = newBoardMatrix;
        availableMovesList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(boardMatrix[i][j].equals(" ")){
//                    availableMovesList.add(new int[]{i, j});  //todo Remove
                    availableMovesList.add(new Move(i, j, ""));
                }
            }
        }
    }

    void addMove(Move move) {
        int row = move.getRow();
        int col = move.getCol();
        String token = move.getToken();
        boardMatrix[row][col] = token;
        removeMoveFromAvailableMoveList(move);
    }

    void removeMoveFromAvailableMoveList(Move moveToRemove) {
        Move move = null;
        for ( Move availableMove : availableMovesList) {
            if (availableMove.getRow() == moveToRemove.getRow() && availableMove.getCol() == moveToRemove.getCol()){
                move = availableMove;
                break;
            }
        }
        availableMovesList.remove(move);
    }

    String getGameState() {
        if (isVictory()) {
            return "Win";
        } else if (boardIsFull()) {
            return "Tie";
        } else {
            return "Ongoing";
        }
    }

    boolean isVictory() {
        for (int i = 0; i < 3; i++) {
            String[] row = new String[]{boardMatrix[i][0], boardMatrix[i][1], boardMatrix[i][2]};
            if (checkRowForVictory(row)){
                return true;
            }

            String[] column = new String[]{boardMatrix[0][i], boardMatrix[1][i], boardMatrix[2][i]};
            if (checkRowForVictory(column)){
                return true;
            }
        }

        String[] frontDiagonal = new String[]{boardMatrix[0][0], boardMatrix[1][1], boardMatrix[2][2]};
        if (checkRowForVictory(frontDiagonal)){
            return true;
        }

        String[] backDiagonal = new String[]{boardMatrix[0][2], boardMatrix[1][1], boardMatrix[2][0]};
        if (checkRowForVictory(backDiagonal)){
            return true;
        }

        return false;
    }

    boolean checkRowForVictory(String[] row) {
        boolean rowIsIdentical = row[0].equals(row[1]) && row[1].equals(row[2]);
        boolean rowIsNotEmpty = !row[0].equals(" ");
        return rowIsIdentical && rowIsNotEmpty;
    }

    boolean boardIsFull() {
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (boardMatrix[i][j].equals(" ")){
//                    return false;
//                }
//            }
//        }
//        return true;
        return availableMovesList.size() == 0;
    }

    HashSet<Move> getWinningMovesSet(String token) {
        HashSet<Move> moveSet = new HashSet<>();
        Move move;
        // check Rows and Columns
        for (int i = 0; i < 3; i++) {
            int[][] rowCoordinates = new int[][]{{i, 0}, {i, 1}, {i, 2}};
            move = findTwoInARow(token, rowCoordinates);
            if (move != null){
                moveSet.add(move);
            }

            int[][] columnCoordinates = new int[][]{{0, i},{1, i},{2, i}};
            move = findTwoInARow(token, columnCoordinates);
            if (move != null){
                moveSet.add(move);
            }
        }

        // check Diagonals
        int[][] forwardDiagonalCoords = new int[][]{{0, 0},{1, 1},{2, 2}};
        move = findTwoInARow(token, forwardDiagonalCoords);
        if(move != null){
            moveSet.add(move);
        }

        int[][] backwardDiagonalCoords = new int[][]{{0, 2},{1, 1},{2, 0}};
        move = findTwoInARow(token, backwardDiagonalCoords);
        if(move != null){
            moveSet.add(move);
        }

        return moveSet;
    }

    Move getWinningMove(String token){
        Move winningMove = null;
        HashSet<Move> moveSet = getWinningMovesSet(token);
        if (moveSet.size() > 0){
            winningMove = moveSet.iterator().next();
        }

        return winningMove;
    }

    Move findTwoInARow(String token, int[][] rowColOrDiagonal){
        String boardCell;
        int boardRowCoordinate;
        int boardColumnCoordinate;
        int tokenCounter = 0;
        int emptyCounter = 0;
        int emptyIndex = 0;
        int[] winningCoordinates;

        for (int i = 0; i < 3; i++){
            boardRowCoordinate = rowColOrDiagonal[i][0];
            boardColumnCoordinate = rowColOrDiagonal[i][1];
            boardCell = boardMatrix[boardRowCoordinate][boardColumnCoordinate];
            if (boardCell.equals(token)){
                tokenCounter++;
            } else if (boardCell.equals(" ")){
                emptyCounter++;
                emptyIndex = i;
            }
        }
        if (tokenCounter == 2 && emptyCounter == 1){
            winningCoordinates = rowColOrDiagonal[emptyIndex];
            return new Move(winningCoordinates[0], winningCoordinates[1], token);
        }
        return null;
    }

    Move findForkOpportunity(String token){
        Move move = null;

        for (Move moveToCheck : availableMovesList){
            int row = moveToCheck.getRow();
            int col = moveToCheck.getCol();
            boardMatrix[row][col] = token;
            HashSet<Move> winningMovesSet = getWinningMovesSet(token);
            boardMatrix[row][col] = " ";

            if (winningMovesSet.size() > 1) {
                move = new Move(row, col, token);
                break;
            }
        }
        return move;
    }

    Move blockOpponentFork(String myToken, String opponentToken){   //todo unit test.
        Move move = null;

        for (Move moveToCheck : availableMovesList){
            int row = moveToCheck.getRow();
            int col = moveToCheck.getCol();

            boardMatrix[row][col] = opponentToken;
            HashSet<Move> opponentWinningMovesSet = getWinningMovesSet(opponentToken);
            boardMatrix[row][col] = " ";

            if (opponentWinningMovesSet.size() > 1) {

                // departure from the "find my own fork" method - look for my own winnings when blocking.
                boardMatrix[row][col] = myToken;
                HashSet<Move> myWinningMovesSet = getWinningMovesSet(myToken);
                boardMatrix[row][col] = " ";

                if (myWinningMovesSet.size() > 0) {
                    move = new Move(row, col, myToken);
                    break;
                }
            }
        }
        return move;
    }

    Move findCenterOpportunity(String token) {
        Move move = null;

        if (boardMatrix[1][1].equals(" ")) {
            move = new Move(1, 1, token);
        }

        return move;
    }

    Move findCornerOpportunity(String myToken, String otherToken) {
        Move cornerMove = null;

        for (Move moveToCheck : availableMovesList){
            int row = moveToCheck.getRow();
            int col = moveToCheck.getCol();

            // check if move is a corner move  //todo break out into method?
            if ((row == 0 || row == 2) && (col == 0 || col == 2)) {

                cornerMove = new Move(row, col, myToken);

                // get opposite corner
                int oppositeCornerRow = row ^ 2;
                int oppositeCornerColumn = col ^ 2;

                String oppositeCornerToken = boardMatrix[oppositeCornerRow][oppositeCornerColumn];
                // if opposite corner is enemy-controlled, return move.
                if (oppositeCornerToken.equals(otherToken)) {
                    return cornerMove;
                }
            }
        }
        return cornerMove;
    }

    Move findEdgeOpportunity(String token) {
        Move edgeMove = null;

        for (Move moveToCheck : availableMovesList){
            int row = moveToCheck.getRow();
            int col = moveToCheck.getCol();

            // check if edge cell.
            if((row == 0 && col == 1) || (row == 1 && col == 0) || (row == 1 && col == 2) || (row == 2 && col == 1)){
                edgeMove = new Move(row, col, token);
                break;
            }
        }

        return edgeMove;
    }

}
