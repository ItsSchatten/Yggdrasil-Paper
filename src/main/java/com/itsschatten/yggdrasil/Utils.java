package com.itsschatten.yggdrasil;

import com.google.common.io.ByteStreams;
import com.itsschatten.yggdrasil.commands.CommandBase;
import com.itsschatten.yggdrasil.menus.manager.DefaultPlayerManager;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolderManager;
import com.itsschatten.yggdrasil.menus.utils.MenuListeners;
import com.itsschatten.yggdrasil.menus.utils.TickingManager;
import com.itsschatten.yggdrasil.wands.Wand;
import com.itsschatten.yggdrasil.wands.WandListeners;
import com.itsschatten.yggdrasil.wands.WandType;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Logger;

/**
 * Yet another utils class.
 */
public class Utils {

    /**
     * The instance of the plugin this class belongs too.
     */
    @Getter
    private static JavaPlugin instance;

    /**
     * Prisoner manager, used for menus.
     */
    @Getter
    private static IMenuHolderManager manager;

    private static DefaultPlayerManager defaultPlayerManager;

    /**
     * Are we in debug?
     */
    @Getter
    @Setter
    private static boolean debug;

    /**
     * A set of all {@link Wand}s
     */
    @Getter
    private static Set<Wand> wands;

    /**
     * Sets the plugin for which this class belongs too, if passed instance is null we also set the wands set to null.
     *
     * @param instance The {@link JavaPlugin} it should be set too.
     */
    public static void setInstance(JavaPlugin instance) {
        Utils.instance = instance;

        if (instance == null) {
            wands = null;
        }
    }

    /**
     * Utility method to quickly create a {@link NamespacedKey} based on this classes {@link JavaPlugin instance}.
     *
     * @param value The name of this tag.
     * @return A new {@link NamespacedKey} with the {@link JavaPlugin java plugin instance} of this class.
     */
    @Contract("_ -> new")
    public static @NotNull NamespacedKey makeKey(final String value) {
        return new NamespacedKey(getInstance(), value);
    }

    /**
     * Sets the manager instance and unregisters the default player manager if it's registered.
     *
     * @param manager The manager instance to set as the new manager.
     */
    public static void setManager(IMenuHolderManager manager) {
        if (defaultPlayerManager != null) {
            HandlerList.unregisterAll(defaultPlayerManager);
            Utils.defaultPlayerManager = null;
        }

        Utils.manager = manager;
    }

    /**
     * Used to register the MenuListener to the plugin.
     * <p>
     * This <b>MUST</b> be run in-order to use the menu system or wands, otherwise just use {@link Utils#setInstance(JavaPlugin)}.
     *
     * @param methodInstance The instance that we are setting the instance too.
     * @param registerMenu   If true we will register the {@link MenuListeners}
     * @param registerWands  If true we will register the wands Set and register the {@link WandListeners}
     */
    public static void setInstance(@NotNull JavaPlugin methodInstance, boolean registerMenu, boolean registerWands) {
        setInstance(methodInstance);

        if (registerMenu) {
            methodInstance.getServer().getPluginManager().registerEvents(new MenuListeners(), methodInstance);

            // Register the default player manager, so we can use menus "out of the box."
            // This also registers the class as a listener
            // to remove the player from the player map as they leave the server.
            final DefaultPlayerManager defaultPlayerManager = new DefaultPlayerManager();
            Utils.defaultPlayerManager = defaultPlayerManager;
            methodInstance.getServer().getPluginManager().registerEvents(defaultPlayerManager, methodInstance);
            setManager(defaultPlayerManager);

            TickingManager.beginTicking();
        }

        if (registerWands) {
            wands = new HashSet<>();
            methodInstance.getServer().getPluginManager().registerEvents(new WandListeners(), methodInstance);
        }
    }

    /**
     * Registers the provided {@link Command}(s) directly to the server's command map.
     *
     * @param commands The commands to register.
     */
    public static void registerCommands(CommandBase... commands) {
        Validate.notNull(commands, "Provided commands must not equal null."); // checking if the commands array provided isn't null, if it is it will error out.
        Validate.isTrue(commands.length > 0, "You must provide commands to register."); // Ensuring that the length of the commands array is larger than 0.

        for (CommandBase command : commands) {
            if (!Bukkit.getServer().getCommandMap().register(getInstance().getName(), command)) {
                Utils.debugLog("Command '" + command.getName() + "' is already registered, command registered with fallback prefix.");
            } else
                Utils.debugLog("Successfully registered command '" + command.getName() + "'!");
        }
    }

