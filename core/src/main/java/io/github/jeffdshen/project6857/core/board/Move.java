package io.github.jeffdshen.project6857.core.board;

import java.util.Objects;

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

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()){
            return false;
        }

        Move that = (Move)other;
        return (Objects.equals(this.start, that.start) && Objects.equals(this.dir, that.dir));
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(start);
        hash = 71 * hash + Objects.hashCode(dir);
        return hash;
    }
}
