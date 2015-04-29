package io.github.jeffdshen.project6857.core.board;

/**
 * Created by chenp on 4/22/2015.
 */
public class Round {
    private final Move myMove;
    private final Move theirMove;
    private final Compare myStatus;
    private final Compare theirStatus;

    public Round(Move myMove, Compare myStatus, Move theirMove, Compare theirStatus){
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
}
