package com.itsschatten.yggdrasil.velocity;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String wrapping utilities.
 *
 * @since 1.0.0
 */
@UtilityClass
public class WrapUtils {

    /**
     * The formatting pattern containing most all the patterns to match.
     */
    private static final Pattern TOKENS;

    //<editor-fold defaultstate="collapsed" desc="Static initialization.">
    static {
        // All patterns.
        TOKENS = Pattern.compile("<[^>]*>", Pattern.CASE_INSENSITIVE);
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
        return convertStringToList(string, 45);
    }

    /**
     * Converts a {@link String} into a {@link List} of strings.
     *
     * @param string     The {@link String} to convert into a {@link List}
     * @param characters The characters of each line in the list.
     * @return Returns an {@link ArrayList} of {@link String}s.
     */
    @Contract("_, _ -> new")
    public static @NotNull List<String> convertStringToList(final @NotNull String string, final int characters) {
        // Color the string; this is here to better support custom tags.
        final String colored = StringUtil.miniMessage().build().serialize(StringUtil.color(string));

        return new ArrayList<>(List.of(wrap(colored, characters, "\\|").split("\n")));
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
        return convertStringToComponentList(string, 45);
    }

    /**
     * Converts a string into a list of colored Components.
     *
     * @param string     The {@link String} to convert into a {@link List}
     * @param characters The characters of each line in the list.
     * @return Returns an {@link ArrayList} of {@link Component}s.
     */
    @Contract("_, _ -> new")
    public static @NotNull List<Component> convertStringToComponentList(final @NotNull String string, final int characters) {
        // Our finalized list of strings that will be sent instead.
        // This depends on the convertStringToList method, it does the same thing may as well use it.
        final List<String> tempList = new ArrayList<>(convertStringToList(string, characters));

        // Convert all strings into the colored components.
        final List<Component> finalList = new ArrayList<>();
        tempList.forEach((entry) -> finalList.add(StringUtil.color(entry)));

        return finalList;
    }

    /**
     * Wrap a String after a certain number of characters.
     *
     * @param input      The String to wrap.
     * @param wrapLength How many characters to go before we wrap.
     * @param wrapOn     What we should immediately wrap on.
     * @return Returns a String with added new lines.
     */
    public static @NotNull String wrap(String input, final int wrapLength, final String wrapOn) {
        if (input == null) return "";
        if (input.isEmpty() || wrapLength <= 0) {
            return input;
        }

        // Implicitly replace any wrapOn tokens with a new line.
        input = input.replace(wrapOn, "\n");

        final StringBuilder wrappedText = new StringBuilder();
        int currentLineLength = 0;
        int lastSpaceIndex = -1;
        final Deque<String> formattingStack = new ArrayDeque<>();
        final StringBuilder currentLinePrefix = new StringBuilder();

        final Matcher matcher = TOKENS.matcher(input);
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == '\n') {
                wrappedText.append('\n');
                wrappedText.append(currentLinePrefix);
                currentLineLength = 0;
                lastSpaceIndex = -1;
                continue;
            }

            matcher.region(i, input.length());
            if (matcher.lookingAt()) {
                final String token = matcher.group();
                final String sanitized = token.replaceAll("(:#?[a-z0-9_\\-.]+(?::#?[a-z0-9_\\-.]+)?)?", "").replace("<", "").replace(">", "").replace("/", "").replace("!", "");
                wrappedText.append(token);

                if (StringUtil.miniMessage().build().tags().has(sanitized)) {
                    // We don't need to handle <reset> tags as those are already handled via MiniMessage already.
                    if (token.startsWith("</")) {
                        // If we have formatting and the current formatting can be closed by this tag.
                        if (!formattingStack.isEmpty() && formattingStack.peek().contains(sanitized)) {
                            formattingStack.pop();

                            if (formattingStack.isEmpty()) {
                                currentLinePrefix.setLength(0);
                            } else {
                                currentLinePrefix.setLength(0);

                                // Temp to ensure we maintain any formatting.
                                final Deque<String> tempStack = new ArrayDeque<>();
                                while (!formattingStack.isEmpty()) {
                                    tempStack.push(formattingStack.pop());
                                }

                                // Rebuild stack and prefix.
                                while (!tempStack.isEmpty()) {
                                    final String prefixToken = tempStack.pop();
                                    currentLinePrefix.append(prefixToken);
                                    formattingStack.push(prefixToken); // Restore to original stack (Deque)
                                }
                            }
                        }
                    } else {
                        // Push opening tags.
                        formattingStack.push(token);
                        currentLinePrefix.append(token);
                    }

                    i += token.length() - 1;
                    continue;
                }
            }

