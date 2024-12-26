package com.itsschatten.yggdrasil.velocity;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

/**
 * Utility class to get a {@link String} from a specified long.
 *
 * @since 1.0.0
 */
@UtilityClass
public class TimeUtils {

    private static final DateTimeFormatter SMALL_DATE_TIME_FORMATTER;
    private static final DateTimeFormatter FULL_DATE_TIME_FORMATTER;
    private static final DateTimeFormatter FULL_YEAR_DATE_TIME_FORMATTER;

    static {
        SMALL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive()
                .appendPattern("MM/dd[ ][h][:mm][ ][a]").toFormatter();

        FULL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive()
                .appendPattern("MM/dd/yy[ ][h][:mm][ ][a]").toFormatter();

        FULL_YEAR_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive()
                .appendPattern("MM/dd/yyyy[ ][h][:mm][ ][a]").toFormatter();
    }

    /**
     * Converts a primitive long into a {@link String} that will format it properly with time labels.
     *
     * @param timeLong A time in long form.
     * @return Returns a string with appended time labels.
     */
    public static @NotNull String getTimeAsStringWithMs(long timeLong) {
        StringBuilder timeString = new StringBuilder();

        long time = timeLong;

        time = appendTime(time, TimeUnits.YEAR_MS.getAsLong(), "years", timeString);
        time = appendTime(time, TimeUnits.MONTH_MS.getAsLong(), "months", timeString);
        time = appendTime(time, TimeUnits.WEEK_MS.getAsLong(), "weeks", timeString);
        time = appendTime(time, TimeUnits.DAY_MS.getAsLong(), "days", timeString);
        time = appendTime(time, TimeUnits.HOUR_MS.getAsLong(), "hours", timeString);
        time = appendTime(time, TimeUnits.MINUTE_MS.getAsLong(), "minutes", timeString);
        time = appendTime(time, TimeUnits.SECOND_MS.getAsLong(), "seconds", timeString);

        if (time != 0) {
            timeString.append(", ").append(time).append(" ms");
        }

        return getString(timeString);
    }

    /**
     * Converts a primitive long into a {@link String} that will format it properly with time labels.
     *
     * @param timeLong A time in long form.
     * @return Returns a string with appended time labels.
     */
    public static @NotNull String getTimeAsString(long timeLong) {
        StringBuilder timeString = new StringBuilder();

        long time = timeLong;

        time = appendTime(time, TimeUnits.YEAR_MS.getAsLong(), "years", timeString);
        time = appendTime(time, TimeUnits.MONTH_MS.getAsLong(), "months", timeString);
        time = appendTime(time, TimeUnits.WEEK_MS.getAsLong(), "weeks", timeString);
        time = appendTime(time, TimeUnits.DAY_MS.getAsLong(), "days", timeString);
        time = appendTime(time, TimeUnits.HOUR_MS.getAsLong(), "hours", timeString);
        time = appendTime(time, TimeUnits.MINUTE_MS.getAsLong(), "minutes", timeString);
        appendTime(time, TimeUnits.SECOND_MS.getAsLong(), "seconds", timeString);

        return getString(timeString);
    }

    /**
     * Converts a primitive long into a {@link String} that will be formatted with proper time labels.
     *
     * @param timeLong A time in long form.
     * @return Returns a string with the appended time labels.
     */
    public static @NotNull String getTimeAsStringShort(long timeLong) {
        StringBuilder timeString = new StringBuilder();

        long time = timeLong;

        time = appendTime(time, TimeUnits.YEAR_MS.getAsLong(), "y", timeString);
        time = appendTime(time, TimeUnits.MONTH_MS.getAsLong(), "m", timeString);
        time = appendTime(time, TimeUnits.WEEK_MS.getAsLong(), "w", timeString);
        time = appendTime(time, TimeUnits.DAY_MS.getAsLong(), "d", timeString);
        time = appendTime(time, TimeUnits.HOUR_MS.getAsLong(), "h", timeString);
        time = appendTime(time, TimeUnits.MINUTE_MS.getAsLong(), "m", timeString);
        appendTime(time, TimeUnits.SECOND_MS.getAsLong(), "s", timeString);

        return _getString(timeString).replace(" and", "");
    }

