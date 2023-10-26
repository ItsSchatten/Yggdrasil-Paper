package com.itsschatten.yggdrasil.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Used to ensure that a player is only allowed to execute this command.
 */
public abstract class PlayerCommand extends CommandBase {
    /**
     * A player only command.
     *
     * @param name The name of the command.
     */
    public PlayerCommand(@NotNull String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     *
     * @param sender The sender of the command.
     * @param args   The arguments for the command.
     */
    @Override
    public final void runCommandSender(CommandSender sender, String[] args) {
        returnTell("<red>This is a player only command.");
    }
}
