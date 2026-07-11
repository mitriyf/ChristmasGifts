package ru.mitriyf.christmasgifts.utils.actions;

import lombok.Getter;

@Getter
public final class Action {
    private final ActionType type;
    private final String context;

    public Action(ActionType type, String context) {
        this.type = type;
        this.context = context;
    }
}