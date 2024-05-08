package com.itsschatten.yggdrasil.commands;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Console subcommand base.
 *
 * @since 2.0.0
 */
public abstract class ConsoleSubCommand extends SubCommandBase {

    /**
     * Build a console only subcommand.
     *
     * @param id The name of this subcommand.
     * @param aliases The aliases for this subcommand.
     * @param owningCommand The owning command.
     */
    public ConsoleSubCommand(@NotNull String id, List<String> aliases, @NotNull CommandBase owningCommand) {
        super(id, aliases, owningCommand);
    }

    @Override
    protected void run(Player player, String[] args) {
        throw new CommandBase.ReturnedCommandException("<red>Only console can use this subcommand!");
    }
}
