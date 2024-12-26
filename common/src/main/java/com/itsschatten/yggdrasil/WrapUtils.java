package com.itsschatten.yggdrasil;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String wrapping utilities.
 */
// Fixme: This class badly handles closing colors.
@UtilityClass
public class WrapUtils {

    /**
     * Pattern for targeting color formatting.
     * <p></p>
     * <b>Note:</b> This does not target legacy color formatting, only MiniMessage's formatting.
     */
    private static final Pattern COLOR_PATTERN;

    /**
     * Pattern for targeting end color formatting.
     * <p></p>
     * <b>Note:</b> This does not target legacy color formatting, only MiniMessage's formatting.
     */
    private static final Pattern COLOR_PATTERN_END;

    /**
     * Pattern for targeting text decoration formatting.
     * <p></p>
     * <b>Note:</b> This does not target legacy decoration formatting, only MiniMessage's formatting.
     */
    private static final Pattern DECORATION_PATTERN;

    /**
     * Pattern for targeting text decoration formatting.
     * <p></p>
     * <b>Note:</b> This does not target legacy decoration formatting, only MiniMessage's formatting.
     */
    private static final Pattern DECORATION_PATTERN_END;

    /**
     * Matches both {@link #DECORATION_PATTERN} and {@link #COLOR_PATTERN}
     */
    private static final Pattern ALL_START_PATTERN;

    /**
     * Matches both {@link #DECORATION_PATTERN_END} and {@link #COLOR_PATTERN_END}
     */
    private static final Pattern ALL_END_PATTERN;

    //<editor-fold defaultstate="collapsed" desc="Static initialization.">
    static {
        // Compiles a pattern for <#aaaFFF>, <#123456>, or <named_color>.
        COLOR_PATTERN = Pattern.compile("<(#[a-f0-9]{6}|(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|grey|dark_gray|dark_grey|blue|green|aqua|red|light_purple|yellow|white))>", Pattern.CASE_INSENSITIVE);
        COLOR_PATTERN_END = Pattern.compile("</(#[a-f0-9]{6}|(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|grey|dark_gray|dark_grey|blue|green|aqua|red|light_purple|yellow|white))>", Pattern.CASE_INSENSITIVE);

        // Compiles a pattern for text decorations.
        DECORATION_PATTERN = Pattern.compile("<!?((b(old)?)|i(talic)?|em|u(nderlined)?|st(rikethrough)?|obf(uscated)?)>", Pattern.CASE_INSENSITIVE);
        DECORATION_PATTERN_END = Pattern.compile("</!?((b(old)?)|i(talic)?|em|u(nderlined)?|st(rikethrough)?|obf(uscated)?)>", Pattern.CASE_INSENSITIVE);

        // All patterns.
        ALL_START_PATTERN = Pattern.compile("<(!?(b(old)?|i(talic)?|em|u(nderlined)?|st(rikethrough)?|obf(uscated)?)|#[a-f0-9]{6}|(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|grey|dark_gray|dark_grey|blue|green|aqua|red|light_purple|yellow|white))>", Pattern.CASE_INSENSITIVE);
        ALL_END_PATTERN = Pattern.compile("</(!?(b(old)?|i(talic)?|em|u(nderlined)?|st(rikethrough)?|obf(uscated)?)|#[a-f0-9]{6}|(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|grey|dark_gray|dark_grey|blue|green|aqua|red|light_purple|yellow|white))>", Pattern.CASE_INSENSITIVE);
    }
    // </editor-fold>

