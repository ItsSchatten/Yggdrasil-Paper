package com.itsschatten.yggdrasil;

import com.itsschatten.yggdrasil.resolvers.AlternateResolver;
import com.itsschatten.yggdrasil.resolvers.DarkRainbowResolver;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for {@link String}s.
 */
@UtilityClass
public class StringUtil {

    /**
     * Money format.
     */
    private static final DecimalFormat BALANCE_FORMAT;

    /**
     * Normal balance format.
     */
    private static final DecimalFormat OTHER_BALANCE_FORMAT;

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

        BALANCE_FORMAT = new DecimalFormat("#,###,###,###.##");
        BALANCE_FORMAT.setCurrency(Currency.getInstance(Locale.US));
        BALANCE_FORMAT.setMaximumFractionDigits(2);
        BALANCE_FORMAT.setMinimumFractionDigits(2);

        OTHER_BALANCE_FORMAT = new DecimalFormat("#,###,###,###");

        ROMAN_NUMERAL_VALUES = new int[]{100, 90, 50, 40, 10, 9, 5, 4, 1};
        ROMAN_NUMERALS = new String[]{"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"}; // Lists roman numerals from the greatest to the least.
    }
    //</editor-fold>

    public static MiniMessage.Builder miniMessage() {
        return MINI_MESSAGE;
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull MiniMessage builtMiniMessage() {
        return MINI_MESSAGE.build();
    }

    // Quickly configure the default builder.
    private static void configureDefaultMiniMessage() {
        MINI_MESSAGE.preProcessor(ColorCodeConverter::replace);

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
     * Convert a {@link Collection} of objects into a nicely formatted list that also inserts 'and' for the final entry.
     *
     * @param collection The collection to map.
     * @param mapper     How to map the collection.
     * @param <T>        The generic type.
     * @return Return {@link #toFormattedList(Collection, Function, String)}
     */
    public static <T> @NotNull String toFormattedList(final Collection<T> collection, final Function<T, ? extends String> mapper) {
        return toFormattedList(collection, mapper, "<gray>, </gray>");
    }

    /**
     * Convert a {@link Collection} of objects into a nicely formatted list that also inserts 'and' for the final entry.
     *
     * @param collection The collection to map.
     * @param mapper     How to map the collection.
     * @param separator  The separator between the values.
     * @param <T> The generic type.
     * @return Returns a properly formatted list of objects, that also inserts 'and' for the final entry.
     */
    public static <T> @NotNull String toFormattedList(final @NotNull Collection<T> collection, final Function<T, ? extends String> mapper, final String separator) {
        final StringBuilder builder = new StringBuilder(collection.stream().map(mapper).collect(Collectors.joining(separator)));
        if (builder.toString().contains(separator)) {
            builder.replace(builder.lastIndexOf(separator), builder.lastIndexOf(separator) + 1, "and");
        }
        return builder.toString();
    }

    /**
     * Used to prettify a player's balance.
     * <p>
     * Recommended for money balance, use {@link #getBalance(double)} for others.
     *
     * @param balance The balance we wish to beautify.
     * @return A properly formatted balance from the value given.
     */
    public static @NotNull String getPrettyBalance(final double balance) {
        return "$" + BALANCE_FORMAT.format(balance);
    }

    /**
     * Returns a full balance.
     * <p>
     * Recommended for other balances, use {@link #getPrettyBalance(double)} for money.
     *
     * @param balance The balance we wish to beautify.
     * @return A properly formatted balance from the value given.
     */
    public static @NotNull String getBalance(final double balance) {
        return OTHER_BALANCE_FORMAT.format(balance);
    }

    /**
     * Formats a double into a more easy to read number, removing excess decimal places.
     *
     * @param number The number to format.
     * @return The formatted number in as a String.
     */
    private static String format(double number) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);
        return format.format(number);
    }

    /**
     * Format a number in a more easy-to-read format.
     *
     * @param number The double we are formatting.
     * @return A String in a properly formatted way.
     */
    public static String formatNumber(double number) {
        if (number < 1000.0D) {
            return format(number);
        } else if (number < 1_000_000.0D) {
            return format(number / 1000.0D) + "k";
        } else if (number < 1.0E9D) {
            return format(number / 1_000_000.0D) + "M";
        } else if (number < 1.0E12D) {
            return format(number / 1.0E9D) + "B";
        } else if (number < 1.0E15D) {
            return format(number / 1.0E12D) + "T";
        } else {
            return number < 1.0E18D ? format(number / 1.0E15D) + "Q" : String.valueOf(number);
        }
    }

    /**
     * Format a number in a more easy-to-read format.
     *
     * @param number The double we are formatting.
     * @param color  The color of the letter.
     * @return A String in a properly formatted way.
     */
    public String formatNumber(final double number, final TextColor color) {
        if (number < 1000.0D) {
            return format(number);
        } else if (number < 1_000_000.0D) {
            return format(number / 1000.0D) + "<c:" + color.asHexString() + ">k</c>";
        } else if (number < 1.0E9D) {
            return format(number / 1_000_000.0D) + "<c:" + color.asHexString() + ">M</c>";
        } else if (number < 1.0E12D) {
            return format(number / 1.0E9D) + "<c:" + color.asHexString() + ">B</c>";
        } else if (number < 1.0E15D) {
            return format(number / 1.0E12D) + "c:<" + color.asHexString() + ">T</c>";
        } else {
            return number < 1.0E18D ? format(number / 1.0E15D) + "<c:" + color.asHexString() + ">Q</c>" : String.valueOf(number);
        }
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
     * Prettifies a {@link Location} into a String in the format (x,y,z)
     *
     * @param location Location to prettify.
     * @return A new {@link String}.
     */
    public static @NotNull String prettifyBracketLocation(final @NotNull Location location) {
        return "(" + location.getBlockX() + ", " +
                location.getBlockY() + ", " +
                location.getBlockZ() + ")";
    }

    /**
     * Prettifies a {@link Location} into a String in the format (x,y,z)
     *
     * @param location       Location to prettify.
     * @param numberColor    The color that should be used for the numbers.
     * @param separatorColor The color that should be used for the comma.
     * @param bracketColor   The color for the parenthesis.
     * @return A new {@link String} supplied with color codes.
     */
    public static @NotNull Component prettifyBracketLocation(final @NotNull Location location, final @NotNull String numberColor,
                                                             final @NotNull String separatorColor, final @NotNull String bracketColor) {
        final String finalNumberColor = numberColor.isBlank() ? "" : numberColor;
        final String finalSeparatorColor = separatorColor.isBlank() ? "" : separatorColor;
        final String finalBracketColor = bracketColor.isBlank() ? "" : bracketColor;

        return color((finalBracketColor + "(") + (finalNumberColor + location.getBlockX()) + (finalSeparatorColor + ", ") +
                (finalNumberColor + location.getBlockY()) + (finalSeparatorColor + ", ") +
                (finalNumberColor + location.getBlockZ()) + (finalBracketColor + ")"));
    }

    /**
     * Used to get a pretty string for a {@link Location}.
     *
     * @param location The {@link Location block location} we are prettifying.
     * @return A pretty string.
     */
    @Contract(pure = true)
    public static @NotNull String prettifyLocation(final @NotNull Location location) {
        return "X: " + location.getBlockX() + ", " +
                "Y: " + (location.getBlockY()) + ", " +
                "Z: " + location.getBlockZ();
    }

    /**
     * Used to get a pretty string for a {@link Location}.
     *
     * @param location    The {@link Location location} we are prettifying.
     * @param letterColor The color the letter should be, set to "" to use the preceding color.
     * @param numberColor The color the number should be, set to "" to use the preceding color.
     * @return A pretty string.
     */
    @Contract(pure = true)
    public static @NotNull Component prettifyLocation(final @NotNull Location location, @NotNull String letterColor, @NotNull String numberColor) {
        final String finalLetterColor = letterColor.isBlank() ? "" : letterColor;
        final String finalNumberColor = numberColor.isBlank() ? "" : numberColor;

        return color((finalLetterColor + "X: ") + (finalNumberColor + location.getBlockX()) + ", " +
                (finalLetterColor + "Y: ") + (finalNumberColor + location.getBlockY()) + ", " +
                (finalLetterColor + "Z: ") + (finalNumberColor + location.getBlockZ()));
    }

    /**
     * Used to get a pretty string for a {@link Location}.
     *
     * @param location        The {@link Location location} we are prettifying.
     * @param letterColor     The color the letter should be, set to "" to use the preceding color.
     * @param numberColor     The color the number should be, set to "" to use the preceding color.
     * @param numberSeparator The separating symbol we should use, set to "" to use ':'.
     * @return A pretty string.
     */
    @Contract(pure = true)
    public static @NotNull Component prettifyLocation(final @NotNull Location location, @NotNull String letterColor,
                                                      @NotNull String numberColor, @NotNull String numberSeparator) {
        final String finalLetterColor = letterColor.isBlank() ? "" : letterColor;
        final String finalNumberColor = numberColor.isBlank() ? "" : numberColor;
        final String finalNumberSeparator = numberSeparator.isBlank() ? ":" : numberSeparator;
        return color((finalLetterColor + ("X" + finalNumberSeparator + " ")) + (finalNumberColor + location.getBlockX()) + ", " +
                (finalLetterColor + ("Y" + finalNumberSeparator + " ")) + (finalNumberColor + location.getBlockY()) + ", " +
                (finalLetterColor + ("Z" + finalNumberSeparator + " ")) + (finalNumberColor + location.getBlockZ()));
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
