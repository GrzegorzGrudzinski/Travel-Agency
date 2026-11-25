package model;

import i18n.*;

public enum Board {
    NONE, BREAKFAST, HALF_BOARD, FULL_BOARD, ALL_INCLUSIVE;

    @Override 
    public String toString() {
        return Messages.get("board." + name());
    }
}

