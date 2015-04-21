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
        if (this.equals(FORWARD)){
            return BACKWARD;
        } else if (this.equals(BACKWARD)){
            return FORWARD;
        } else if (this.equals(RIGHT)){
            return LEFT;
        } else if (this.equals(LEFT)){
            return RIGHT;
        }
        return null;
    }
}
