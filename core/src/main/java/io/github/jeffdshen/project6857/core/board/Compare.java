package io.github.jeffdshen.project6857.core.board;

/**
 * Created by chenp on 4/18/2015.
 */
public enum Compare {
    WIN, LOSS, TIE;

    public Compare opposite() {
        switch (this) {
            case WIN:
                return LOSS;
            case LOSS:
                return WIN;
            case TIE:
                return TIE;
        }
        return null;
    }
}
