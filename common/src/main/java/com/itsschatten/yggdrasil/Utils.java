package com.itsschatten.yggdrasil;

import com.google.common.io.ByteStreams;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
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
public final class Utils {

    private final static PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
    private final static MiniMessage MINI_MESSAGE = StringUtil.builtMiniMessage();

    /**
     * The instance of the plugin this class belongs too.
     */
    @Getter
    private static JavaPlugin instance;

    /**
     * Are we in debug?
     */
    @Getter
    @Setter
    private static boolean debug;

    /**
     * Sets the plugin for which this class belongs too,
     * if passed instance is {@code null}; we also set the {@code wands} Set to {@code null}.
     *
     * @param instance The {@link JavaPlugin} it should be set too.
     */
    public static void setInstance(@Nullable JavaPlugin instance) {
        Utils.instance = instance;
    }

    /**
     * Utility method to quickly create a {@link NamespacedKey} based on this classes {@link JavaPlugin instance}.
     *
     * @param value The name of this tag.
     * @return A new {@link NamespacedKey} with the {@link JavaPlugin java plugin instance} of this class.
     */
    @Contract("_ -> new")
    public static @NotNull NamespacedKey makeNamespacedKey(@NotNull final String value) {
        return new NamespacedKey(getInstance(), value);
    }

    /**
     * Utility method to quickly create a {@link Key} based on this classes {@link JavaPlugin instance}.
     *
     * @param value The name of this tag.
     * @return A new {@link Key} with the {@link JavaPlugin java plugin instance} of this class.
     */
    public static @NotNull Key makeKey(@Pattern("[a-z0-9_\\-./]+") @Subst("invalid_value") final String value) {
        @Subst("yggdrasil") String name = getInstance().getName();

        return Key.key(name, value);
    }

    /**
     * Utility method to get a number from a permission node.
     *
     * @param player The player we want to get the number for.
     * @param prefix The prefix of the permission node we want to find. (EX: {@code yggdrasil.storage})
     * @return The number found on the permission, if player contains the <code>*</code> permission returns <code>99</code>
     */
    public static int getNumberFromPermission(final @NotNull Player player, final String prefix) {
        return getNumberFromPermission(player, prefix, 100);
    }