            // Maintain last space.
            if (currentChar == ' ') {
                lastSpaceIndex = wrappedText.length();
            }

            // Append the character.
            wrappedText.append(currentChar);
            currentLineLength++;

            // Check if we need to wrap.
            if (currentLineLength > wrapLength) {

                // Wrap on the last space index.
                if (lastSpaceIndex != -1) {
                    wrappedText.insert(lastSpaceIndex, '\n');

                    // Validation to ensure that we need to add the current line prefix.
                    // This is required because wrapping near a formatting may cause double prefixes.
                    final String last = wrappedText.substring(wrappedText.lastIndexOf(" ") + 1);
                    boolean added = false;
                    // Only valid if we start with a less than.
                    if (last.startsWith("<")) {
                        final Matcher match = TOKENS.matcher(last);
                        // Must have at least one match.
                        if (match.find()) {
                            // Get the first (which should be the last used formatting anyway)
                            final String group = match.group();
                            // Doesn't start with the match, add the prefix.
                            if (!last.startsWith(group)) {
                                wrappedText.insert(lastSpaceIndex + 1, currentLinePrefix);
                                currentLineLength = wrappedText.length() - lastSpaceIndex - 1 - currentLinePrefix.length();
                                added = true;
                            } else {
                                currentLineLength = wrappedText.length() - lastSpaceIndex - 1;
                            }
                        } else {
                            // Doesn't match, add the prefix.
                            wrappedText.insert(lastSpaceIndex + 1, currentLinePrefix);
                            currentLineLength = wrappedText.length() - lastSpaceIndex - 1 - currentLinePrefix.length();
                            added = true;
                        }
                    } else {
                        // Default to adding the prefix.
                        wrappedText.insert(lastSpaceIndex + 1, currentLinePrefix);
                        currentLineLength = wrappedText.length() - lastSpaceIndex - 1 - currentLinePrefix.length();
                        added = true;
                    }

                    // Remove leading spaces. (after a prefix)
                    final int firstCharAfterPrefixIndex = lastSpaceIndex + 1 + (added ? currentLinePrefix.length() : 0);
                    if (firstCharAfterPrefixIndex < wrappedText.length() && wrappedText.charAt(firstCharAfterPrefixIndex) == ' ') {
                        wrappedText.deleteCharAt(firstCharAfterPrefixIndex);
                        currentLineLength--;
                    }

                } else {
                    // Handles long text with no spaces.
                    wrappedText.append('\n').append(currentLinePrefix);
                    currentLineLength = wrappedText.length() - wrappedText.lastIndexOf("\n") - 1 - currentLinePrefix.length();

                    // Remove leading spaces.
                    // (after a prefix).
                    // Honestly, this likely shouldn't exist as reaching this point there are likely no spaces.
                    final int firstCharAfterPrefixIndex = wrappedText.lastIndexOf("\n") + 1 + currentLinePrefix.length();
                    if (firstCharAfterPrefixIndex < wrappedText.length() && wrappedText.charAt(firstCharAfterPrefixIndex) == ' ') {
                        wrappedText.deleteCharAt(firstCharAfterPrefixIndex);
                        currentLineLength--;
                    }
                }

                // Always reset the space index to -1 after a wrap
                lastSpaceIndex = -1;
            }
        }

        return wrappedText.toString();
    }

}
