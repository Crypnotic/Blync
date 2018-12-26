package com.projectaleph.blync;

import com.projectaleph.blync.exception.CommandExitException;

public interface CommandExecutor<S> {

    public void execute(S sender, CommandContext context) throws CommandExitException;
}
