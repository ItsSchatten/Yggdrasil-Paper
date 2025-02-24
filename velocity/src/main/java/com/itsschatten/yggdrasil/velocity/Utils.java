package com.itsschatten.yggdrasil.velocity;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;

/**
 * General Utilities.
 *
 * @since 1.0.0
 */
@UtilityClass
public class Utils {

    /**
     * The main plugin class.
     * --- SETTER ---
     * Set the plugin class object.
     *
     * @param instance The instance to set.
     * --- GETTER ---
     * Get the plugin instance.
     * @return Returns the current instance.
     */
    @Setter
    @Getter
    private Object instance;

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
     * Sends a message to an {@link Audience}.
     *
     * @param source   The source to send the message(s) to.
     * @param message  The main message.
     * @param messages An array of messages to send.
     */
    public void tell(@NotNull Audience source, @NotNull String message, String... messages) {
        tell(source, message, List.of(messages));
    }

    /**
     * Sends a message to an {@link Audience}.
     *
     * @param source   The source to send the message(s) to.
     * @param message  The main message.
     * @param messages A Collection of messages to send.
     */
    public void tell(@NotNull Audience source, @NotNull String message, Collection<String> messages) {
        if (!message.isBlank()) {
            source.sendMessage(StringUtil.color(message));
        }

        messages.iterator().forEachRemaining((s) -> {
            if (!s.isBlank()) {
                source.sendMessage(StringUtil.color(s));
            }
        });
    }

    /**
     * Sends a message to a {@link CommandSource} from a {@link CommandContext}.
     *
     * @param context  The context to get the source to send the message(s) to.
     * @param message  The main message.
     * @param messages An array of messages to send.
     */
    public void tell(@NotNull CommandContext<CommandSource> context, @NotNull String message, String... messages) {
        tell(context.getSource(), message, messages);
    }

    /**
     * Sends a message to a {@link CommandSource} from a {@link CommandContext}.
     *
     * @param context  The context to get the source to send the message(s) to.
     * @param message  The main message.
     * @param messages An array of messages to send.
     */
    public void tell(@NotNull CommandContext<CommandSource> context, @NotNull String message, Collection<String> messages) {
        tell(context.getSource(), message, messages);
    }

    /**
     * Sends a message to an {@link Audience}.
     *
     * @param source   The source to send the message(s) to.
     * @param message  The main message.
     * @param messages An array of messages to send.
     */
    public void tell(Audience source, Component message, Component... messages) {
        tell(source, message, List.of(messages));
    }

    /**
     * Sends a message to an {@link Audience}.
     *
     * @param source   The source to send the message(s) to.
     * @param message  The main message.
     * @param messages A Collection of messages to send.
     */
    public void tell(@NotNull Audience source, Component message, Collection<Component> messages) {
        if (message != null) {
            source.sendMessage(message);
        }

        messages.iterator().forEachRemaining((msg) -> {
            if (msg != null) {
                source.sendMessage(msg);
            }
        });
    }

    /**
     * Sends a message to a {@link CommandSource} from a {@link CommandContext}.
     *
     * @param context  The context to get the source to send the message(s) to.
     * @param message  The main message.
     * @param messages An array of messages to send.
     */
    public void tell(@NotNull CommandContext<CommandSource> context, Component message, Component... messages) {
        tell(context.getSource(), message, messages);
    }

    /**
     * Sends a message to a {@link CommandSource} from a {@link CommandContext}.
     *
     * @param context  The context to get the source to send the message(s) to.
     * @param message  The main message.
     * @param messages An array of messages to send.
     */
    public void tell(@NotNull CommandContext<CommandSource> context, @NotNull Component message, Collection<Component> messages) {
        tell(context.getSource(), message, messages);
    }
}
