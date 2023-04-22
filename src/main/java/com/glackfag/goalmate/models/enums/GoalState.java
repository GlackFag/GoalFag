package com.glackfag.goalmate.models.enums;

public enum GoalState {
    IN_PROGRESS("In progress"),
    FINISHED("Finished"),
    FAILED("Failed");

    private final String str;

    GoalState(String string) {
        str = string;
    }

    @Override
    public String toString() {
        return str;
    }
}
