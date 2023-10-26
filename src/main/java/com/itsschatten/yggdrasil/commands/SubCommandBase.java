package com.itsschatten.yggdrasil.commands;

import com.itsschatten.yggdrasil.IPermission;
import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.StringUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Subcommand base.
 *
 * @since 2.0.0
 */
public abstract class SubCommandBase {

    /**
     * Permission required for this subcommand.
     * {@code <instance_name>.<owningCommand name>.<subcommand name>}
     * {@code yggdrasil.gamemode.creative}
     */
    @Getter
    private final String permission;

    /**
     * The id of this subcommand.
     */
    @Getter
    private final String id;

    /**
     * Aliases for this subcommand.
     */
    @Getter
    private final List<String> aliases;

    // CommandSender; used for returnTell / tell.
    private CommandSender sender;
    // Arguments.
    private String[] args;

    // The command that "owns" this subcommand.
    private final CommandBase owningCommand;

    public SubCommandBase(final @NotNull String id, final List<String> aliases, final @NotNull CommandBase owningCommand) {
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.aliases = new ArrayList<>(aliases);
        this.owningCommand = owningCommand;

        // <instance name>.<owningCommand>.<subcommand>
        this.permission = Utils.getInstance().getName() + "." + owningCommand.getName() + "." + id;
    }

    /**
     * Run the command without any checking if the command is a player.
     *
     * @param sender The sender of the command.
     * @param args   The arguments for the command.
     */
    protected abstract void run(final CommandSender sender, final String[] args);

    /**
     * Run the command as if a {@link Player} sent the command.
     *
     * @param user The user that sent this command.
     * @param args The arguments for the command.
     */
    protected abstract void run(final Player user, final String[] args);

    /**
     * Executes this subcommand.
     *
     * @param sender The sender of this sub command.
     * @param args   The arguments for this subcommand.
     */
    protected final void execute(final CommandSender sender, final String[] args) {
        this.sender = sender;
        this.args = args;

        // Test permission.
        if (!testPermission(sender)) {
            return;
        }

        // Run the command based on if a player or not.
        if (sender instanceof Player player) {
            run(player, args);
        } else {
            run(sender, args);
        }
    }

    /**
     * Silently tests if the {@link CommandSender} has permission.
     *
     * @param sender The sender to check if they have permission.
     * @return <code>true</code> if they have permission, <code>false</code> otherwise.
     */
    public final boolean testPermissionSilent(final @NotNull CommandSender sender) {
        return sender.hasPermission(getPermission());
    }

    /**
     * Test if a {@link CommandSender} has permission to execute this subcommand.
     * This will send an error message if false.
     *
     * @param sender The sender of this subcommand.
     * @return <code>true</code> if they have permission, <code>false</code> otherwise.
     * @see #testPermissionSilent(CommandSender)
     * @see #testPermission(Player)
     */
    public final boolean testPermission(final CommandSender sender) {
        if (testPermissionSilent(sender)) {
            return true;
        }

        sender.sendMessage(Objects.requireNonNullElse(owningCommand.permissionMessage(), Bukkit.permissionMessage()));
        return false;
    }

    /**
     * Used to test if the {@link Player} has permission to execute this subcommand.
     * This will send an error message if false.
     *
     * @param player The player to check permission for.
     * @return Returns {@link #testPermission(CommandSender)}
     * @see #testPermission(CommandSender)
     * @see #testPermissionSilent(CommandSender)
     */
    public final boolean testPermission(final @NotNull Player player) {
        return testPermission((CommandSender) player);
    }


    /**
     * Check if an {@link Object} is null.
     *
     * @param toCheck     The {@link Object} we are checking.
     * @param nullMessage The message that will be sent to the {@link CommandSender} should the value be null.
     */
    protected void checkNotNull(final Object toCheck, final String nullMessage) {
        if (toCheck == null)
            returnTell(nullMessage);
    }

    /**
     * Check if the command has an appropriate number of arguments.
     *
     * @param minLength The minimum length the command must have to execute.
     * @param message   The message that will be sent if the argument length does not match.
     */
    protected void checkArgs(int minLength, String message) {
        if (args.length < minLength)
            returnTell(message);
    }

