package io.github.jeffdshen.project6857.core.board;

/**
 * Created by chenp on 4/18/2015.
 */
public class Move {
    private final Location start;
    private final Location end;
    private final Direction dir;

    public Move(Location start, Direction dir){
        this.start = start;
        this.end = start.add(dir);
        this.dir = dir;
    }

    public Direction getDirection() {
        return dir;
    }

    public Location getEnd() {
        return end;
    }

    public Location getStart() {
        return start;
    }
}
