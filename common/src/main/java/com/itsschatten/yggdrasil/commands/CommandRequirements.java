package com.itsschatten.yggdrasil.commands;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.experimental.UtilityClass;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Utility class for common requirements for commands, this does not cover base command permissions as that is already handled within {@link BrigadierCommand}.
 *
 * @since 2.1.1
 */
@UtilityClass
public class CommandRequirements {

    /**
     * Quickly checks if {@link CommandSourceStack} is an instanceof {@link Player}.
     *
     * @since 2.1.1
     */
    public static Predicate<CommandSourceStack> PLAYER = (source) -> source.getSender() instanceof Player;

    /**
     * Quickly checks if {@link CommandSourceStack} is an instanceof {@link ConsoleCommandSender}.
     *
     * @since 2.1.1
     */
    public static Predicate<CommandSourceStack> CONSOLE = (source) -> source.getSender() instanceof ConsoleCommandSender;

    /**
     * Utility method to quickly check if a {@link CommandSourceStack} has a permission.
     *
     * @param permission The permission to check.
     * @return Returns {@link org.bukkit.command.CommandSender#hasPermission(String)}
     * @since 2.1.1
     */
    @Contract(pure = true)
    public static @NotNull Predicate<CommandSourceStack> permission(final String permission) {
        return commandSourceStack -> commandSourceStack.getSender().hasPermission(permission);
    }

}
