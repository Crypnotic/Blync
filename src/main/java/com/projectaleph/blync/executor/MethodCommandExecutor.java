package com.projectaleph.blync.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.projectaleph.blync.CommandContext;
import com.projectaleph.blync.CommandExecutor;
import com.projectaleph.blync.CommandSourceTransformer;
import com.projectaleph.blync.exception.CommandExitException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MethodCommandExecutor<S> implements CommandExecutor<S> {

    @Getter
    private final Object command;
    @Getter
    private final Method method;
    @Getter
    private final String usage;
    @Getter
    private final CommandSourceTransformer<S> transformer;

    @Override
    public void execute(S sender, CommandContext context) throws CommandExitException {
        Object source = null;
        try {
            source = transformer.transform(sender);
        } catch (Throwable throwable) {
            transformer.onError(throwable);
            return;
        }

        try {
            method.invoke(command, source, context);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    method.getParameterTypes()[0] + " does not have a registered CommandSourceTransformer!");
        }
    }
}
