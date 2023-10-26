package com.itsschatten.yggdrasil.commands;

import org.bukkit.command.CommandSender;
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
}
