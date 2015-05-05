package io.github.jeffdshen.project6857.core.board;

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
}
