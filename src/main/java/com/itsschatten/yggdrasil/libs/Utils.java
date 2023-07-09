package com.itsschatten.yggdrasil.libs;

import com.google.common.io.ByteStreams;
import com.itsschatten.yggdrasil.libs.commands.CommandBase;
import com.itsschatten.yggdrasil.libs.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.libs.menus.utils.MenuListeners;
import com.itsschatten.yggdrasil.libs.menus.utils.TickingManager;
import com.itsschatten.yggdrasil.libs.wands.Wand;
import com.itsschatten.yggdrasil.libs.wands.WandListeners;
import com.itsschatten.yggdrasil.libs.wands.WandType;
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
    @Setter
    private static IMenuHolderManager manager;

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
     * Sets the plugin for which this class belongs too. Only sets the instance.
     *
     * @param instance The {@link JavaPlugin} it should be set too.
     */
    public static void justSetInstance(JavaPlugin instance) {
        Utils.instance = instance;
    }

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
        if (player.hasPermission("*")) return 100;
        int highest = 0;
        for (int x = 1; x <= 100; x++) {
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
                    if (trace[i].toString().toLowerCase().contains("net.prisontech") || trace[i].toString().toLowerCase().contains("com.itsschatten")) {
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
                // If the element contains net.shadowsmc or com.itsschatten highlight it in blue.
                if (stackTraceElement.toString().contains("net.prisontech") || stackTraceElement.toString().contains("com.itsschatten")) {
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
     * Sends a {@link String message} (or multiple) to the console.
     *
     * @param message  The first message that should be sent.
     * @param messages An array of messages that are then iterated through and sent to the console.
     */
    public static void log(@NotNull final String message, final String... messages) {
        tellConsole(StringUtil.color("[Yggdrasil] <yellow>").append(Component.text(message)));

        if (message.length() > 0)
            for (final String msg : messages)
                tellConsole(StringUtil.color("[Yggdrasil] <yellow>").append(Component.text(msg)));

    }

    /**
     * Sends a {@link String message} (or multiple) to the console with a DEBUG color scheme and prefix.
     *
     * @param message  The first message that should be sent.
     * @param messages An array of messages that are then iterated through and sent to the console.
     */
    public static void debugLog(@NotNull final String message, final String... messages) {
        if (debug) {
            tellConsole(StringUtil.color("[Yggdrasil] <red>[DEBUG] <reset>").append(Component.text(message)));

            if (message.length() > 0)
                for (final String msg : messages)
                    tellConsole(StringUtil.color("[Yggdrasil] <red>[DEBUG] <reset>").append(Component.text(msg)));
        }
    }

    /**
     * Sends a {@link String message} (or multiple) to the console with an ERROR color scheme and prefix.
     *
     * @param message  The first message that should be sent.
     * @param messages An array of messages that are then iterated through and sent to the console.
     */
    public static void logError(@NotNull String message, String... messages) {
        tellConsole(StringUtil.color("[Yggdrasil] <red>[ERROR] ").append(Component.text(message)));

        if (message.length() > 0)
            for (final String msg : messages)
                tellConsole(StringUtil.color("[Yggdrasil] <red>[ERROR] ").append(Component.text(msg)));
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
     * Sends a {@link Component} to the console, with no color.
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
     */
    public static @Nullable UUID convertBytesToUUID(final InputStream stream) {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        try {
            byteBuffer.put(ByteStreams.toByteArray(stream));
            byteBuffer.flip();
            return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
