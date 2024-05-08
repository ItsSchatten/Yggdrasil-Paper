package com.itsschatten.yggdrasil.commands;

import com.itsschatten.yggdrasil.IPermission;
import com.itsschatten.yggdrasil.StringUtil;
import com.itsschatten.yggdrasil.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The base command for all other commands.
 */
public abstract class CommandBase extends Command implements PluginIdentifiableCommand {

    /**
     * The sender of the command.
     */
    @Getter
    private CommandSender commandSender;

    /**
     * The arguments for the command.
     */
    private String[] args;

    /**
     * The command name.
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String commandLabel;

    /**
     * Subcommands.
     */
    private final Set<SubCommandBase> SUB_COMMANDS;

    /**
     * Used to construct the command.
     *
     * @param name The name of the command.
     */
    public CommandBase(final @NotNull String name) {
        super(name);
        SUB_COMMANDS = new HashSet<>();
    }

    /**
     * Registers a sub-command for this command.
     *
     * @param subCommands The {@link SubCommandBase}(s) to register for this command.
     */
    public final void registerSubCommands(final SubCommandBase... subCommands) {
        SUB_COMMANDS.addAll(List.of(subCommands));
    }

    /**
     * Obtains a sub command based on its name.
     *
     * @param name the name of the {@link SubCommandBase}
     */
    public final SubCommandBase getSubCommand(final String name) {
        return SUB_COMMANDS.stream().filter((subCommand) -> subCommand.getId().equalsIgnoreCase(name) || subCommand.getAliases().contains(name.toLowerCase())).findFirst().orElse(null);
    }

    /**
     * Gets the {@link SubCommandBase} {@link Set}.
     *
     * @return Return's a {@link HashSet} of all registered {@link SubCommandBase sub commands.}, aka returns the variable {@link #SUB_COMMANDS}.
     */
    public final Set<SubCommandBase> getSubCommands() {
        return SUB_COMMANDS;
    }

    /**
     * Returns a sorted {@link List} of all registered {@link SubCommandBase sub commands} description {@link String strings}.
     *
     * @return Returns a sorted list, generated from a stream.
     */
    public final List<String> getSubCommandDescriptions() {
        return SUB_COMMANDS.stream().sorted().map(SubCommandBase::description).filter(description -> !description.isEmpty()).toList();
    }

    /**
     * Returns a sorted {@link List} of all registered {@link SubCommandBase sub commands} description {@link Component components}.
     *
     * @return Returns a sorted list, generated from a stream.
     */
    public final List<Component> getSubCommandDescriptionComponents() {
        return SUB_COMMANDS.stream().sorted().filter((cmd) -> {
            if (cmd.descriptionComponent() instanceof TextComponent text) {
                return !text.content().isEmpty();
            }

            return false;
        }).map(SubCommandBase::descriptionComponent).toList();
    }

    /**
     * Sends the help message with all sub commands.
     *
     * @param start The message to send first.
     */
    public final void sendHelpMessage(final String start) {
        sendHelpMessage(StringUtil.color(start));
    }

    /**
     * Sends the help message with all sub commands.
     *
     * @param component The component to send first.
     */
    public final void sendHelpMessage(final Component component) {
        tell(component, getSubCommandDescriptionComponents());
    }

    /**
     * Used to run the command for an unspecific command sender.
     *
     * @param sender The sender of the command.
     * @param args   The arguments for the command.
     */
    public abstract void runCommandSender(final CommandSender sender, final String[] args);

    /**
     * Used to run the command for a {@link Player}.
     *
     * @param player The player that issued this command.
     * @param args   The arguments for the command.
     */
    public void run(final Player player, final String[] args) {
        runCommandSender(player, args);
    }

    /**
     * Sets the permission for the command.
     *
     * @param permission The {@link IPermission} to set.
     */
    public void setPermission(@NotNull IPermission permission) {
        super.setPermission(permission.getPermission());
    }

