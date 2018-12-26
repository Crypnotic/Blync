package com.projectaleph.blync;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.projectaleph.blync.annotation.Aliases;
import com.projectaleph.blync.annotation.Command;
import com.projectaleph.blync.annotation.Usage;
import com.projectaleph.blync.exception.CommandExitException;
import com.projectaleph.blync.exception.MalformedCommandException;
import com.projectaleph.blync.executor.MethodCommandExecutor;
import com.projectaleph.blync.transformer.EchoTransformer;

import lombok.SneakyThrows;

public class CommandLoader<S> {

    private final Map<String, CommandWrapper<S>> commands = new ConcurrentHashMap<String, CommandWrapper<S>>();
    private final Map<Class<?>, CommandSourceTransformer<S>> transformers = new ConcurrentHashMap<Class<?>, CommandSourceTransformer<S>>();
    private final EchoTransformer<S> echoTransformer = new EchoTransformer<S>();

    @SneakyThrows
    @SuppressWarnings("deprecation")
    public void register(Object command) {
        String name = getCommandName(command);
        String usage = getUsage(command);
        String[] aliases = getAliases(command);

        CommandWrapper<S> wrapper = new CommandWrapper<S>(name, usage);

        for (Method method : command.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Command.class)) {
                continue;
            }

            Command subcommand = method.getAnnotation(Command.class);
            String[] subAliases = getAliases(method);
            if (method.getParameterCount() != 2) {
                throw new MalformedCommandException();
            }

            Class<?> sourceClass = method.getParameterTypes()[0];
            CommandSourceTransformer<S> transformer = transformers.getOrDefault(sourceClass, echoTransformer);
            if (method.getParameterTypes()[1] != CommandContext.class) {
                throw new MalformedCommandException("Parameter 2 is not of type CommandContext");
            }

            MethodCommandExecutor<S> executor = new MethodCommandExecutor<S>(command, method, usage, transformer);

            wrapper.add(subcommand.value(), executor, subAliases);
        }

        if (commands.putIfAbsent(name, wrapper) == null) {
            for (String alias : aliases) {
                if (commands.putIfAbsent(alias, wrapper) != null) {
                    throw new IllegalStateException("Command already registered: " + alias);
                }
            }
        } else {
            throw new IllegalStateException("Command already registered: " + name);
        }
    }

    @SuppressWarnings("deprecation")
    public String execute(S source, String command, String[] args) {
        CommandWrapper<S> wrapper = commands.get(command);
        if (wrapper != null) {
            CommandExecutor<S> executor = wrapper.getSubcommand(args[0].toLowerCase());
            if (executor != null) {
                String[] arguments = new String[args.length - 1];
                System.arraycopy(args, 1, arguments, 0, arguments.length);

                try {
                    executor.execute(source, new CommandContext(arguments));
                } catch (CommandExitException exception) {
                    return exception.getMessage();
                }
            } else {
                return ""; // TODO: Return CommandWrapper usage
            }
        } else {
            return ""; // TODO: Return unknown command
        }
        return "";
    }

    public void addCommandSourceTransformer(Class<?> resultClass, CommandSourceTransformer<S> transformer) {
        if (transformers.putIfAbsent(resultClass, transformer) != null) {
            transformer.onError(new IllegalStateException(
                    "Command source transformer already registered for class: " + resultClass));
        }
    }

    private String getCommandName(Object value) {
        if (value.getClass().isAnnotationPresent(Command.class)) {
            return value.getClass().getAnnotation(Command.class).value();
        }
        return "";
    }

    private String getUsage(Object value) {
        if (value.getClass().isAnnotationPresent(Usage.class)) {
            return value.getClass().getAnnotation(Usage.class).value();
        }
        return "";
    }

    private String[] getAliases(Object value) {
        if (value.getClass().isAnnotationPresent(Aliases.class)) {
            return value.getClass().getAnnotation(Aliases.class).value().split("|");
        }
        return new String[] {};
    }
}
