package io.github.jeffdshen.project6857.core.board;

import java.util.Objects;

/**
 * Created by chenp on 4/22/2015.
 */
public class Round {
    private final Move myMove;
    private final Move theirMove;
    private final Result myStatus;
    private final Result theirStatus;

    public Round(Move myMove, Result myStatus, Move theirMove, Result theirStatus){
        this.myMove = myMove;
        this.theirMove = theirMove;
        this.myStatus = myStatus;
        this.theirStatus = theirStatus;
    }

    public Move getMyMove() {
        return myMove;
    }

    public Move getTheirMove() {
        return theirMove;
    }

    public Result getMyStatus() {
        return myStatus;
    }

    public Result getTheirStatus() {
        return theirStatus;
    }

    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()){
            return false;
        }

        Round that = (Round)other;
        return (Objects.equals(this.myMove, that.myMove) && Objects.equals(this.theirMove, that.theirMove) && Objects.equals(this.myStatus, that.myStatus) && Objects.equals(this.theirStatus, that.theirStatus));
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(myMove);
        hash = 71 * hash + Objects.hashCode(theirMove);
        hash = 71 * hash + Objects.hashCode(myStatus);
        hash = 71 * hash + Objects.hashCode(theirStatus);
        return hash;
    }
}
