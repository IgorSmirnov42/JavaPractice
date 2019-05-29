package spb.hse.smirnov.findpair;

import java.util.ArrayList;
import java.util.Collections;

public class Field {
    public static final int HEIGHT = 700;
    public static final int WIDTH = 700;
    private final int[][] field;
    private CellStatus[][] cells;
    private final int fieldSide;
    private ArrayList<OpenCell> temporaryOpenCells;
    private final static long TIME_TO_SHOW_MILLISECONDS = 2000;

    public Field(int fieldSide) {
        this.fieldSide = fieldSide;
        field = new int[fieldSide][fieldSide];
        generateRandomField();
        cells = new CellStatus[fieldSide][fieldSide];
        for (int i = 0; i < fieldSide; ++i) {
            for (int j = 0; j < fieldSide; ++j) {
                cells[i][j] = CellStatus.CLOSED;
            }
        }
        temporaryOpenCells = new ArrayList<>();
    }

    public int getSize() {
        return fieldSide;
    }

    private void generateRandomField() {
        int maxN = fieldSide * fieldSide / 2;
        ArrayList<Integer> listOfNumbers = new ArrayList<Integer>();
        for (int curN = 0; curN < maxN; ++curN) {
            listOfNumbers.add(curN);
            listOfNumbers.add(curN);
        }
        Collections.shuffle(listOfNumbers);
        for (int i = 0; i < fieldSide; ++i) {
            for (int j = 0; j < fieldSide; ++j) {
                field[i][j] = listOfNumbers.get(i * fieldSide + j);
            }
        }
    }

    public CellStatus getStatus(int row, int column) {
        return cells[row][column];
    }

    private void closeOpenedCells() {
        for (OpenCell cell : temporaryOpenCells) {
            cells[cell.row][cell.column] = CellStatus.CLOSED;
        }
        temporaryOpenCells.clear();
    }

    private void acceptOpenedCells() {
        for (OpenCell cell : temporaryOpenCells) {
            cells[cell.row][cell.column] = CellStatus.FOREVER_OPEN;
        }
        temporaryOpenCells.clear();
    }

    public int getNumber(int row, int column) {
        return field[row][column];
    }

    public void tick() {
        if (temporaryOpenCells.size() != 2) {
            return;
        }
        long deltaTime = System.currentTimeMillis() - temporaryOpenCells.get(0).timeOpen;
        if (deltaTime >= TIME_TO_SHOW_MILLISECONDS) {
            closeOpenedCells();
        }
    }

    private OpenCell coordinatesToCell(double x, double y) {
        return new OpenCell( (int) (x * fieldSide / WIDTH),
                (int) (y * fieldSide / HEIGHT));
    }

    public boolean isEndOfGame() {
        for (int row = 0; row < fieldSide; ++row) {
            for (int column = 0; column < fieldSide; ++column) {
                if (cells[row][column] != CellStatus.FOREVER_OPEN) {
                    return false;
                }
            }
        }
        return true;
    }

    public void hit(double x, double y) {
        OpenCell tempCell = coordinatesToCell(x, y);
        int row = tempCell.row;
        int column = tempCell.column;
        if (cells[row][column] != CellStatus.CLOSED) {
            return;
        }
        cells[row][column] = CellStatus.OPEN;
        if (temporaryOpenCells.size() == 2) {
            closeOpenedCells();
        }
        temporaryOpenCells.add(new OpenCell(row, column));
        if (temporaryOpenCells.size() == 2) {
            OpenCell cell = temporaryOpenCells.get(0);
            if (field[cell.row][cell.column] == field[row][column]) {
                acceptOpenedCells();
            } else {
                long time = System.currentTimeMillis();
                for (OpenCell tCell : temporaryOpenCells) {
                    tCell.timeOpen = time;
                }
            }
        }
    }

    private static class OpenCell {
        private int row;
        private int column;
        private long timeOpen;

        private OpenCell(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }
}