    /**
     * Utility method to obtain a number from a permission node.
     *
     * @param player The player we want to get the number for.
     * @param prefix The prefix of the permission node we want to find. (EX: yggdrasil.storage)
     * @return The number found on the permission, if player contains the <code>*</code> permission returns <code>99</code>
     */
    public static int getNumberFromPermission(final @NotNull Player player, final String prefix) {
        return getNumberFromPermission(player, prefix, 100);
    }

    /**
     * Utility method to obtain a number from a permission node.
     *
     * @param player The player we want to get the number for.
     * @param prefix The prefix of the permission node we want to find. (EX: yggdrasil.storage)
     * @param max    The maximum number to return, also used in the loop.
     * @return The number found on the permission, if player contains the <code>*</code> permission returns <code>99</code>
     */
    public static int getNumberFromPermission(final @NotNull Player player, final String prefix, int max) {
        if (player.hasPermission("*")) return max;
        int highest = 0;
        for (int x = 1; x <= max; x++) {
            if (player.hasPermission(prefix + x)) {
                highest = x;
            }
        }
        return highest;
    }

    /**
     * Sends a message of an error if the {@link Player} has the developer permission.
     *
     * @param user      The player to send the message to.
     * @param throwable The error to send.
     * @see #sendDeveloperErrorMessage(Player, Throwable)
     */
    public static void sendDeveloperErrorMessage(final @NotNull IMenuHolder user, final Throwable throwable) {
        sendDeveloperErrorMessage(user.getBase(), throwable);
    }

    /**
     * Sends a message of an error if the {@link Player} has the developer permission.
     *
     * @param player    The player to send the message to.
     * @param throwable The error to send.
     * @see #sendDeveloperErrorMessage(IMenuHolder, Throwable)
     */
    public static void sendDeveloperErrorMessage(final @NotNull Player player, final Throwable throwable) {
        // If the user has the developer permission, send them information on the error that occurred.
        if (player.hasPermission(getInstance().getName() + ".developer")) {
            // The message that we want to send.
            Component message = StringUtil.color("""
                    <red>Some error occurred while attempting to handle your menu:
                    <gray>Cause: <yellow>{cause}
                    <gray>Message: <yellow>{message}
                    <gray>Stack Trace:
                    <reset>
                    """.replace("{cause}", throwable.getCause() == null ? "N/A" : throwable.getCause().toString())
                    .replace("{message}", throwable.getMessage() == null ? "N/A" : throwable.getMessage()));

            // Stack trace to send in chat.
            final StringBuilder startBuilder = new StringBuilder();
            final StackTraceElement[] trace = throwable.getStackTrace();
            if (trace.length > 0) {
                for (int i = 0; i < 5; i++) {
                    if (trace[i].toString().toLowerCase().contains("com.itsschatten")) {
                        startBuilder.append("<aqua>").append(trace[i]).append("<red>\n");
                        continue;
                    }
                    startBuilder.append("<red>").append(trace[i]).append("\n");
                }
            }

            // Append the start of the trace to the main message.
            final String startOfTrace = startBuilder.toString();
            message = message.append(StringUtil.color(startOfTrace));

            // Use this for a hover event on the message to show the full stacktrace in the menu.
            final StringBuilder endBuilder = new StringBuilder();
            for (StackTraceElement stackTraceElement : trace) {
                // If the element contains com.itsschatten highlight it in blue.
                if (stackTraceElement.toString().contains("com.itsschatten")) {
                    endBuilder.append("<aqua>").append(stackTraceElement).append("<gray>\n");
                    continue;
                }
                // Otherwise, leave it as gray and append the message.
                endBuilder.append(stackTraceElement).append("\n");
            }
            // Adds the hover event and finally sends the message to the viewer of this menu.
            final String hoverStack = endBuilder.toString();
            message = message.hoverEvent(StringUtil.color("<gray>" + hoverStack).asHoverEvent());

            player.sendMessage(message);
        }
    }

