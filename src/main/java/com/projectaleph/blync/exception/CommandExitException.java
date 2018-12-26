package com.projectaleph.blync.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CommandExitException extends Exception {

    private static final long serialVersionUID = 7632207698307238475L;

    @Getter
    private ExitReason reason;

    public CommandExitException(String message) {
        super(message);
    }

    public CommandExitException(ExitReason reason, String message) {
        super(message);

        this.reason = reason;
    }

    public boolean hasReason() {
        return reason != null;
    }

    public static enum ExitReason {
        ERROR, USAGE;
    }
}
