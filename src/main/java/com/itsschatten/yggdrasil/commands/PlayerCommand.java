package com.itsschatten.yggdrasil.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    /**
     * Used to pass tab complete for this command.
     *
     * @param player  The sender of this command.
     * @param command The name of the command used.
     * @param args    The arguments for said command.
     * @return Returns a List used for tab complete.
     * @see #getTabComplete(CommandSender, String, String[])
     */
    public List<String> getTabComplete(final Player player, final String command, final String[] args) {
        return super.getTabComplete(player, command, args);
    }

    /**
     * {@inheritDoc}
     *
     * @param sender  The sender of this command.
     * @param command The name of the command used.
     * @param args    The arguments for said command.
     * @return Returns a List used for tab complete.
     */
    @Override
    public final @NotNull List<String> getTabComplete(CommandSender sender, String command, String[] args) {
        if (sender instanceof final Player player) {
            return getTabComplete(player, command, args);
        }
        return super.getTabComplete(sender, command, args);
    }
}
