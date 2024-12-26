package com.itsschatten.yggdrasil.velocity;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

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
     * Sends a message to a {@link CommandSource}.
     *
     * @param source   The source to send the message(s) to.
     * @param message  The main message.
     * @param messages An array of messages to send.
     */
    public void tell(@NotNull Audience source, @NotNull String message, String... messages) {
        if (!message.isBlank()) {
            source.sendMessage(StringUtil.color(message));
        }

        for (final String s : messages) {
            if (!s.isBlank()) {
                source.sendMessage(StringUtil.color(s));
            }
        }
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
     * Sends a message to a {@link CommandSource}.
     *
     * @param source   The source to send the message(s) to.
     * @param message  The main message.
     * @param messages An array of messages to send.
     */
    public void tell(Audience source, Component message, Component... messages) {
        if (message != null) {
            source.sendMessage(message);
        }

        for (final Component s : messages) {
            if (s != null) {
                source.sendMessage(s);
            }
        }
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

}
