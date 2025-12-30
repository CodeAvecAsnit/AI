package snakegame;

import java.security.SecureRandom;

public class Grid {
    public final static int rows;
    public final static int cols;

    static{
        rows = 10;
        cols = 10;
    }

    private int[][] game;
    private int appleX;
    private int appleY;
    private int snakeLength;
    private int snakeHeadX;
    private int snakeHeadY;
    private SecureRandom secureRandom;



    public Grid(){
        game = new int[rows][cols];
        this.appleX = 6;
        this.appleY = 6;
        this.snakeHeadX = 6;
        this.snakeHeadY = 3;
        this.snakeLength= 3;
        this.secureRandom = new SecureRandom();
    }

    public void reset(){
        this.appleX = 6;
        this.appleY = 6;
        this.snakeHeadX = 6;
        this.snakeHeadY = 3;
        this.snakeLength= 3;
    }

    public void generateApple(){
        this.appleX = secureRandom.nextInt(0,10);
        this.appleY = secureRandom.nextInt(0,10);
    }

}
