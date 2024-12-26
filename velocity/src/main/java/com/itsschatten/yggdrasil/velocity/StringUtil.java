package com.itsschatten.yggdrasil.velocity;

import com.itsschatten.yggdrasil.velocity.resolvers.AlternateResolver;
import com.itsschatten.yggdrasil.velocity.resolvers.DarkRainbowResolver;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Utility class for {@link String}s.
 *
 * @since 1.0.0
 */
@UtilityClass
public class StringUtil {

    /**
     * Array of roman numerals.
     */
    private static final String[] ROMAN_NUMERALS;

    /**
     * Array of roman numerals values.
     */
    private static final int[] ROMAN_NUMERAL_VALUES;

    /**
     * The MiniMessage builder used to convert Strings into adventure components, built per-colored-message.
     */
    // Being built per-colored-message may be rather resource intensive,
    // if that does become the case, this will be converted to just a fully built MiniMessage object instead.
    // Then, if we need to "dynamically" edit it, it can be converted back to a builder using a helper method.
    private static MiniMessage.Builder MINI_MESSAGE;

    //<editor-fold defaultstate="collapsed" desc="Static initialization.">
    static {
        MINI_MESSAGE = MiniMessage.builder();
        configureDefaultMiniMessage();

        ROMAN_NUMERAL_VALUES = new int[]{100, 90, 50, 40, 10, 9, 5, 4, 1};
        ROMAN_NUMERALS = new String[]{"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"}; // Lists roman numerals from the greatest to the least.
    }
    //</editor-fold>

    public static MiniMessage.Builder miniMessage() {
        return MINI_MESSAGE;
    }

    // Quickly configure the default builder.
    private static void configureDefaultMiniMessage() {
        MINI_MESSAGE.editTags(builder -> {
            builder.resolver(DarkRainbowResolver.RESOLVER);
            builder.resolver(AlternateResolver.RESOLVER);
        });
    }

    /**
     * Clear the resolvers from the MiniMessage builder, basically reverting to default.
     */
    public static void clearResolvers() {
        MINI_MESSAGE = MiniMessage.builder();
        configureDefaultMiniMessage();
    }

    /**
     * Add the provided resolver to {@link #MINI_MESSAGE}, so they can be used in {@link #color(String)}
     *
     * @param resolver The resolvers to add.
     */
    public static void addResolvers(final TagResolver @NotNull ... resolver) {
        MINI_MESSAGE.editTags(builder -> builder.resolvers(resolver));
    }

    /**
     * Add the provided resolver to {@link #MINI_MESSAGE}, so they can be used in {@link #color(String)}
     *
     * @param resolver The resolvers to add.
     */
    public static void addResolvers(final @NotNull Collection<TagResolver> resolver) {
        MINI_MESSAGE.editTags(builder -> builder.resolvers(resolver));
    }

    /**
     * Utility method to quickly format a message using {@link MiniMessage} formatting.
     *
     * @param message The message we are attempting to color.
     * @return The colored String.
     */
    @NotNull
    @Contract("null -> fail")
    public static Component color(String message) {
        if (message == null)
            throw new NullPointerException("Unable to color a null String!");

        return MINI_MESSAGE.build().deserialize(message);
    }

    /**
     * Make a progress bar of 30 total bars with the provided values.
     *
     * @param currentValue    The current value for what we want to show the progress for.
     * @param maxValue        The maximum progress we can achieve.
     * @param character       What character we want to use.
     * @param progressColor   The color prepended before all progress towards the max value.
     * @param noProgressColor The color prepended before the remaining.
     * @return The colored progress bar.
     */
    public static @NotNull String getProgressBar(final double currentValue, final double maxValue, final char character, final @NotNull TextColor progressColor, final @NotNull TextColor noProgressColor) {
        final int totalBars = 30;
        return createBar(currentValue, maxValue, character, progressColor, noProgressColor, totalBars);
    }