    /**
     * Converts a primitive long into a {@link String} that will format it properly with time labels.
     *
     * @param timeLong A time in long form.
     * @return Returns a string with appended time labels.
     */
    public static @NotNull String getMinecraftTimeAsStringWithMs(long timeLong) {
        StringBuilder timeString = new StringBuilder();

        long time = timeLong;

        time = appendTime(time, MinecraftTimeUnits.YEAR_MS.getAsLong(), "years", timeString);
        time = appendTime(time, MinecraftTimeUnits.MONTH_MS.getAsLong(), "months", timeString);
        time = appendTime(time, MinecraftTimeUnits.WEEK_MS.getAsLong(), "weeks", timeString);
        time = appendTime(time, MinecraftTimeUnits.DAY_MS.getAsLong(), "days", timeString);
        time = appendTime(time, MinecraftTimeUnits.HOUR_MS.getAsLong(), "hours", timeString);
        time = appendTime(time, MinecraftTimeUnits.MINUTE_MS.getAsLong(), "minutes", timeString);
        time = appendTime(time, MinecraftTimeUnits.SECOND_MS.getAsLong(), "seconds", timeString);

        if (time != 0) {
            timeString.append(", ").append(time).append(" ms");
        }

        return getString(timeString);
    }

    /**
     * Converts a primitive long into a {@link String} that will format it properly with time labels,
     * using {@link MinecraftTimeUnits}.
     *
     * @param timeLong A time in long form.
     * @return Returns a string with appended time labels.
     */
    public static @NotNull String getMinecraftTimeAsString(long timeLong) {
        StringBuilder timeString = new StringBuilder();

        long time = timeLong;

        time = appendTime(time, MinecraftTimeUnits.YEAR_MS.getAsLong(), "years", timeString);
        time = appendTime(time, MinecraftTimeUnits.MONTH_MS.getAsLong(), "months", timeString);
        time = appendTime(time, MinecraftTimeUnits.WEEK_MS.getAsLong(), "weeks", timeString);
        time = appendTime(time, MinecraftTimeUnits.DAY_MS.getAsLong(), "days", timeString);
        time = appendTime(time, MinecraftTimeUnits.HOUR_MS.getAsLong(), "hours", timeString);
        time = appendTime(time, MinecraftTimeUnits.MINUTE_MS.getAsLong(), "minutes", timeString);
        appendTime(time, MinecraftTimeUnits.SECOND_MS.getAsLong(), "seconds", timeString);

        return getString(timeString);
    }

    /**
     * Converts a primitive long into a {@link String} that will be formatted with proper time labels,
     * using {@link MinecraftTimeUnits}.
     *
     * @param timeLong A time in long form.
     * @return Returns a string with the appended time labels.
     */
    public static @NotNull String getMinecraftTimeAsStringShort(long timeLong) {
        StringBuilder timeString = new StringBuilder();

        long time = timeLong;

        time = appendTime(time, MinecraftTimeUnits.YEAR_MS.getAsLong(), "y", timeString);
        time = appendTime(time, MinecraftTimeUnits.MONTH_MS.getAsLong(), "m", timeString);
        time = appendTime(time, MinecraftTimeUnits.WEEK_MS.getAsLong(), "w", timeString);
        time = appendTime(time, MinecraftTimeUnits.DAY_MS.getAsLong(), "d", timeString);
        time = appendTime(time, MinecraftTimeUnits.HOUR_MS.getAsLong(), "h", timeString);
        time = appendTime(time, MinecraftTimeUnits.MINUTE_MS.getAsLong(), "m", timeString);
        appendTime(time, MinecraftTimeUnits.SECOND_MS.getAsLong(), "s", timeString);

        return _getString(timeString).replace(" and", "");
    }

    /**
     * Convert primitive long value to be in a similar style to a digital clock, using {@link MinecraftTimeUnits}.
     *
     * @param timeLong The time we are converting.
     * @return The string.
     */
    public static @NotNull String getMinecraftTimeClock(long timeLong) {
        final StringBuilder timeString = new StringBuilder();
        long time = timeLong;

        int hours = 0, minutes = 0, seconds = 0;

        if (time >= MinecraftTimeUnits.HOUR_MS.getAsLong()) {
            hours = (int) (time / MinecraftTimeUnits.HOUR_MS.getAsLong());
            time -= hours * MinecraftTimeUnits.HOUR_MS.getAsLong();
        }

        if (time >= MinecraftTimeUnits.MINUTE_MS.getAsLong()) {
            minutes = (int) (time / MinecraftTimeUnits.MINUTE_MS.getAsLong());
            time -= minutes * MinecraftTimeUnits.MINUTE_MS.getAsLong();
        }

        if (time >= MinecraftTimeUnits.SECOND_MS.getAsLong()) {
            seconds = (int) (time / MinecraftTimeUnits.SECOND_MS.getAsLong());
        }

        timeString.append(hours > 0 ? hours + ":" : "").append(minutes > 0 ? (hours >= 1 ? (minutes > 9 ? minutes : "0" + minutes) : minutes) + ":" : "00:").append(seconds > 0 ? (seconds > 9 ? seconds : "0" + seconds) : "00");
        return timeString.toString();
    }


