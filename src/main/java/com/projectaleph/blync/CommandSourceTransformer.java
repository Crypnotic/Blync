package com.projectaleph.blync;

import com.projectaleph.blync.exception.CommandExitException;

public abstract class CommandSourceTransformer<S> {

    public abstract Object transform(S source) throws CommandExitException;

    public abstract void onError(Throwable throwable);
}