    /**
     * Converts a {@link String} into a {@link List} of strings.
     * <p>Default words of 10 and a default color of gray.</p>
     *
     * @param string The {@link String} to convert into a {@link List}
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_ -> new")
    public static @NotNull List<String> convertStringToList(final String string) {
        return convertStringToList(string, null, 45);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     * <p>Default characters of 45.</p>
     *
     * @param string       The {@link String} to convert into a {@link List}
     * @param defaultColor The color that is used as the reset color and the default color of the {@link String}s.
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_, _ -> new")
    public static @NotNull List<String> convertStringToList(final String string, final String defaultColor) {
        return convertStringToList(string, defaultColor, 45);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     * <p>Default color of gray.</p>
     *
     * @param string     The {@link String} to convert into a {@link List}
     * @param characters The characters of each line in the list.
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_, _ -> new")
    public static @NotNull List<String> convertStringToList(final String string, final int characters) {
        return convertStringToList(string, null, characters);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     *
     * @param string       The {@link String} to convert into a {@link List}
     * @param defaultColor The color used as the reset color and the default color of the {@link String}s. Default to {@code <gray>}
     * @param characters   The characters of each line in the list.
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_, _, _ -> new")
    public static @NotNull List<String> convertStringToList(final @NotNull String string, @Nullable String defaultColor, final int characters) {
        // If default is returned null, set to light gray.
        if (defaultColor == null) {
            defaultColor = "<gray>";
        }

        // Color the string; this is here to better support custom tags.
        final String colored = MiniMessage.miniMessage().serialize(StringUtil.color(string));

        // Updated string storage.
        String colorless = colored.replace("<reset>", defaultColor);

        // Replace colors to get an accurate string wrap.
        // There may be a better way for this,
        // but because we're dealing with components and such, it may be challenging.
        final Map<Character, String> replacementMap = new HashMap<>();

        // Because we want to use a different default color, we change any resets to that.
        final Matcher matcher = ALL_START_PATTERN.matcher(colorless);

        // Used to obtain a character for replacement later.
        int iteration = 0;
        while (matcher.find()) {
            final String color = matcher.group();
            // Get our character based on the iteration.
            final char character = Character.forDigit(iteration, Character.MAX_RADIX);

            // Only increment if contained.
            if (!replacementMap.containsValue(color))
                iteration++;

            // Add character to the map and then our color.
            replacementMap.put(character, color);

            // Replace it in the string to help get an accurate string wrap.
            colorless = colorless.replace(color, "§" + character);
        }

        // Replace end colors and mods.
        final Matcher endMatcher = ALL_END_PATTERN.matcher(colorless);
        while (endMatcher.find()) {
            final String color = endMatcher.group();
            // Get our character based on the iteration.
            final char character = Character.forDigit(iteration, Character.MAX_RADIX);

            // Only increment if contained.
            if (!replacementMap.containsValue(color))
                iteration++;

            // Add character to the map and our color.
            replacementMap.put(character, color);

            // Replace it in the string to help get an accurate string wrap.
            colorless = colorless.replace(color, "§" + character);
        }

        // Our finalized list of strings that will be sent instead.
        final List<String> temp = new ArrayList<>(List.of(wrap(colorless, characters, "\\|").split("\n")));

        String lastUsed = defaultColor;
        // Attempt to find the last used colors.
        for (int i = 0; i < temp.size(); i++) {
            final String toSet = buildProperString(temp, i, replacementMap);

            // If it's the first iteration, we just add the default color.
            // If we are on other iterations, we obtain the last used color and modifier to make them multi-line.
            if (i == 0) {
                temp.set(i, lastUsed + toSet);
            } else {
                lastUsed = getLastUsedColor(temp.get(Math.max(0, i - 1)), lastUsed);
                temp.set(i, lastUsed + getLastUsedModifier(temp.get(Math.max(0, i - 1))) + toSet);
            }
        }

        return temp;
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     * <p>Default words of 10 and a default color of gray.</p>
     *
     * @param string The {@link String} to convert into a {@link List}
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_ -> new")
    public static @NotNull List<Component> convertStringToComponentList(final String string) {
        return convertStringToComponentList(string, null, 45);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     * <p>Default characters of 45.</p>
     *
     * @param string       The {@link String} to convert into a {@link List}
     * @param defaultColor The color that is used as the reset color and the default color of the {@link String}s.
     * @return Returns an {@link ArrayList} of {@link Component}s.
     */
    @Contract("_, _ -> new")
    public static @NotNull List<Component> convertStringToComponentList(final String string, final String defaultColor) {
        return convertStringToComponentList(string, defaultColor, 45);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     * <p>Default color of gray.</p>
     *
     * @param string     The {@link String} to convert into a {@link List}
     * @param characters The characters of each line in the list.
     * @return Returns an {@link ArrayList} of {@link Component}s.
     */
    @Contract("_, _ -> new")
    public static @NotNull List<Component> convertStringToComponentList(final String string, final int characters) {
        return convertStringToComponentList(string, null, characters);
    }

    /**
     * Converts a string into a list of colored Components.
     *
     * @param string       The {@link String} to convert into a {@link List}
     * @param defaultColor The color used as the reset color and the default color of the {@link String}s. Default to {@code <gray>}
     * @param characters   The characters of each line in the list.
     * @return Returns an {@link ArrayList} of {@link Component}s.
     */
    @Contract("_, _, _ -> new")
    public static @NotNull List<Component> convertStringToComponentList(final @NotNull String string, String defaultColor, final int characters) {
        // Our finalized list of strings that will be sent instead.
        // This depends on the convertStringToList method, it does the same thing may as well use it.
        final List<String> tempList = new ArrayList<>(convertStringToList(string, defaultColor, characters));

        // Convert all strings into the colored components.
        final List<Component> finalList = new ArrayList<>();
        tempList.forEach((entry) -> finalList.add(StringUtil.color(entry)));

        return finalList;
    }

    /**
     * Builds a String with a replacement map.
     *
     * @param temp           The list to build on.
     * @param i              The iteration in the list.
     * @param replacementMap The map to use as the replacement.
     * @return Returns the string with updated values.
     */
    @NotNull
    private static String buildProperString(@NotNull List<String> temp, int i, @NotNull Map<Character, String> replacementMap) {
        // Get the string from our wrapped list.
        String toSet = temp.get(i).replace("\n", "");

        // Check if we have replacements, then loop and replace all.
        if (!replacementMap.isEmpty()) {
            for (Map.Entry<Character, String> entry : replacementMap.entrySet()) {
                final Character character = entry.getKey();
                // Called color because it's what this method is used for.
                final String color = entry.getValue();

                // Replace the section symbol and our character with our color.
                toSet = toSet.replace("§" + character, color);
            }
        }

        return toSet;
    }

    /**
     * Wrap a String after a certain number of characters.
     *
     * @param input      The String to wrap.
     * @param wrapLength How many characters to go before we wrap.
     * @param wrapOn     What we should immediately wrap on.
     * @return Returns a String with added new lines.
     */
    public static @NotNull String wrap(final @NotNull String input, final int wrapLength, final String wrapOn) {
        final StringBuilder builder = new StringBuilder(input.replace(wrapOn, "\n"));
        int index = 0;
        // Make sure we don't go over the length.
        while (builder.length() > index + wrapLength) {
            // Get the new line location if one is present.
            final int lastLineReturn = builder.lastIndexOf("\n", index + wrapLength);
            if (lastLineReturn > index) {
                // Set last line return to the new index.
                index = lastLineReturn;
            } else {
                // Continue as normal, find the last space.
                index = builder.lastIndexOf(" ", index + wrapLength);
                // We don't have a line to index, break off.
                if (index == -1) {
                    break;
                }
                // Replace the index and one other character with a new line.
                builder.replace(index, index + 1, "\n");
                // Increment index.
                index++;
            }
        }

        // Finally, return the string.
        return builder.toString();
    }


    /**
     * Gets the last used modifier on a String or returns non-italic.
     *
     * @param string The String to get the last modifier for.
     * @return Returns the last modifier used in a String or {@code <!i>}
     * @see #getLastUsedModifier(String, String)
     * @see #findFinalOfPattern(Pattern, Pattern, String, String)
     */
    public static @NotNull String getLastUsedModifier(final String string) {
        return getLastUsedModifier(string, "<!i>");
    }

    /**
     * Gets the last used modifier on a String or returns the fallback.
     *
     * @param string The String to get the last modifier for.
     * @param mod    The modifier to use as a fallback.
     * @return Returns the last modifier used in a String or the fallback.
     * @see #getLastUsedModifier(String)
     * @see #findFinalOfPattern(Pattern, Pattern, String, String)
     */
    public static @NotNull String getLastUsedModifier(@NotNull String string, final String mod) {
        return findFinalOfPattern(DECORATION_PATTERN, DECORATION_PATTERN_END, string, mod);
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
        return findFinalOfPattern(COLOR_PATTERN, COLOR_PATTERN_END, string, color);
    }

    /**
     * Finds the final version of a Pattern. <b>Mainly used in finding last used colors and modifiers.</b>
     *
     * @param findPattern The main pattern we are looking for.
     * @param endPattern  The ending pattern, required because of how we search for colors.
     * @param string      The String to search.
     * @param fallback    The fallback if none is found.
     * @return Returns a String with either the last used color or the fallback.
     */
    private static String findFinalOfPattern(final @NotNull Pattern findPattern, final @NotNull Pattern endPattern, final String string, final String fallback) {
        // Our matchers.
        final Matcher matcher = findPattern.matcher(string);
        final Matcher endMatcher = endPattern.matcher(string);

        // The stuff to return later.
        String finalFound = fallback;
        String endFound = "";

        // Find the string we want.
        while (matcher.find()) {
            finalFound = matcher.group();
        }

        String similarFound = "";
        // Find any endings.
        while (endMatcher.find()) {
            try {
                endFound = endMatcher.group();

                if (!finalFound.isBlank() && endFound.contains(finalFound.substring(1, finalFound.length() - 1))) {
                    similarFound = endFound;
                }
            } catch (IllegalStateException ex) {
                endFound = "";
            }
        }

        if (similarFound.isBlank()) {
            if (endFound.contains(finalFound.substring(1, finalFound.length() - 1))) {
                return fallback;
            }
        } else {
            if (!fallback.isBlank() && endFound.contains(fallback.substring(1, fallback.length() - 1))) {
                return "";
            } else if (endFound.contains(finalFound.substring(1, finalFound.length() - 1))) {
                return fallback;
            }
        }


        return finalFound;
    }

}