    /**
     * Gets the length of time from a {@link String} and returns it as milliseconds.
     * <p><b>Note:</b> This will not work with a mixture of non-spaced and spaced time variables.</p>
     *
     * @param string The {@link String} we are getting the time from.
     * @return The time in the form of a long value.
     */
    public static long getTimeFromString(@NotNull String string) {
        if (string.isEmpty()) {
            return 0L;
        }
        // Valid modifiers: Y, M, W, D, h, m, s
        String updatedString = string.replace("d", "D").replace("w", "W").replace("y", "Y");
        String[] strings = updatedString.split(" "); // Split by spaces in the string.
        long time = 0; // Current time.


        if (strings.length == 1) {
            // Use the positive look behind to properly split a jumbled string. (i hate regex.)
            strings = updatedString.split("(?<=[YMWDhms])");
        }

        for (String s : strings) { // Loop through all strings split.
            // Replace day, week, and year tokens with properly capitalized ones.
            char character = s.charAt(s.length() - 1); // Get the character to properly parse time.
            if (Character.isUpperCase(character)) {
                if (character == 'M') {
                    time += Period.parse("P" + s).getMonths() * TimeUnits.MONTH_MS.getAsLong(); // Gets the months from the provided string.
                    continue;
                }

                if (character == 'Y') {
                    time += Period.parse("P" + s).getYears() * TimeUnits.YEAR_MS.getAsLong(); // gets the years from the provided string.
                    continue;
                }

                time += Period.parse("P" + s).getDays() * TimeUnits.DAY_MS.getAsLong(); // Gets the days from the provided string.
            } else {
                time += Duration.parse("PT" + s).toMillis(); // Gets the remaining: hours, minutes, seconds and parses them to milliseconds.
            }
        }

        return time;
    }


    /**
     * Gets the string.
     *
     * @param timeString The {@link StringBuilder} we should use.
     * @return A {@link String} from the StringBuilder, properly format with an 'and' before the last entry.
     */
    @NotNull
    private static String getString(@NotNull StringBuilder timeString) {
        if (timeString.isEmpty()) {
            return "0 seconds";
        }

        if (timeString.substring(2).trim().contains(",")) {
            return timeString.insert(nthLastIndexOf(1, ",", timeString.toString()) + 1, " and").substring(2).trim();
        }
        return timeString.substring(2).trim();
    }

    /**
     * Gets a string.
     *
     * @param timeString The {@link StringBuilder} we should use.
     * @return The full StringBuilder String with no additions.
     */
    @NotNull
    private static String _getString(@NotNull StringBuilder timeString) {
        if (timeString.isEmpty()) {
            return "0 seconds";
        }

        return timeString.substring(2).trim();
    }

    /**
     * Appends the time to a {@link StringBuilder}.
     *
     * @param time     The time we should use for our calculations.
     * @param unitInMS The units we should use for or calculations.
     * @param name     The name of the units used.
     * @param builder  The {@link StringBuilder} we should append the {@link String} too.
     * @return Returns the remainder from our calculations.
     */
    private static long appendTime(long time, long unitInMS, String name, StringBuilder builder) {
        long timeInUnits = (time - (time % unitInMS)) / unitInMS;

        if (timeInUnits > 0) {
            builder.append(", ").append(timeInUnits).append(name.length() > 1 ? ' ' : "").append(name.length() > 1 ? (timeInUnits == 1 ? name.substring(0, name.length() - 1) : name) : name);
        }

        return time - timeInUnits * unitInMS;
    }

    /**
     * Gets the nth-index of a specified character in a {@link String}.
     *
     * @param nth    The place we are looking for.
     * @param ch     The character we are looking for.
     * @param string The string we are searching.
     * @return Loops itself until it finds the appropriate character.
     */
    static int nthLastIndexOf(int nth, String ch, String string) {
        if (nth <= 0) return string.length();
        return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
    }

    /**
     * Gets the time in provided in Minecraft ticks.
     *
     * @param hours   The hours.
     * @param minutes The minutes.
     * @param seconds The seconds.
     * @return Returns all hours, minutes, seconds, added together, in ticks.
     */
    public static long getTimeTicks(final int hours, final int minutes, final int seconds) {
        return (hours * MinecraftTimeUnits.HOUR_MS.getAsLong()) + (minutes * MinecraftTimeUnits.MINUTE_MS.getAsLong()) + (seconds * MinecraftTimeUnits.SECOND_MS.getAsLong());
    }

    /**
     * Gets the time in provided in Minecraft ticks.
     *
     * @param minutes The minutes.
     * @param seconds The seconds.
     * @return Returns all hours, minutes, seconds, added together, in ticks.
     * @see #getTimeTicks(int, int, int)
     */
    public static long getTimeTicks(final int minutes, final int seconds) {
        return getTimeTicks(0, minutes, seconds);
    }