    /**
     * Utility method to get a number from a permission node.
     *
     * @param player The player we want to get the number for.
     * @param prefix The prefix of the permission node we want to find. (EX: {@code yggdrasil.storage})
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
     * @param player    The player to send the message to.
     * @param throwable The error to send.
     */
    public static void sendDeveloperErrorMessage(final @NotNull Player player, final Throwable throwable) {
        // If the user has the developer permission, send them information on the error that occurred.
        if (player.hasPermission(getInstance().getName() + ".developer")) {
            // The message that we want to send.
            Component message = StringUtil.color("""
                    <red>Some error occurred while attempting that action:
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
                    if (trace[i].toString().toLowerCase().contains("com.itsschatten") || trace[i].toString().toLowerCase().contains(instance.getClass().getPackageName())) {
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
                if (stackTraceElement.toString().contains("com.itsschatten") || stackTraceElement.toString().contains(instance.getClass().getPackageName())) {
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
     * Converts an array of {@link ItemStack}s into a Base64 encoded {@link String}.
     * <br/>
     * Credits to <a href="https://gist.github.com/graywolf336/8153678">this gist</a> for this method.
     *
     * @param items The array of items that is to be converted.
     * @return Returns the encoded {@link ItemStack} array.
     * @throws IllegalStateException If unable to save the ItemStack, this is thrown.
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
     * Converts a Base64 encoded {@link String} into an array of {@link ItemStack}.
     * Gets an array of ItemStacks from Base64 string.
     *
     * @param data Base64 string to convert to {@link ItemStack} array.
     * @return {@link ItemStack} array created from the Base64 string, may be empty.
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
     * Get the {@link #instance} component logger.
     *
     * @return Returns the component logger for the instance.
     * @throws UnsupportedOperationException Thrown if the plugin instance is null.
     */
    public static @NotNull ComponentLogger getLogger() {
        if (Utils.instance == null) {
            throw new UnsupportedOperationException("Plugin instance is null and attempted to get it's logger!");
        }
        return Utils.instance.getComponentLogger();
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
        log("[Yggdrasil]", message, messages);
    }

    /**
     * Internal method to log a message to the console with a provided prefix.
     *
     * @param prefix   The prefix.
     * @param message  The main message.
     * @param messages The secondary messages.
     */
    private static void log(String prefix, String message, String... messages) {
        getLogger().info("{} {}", prefix, message);

        if (!message.isEmpty())
            for (final String msg : messages)
                getLogger().info("{} {}", prefix, msg);
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
            log("[Yggdrasil] [DEBUG]", message, messages);
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

        getLogger().warn("[Yggdrasil] {}", message);

        if (!message.isEmpty())
            for (final String msg : messages)
                getLogger().warn("[Yggdrasil] {}", msg);
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

        getLogger().error("[Yggdrasil] {}", message);

        if (!message.isEmpty())
            for (final String msg : messages)
                getLogger().error("[Yggdrasil] {}", msg);
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
     * Tell the sender of a command a translatable message
     * using either provided lang files in resource packs or server-side translation files.
     *
     * @param source       The command source to send the message to.
     * @param translatable The key for the translatable message.
     * @param placeholders The placeholders for the message.
     */
    public static void translate(@NotNull CommandSourceStack source, String translatable, Component... placeholders) {
        translate(source.getSender(), translatable, placeholders);
    }

    /**
     * Tell the sender of a command a translatable message
     * using either provided lang files in resource packs or server-side translation files.
     *
     * @param context      The command context to send the message to.
     * @param translatable The key for the translatable message.
     * @param placeholders The placeholders for the message.
     */
    public static void translate(@NotNull CommandContext<CommandSourceStack> context, String translatable, Component... placeholders) {
        translate(context.getSource().getSender(), translatable, placeholders);
    }

    /**
     * Tell the audience a translatable message
     * using either provided lang files in resource packs or server-side translation files.
     *
     * @param audience     The audience to send the message to.
     * @param translatable The key for the translatable message.
     * @param placeholders The placeholders for the message.
     */
    public static void translate(@NotNull Audience audience, String translatable, Component... placeholders) {
        final String message = PLAIN.serialize(GlobalTranslator.render(Component.translatable(translatable,
                        Arrays.stream(placeholders).map(MINI_MESSAGE::serialize).map(Component::text).toList()),
                audience.get(Identity.LOCALE).orElse(Locale.ENGLISH)));

        audience.sendMessage(StringUtil.color(message));
    }

    /**
     * Tell the sender of a command a translatable message
     * using either provided lang files in resource packs or server-side translation files.
     *
     * @param source       The command source to send the message to.
     * @param translatable The key for the translatable message.
     * @param placeholders The placeholders for the message.
     */
    public static void translate(@NotNull CommandSourceStack source, String translatable, String... placeholders) {
        translate(source.getSender(), translatable, placeholders);
    }

    /**
     * Tell the sender of a command a translatable message
     * using either provided lang files in resource packs or server-side translation files.
     *
     * @param context      The command context to send the message to.
     * @param translatable The key for the translatable message.
     * @param placeholders The placeholders for the message.
     */
    public static void translate(@NotNull CommandContext<CommandSourceStack> context, String translatable, String... placeholders) {
        translate(context.getSource().getSender(), translatable, placeholders);
    }

    /**
     * Tell the audience a translatable message
     * using either provided lang files in resource packs or server-side translation files.
     *
     * @param audience     The audience to send the message to.
     * @param translatable The key for the translatable message.
     * @param placeholders The placeholders for the message.
     */
    public static void translate(@NotNull Audience audience, String translatable, String... placeholders) {
        final String message = PlainTextComponentSerializer.plainText()
                .serialize(GlobalTranslator.render(Component.translatable(translatable, Arrays.stream(placeholders).map(StringUtil::color).toList()),
                        audience.get(Identity.LOCALE).orElse(Locale.ENGLISH)));

        audience.sendMessage(StringUtil.color(message));
    }

    /**
     * Tell the sender of a command a translatable message
     * using either provided lang files in resource packs or server-side translation files.
     *
     * @param source       The command source to send the message to.
     * @param translatable The key for the translatable message.
     */
    public static void translate(@NotNull CommandSourceStack source, String translatable) {
        translate(source.getSender(), translatable);
    }

    /**
     * Tell the sender of a command a translatable message
     * using either provided lang files in resource packs or server-side translation files.
     *
     * @param context      The command context to send the message to.
     * @param translatable The key for the translatable message.
     */
    public static void translate(@NotNull CommandContext<CommandSourceStack> context, String translatable) {
        translate(context.getSource().getSender(), translatable);
    }

    /**
     * Tell the audience a translatable message
     * using either provided lang files in resource packs or server-side translation files.
     *
     * @param audience     The audience to send the message to.
     * @param translatable The key for the translatable message.
     */
    public static void translate(@NotNull Audience audience, String translatable) {
        final String message = PlainTextComponentSerializer.plainText()
                .serialize(GlobalTranslator.render(Component.translatable(translatable), audience.get(Identity.LOCALE).orElse(Locale.ENGLISH)));

        audience.sendMessage(StringUtil.color(message));
    }

    // TODO: Convert 'tell' to use Audience?

    /**
     * Tell the specified {@link CommandSourceStack}
     * (either a player, console, or command block) the message(s) supplied.
     *
     * @param source   The source we should send the message(s) to.
     * @param message  The first message that should be sent to the supplied CommandSender.
     * @param messages An array of messages that are then iterated through and sent to the supplied CommandSender.
     */
    // FIXME: Remove unstable when stable.
    @SuppressWarnings("UnstableApiUsage")
    public static void tell(CommandSourceStack source, @NotNull String message, String... messages) {
        tell(source, message, List.of(messages));
    }

    /**
     * Tell the specified {@link CommandSourceStack}
     * (either a player, console, or command block) the message(s) supplied.
     *
     * @param context  The context we should send the message(s) to.
     * @param message  The first message that should be sent to the supplied CommandSender.
     * @param messages An array of messages that are then iterated through and sent to the supplied CommandSender.
     */
    // FIXME: Remove unstable when stable.
    @SuppressWarnings("UnstableApiUsage")
    public static void tell(@NotNull CommandContext<CommandSourceStack> context, @NotNull String message, String... messages) {
        tell(context.getSource(), message, List.of(messages));
    }

    /**
     * Tell the specified {@link CommandSourceStack} sender
     * (either a player, console, or command block) the message(s) supplied.
     *
     * @param source   The source we should send the message(s) to.
     * @param message  The first message that should be sent to the supplied CommandSender.
     * @param messages An array of messages that are then iterated through and sent to the supplied CommandSender.
     */
    // FIXME: Remove unstable when stable.
    @SuppressWarnings("UnstableApiUsage")
    public static void tell(CommandSourceStack source, @NotNull String message, Collection<String> messages) {
        if (!message.isBlank()) {
            source.getSender().sendMessage(StringUtil.color(message));
        }

        for (final String msg : messages) {
            if (msg.isBlank()) continue;
            source.getSender().sendMessage(StringUtil.color(msg));
        }
    }

    /**
     * Tell the specified {@link CommandSourceStack} sender
     * (either a player, console, or command block) the message(s) supplied.
     *
     * @param source   The CommandSender we should send the message(s) to.
     * @param message  The first {@link Component} that should be sent to the supplied CommandSender.
     * @param messages An array of messages that are then iterated through and sent to the supplied CommandSender.
     */
    // FIXME: Remove unstable when stable.
    @SuppressWarnings("UnstableApiUsage")
    public static void tell(CommandSourceStack source, Component message, Component... messages) {
        tell(source, message, List.of(messages));
    }

    /**
     * Tell the specified {@link CommandSourceStack} sender
     * (either a player, console, or command block) the message(s) supplied.
     *
     * @param source   The CommandSender we should send the message(s) to.
     * @param message  The first {@link Component} that should be sent to the supplied CommandSender.
     * @param messages An array of messages that are then iterated through and sent to the supplied CommandSender.
     */
    // FIXME: Remove unstable when stable.
    @SuppressWarnings("UnstableApiUsage")
    public static void tell(CommandSourceStack source, Component message, Collection<Component> messages) {
        if (message != null) {
            source.getSender().sendMessage(message);
        }

        for (final Component msg : messages) {
            if (msg == null) continue;
            source.getSender().sendMessage(msg);
        }
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
     * @param messages An iterable list of messages that are then iterated through and sent to the supplied CommandSender.
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
     * @param messages An iterable list of messages that are then iterated through and sent to the supplied CommandSender.
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
     * Converts a {@link UUID} to a {@link ByteArrayInputStream}.
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
     * Converts an {@link InputStream} filled with bytes to an {@link UUID}.
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
