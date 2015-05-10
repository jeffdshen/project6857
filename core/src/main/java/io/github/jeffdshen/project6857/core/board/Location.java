package io.github.jeffdshen.project6857.core.board;

/**
 * Created by chenp on 4/21/2015.
 */
public class Location {
    private final int x;
    private final int y;

    public Location(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Location add(Direction d) {
        return new Location(x + d.getX(), y + d.getY());
    }

    @Override
    public boolean equals(Object other){
        if (other == null || other.getClass() != this.getClass()){
            return false;
        }

        Location that = (Location)other;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 71 * hash + this.x;
        hash = 71 * hash + this.y;
        return hash;
    }
}
