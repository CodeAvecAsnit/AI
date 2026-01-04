package snakegame;

import java.security.SecureRandom;

public class SnakeWorld {

    public final static int GridX;
    public final static int GridY;
    public final static int MOVE_UP;
    public final static int MOVE_DOWN;
    public final static int MOVE_FRONT;
    public final static int MOVE_BACK;

    static{
        GridX = 25;
        GridY= 25;
        MOVE_FRONT=1;
        MOVE_BACK=2;
        MOVE_UP=3;
        MOVE_DOWN=4;
    }

    private int AppleX;
    private int AppleY;

    private int SnakeX;
    private int SnakeY;

    private int snakeLength;

    private SecureRandom secureRandom;

    private int movingDirection; // 1 -> front,2->back,3 -> up,4->down

    public SnakeWorld() {
        secureRandom = new SecureRandom();
        snakeLength = 1;
        movingDirection = MOVE_FRONT;
        start();
    }

    private boolean isDead(){
        return (SnakeX<0||SnakeX>=GridX)||(SnakeY<0||SnakeY>=GridY||snakeLength>5500);
    }

    public void start(){
        SnakeX=12;
        SnakeY=5;
        AppleX = 12;
        AppleY = 15;
        snakeLength = 1;
        movingDirection = MOVE_FRONT;
    }

    public boolean eatApple(){
        if((SnakeX == AppleX)&&(SnakeY==AppleY)){
            AppleX = secureRandom.nextInt(GridX);
            AppleY = secureRandom.nextInt(GridY);
            ++snakeLength;
            return true;
        }
        return false;
    }

    private void move(int x) {
        switch (x) {
            case 1 -> SnakeX++;
            case 2 -> SnakeX--;
            case 3 -> SnakeY--;
            case 4 -> SnakeY++;
        }
    }

    public double performAction(int action) {

        double oldDistance = getDistanceToApple();

        movingDirection = action;
        move(action);

        if (isDead()) {
            return -100.0;
        }
        if (eatApple()) {
            return 10.0;
        }

        double newDistance = getDistanceToApple();
        if (newDistance < oldDistance) {
            return +1.0;
        } else {
            return -1.0;
        }
    }

    public int[] getState() {
        return new int[]{SnakeX, SnakeY, AppleX, AppleY};
    }

    public boolean isGameOver() {
        return isDead();
    }

    public void reset() {
        start();
    }

    public int getScore(){
        return snakeLength;
    }

    public double getDistanceToApple() {
        return Math.sqrt(Math.pow(SnakeX - AppleX, 2) + Math.pow(SnakeY - AppleY, 2));
    }
}