    /**
     * Gets the time in provided in Minecraft ticks.
     *
     * @param hours   The hours.
     * @param minutes The minutes.
     * @param seconds The seconds.
     * @return Returns all hours, minutes, seconds, added together, in ticks.
     */
    public static int getTimeTicksInt(final int hours, final int minutes, final int seconds) {
        return Math.toIntExact((hours * MinecraftTimeUnits.HOUR_MS.getAsLong()) + (minutes * MinecraftTimeUnits.MINUTE_MS.getAsLong()) + (seconds * MinecraftTimeUnits.SECOND_MS.getAsLong()));
    }

    /**
     * Gets the time in provided in Minecraft ticks.
     *
     * @param minutes The minutes.
     * @param seconds The seconds.
     * @return Returns all hours, minutes, seconds, added together, in ticks.
     * @see #getTimeTicks(int, int, int)
     */
    public static int getTimeTicksInt(final int minutes, final int seconds) {
        return getTimeTicksInt(0, minutes, seconds);
    }

    /**
     * Get a {@link LocalDateTime} from a {@link String}, this will use the current time's year.
     * To change the year of this time use {@link LocalDateTime#withYear(int)}.
     *
     * <p>This method uses {@link #SMALL_DATE_TIME_FORMATTER} parsed as case-insensitive <code>MM/dd[ ][h][:mm][ ][a]</code>.
     * See {@link DateTimeFormatterBuilder#appendPattern(String) this method's documentation} for more information on the variables above.</p>
     *
     * @param toParse The {@link String} to parse into a LocalDateTime
     * @return A {@link LocalDateTime} from the parsed String and using the current Year.
     * @see DateTimeFormatterBuilder#appendPattern(String)
     * @see LocalDateTime
     * @see LocalDateTime#withYear(int)
     */
    public static @NotNull LocalDateTime getLocalDateTime(final String toParse) {
        final TemporalAccessor parsed = SMALL_DATE_TIME_FORMATTER.parse(toParse);

        return LocalDateTime.of(LocalDateTime.now().getYear(),
                Month.of(parsed.get(ChronoField.MONTH_OF_YEAR)), parsed.get(ChronoField.DAY_OF_MONTH),
                parsed.isSupported(ChronoField.HOUR_OF_DAY) ? parsed.get(ChronoField.HOUR_OF_DAY) : 0,
                parsed.isSupported(ChronoField.MINUTE_OF_HOUR) ? parsed.get(ChronoField.MINUTE_OF_HOUR) : 0);
    }

    /**
     * Utility to make working with TimeUnits more "human friendly".
     */
    @Getter
    public enum TimeUnits {
        /**
         * A Minecraft game tick in real mills.
         */
        TICK_MS(50),
        /**
         * A second in millis.
         */
        SECOND_MS(1000),
        /**
         * A minute in millis.
         */
        MINUTE_MS(SECOND_MS.getAsLong() * 60),
        /**
         * An hour in millis.
         */
        HOUR_MS(MINUTE_MS.getAsLong() * 60),
        /**
         * A day in millis.
         */
        DAY_MS(HOUR_MS.getAsLong() * 24),
        /**
         * A week in millis.
         */
        WEEK_MS(DAY_MS.getAsLong() * 7),
        /**
         * A month in millis.
         */
        MONTH_MS(DAY_MS.getAsLong() * 30),
        /**
         * A year in millis.
         */
        YEAR_MS(DAY_MS.getAsLong() * 365);

        /**
         * Get the value of the enum as a primitive long.
         */
        final long asLong;

        /**
         * Constructor.
         *
         * @param time The millis.
         */
        TimeUnits(long time) {
            this.asLong = time;
        }

    }

    /**
     * Utility class to make working with Minecraft ticks easier.
     */
    @Getter
    public enum MinecraftTimeUnits {
        /**
         * A second in ticks.
         */
        SECOND_MS(20),
        /**
         * A minute in ticks.
         */
        MINUTE_MS(SECOND_MS.getAsLong() * 60),
        /**
         * An hour in ticks.
         */
        HOUR_MS(MINUTE_MS.getAsLong() * 60),
        /**
         * A day in ticks.
         */
        DAY_MS(HOUR_MS.getAsLong() * 24),
        /**
         * A week in ticks.
         */
        WEEK_MS(DAY_MS.getAsLong() * 7),
        /**
         * A month in ticks.
         */
        MONTH_MS(DAY_MS.getAsLong() * 30),
        /**
         * A year in ticks.
         */
        YEAR_MS(DAY_MS.getAsLong() * 365);

        /**
         * Get the enum value as a primitive long.
         */
        final long asLong;

        /**
         * Constructor.
         *
         * @param time The time in ticks.
         */
        MinecraftTimeUnits(long time) {
            this.asLong = time;
        }

        /**
         * @return Return the value of this ordinal as an integer.
         * @throws ArithmeticException Thrown if number exceeds {@link Integer#MAX_VALUE}
         */
        public final int getAsInt() {
            return Math.toIntExact(getAsLong());
        }

    }

}
