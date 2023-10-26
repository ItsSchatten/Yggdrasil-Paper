package com.itsschatten.yggdrasil.commands;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Used for commands that should be for console only.
 */
public abstract class ConsoleCommand extends CommandBase {

    /**
     * A console only command.
     *
     * @param name The name of the command.
     */
    public ConsoleCommand(@NotNull String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     *
     * @param player The player that issued this command.
     * @param args   The arguments for the command.
     */
    @Override
    public final void run(Player player, String[] args) {
        returnTell("<red>This is a console only command.");
    }
}