    /**
     * Credits to <a href="https://gist.github.com/graywolf336/8153678">this gist</a> for this method.
     *
     * @param items The array of items that is to be converted.
     * @return Returns the encoded {@link ItemStack} array.
     * @throws IllegalStateException If unable to save the ItemStack this is thrown.
     */
    public static @NotNull String convertItemStackToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (final ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException Thrown if unable to decode a class type.
     */
    @Contract("null -> new")
    public static ItemStack @NotNull [] getItemStackFromBase64(String data) throws IOException {
        if (data == null) return new ItemStack[0];
        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            final ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Gets the range from the values.
     *
     * @param value The value.
     * @param min   The minimum value
     * @param max   The max value.
     * @return The found value.
     */
    public static int range(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * @return Returns the instance's logger.
     */
    public static @NotNull Logger getLogger() {
        return getInstance().getLogger();
    }

    /**
     * Sends a {@link String message} (or multiple) to the console with the INFO level.
     *
     * @param message  The first message that should be sent.
     * @param messages An array of messages that are then iterated through and sent to the console.
     */
    public static void log(@NotNull final String message, final String... messages) {
        if (instance == null) {
            throw new NullPointerException("Cannot log messages with a null plugin instance.");
        }

        getLogger().info("[Yggdrasil] " + message);

        if (!message.isEmpty())
            for (final String msg : messages)
                getLogger().info("[Yggdrasil] " + msg);


    }

    /**
     * Sends a {@link String message} (or multiple) to the console with the INFO level and an added [DEBUG].
     *
     * @param message  The first message that should be sent.
     * @param messages An array of messages that are then iterated through and sent to the console.
     */
    public static void debugLog(@NotNull final String message, final String... messages) {
        if (instance == null) {
            throw new NullPointerException("Cannot log messages with a null plugin instance.");
        }

        if (debug) {
            getLogger().info("[Yggdrasil] [DEBUG] " + message);

            if (!message.isEmpty())
                for (final String msg : messages)
                    getLogger().info("[Yggdrasil] [DEBUG] " + msg);
        }
    }

    /**
     * Sends a {@link String message} (or multiple) to the console with the WARNING level.
     *
     * @param message  The first message that should be sent.
     * @param messages An array of messages that are then iterated through and sent to the console.
     */
    public static void logWarning(@NotNull String message, String... messages) {
        if (instance == null) {
            throw new NullPointerException("Cannot log messages with a null plugin instance.");
        }

        getLogger().warning("[Yggdrasil] " + message);

        if (!message.isEmpty())
            for (final String msg : messages)
                getLogger().warning("[Yggdrasil] " + msg);
    }

    /**
     * Sends a {@link String message} (or multiple) to the console with the ERROR level.
     *
     * @param message  The first message that should be sent.
     * @param messages An array of messages that are then iterated through and sent to the console.
     */
    public static void logError(@NotNull String message, String... messages) {
        if (instance == null) {
            throw new NullPointerException("Cannot log messages with a null plugin instance.");
        }

        getLogger().severe("[Yggdrasil] " + message);

        if (!message.isEmpty())
            for (final String msg : messages)
                getLogger().severe("[Yggdrasil] " + msg);
    }

    /**
     * Quickly log a {@link Throwable} error to console.
     *
     * @param error The error we wish to send to console.
     */
    public static void logError(@NotNull Throwable error) {
        logError("---------------- [ ERROR LOG START ] ----------------");
        logError("ERROR TYPE: " + error);
        logError("CAUSE: " + (error.getCause() == null ? "N/A" : error.getCause().getMessage()));
        logError("MESSAGE: " + (error.getMessage() == null ? "" : error.getMessage()));
        for (final StackTraceElement elm : error.getStackTrace()) {
            logError(elm.toString());
        }
        logError("----------------- [ ERROR LOG END ] -----------------");
    }

    /**
     * Tell the specified {@link CommandSender} (either a player, console, or command block) the message(s) supplied.
     *
     * @param toWhom   The CommandSender we should send the message(s) to.
     * @param message  The first message that should be sent to the supplied CommandSender.
     * @param messages An array of messages that are then iterated through and sent to the supplied CommandSender.
     */
    public static void tell(@NotNull CommandSender toWhom, @NotNull String message, String... messages) {
        if (!message.isBlank())
            toWhom.sendMessage(StringUtil.color(message));

        for (final String msg : messages) {
            if (msg.isBlank()) continue;
            toWhom.sendMessage(StringUtil.color(msg));
        }
    }

    /**
     * Tell the specified {@link CommandSender} (either a player, console, or command block) the message(s) supplied.
     *
     * @param toWhom   The CommandSender we should send the message(s) to.
     * @param message  The first {@link Component} that should be sent to the supplied CommandSender.
     * @param messages An array of messages that are then iterated through and sent to the supplied CommandSender.
     */
    public static void tell(@NotNull CommandSender toWhom, @Nullable Component message, Component... messages) {
        if (message != null)
            toWhom.sendMessage(message);

        for (final Component msg : messages) {
            if (msg == null) continue;
            toWhom.sendMessage(msg);
        }
    }

    /**
     * Tell the specified {@link CommandSender} (either a player, console, or command block) the message(s) supplied.
     *
     * @param toWhom   The CommandSender we should send the message(s) to.
     * @param message  The first message that should be sent to the supplied CommandSender.
     * @param messages An iterable of messages that are then iterated through and sent to the supplied CommandSender.
     */
    public static void tell(@NotNull CommandSender toWhom, @NotNull String message, Iterable<String> messages) {
        if (!message.isBlank())
            toWhom.sendMessage(StringUtil.color(message));

        for (final String msg : messages) {
            if (msg.isBlank()) continue;
            toWhom.sendMessage(StringUtil.color(msg));
        }
    }

    /**
     * Tell the specified {@link CommandSender} (either a player, console, or command block) the message(s) supplied.
     *
     * @param toWhom   The CommandSender we should send the message(s) to.
     * @param message  The first {@link Component} that should be sent to the supplied CommandSender.
     * @param messages An iterable of messages that are then iterated through and sent to the supplied CommandSender.
     */
    public static void tell(@NotNull CommandSender toWhom, @Nullable Component message, Iterable<Component> messages) {
        if (message != null)
            toWhom.sendMessage(message);

        for (final Component msg : messages) {
            if (msg == null) continue;
            toWhom.sendMessage(msg);
        }
    }

    /**
     * Sends a {@link Component} to the console.
     *
     * @param message The message to send.
     */
    private static void tellConsole(final Component message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * Sort a map based on its values.
     *
     * @param map The {@link Map} we should sort.
     * @param <K> The key!
     * @param <V> The value!
     * @return Returns a new {@link LinkedHashMap} sorted by our values.
     */
    public static <K, V extends Comparable<? super V>> @NotNull Map<K, V> sortMapByValue(@NotNull Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        final Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }


    /**
     * Get both selection {@link Location locations} for a {@link Player player}.
     * <p>
     * <b>It should be noted that this will not work properly with a {@link WandType#SINGLE_SELECTION single selection wand},</b>
     * <b>as the locations maps are purged of the selectors UUID once the selection has been completed.</b>
     *
     * @param selectorUUID The {@link UUID uuid} of the selector.
     * @return An {@link ArrayList} of the selector's locations.
     */
    public static @NotNull List<Location> getSelectionLocations(@NotNull UUID selectorUUID) {
        if (wands == null) {
            throw new RuntimeException("Cannot get selection locations when wands have not been registered.");
        }

        final List<Location> locationList = new ArrayList<>();
        if (WandListeners.getFirstLocationMap().containsKey(selectorUUID)) {
            locationList.add(WandListeners.getFirstLocationMap().get(selectorUUID));
        }

        if (WandListeners.getSecondLocationMap().containsKey(selectorUUID)) {
            locationList.add(WandListeners.getSecondLocationMap().get(selectorUUID));
        }

        return locationList;
    }

    /**
     * Converts a {@link UUID} to a byte stream.
     *
     * @param uuid The uuid we want to convert.
     * @return A new {@link ByteArrayInputStream} with our converted bytes.
     */
    @Contract("_ -> new")
    public static @NotNull InputStream convertUUIDToByteStream(final @NotNull UUID uuid) {
        final byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return new ByteArrayInputStream(bytes);
    }

    /**
     * Converts a {@link InputStream} filled with bytes to a UUID.
     *
     * @param stream The stream we should use for our conversion.
     * @return A {@link UUID} if successful, otherwise <code>null</code>.
     * @throws IOException thrown by ByteStreams.toByteArray if an I/O error occurs.
     */
    @Contract("_ -> new")
    public static @NotNull UUID convertBytesToUUID(final InputStream stream) throws IOException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put(ByteStreams.toByteArray(stream));
        byteBuffer.flip();
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}