    /**
     * Throws a {@link CommandBase.ReturnedCommandException} effectively stopping execution of the command.
     *
     * @param message The message that should be sent to the {@link CommandSender}.
     */
    protected void returnTell(final String message) {
        throw new CommandBase.ReturnedCommandException(message);
    }

    /**
     * Sends a message to the sender of the command.
     *
     * @param message  The message to send.
     * @param messages An array of Strings that will be sent to the player, not required for this method to function.
     */
    protected void tell(String message, String... messages) {
        Utils.tell(sender, message, messages);
    }

    /**
     * Sends a message to the sender of the command.
     *
     * @param message  The message to send.
     * @param messages An array of Strings that will be sent to the player, not required for this method to function.
     */
    protected void tell(Component message, Component... messages) {
        Utils.tell(sender, message, messages);
    }

    /**
     * Tells a specified player a message.
     *
     * @param target   The player to tell.
     * @param message  The message to send.
     * @param messages An array of Strings that will be sent to the player, not required for this method to function.
     */
    protected void tellTarget(Player target, Component message, Component... messages) {
        Utils.tell(target, message, messages);
    }

    /**
     * Tells a specified player a message.
     *
     * @param target   The player to tell.
     * @param message  The message to send.
     * @param messages An array of Strings that will be sent to the player, not required for this method to function.
     */
    protected void tellTarget(Player target, String message, String... messages) {
        Utils.tell(target, message, messages);
    }

    /**
     * Utility method to match a player to the provided partial.
     *
     * @param partial The partial name of the player we want to find.
     * @return <code>null</code> if no found {@link Player}, otherwise a list of all found players matching the partial.
     */
    protected List<Player> matchPartialPlayers(final String partial) {
        if (Bukkit.matchPlayer(partial).size() == 0) {
            returnTell("<red>A player containing the partial '<yellow>" + partial + "<red>' could not be found!");
            return null;
        }

        if (sender != null && sender instanceof Player player) {
            return Bukkit.matchPlayer(partial).stream().filter(player::canSee).collect(Collectors.toList());
        }

        return Bukkit.matchPlayer(partial);
    }

    /**
     * Get one player from the partial provided.
     * This will send an 'error' message if more than one player is found matching the partial string.
     * This is done to ensure we are acting on the intended player.
     *
     * @param partial The partial name of the player we are searching for.
     * @return A {@link Player} instance of only one is found.
     * @see #matchPartialPlayers(String)
     */
    protected final Player getPlayerFromPartial(final String partial) {
        final List<Player> partialPlayers = matchPartialPlayers(partial);
        return getPlayer(partial, partialPlayers);
    }

    /**
     * Will attempt to match a player from the partial but will not 'error' out if no player is found with the partial.
     * <p>This will still 'error' out if more than one player is found matching the partial.</p>
     *
     * @param partial The partial name of the player we are searching for.
     * @return A {@link Player} instance of only one is found.
     * @see #matchPartialPlayers(String)
     */
    protected final @Nullable Player attemptGetPlayerFromPartial(final String partial) {
        final List<Player> partialPlayers = sender != null && sender instanceof Player player ?
                Bukkit.matchPlayer(partial).stream().filter(player::canSee).toList() : Bukkit.matchPlayer(partial);

        if (partialPlayers.size() == 0) {
            return null;
        }

        return getPlayer(partial, partialPlayers);
    }

