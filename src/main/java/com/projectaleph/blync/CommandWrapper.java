package com.projectaleph.blync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandWrapper<S> {

    @Getter
    private final String name;
    @Getter
    private final String usage;

    private Map<String, CommandExecutor<S>> subcommands = new ConcurrentHashMap<String, CommandExecutor<S>>();

    @Deprecated
    public CommandExecutor<S> getSubcommand(String name) {
        return subcommands.get(name);
    }

    @Deprecated
    public void add(String commandName, CommandExecutor<S> executor, String... aliases) {
        if (commandName == null) {
            throw new NullPointerException("command(" + this.name + ")");
        }

        if (subcommands.containsKey(commandName)) {
            throw new IllegalStateException(
                    this.name + " already has a registered subcommand with name: " + commandName);
        }

        subcommands.put(name.toLowerCase(), executor);

        for (String alias : aliases) {
            if (alias == null) {
                throw new NullPointerException("command(" + this.name + ")");
            }

            if (subcommands.containsKey(alias)) {
                throw new IllegalStateException(
                        this.name + " already has a registered subcommand alias with name: " + alias);
            }

            subcommands.put(alias.toLowerCase(), executor);
        }
    }
}
