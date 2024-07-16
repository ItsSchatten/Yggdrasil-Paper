package com.itsschatten.yggdrasil.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Player subcommand base.
 *
 * @since 2.0.0
 */
public abstract class PlayerSubCommand extends SubCommandBase {

    /**
     * Build a player only subcommand.
     *
     * @param id            The name of this subcommand.
     * @param aliases       The aliases for this subcommand.
     * @param owningCommand The owning command.
     */
    public PlayerSubCommand(@NotNull String id, List<String> aliases, @NotNull CommandBase owningCommand) {
        super(id, aliases, owningCommand);
    }

    @Override
    protected final void run(CommandSender sender, String[] args) {
        throw new CommandBase.ReturnedCommandException("<red>This is a player only subcommand.");
    }

    /**
     * Used to pass tab complete for this command.
     *
     * @param player The sender of this command.
     * @param args   The arguments for said command.
     * @return Returns a {@link List} used for tab complete.
     * @see #getTabComplete(CommandSender, String[])
     */
    public List<String> getTabComplete(final Player player, final String[] args) {
        return super.getTabComplete(player, args);
    }

    /**
     * {@inheritDoc}
     *
     * @param sender The sender of this command.
     * @param args   The arguments for said command.
     * @return Returns a {@link List} used for tab complete.
     */
    @Override
    public final @NotNull List<String> getTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof final Player player) {
            return getTabComplete(player, args);
        }

        return super.getTabComplete(sender, args);
    }
}