    /**
     * Gets the player from a list of partial players.
     *
     * @param partial        The partial we used to detect players.
     * @param partialPlayers The List of partial players used.
     * @return A player if found or null otherwise.
     */
    @Nullable
    private Player getPlayer(String partial, @NotNull List<Player> partialPlayers) {
        if (partialPlayers.size() > 1) {
            List<String> names = partialPlayers.stream().map((p) -> {
                if (p != null) {
                    return p.getName();
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            returnTell(("<red>Multiple players have been found matching the partial name of <yellow>'" + partial + "'<red>! " +
                    "Please complete the player's name you wish to execute this command on!" +
                    "\n<red>Matches:" +
                    "\n<yellow>{list}").replace("{list}", StringUtils.join(names, ", ")));
            return null;
        }

        if (partialPlayers.size() == 0) {
            return null;
        }

        return Bukkit.getPlayer(partialPlayers.get(0).getUniqueId());
    }


    /**
     * Utility method to check a permission for the {@link CommandSender} of this command.
     * <p>
     *
     * @param permission The {@link IPermission} to check for this player.
     * @return <code>true</code> if the player has permission, <code>false</code> otherwise.
     * @see #checkPermissionSilent(IPermission)
     */
    public boolean checkPermission(final IPermission permission) {
        if (checkPermissionSilent(permission)) {
            return true;
        }

        tell(owningCommand.permissionMessage() == null ? StringUtil.color("<red>I'm sorry, but you do not have permission to perform this command.") : Bukkit.permissionMessage());
        return false;
    }

    /**
     * Utility method to silently check a permission of a {@link CommandSender}.
     *
     * @param permission The {@link IPermission} to check.
     * @return <code>true</code> if the sender of the command has permission, <code>false</code> otherwise.
     * @see #checkPermissionSilent(CommandSender, IPermission)
     */
    public boolean checkPermissionSilent(@NotNull IPermission permission) {
        return checkPermissionSilent(sender, permission);
    }

    /**
     * Utility method to silently check a permission of a {@link CommandSender}.
     *
     * @param permission The {@link IPermission} to check.
     * @param sender     The {@link CommandSender} for this command.
     * @return <code>true</code> if the sender of the command has permission, <code>false</code> otherwise.
     */
    public boolean checkPermissionSilent(final @NotNull CommandSender sender, @NotNull IPermission permission) {
        return sender.hasPermission(permission.getPermission());
    }

    /**
     * Get a list of online players that excludes the {@link CommandSender} if they are an instance of {@link Player}
     *
     * @param sender   The {@link CommandSender} of this command.
     * @param args     The arguments of this command.
     * @param argIndex The index that we are checking for a player name.
     * @return A list of all online players that match the arg index partial, ignoring the sender of the command if they are a {@link Player}
     * @see #getPlayers(String[], int)
     */
    public List<String> getPlayersExcludeSelf(@NotNull CommandSender sender, String[] args, int argIndex) {
        if (sender instanceof Player) {
            return Bukkit.getOnlinePlayers().stream().filter((player) -> !(((Player) sender).getUniqueId().equals(player.getUniqueId())))
                    .map(HumanEntity::getName).filter(name -> name.toLowerCase(Locale.ROOT).contains(args[argIndex].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        }
        return getPlayers(args, argIndex);
    }

    /**
     * Get a list of online players.
     *
     * @param args     The arguments of this command.
     * @param argIndex The index that we are checking for a player name.
     * @return A list of all online players that match the arg index partial
     */
    public List<String> getPlayers(String[] args, int argIndex) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((name) -> name.toLowerCase(Locale.ROOT)
                .contains(args[argIndex].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
    }

    /**
     * Ensures that an argument is actually a double.
     *
     * @param argsIndex    The number's location in the array.
     * @param errorMessage The message to send if it doesn't equal a number.
     * @return Returns the number (If valid)
     */
    protected double getDouble(int argsIndex, String errorMessage) {
        double number = 0;

        try {
            number = Double.parseDouble(args[argsIndex]);
        } catch (final NumberFormatException ex) {
            returnTell(errorMessage);
        }

        return number;
    }

    /**
     * Get an Integer.
     *
     * @param argsIndex    The index in the command args to get the int from.
     * @param errorMessage The message to be sent if unsuccessful.
     * @return The number if found or 0.
     */
    protected int getNumber(int argsIndex, String errorMessage) {
        int number = 0;

        try {
            number = Integer.parseInt(args[argsIndex]);
        } catch (final NumberFormatException ex) {
            returnTell(errorMessage);
        }

        return number;
    }

    /**
     * Get a Long.
     *
     * @param argsIndex    The index in the command args to get the long from.
     * @param errorMessage The message to be sent if unsuccessful.
     * @return The number if found or 0.
     */
    protected long getLong(int argsIndex, String errorMessage) {
        long number = 0;

        try {
            number = Long.parseLong(args[argsIndex]);
        } catch (final NumberFormatException ex) {
            returnTell(errorMessage);
        }

        return number;
    }

    /**
     * Used to pass tab complete for this command.
     *
     * @param sender The sender of this command.
     * @param args   The arguments for said command.
     * @return Returns a List that is used for tab complete.
     */
    public List<String> getTabComplete(final CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
