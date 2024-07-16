package com.itsschatten.yggdrasil;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * Used to convert normal Minecraft color codes into MiniMessage color codes.
 *
 * @since 2.0.0
 */
public enum ColorCodeConverter {

    /**
     * Standard black color.
     */
    BLACK('0', "<black>", NamedTextColor.BLACK),
    /**
     * Standard dark_blue color.
     */
    DARK_BLUE('1', "<dark_blue>", NamedTextColor.DARK_BLUE),
    /**
     * Standard dark_green color.
     */
    DARK_GREEN('2', "<dark_green>", NamedTextColor.DARK_GREEN),
    /**
     * Standard dark_aqua color.
     */
    DARK_AQUA('3', "<dark_aqua>", NamedTextColor.DARK_AQUA),
    /**
     * Standard dark_red color.
     */
    DARK_RED('4', "<dark_red>", NamedTextColor.DARK_RED),
    /**
     * Standard dark_purple color.
     */
    DARK_PURPLE('5', "<dark_purple>", NamedTextColor.DARK_PURPLE),
    /**
     * Standard gold color.
     */
    GOLD('6', "<gold>", NamedTextColor.GOLD),
    /**
     * Standard gray color.
     */
    GRAY('7', "<gray>", NamedTextColor.GRAY),
    /**
     * Standard dark_gray color.
     */
    DARK_GRAY('8', "<dark_gray>", NamedTextColor.DARK_GRAY),
    /**
     * Standard blue color.
     */
    BLUE('9', "<blue>", NamedTextColor.BLUE),
    /**
     * Standard green color.
     */
    GREEN('a', "<green>", NamedTextColor.GREEN),
    /**
     * Standard aqua color.
     */
    AQUA('b', "<aqua>", NamedTextColor.AQUA),
    /**
     * Standard red color.
     */
    RED('c', "<red>", NamedTextColor.RED),
    /**
     * Standard light_purple color.
     */
    LIGHT_PURPLE('d', "<light_purple>", NamedTextColor.LIGHT_PURPLE),
    /**
     * Standard yellow color.
     */
    YELLOW('e', "<yellow>", NamedTextColor.YELLOW),
    /**
     * Standard white color.
     */
    WHITE('f', "<white>", NamedTextColor.WHITE),

    /**
     * Makes text appear bold.
     */
    BOLD('l', "<b>", TextDecoration.BOLD),
    /**
     * Make text appear italic.
     */
    ITALIC('o', "<i>", TextDecoration.ITALIC),
    /**
     * Apply a strike through the text.
     */
    STRIKETHROUGH('m', "<st>", TextDecoration.STRIKETHROUGH),
    /**
     * Underline text.
     */
    UNDERLINE('n', "<u>", TextDecoration.UNDERLINED),
    /**
     * Obfuscate the text.
     */
    OBFUSCATE('k', "<obf>", TextDecoration.OBFUSCATED),
    /**
     * Reset color and text decorations.
     */
    RESET('r', "<reset>");

    // Pattern to determine the color codes.
    private static final Pattern colorPattern = Pattern.compile("[&§]([a-f0-9lkmno])", Pattern.CASE_INSENSITIVE);

    /**
     * The color code.
     */
    final char colorCode;

    /**
     * The string to replace it with.
     */
    final String replacement;

    /**
     * The actual named text color.
     */
    final NamedTextColor color;

    /**
     * Text decoration.
     */
    final TextDecoration decoration;

    /**
     * Constructor.
     *
     * @param colorCode   The color code character.
     * @param replacement The String to replace the color code.
     * @param color       The actual {@link NamedTextColor}
     */
    ColorCodeConverter(final char colorCode, final String replacement, NamedTextColor color) {
        this.colorCode = colorCode;
        this.replacement = replacement;
        this.color = color;
        this.decoration = null;
    }

    /**
     * Constructor.
     *
     * @param colorCode   The color code character.
     * @param replacement The String to replace the color code.
     * @param decoration  The actual {@link net.kyori.adventure.text.format.TextDecoration}
     */
    ColorCodeConverter(final char colorCode, final String replacement, TextDecoration decoration) {
        this.colorCode = colorCode;
        this.replacement = replacement;
        this.color = null;
        this.decoration = decoration;
    }

    /**
     * Constructor.
     *
     * @param colorCode   The color code character.
     * @param replacement The String to replace the color code.
     */
    ColorCodeConverter(final char colorCode, final String replacement) {
        this.colorCode = colorCode;
        this.replacement = replacement;
        this.color = null;
        this.decoration = null;
    }

    /**
     * Replaces normal Minecraft color codes to MiniMessage color tags.
     *
     * @param string The string to replace codes in.
     * @return Returns the provided String if no colors are found. Otherwise, it will replace the color codes and return that string.
     */
    public static String replace(String string) {
        if (!colorPattern.matcher(string).find()) {
            return string;
        }

        // Replace the colors.
        for (final ColorCodeConverter converter : values()) {
            string = string.replaceAll("[§&]" + converter.colorCode, converter.replacement);
        }

        return string;
    }

    @Contract(pure = true)
    public static @Nullable NamedTextColor getNamedColor(final @NotNull String code) {
        return switch (code) {
            case "a" -> GREEN.color;
            case "b" -> AQUA.color;
            case "c" -> RED.color;
            case "d" -> LIGHT_PURPLE.color;
            case "e" -> YELLOW.color;
            case "f" -> WHITE.color;
            case "0" -> BLACK.color;
            case "1" -> DARK_BLUE.color;
            case "2" -> DARK_GREEN.color;
            case "3" -> DARK_AQUA.color;
            case "4" -> DARK_RED.color;
            case "5" -> DARK_PURPLE.color;
            case "6" -> GOLD.color;
            case "7" -> GRAY.color;
            case "8" -> DARK_GRAY.color;
            case "9" -> BLUE.color;
            default -> null;
        };
    }

}
