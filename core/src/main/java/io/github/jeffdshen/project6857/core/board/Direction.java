package io.github.jeffdshen.project6857.core.board;

/**
 * Created by chenp on 4/18/2015.
 */
public enum Direction {
    FORWARD (0, 1),
    BACKWARD (0, -1),
    RIGHT (1, 0),
    LEFT (-1, 0);

    private final int x;
    private final int y;
    Direction(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getOpposite(){
        switch(this){
            case FORWARD: return BACKWARD;
            case BACKWARD: return FORWARD;
            case RIGHT: return LEFT;
            case LEFT: return RIGHT;
        }
        return null;
    }
}
