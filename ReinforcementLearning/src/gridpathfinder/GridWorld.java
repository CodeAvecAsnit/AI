package gridpathfinder;

import java.util.Random;

class GridWorld {

    static final int ROWS;
    static final int COLS;

    static{
        ROWS= 10;
        COLS=15;
    }

    private int row;
    private int col;
    private int rowGoal;
    private int colGoal;
    private Random random;


    GridWorld(){
        this.random = new Random();
        this.rowGoal = 0;
        this.colGoal = 0;
        this.row = ROWS-1;
        this.col = COLS-1;
    }

    public void reset() {
        row = random.nextInt(ROWS);
        col = random.nextInt(COLS);
    }

    public boolean isGoal() {
        return row == rowGoal && col == colGoal;
    }

    public int getState() {
        return row * COLS + col;
    }

    public int step(int action) {
        int newRow = row;
        int newCol = col;

        switch (action) {
            case 0 -> newRow--;
            case 1 -> newRow++;
            case 2 -> newCol--;
            case 3 -> newCol++;
        }
        if (newRow >= 0 && newRow < ROWS &&
                newCol >= 0 && newCol < COLS) {
            row = newRow;
            col = newCol;
        }

        return isGoal() ? 100 : -1;
    }

    public void printGrid() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (r == row && c == col)
                    System.out.print("A ");
                else if (r == 0 && c == 0)
                    System.out.print("G ");
                else
                    System.out.print(". ");
            }
            System.out.println();
        }
        System.out.println("----------------------");
    }
}
