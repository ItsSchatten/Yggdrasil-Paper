package com.itsschatten.yggdrasil.libs;

import com.itsschatten.yggdrasil.libs.resolvers.AlternateResolver;
import com.itsschatten.yggdrasil.libs.resolvers.DarkRainbowResolver;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Date format.
     */
    private static final SimpleDateFormat DATE_FORMAT;

    /**
     * Array of roman numerals.
     */
    private static final String[] ROMAN_NUMERALS;
    /**
     * Array of roman numerals values.
     */
    private static final int[] ROMAN_NUMERAL_VALUES;

    /**
     * Full date format.
     */
    private static final SimpleDateFormat FULL_DATE_FORMAT;
    private static final Pattern COLOR_PATTERN;


    static {
        COLOR_PATTERN = Pattern.compile("<#[a-f0-9]{6}>|<(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|grey|dark_gray|dark_grey|blue|green|aqua|red|light_purple|yellow|white)>", Pattern.CASE_INSENSITIVE);
        BALANCE_FORMAT = new DecimalFormat("#,###,###,###.##");
        BALANCE_FORMAT.setCurrency(Currency.getInstance(Locale.US));
        BALANCE_FORMAT.setMaximumFractionDigits(2);
        BALANCE_FORMAT.setMinimumFractionDigits(2);
        OTHER_BALANCE_FORMAT = new DecimalFormat("#,###,###,###");

        ROMAN_NUMERAL_VALUES = new int[]{100, 90, 50, 40, 10, 9, 5, 4, 1};
        ROMAN_NUMERALS = new String[]{"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"}; // Lists roman numerals from the greatest to the least.

        DATE_FORMAT = new SimpleDateFormat("MMM'.' dd 'at' h:mm a");
        FULL_DATE_FORMAT = new SimpleDateFormat("MMM'.' dd yyyy 'at' h:mm a");
    }

    /**
     * Used to get the date format.
     *
     * @return {@link #DATE_FORMAT}
     */
    public static SimpleDateFormat getDateFormat() {
        return DATE_FORMAT;
    }

    /**
     * Used to get the full date format.
     *
     * @return {@link #FULL_DATE_FORMAT}
     */
    public static SimpleDateFormat getFullDateFormat() {
        return FULL_DATE_FORMAT;
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
     * Formats a double into an easier to read number, removing excess decimal places.
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
     * Format a number in a more easy to read format.
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
     * Format a number in an easier to read format.
     *
     * @param number The double we are formatting.
     * @param color  The color of the letter.
     * @return A String in a properly formatted way.
     */
    public static String formatNumber(double number, ChatColor color) {
        if (number < 1000.0D) {
            return format(number);
        } else if (number < 1_000_000.0D) {
            return format(number / 1000.0D) + color + "k";
        } else if (number < 1.0E9D) {
            return format(number / 1_000_000.0D) + color + "m";
        } else if (number < 1.0E12D) {
            return format(number / 1.0E9D) + color + "b";
        } else if (number < 1.0E15D) {
            return format(number / 1.0E12D) + color + "t";
        } else {
            return number < 1.0E18D ? format(number / 1.0E15D) + color + "q" : String.valueOf(number);
        }
    }

    /**
     * Attempts to color a {@link String}.
     * <p>
     * To color a string with normal Minecraft colors:
     * <code>{@literal &[a-f0-9]Your string here.}</code>
     * <p>
     * To color a string with hex colors:
     * <code>{@literal &#333aaaYour string here.}</code>
     * <p>
     * To apply a gradient to a string:
     * <code>{@literal &$#eee00fYour string here&$#ddd999.}</code>
     * <p>
     * It is best practice, to apply the last color one character
     * before the end of the String in order to get the full colors.
     * <p>
     * To alternate colors use <code>{@literal <a:5|4|a|#445522|b;>Your string here.}</code>
     * This does support alternate color codes.
     * <p>
     * To apply rainbow to a string:
     * <code>{@literal <rainbow>Your rainbow string here.</rainbow>}</code> or
     * {@code <dark_rainbow>Your rainbow string here.</dark_rainbow>}<p>
     * It should be noted that this likely works best with longer strings; shorter strings may not contain
     * all colors of the rainbow.
     *
     * @param message The message we are attempting to color.
     * @return The colored String.
     */
    @NotNull
    @Contract("null -> fail")
    public static Component color(String message) {
        if (message == null)
            throw new NullPointerException("Unable to color a null String!");

        message = ColorCodeConverter.replace(message);

        return MiniMessage.miniMessage().deserialize(message, TagResolver.builder().resolvers(StandardTags.defaults(), DarkRainbowResolver.RESOLVER, AlternateResolver.RESOLVER).build());
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
        final double percent = currentValue / maxValue;
        final int progressBars = (int) (totalBars * percent);

        return "<" + progressColor.asHexString() + ">" + StringUtils.repeat(String.valueOf(character), Math.min(progressBars, totalBars)) + "<" + noProgressColor.asHexString() + ">" + StringUtils.repeat(String.valueOf(character), totalBars - progressBars);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     * <p>Default words of 8 and a default color of gray.</p>
     *
     * @param string The {@link String} to convert into a {@link List}
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_ -> new")
    public static @NotNull List<String> convertStringToList(final String string) {
        return convertStringToList(string, null, 8);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     * <p>Default words of 8.</p>
     *
     * @param string       The {@link String} to convert into a {@link List}
     * @param defaultColor The color that is used as the reset color and the default color of the {@link String}s.
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_, _ -> new")
    public static @NotNull List<String> convertStringToList(final String string, final String defaultColor) {
        return convertStringToList(string, defaultColor, 8);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     * <p>Default color of gray.</p>
     *
     * @param string The {@link String} to convert into a {@link List}
     * @param words  The words of each line in the list.
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_, _ -> new")
    public static @NotNull List<String> convertStringToList(final String string, final int words) {
        return convertStringToList(string, null, words);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     *
     * @param string       The {@link String} to convert into a {@link List}
     * @param defaultColor The color that is used as the reset color and the default color of the {@link String}s. Default to {@code <gray>}
     * @param words        The words of each line in the list.
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_, _, _ -> new")
    public static @NotNull List<String> convertStringToList(final @NotNull String string, String defaultColor, final int words) {
        // If default is returned null, set to light gray.
        if (defaultColor == null) {
            defaultColor = "<gray>";
        }

        // Split a string on any spaces and convert to an iterator.
        // Also replace any reset strings to the default color and replace new line tokens with an internal <nl>
        final Iterator<String> splitWords = Arrays.stream(string.replace("<reset>", defaultColor).replace("\\n", "<nl>").replace("\n", "<nl>").split("\\s+")).iterator();
        // Our finalized list of strings that will be sent instead.
        final List<String> finalList = new ArrayList<>();

        // String builder used to build a string containing the provided number of words.
        final StringBuilder builder = new StringBuilder(defaultColor);
        // Used if there was a last word on a splittable line.
        String builderStart = null;

        // The current word count we are at, incremented in while loop.
        int wordCount = 1;
        while (splitWords.hasNext()) {
            // Our current word.
            final String word = splitWords.next();
            // Check if word count is equal to our wanted words.
            if (wordCount == words) {
                // Add the builder to the list.
                addStringToList(defaultColor, finalList, builder);

                // Reset the builder.
                builder.setLength(0);
                // Check if there was a previous word, if so add it.
                if (builderStart != null && !builderStart.isBlank()) {
                    builder.append(getLastUsedColor(word).isBlank() ? defaultColor : getLastUsedColor(word)).append(builderStart).append(" ");
                    builderStart = null;
                }
                // Reset word count.
                wordCount = 1;
            }

            // Append the word and a space.
            builder.append(word).append(" ");

            // If the word contains a new line
            if (word.contains("<nl>")) {
                // Split the word on the new line.
                final String[] splitter = word.split("<nl>");
                // Get the last word.
                if (splitter[splitter.length - 1] != null && !splitter[splitter.length - 1].isBlank()) {
                    // Add the last word to the builderStart to be appended first to the builder.
                    builderStart = (getLastUsedColor(word).isBlank() ? defaultColor : getLastUsedColor(word)) + splitter[splitter.length - 1];
                    // Delete the last word from the builder.
                    builder.delete(builder.toString().indexOf(splitter[splitter.length - 1]), builder.length());
                }
                // Set the word count to max, so it is forced to insert it into the list.
                wordCount = words;
            } else {
                wordCount++;
            }

            if (!splitWords.hasNext()) {
                if (builderStart != null && builderStart.length() != 0) {
                    builder.append(builderStart);
                }

                addStringToList(defaultColor, finalList, builder);
            }
        }
        return finalList;
    }

    private static void addStringToList(String defaultColor, List<String> finalList, @NotNull StringBuilder builder) {
        final String[] finalWords = builder.toString()
                .replace("<nl>", "\n")
                .replace("\\s", " ")
                // Look behind my beloved.
                .split(Pattern.compile("(?<=\n)", Pattern.CASE_INSENSITIVE).pattern());

        // Words to add.
        final List<String> tempList = new ArrayList<>();
        // The last used color.
        String lastColorString = null;
        // Loop the words.
        for (final String finalWord : finalWords) {
            tempList.add(((lastColorString != null ? (getLastUsedColor(lastColorString).isBlank() ? defaultColor : getLastUsedColor(lastColorString)) : "") +
                    (finalWord.equalsIgnoreCase("<nl>") ? "" : finalWord.strip())));
            lastColorString = finalWord;
        }

        finalList.addAll(tempList);
    }

    /**
     * Returns the last used colors in a string, this will default to gray if none is found.
     *
     * @param string A String
     * @return Returns a {@link String} with the last used color.
     */
    public static @NotNull String getLastUsedColor(@NotNull String string) {
        return getLastUsedColor(string, "<gray>");
    }

    /**
     * Returns the last used colors in a string.
     *
     * @param string A String
     * @param color  The default final color.
     * @return Returns a {@link String} with the last used color.
     */
    public static @NotNull String getLastUsedColor(@NotNull String string, final String color) {
        final Matcher matcher = COLOR_PATTERN.matcher(string);

        String finalColor = color;

        while (matcher.find()) {
            finalColor = matcher.group();
        }

        return finalColor;
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
     * @param separatorColor The color that should be used for the ','
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
    public static @NotNull String convertToRomanNumeral(int number, boolean ignoreLevelOne,
                                                        boolean addSpaceToFront) {
        if (number == 1 && ignoreLevelOne) {
            return "";
        }

        if (number == 0) {
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
     * Utility method to quickly ignore a 1 number.
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
