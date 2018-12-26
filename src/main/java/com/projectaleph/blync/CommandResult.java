package com.projectaleph.blync;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandResult {

    public static final CommandResult SUCCESS = new CommandResult(Reason.SUCCESS, "");

    @Getter
    private final Reason reason;
    @Getter
    private final String message;

    public static enum Reason {
        SUCCESS, UNKNOWN_COMMAND, USAGE, ERROR;
    }
}
