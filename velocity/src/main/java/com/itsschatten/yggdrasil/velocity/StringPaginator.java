package com.itsschatten.yggdrasil.velocity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Paginates a string.
 *
 * @since 1.0.0
 */
public final class StringPaginator {

    /**
     * The string to paginate.
     */
    final String toPaginate;

    /**
     * The pages of this {@link StringPaginator}.
     *
     * @see Page
     */
    final List<Page> pages = new ArrayList<>();

    /**
     * The command to use to navigate between pages, the page number will be automatically appended.
     */
    final String navigationCommand;

    /**
     * Constructs a new {@link StringPaginator}.
     *
     * @param toPaginate The {@link String} to paginate. The String will be split based on the following pattern: {@code [\n|<(br)|(newline)>]}
     */
    public StringPaginator(final @NotNull String toPaginate, final @NotNull String navigationCommand) {
        this.toPaginate = toPaginate;
        this.navigationCommand = navigationCommand.startsWith("/") ? navigationCommand.substring(1) : navigationCommand;

        buildPages();
    }


    /**
     * Determines if we can advance to the next page.
     *
     * @param page The current page we are on.
     * @return Returns {@code true} if the current page is less than the {@link #pages} size.
     */
    public boolean hasNext(final int page) {
        return (page) < pages.size();
    }

    /**
     * Determines if we can return to a previous page.
     *
     * @param page The current page we are on.
     * @return Returns {@code true} if the current page is greater or equal to {@code 2}
     */
    public boolean hasPrevious(final int page) {
        return page >= 2;
    }

    /**
     * Builds a navigation {@link String}.
     *
     * @param page The page we are currently viewing.
     * @return Returns a {@link String} that uses {@link net.kyori.adventure.text.minimessage.MiniMessage} formatting
     * for clickable messages in chat that will change the page the player is viewing.
     */
    public @NotNull String navigation(final int page) {
        // Our builder. This, in theory, will never be empty.
        final StringBuilder builder = new StringBuilder();

        // Variables so we can use them later.
        final boolean hasPrevious = hasPrevious(page);

        // We have a previous page, we can add a back navigation.
        if (hasPrevious) {
            builder.append("<click:run_command:'/").append(navigationCommand).append(" ").append(page - 1).append("'><hover:show_text:'<gray>Return back to page ").append(page - 1).append(".'><secondary><<<</secondary> <primary>Previous</primary></hover></click>");
        }

        // We can go to the next page.
        if (hasNext(page)) {
            // We have a previous page, add a separator.
            if (hasPrevious) {
                builder.append("<gray>   |   </gray>");
            }

            // Next navigation.
            builder.append("<click:run_command:'/").append(navigationCommand).append(" ").append(page + 1)
                    .append("'><hover:show_text:'<gray>Advances to page ").append(page + 1).append(".'><primary>Next</primary> <secondary>>>></secondary></hover></click>");
        }

        // Return the builder.
        return builder.toString();
    }

    /**
     * The total number of pages.
     *
     * @return The size of {@link #pages}
     */
    public int totalPages() {
        return pages.size();
    }

    /**
     * Returns a page based on an {@link Integer}.
     *
     * @param page The page we are requesting.
     * @return The page if one is found, otherwise it throws an {@link IndexOutOfBoundsException}
     * @implNote This implementation does not start counting at 0 and instead starts at 1.
     */
    public Page page(int page) {
        return pages.get(page - 1);
    }

    // Utility method to build the pages for this paginator, this is only called in the constructor.
    private void buildPages() {
        final String[] strings = toPaginate.split("\n|<br>|<newline>");
        final List<String> lines = new ArrayList<>();
        int count = 0;

        for (final String string : strings) {
            count++;
            lines.add(string);

            if (count == 15) {
                pages.add(new Page(lines));
                lines.clear();
                count = 0;
            }
        }

        if (!lines.isEmpty()) {
            pages.add(new Page(lines));
            lines.clear();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringPaginator stringPaginator)) return false;
        return Objects.equals(toPaginate, stringPaginator.toPaginate) && Objects.equals(pages, stringPaginator.pages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toPaginate, pages);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "com.itsschatten.yggdrasil.velocity.StringPaginator{" +
                ", toPaginate='" + toPaginate + '\'' +
                ", pages=" + pages +
                '}';
    }

    /**
     * Represents a page in the {@link StringPaginator}.
     */
    public final static class Page {

        /**
         * The lines of this page.
         */
        final List<String> lines = new ArrayList<>();

        /**
         * Builds a page.
         *
         * @param lines A {@link Collection} of {@link String}s.
         */
        public Page(final @NotNull Collection<String> lines) {
            this.lines.addAll(lines);
        }

        /**
         * Returns {@link #lines} as a {@link String}.
         *
         * @return Using {@link String#join(CharSequence, Iterable)} joins {@link #lines} into a string seperated by a new line character.
         */
        @Contract(" -> new")
        public @NotNull String asString() {
            return String.join("\n", lines);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Page page)) return false;
            return Objects.equals(lines, page.lines);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(lines);
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "com.itsschatten.yggdrasil.velocity.Paginator.Page{" +
                    "lines=" + lines +
                    '}';
        }
    }

}
