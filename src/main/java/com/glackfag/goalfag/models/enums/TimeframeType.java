package com.glackfag.goalfag.models.enums;


public enum TimeframeType {
    LONG_TERM("Long-term"),
    MEDIUM_TERM("Medium-term"),
    NEAR_TERM("Near-term");

    private final String str;

    TimeframeType(String string) {
        str = string;
    }

    @Override
    public String toString() {
        return str;
    }
}
