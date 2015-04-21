package io.github.jeffdshen.project6857.core.board;

/**
 * Created by chenp on 4/18/2015.
 */
public class Move {
    private final Location start;
    private final Location end;

    public Move(Location start, Location end){
        this.start = start;
        this.end = end;
    }

    public Location getEnd() {
        return end;
    }

    public Location getStart() {
        return start;
    }
}
