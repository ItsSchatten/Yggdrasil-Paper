package com.itsschatten.yggdrasil;

import com.google.common.io.ByteStreams;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Yet another utils class.
 */
public final class Utils {

    private final static PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private static ComponentLogger logger;
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
     * Generate a file based from a resource and into the provided path.
     *
     * @param path The path to place the generated file.
     * @param name The name of the file to generate.
     * @return The made {@link File}.
     * @throws IOException Thrown if a file cannot be made.
     */
    public static @NotNull File makeFile(final @NotNull Path path, final String name) throws IOException {
        final File file = new File(path.toFile(), name);

        // Check if the file exists; if it doesn't create it and any required subdirectories.
        if (!file.exists()) {
            if (file.getParentFile().mkdirs()) {
                try (final InputStream stream = Utils.class.getResourceAsStream("/" + name)) {
                    if (stream != null)
                        Files.copy(stream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                if (file.createNewFile()) {
                    try (final InputStream stream = Utils.class.getResourceAsStream("/" + name)) {
                        if (stream != null)
                            Files.copy(stream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }

        return file;
    }

    /**
     * Generate a file based from a resource and into the provided path.
     *
     * @param path The path to place the generated file.
     * @param name The name of the file to generate.
     * @throws IOException Thrown if a file cannot be made.
     */
    public static void generateFile(final @NotNull Path path, final String name) throws IOException {
        final File file = new File(path.toFile(), name);

        // Check if the file exists; if it doesn't create it and any required subdirectories.
        if (!file.exists()) {
            if (file.getParentFile().mkdirs()) {
                try (final InputStream stream = Utils.class.getResourceAsStream("/" + name)) {
                    if (stream != null)
                        Files.copy(stream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                if (file.createNewFile()) {
                    try (final InputStream stream = Utils.class.getResourceAsStream("/" + name)) {
                        if (stream != null)
                            Files.copy(stream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }

    /**
     * Utility method to quickly create a {@link NamespacedKey} based on this classes {@link JavaPlugin instance}.
     *
     * @param value The name of this tag.
     * @return A new {@link NamespacedKey} with the {@link JavaPlugin java plugin instance} of this class.
     */
    @Contract("_ -> new")
    public static @NotNull NamespacedKey makeNSKey(@NotNull final String value) {
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
     * @param audience  The audience to send the message to.
     * @param throwable The error to send.
     */
    public static void sendDeveloperErrorMessage(final @NotNull Audience audience, final Throwable throwable) {
        // If the user has the developer permission, send them information on the error that occurred.
        if ((audience instanceof Player player) && !player.hasPermission(getInstance().getName() + ".developer")) {
            return;
        }

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

        audience.sendMessage(message);
    }

    /**
     * Converts an array of {@link ItemStack}s into a Base64 encoded {@link String}.
     *
     * @param items The items that are to be converted.
     * @return Returns the encoded {@link ItemStack}.
     * @throws IllegalStateException If unable to save the ItemStack, this is thrown.
     */
    public static @NotNull String convertItemStacksToBase64(@NotNull ItemStack[] items) throws IllegalStateException {
        return Base64Coder.encodeLines(ItemStack.serializeItemsAsBytes(items));
    }

    /**
     * Converts a Base64 encoded {@link String} into an array of {@link ItemStack}.
     * Gets an array of ItemStacks from Base64 string.
     *
     * @param data Base64 string to convert to {@link ItemStack} array.
     * @return {@link ItemStack}s created from the Base64 string, may be null.
     */
    @Contract("null -> null; !null -> !null")
    public static ItemStack[] getItemStacksFromBase64(String data) {
        if (data == null) return null;
        return ItemStack.deserializeItemsFromBytes(Base64Coder.decodeLines(data));
    }

    /**
     * Converts an {@link ItemStack} into a Base64 encoded {@link String}.
     *
     * @param item The item that is to be converted.
     * @return Returns the encoded {@link ItemStack}.
     * @throws IllegalStateException If unable to save the ItemStack, this is thrown.
     */
    public static @NotNull String convertItemStackToBase64(@NotNull ItemStack item) throws IllegalStateException {
        return Base64Coder.encodeLines(item.serializeAsBytes());
    }

    /**
     * Converts a Base64 encoded {@link String} into an {@link ItemStack}.
     *
     * @param data Base64 string to convert to an {@link ItemStack}.
     * @return {@link ItemStack} created from the Base64 string, may be null.
     */
    @Contract("null -> null; !null -> !null")
    public static ItemStack getItemStackFromBase64(String data) {
        if (data == null) return null;
        return ItemStack.deserializeBytes(Base64Coder.decodeLines(data));
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
        if (Utils.instance == null && Utils.logger == null) {
            throw new UnsupportedOperationException("Plugin instance is null and attempted to get it's logger!");
        }
        return Utils.logger != null && Utils.instance == null ? logger : Utils.instance.getComponentLogger();
    }

    /**
     * Used to allow the logger during bootstrap.
     *
     * @param logger The {@link ComponentLogger} to set.
     */
    public static void setLogger(ComponentLogger logger) {
        if (Utils.logger == null) {
            if (Utils.instance == null) {
                Utils.logger = logger;
            } else {
                throw new UnsupportedOperationException("Plugin instance has been set! The logger defaults to the plugins.");
            }
        } else {
            throw new UnsupportedOperationException("Cannot update logger, please set the instance. (Utils#setInstance)");
        }
    }

    /**
     * Sends a {@link String message} (or multiple) to the console with the INFO level.
     *
     * @param message  The first message that should be sent.
     * @param messages An array of messages that are then iterated through and sent to the console.
     */
    public static void log(@NotNull final String message, final String... messages) {
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
     * Utility method to quickly convert a {@link String} into a {@link Message} for use with Brigadier commands.
     *
     * @param message The message to send.
     * @return Returns a {@link Message} using {@link MessageComponentSerializer#message()}.
     */
    public static @NotNull Message message(@NotNull String message) {
        return message(StringUtil.color(message));
    }

    /**
     * Utility method to quickly convert a {@link String} into a {@link Message} for use with Brigadier commands.
     *
     * @param message The message to send.
     * @return Returns a {@link Message} using {@link MessageComponentSerializer#message()}.
     */
    public static @NotNull Message message(@NotNull Component message) {
        return MessageComponentSerializer.message().serialize(message);
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
                        Arrays.stream(placeholders).map(StringUtil.builtMiniMessage()::serialize).map(Component::text).toList()),
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

    /**
     * Method to quickly tell an the {@link CommandSourceStack} of a command an empty line.
     *
     * @param source The {@link CommandSourceStack sender} to send the message to.
     */
    public static void emptyLine(@NotNull CommandSourceStack source) {
        emptyLine(source.getSender());
    }

    /**
     * Method to quickly tell an the {@link CommandSourceStack} of a command (taken from the context) an empty line.
     *
     * @param context The command context to pull the sender from.
     */
    public static void emptyLine(@NotNull CommandContext<CommandSourceStack> context) {
        emptyLine(context.getSource().getSender());
    }

    /**
     * Method to quickly tell an {@link Audience} an empty line.
     *
     * @param audience The audience to send the message to.
     */
    public static void emptyLine(@NotNull Audience audience) {
        audience.sendMessage(Component.empty());
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
    public static void tell(@NotNull CommandSourceStack source, @NotNull String message, Collection<String> messages) {
        tell(source.getSender(), message, messages);
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
    public static void tell(@NotNull CommandSourceStack source, Component message, Collection<Component> messages) {
        tell(source.getSender(), message, messages);
    }

    /**
     * Tell the specified {@link Audience} (either a player, console, or command block) the message(s) supplied.
     *
     * @param toWhom   The {@link Audience} we should send the message(s) to.
     * @param message  The first message that should be sent to the supplied CommandSender.
     * @param messages An array of messages that are then iterated through and sent to the supplied CommandSender.
     */
    public static void tell(@NotNull Audience toWhom, @NotNull String message, String... messages) {
        if (!message.isBlank())
            toWhom.sendMessage(StringUtil.color(message));

        for (final String msg : messages) {
            if (msg.isBlank()) continue;
            toWhom.sendMessage(StringUtil.color(msg));
        }
    }

    /**
     * Tell the specified {@link Audience} (either a player, console, or command block) the message(s) supplied.
     *
     * @param toWhom   The {@link Audience} we should send the message(s) to.
     * @param message  The first {@link Component} that should be sent to the supplied CommandSender.
     * @param messages An array of messages that are then iterated through and sent to the supplied CommandSender.
     */
    public static void tell(@NotNull Audience toWhom, @Nullable Component message, Component... messages) {
        if (message != null)
            toWhom.sendMessage(message);

        for (final Component msg : messages) {
            if (msg == null) continue;
            toWhom.sendMessage(msg);
        }
    }

    /**
     * Tell the specified {@link Audience} (either a player, console, or command block) the message(s) supplied.
     *
     * @param toWhom   The {@link Audience} we should send the message(s) to.
     * @param message  The first message that should be sent to the supplied CommandSender.
     * @param messages An iterable list of messages that are then iterated through and sent to the supplied CommandSender.
     */
    public static void tell(@NotNull Audience toWhom, @NotNull String message, Iterable<String> messages) {
        if (!message.isBlank())
            toWhom.sendMessage(StringUtil.color(message));

        for (final String msg : messages) {
            if (msg.isBlank()) continue;
            toWhom.sendMessage(StringUtil.color(msg));
        }
    }

    /**
     * Tell the specified {@link Audience} (either a player, console, or command block) the message(s) supplied.
     *
     * @param toWhom   The {@link Audience} we should send the message(s) to.
     * @param message  The first {@link Component} that should be sent to the supplied CommandSender.
     * @param messages An iterable list of messages that are then iterated through and sent to the supplied CommandSender.
     */
    public static void tell(@NotNull Audience toWhom, @Nullable Component message, Iterable<Component> messages) {
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
