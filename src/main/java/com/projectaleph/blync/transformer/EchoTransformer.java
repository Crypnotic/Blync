package com.projectaleph.blync.transformer;

import com.projectaleph.blync.CommandSourceTransformer;
import com.projectaleph.blync.exception.CommandExitException;

public class EchoTransformer<S> extends CommandSourceTransformer<S> {

    @Override
    public Object transform(S source) throws CommandExitException {
        return source;
    }

    @Override
    public void onError(Throwable error) {
        error.printStackTrace();
    }
}