    /**
     * Returns the usage of this command.
     */
    @NotNull
    public Component usage() {
        return StringUtil.color(super.usageMessage);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public final Plugin getPlugin() {
        return Utils.getInstance();
    }

    /**
     * Executes the command.
     *
     * @param commandSender The sender of the command.
     * @param commandName   The name of the command executed.
     * @param args          The arguments for the command.
     * @return Will always return true to avoid sending any usage information, the plugin should handle that.
     */
    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String commandName, @NotNull String[] args) {
        if (commandSender instanceof ConsoleCommandSender || testPermission(commandSender)) {
            setCommandLabel(commandName.replace(Utils.getInstance().getName() + ":", "")); // Used to get the proper label for the command.
            this.commandSender = commandSender; // Our command sender.
            this.args = args; // The arguments of this command.

            try {
                // Check if we have at minimum one argument
                if (args.length >= 1) {
                    // Attempt to get a sub command and check permission.
                    final SubCommandBase subCommand = getSubCommand(args[0]);
                    if (subCommand != null && subCommand.testPermission(commandSender)) {
                        // Execute the subcommand.
                        subCommand.execute(commandSender, Arrays.copyOfRange(args, 1, args.length));
                        return true;
                    }
                }

                if (commandSender instanceof Player player) { // Check if the sender is a player.
                    run(player, args);
                    // If so, pass a player object to allow easier access to player with safe casting already done.
                } else
                    runCommandSender(commandSender, args); // If not a player execute the command as a normal command sender.
            } catch (final ReturnedCommandException ex) {
                // Custom exception used to end the execution of a command.
                tell(ex.tellMessage);
                return true;
            } catch (final IllegalArgumentException ex) {
                ex.printStackTrace();
                Utils.logError("There was an error during tab completion for the command " + commandName + "!");
                tell("<red>An error occurred while tab completing this command!" + (ex.getCause() == null ? ex.getMessage() : ex.getCause()));
            } catch (final Exception ex) {
                ex.printStackTrace();
                Utils.logError("An error occurred that couldn't be caught!");
                tell("<red>Some error occurred while attempting to run the command! " + (ex.getCause() == null ? ex.getMessage() : ex.getCause()));
            }
        }
        return true; // Return true; we should handle the usage messages if our logic fails.
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
     * Check if the command has an appropriate amount of arguments.
     *
     * @param minLength The minimum length the command must have to execute.
     * @param message   The message that will be sent if the argument length does not match.
     */
    protected void checkArgs(int minLength, String message) {
        if (args.length < minLength)
            returnTell(message);
    }

    /**
     * Throws a {@link ReturnedCommandException} effectively stopping execution of the command.
     *
     * @param message The message that should be sent to the {@link CommandSender}.
     */
    protected void returnTell(final String message) throws ReturnedCommandException {
        throw new ReturnedCommandException(message);
    }

    /**
     * Sends a message to the sender of the command.
     *
     * @param message  The message to send.
     * @param messages An array of Strings that will be sent to the player, not required for this method to function.
     */
    protected void tell(String message, String... messages) {
        Utils.tell(commandSender, message, messages);
    }

    /**
     * Sends a message to the sender of the command.
     *
     * @param message  The message to send.
     * @param messages An array of {@link Component components} that will be sent to the player, not required for this method to function.
     */
    protected void tell(Component message, Component... messages) {
        Utils.tell(commandSender, message, messages);
    }

    /**
     * Sends a message to the sender of the command.
     *
     * @param message  The message to send.
     * @param messages An iterable of Strings that will be sent to the player, not required for this method to function.
     */
    protected void tell(String message, Iterable<String> messages) {
        Utils.tell(commandSender, message, messages);
    }

    /**
     * Sends a message to the sender of the command.
     *
     * @param message  The message to send.
     * @param messages An iterable of {@link Component components} that will be sent to the player, not required for this method to function.
     */
    protected void tell(Component message, Iterable<Component> messages) {
        Utils.tell(commandSender, message, messages);
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
        if (Bukkit.matchPlayer(partial).isEmpty()) {
            returnTell("<red>A player containing the partial '<yellow>" + partial + "<red>' could not be found!");
            return null;
        }

        if (getCommandSender() != null && getCommandSender() instanceof Player player) {
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
        final List<Player> partialPlayers = getCommandSender() != null && getCommandSender() instanceof Player player ?
                Bukkit.matchPlayer(partial).stream().filter(player::canSee).toList() : Bukkit.matchPlayer(partial);

        if (partialPlayers.isEmpty()) {
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

        if (partialPlayers.isEmpty()) {
            return null;
        }

        return Bukkit.getPlayer(partialPlayers.get(0).getUniqueId());
    }


    /**
     * Utility method to check a permission for the {@link CommandSender} of this command.
     * <p>
     * If the player doesn't have the permission it will also send a message to the sender of the command with the {@link #permissionMessage(String)} of this command.
     *
     * @param permission The {@link IPermission} to check for this player.
     * @return <code>true</code> if the player has permission, <code>false</code> otherwise.
     * @see #checkPermissionSilent(IPermission)
     */
    public boolean checkPermission(final IPermission permission) {
        if (checkPermissionSilent(permission)) {
            return true;
        }

        tell(permissionMessage() == null ? StringUtil.color("<red>I'm sorry, but you do not have permission to perform this command.") : permissionMessage());
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
        return checkPermissionSilent(commandSender, permission);
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
     * Overridden from superclass to translate colors codes in a message.
     *
     * @param permissionMessage The message we wish to send if the executor of the command does not have permission.
     */
    public void permissionMessage(@Nullable String permissionMessage) {
        super.permissionMessage(StringUtil.color(permissionMessage));
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
     * Forcefully send all subcommands for this command.
     * If the {@link #commandSender} lacks permission to use a subcommand it is shown in red and italic.
     */
    public final void forceSendSubCommands() {
        returnTell("<gray>" + StringUtils.join(SUB_COMMANDS.stream().sorted().map((command) -> (command.testPermission(commandSender) ? "" : "<red><i>") + command.getId() + (command.testPermission(commandSender) ? "" : "</red></i>")).toList(), "<dark_gray>,</dark_gray> "));
    }

    /**
     * Sends all sub commands for this command that the {@link #commandSender} has permission for.
     */
    public final void sendSubcommands() {
        returnTell("<gray>" + StringUtils.join(SUB_COMMANDS.stream().sorted().filter((command) -> command.testPermission(commandSender)).map(SubCommandBase::getId).toList(), "<dark_gray>,</dark_gray> "));
    }

    /**
     * Used to pass tab complete for this command.
     *
     * @param sender  The sender of this command.
     * @param command The name of the command used.
     * @param args    The arguments for said command.
     * @return Returns a List that is used for tab complete.
     */
    @Contract(pure = true)
    public @NotNull List<String> getTabComplete(CommandSender sender, final String command, final String[] args) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     *
     * @param sender Source object which is executing this command
     * @param alias  the alias being used
     * @param args   All arguments passed to the command, split via ' '
     * @return a list of tab-completions for the specified arguments. This will never be null. The List may be immutable.
     * @throws IllegalArgumentException if sender, alias, or args is null
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public final @Unmodifiable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        // Store what we can tab.
        final List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            // Proper tab-complete based if the args contain part of the sub command.
            tab.addAll(SUB_COMMANDS.stream().filter((subCommand) -> sender.hasPermission(subCommand.getPermission())).map(SubCommandBase::getId).filter((name) -> name.toLowerCase().contains(args[0].toLowerCase())).toList());
            SUB_COMMANDS.stream().filter((subCommand) -> sender.hasPermission(subCommand.getPermission())).map(SubCommandBase::getAliases)
                    .forEach((list) -> list.stream().filter((name) -> name.toLowerCase().contains(args[0].toLowerCase())).forEach(tab::add));
        }

        // Check if we are over two arguments.
        // If we are, attempt to find a sub command with args[0].
        if (args.length >= 2) {
            final SubCommandBase command = getSubCommand(args[0]);
            // Ensure that command is not null.
            if (command != null) {
                // Add the sub-commands tab complete.
                tab.addAll(command.getTabComplete(sender, Arrays.copyOfRange(args, 1, args.length)));
            }
        }

        // Finally, add the general tab complete for this command.
        tab.addAll(getTabComplete(sender, alias, args));
        return tab;
    }

    @Override
    public String toString() {
        return "CommandBase{" +
                "commandSender=" + commandSender +
                ", args=" + Arrays.toString(args) +
                ", commandLabel='" + commandLabel + '\'' +
                ", SUB_COMMANDS=" + SUB_COMMANDS +
                ", description='" + description + '\'' +
                ", usageMessage='" + usageMessage + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandBase that = (CommandBase) o;
        return Objects.equals(commandSender, that.commandSender) && Arrays.equals(args, that.args) && Objects.equals(commandLabel, that.commandLabel) && Objects.equals(SUB_COMMANDS, that.SUB_COMMANDS);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(commandSender, commandLabel, SUB_COMMANDS);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    /**
     * Our exception that is thrown when {@link #returnTell(String)} is called.
     */
    @RequiredArgsConstructor
    public static final class ReturnedCommandException extends RuntimeException {
        /**
         * The serial version UID of this exception.
         */
        @Serial
        private static final long serialVersionUID = -5110784502530626507L;

        /**
         * The message that should be sent to the {@link CommandSender} of the command.
         */
        private final String tellMessage;
    }
}