    /**
     * Quickly creates the bar string for {@link #getProgressBar(double, double, char, TextColor, TextColor)} and {@link #getProgressBar(int, double, double, char, TextColor, TextColor)}.
     *
     * @param currentValue    The current value for the progress.
     * @param maxValue        The maximum progress.
     * @param character       The character to use for the bar.
     * @param progressColor   The color for progress.
     * @param noProgressColor The no progress color.
     * @param totalBars       How many total bars we want to put
     * @return Returns a new string, formatted with MiniMessage formatting.
     */
    private static @NotNull String createBar(double currentValue, double maxValue, char character, @NotNull TextColor progressColor, @NotNull TextColor noProgressColor, int totalBars) {
        final double percent = currentValue / maxValue;
        final int progressBars = (int) (totalBars * percent);

        return "<" + progressColor.asHexString() + ">" + StringUtils.repeat(String.valueOf(character), Math.min(progressBars, totalBars)) +
                "<" + noProgressColor.asHexString() + ">" + StringUtils.repeat(String.valueOf(character), totalBars - progressBars);
    }

    /**
     * Make a progress bar with the provided values.
     *
     * @param totalBars       The amount of characters this String should contain.
     * @param currentValue    The current value for what we want to show the progress for.
     * @param maxValue        The maximum progress we can achieve.
     * @param character       What character we want to use.
     * @param progressColor   The color prepended before all progress towards the max value.
     * @param noProgressColor The color prepended before the remaining.
     * @return The colored progress bar.
     */
    public static @NotNull String getProgressBar(final int totalBars, final double currentValue, final double maxValue, final char character, final @NotNull TextColor progressColor, final @NotNull TextColor noProgressColor) {
        return createBar(currentValue, maxValue, character, progressColor, noProgressColor, totalBars);
    }

    /**
     * Properly pluralizes a {@link String}
     *
     * @param string The string to pluralize.
     * @return The proper pluralized String
     */
    @Contract("null -> fail")
    public static @NotNull String pluralize(final String string) {
        Validate.notNull(string, "String must not be null in order to pluralize.");
        return string + (string.endsWith("s") ? "'" : "'s");
    }

    /**
     * Convert a numerical value into a roman numeral String.
     *
     * @param number          The number we want to convert.
     * @param ignoreLevelOne  Should we ignore if this number is one?
     * @param addSpaceToFront Should we prepend a single space before returning the final string?
     * @return A string containing proper roman numeral equivalent to passed number.
     */
    public static @NotNull String convertToRomanNumeral(int number, boolean ignoreLevelOne, boolean addSpaceToFront) {
        if (number == 0) {
            return "";
        }

        if (number == 1 && ignoreLevelOne) {
            return "";
        }

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ROMAN_NUMERAL_VALUES.length; i++) {
            while (number >= ROMAN_NUMERAL_VALUES[i]) {
                number -= ROMAN_NUMERAL_VALUES[i];
                builder.append(ROMAN_NUMERALS[i]);
            }
        }

        return (addSpaceToFront ? " " : "") + builder;
    }

    /**
     * Utility method to quickly ignore a one number.
     *
     * @param number          The number we want to convert.
     * @param addSpaceToFront Should we add a space?
     * @return See return of {@link #convertToRomanNumeral(int, boolean, boolean)}
     * @see #convertToRomanNumeral(int, boolean, boolean)
     */
    public static @NotNull String convertToRomanNumeral(int number, boolean addSpaceToFront) {
        return convertToRomanNumeral(number, true, addSpaceToFront);
    }

    /**
     * Utility method to quickly convert a number into a roman numeral.
     *
     * @param number The number we want to convert.
     * @return See return of {@link #convertToRomanNumeral(int, boolean, boolean)}
     * @see #convertToRomanNumeral(int, boolean, boolean)
     */
    public static @NotNull String convertToRomanNumeral(int number) {
        return convertToRomanNumeral(number, true, false);
    }

}
