package com.itsschatten.yggdrasil.menus.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Utility class to paginate a menu.
 *
 * @param <T> The object.
 */
@Getter
public final class MenuPaginator<T> {

    /**
     * The number of menus/pages.
     * --- GETTER ---
     * Gets the pages.
     *
     * @return The pages.
     */
    @NotNull
    private final Map<Integer, List<T>> pages;

    /**
     * The {@link T} values of this paginator.
     */
    private final List<T> values;

    /**
     * The size of the inventory.
     * --- GETTER ---
     * Gets the size of the inventory.
     *
     * @return Gets the size of the inventory.
     */
    private final int cellSize;

    /**
     * The constructor that sets things.
     *
     * @param cellSize The size of the inventory.
     * @param values   The values that should be added to the inventory.
     */
    public MenuPaginator(int cellSize, @NotNull Iterable<T> values) {
        this.cellSize = cellSize;
        this.values = new ArrayList<>();
        values.forEach(this.values::add);
        this.pages = fillPages(cellSize, this.values);
    }

    /**
     * The constructor that sets things.
     *
     * @param cellSize The size of the inventory.
     * @param values   The values that should be added to the inventory. (As a list.)
     */
    public MenuPaginator(int cellSize, Collection<T> values) {
        this.cellSize = cellSize;
        this.values = new ArrayList<>(values);
        this.pages = fillPages(cellSize, this.values);
    }

    /**
     * Recalculates the pages of this paginator.
     */
    public void recalculate() {
        this.pages.clear();
        this.pages.putAll(fillPages(this.cellSize, this.values));
    }

    /**
     * Fills the pages.
     *
     * @param cellSize Gets the size of the inventory.
     * @param values   The stuff that is to be added to the inventory.
     * @return Returns the pages.
     */
    private @NotNull Map<Integer, List<T>> fillPages(int cellSize, @NotNull List<T> values) {
        final Map<Integer, List<T>> pages = new HashMap<>();
        final int pageAmount = Math.max(1, values.size() == cellSize ? 1 : (values.size() / cellSize) + ((values.size() % cellSize) != 0 ? 1 : 0));

        for (int pageNumber = 0; pageNumber < pageAmount; pageNumber++) {
            final List<T> pageValues = new ArrayList<>();

            final int down = cellSize * pageNumber;
            final int up = down + cellSize;

            for (int valueIndex = down; valueIndex < up; valueIndex++)
                if (valueIndex < values.size()) {
                    final T page = values.get(valueIndex);

                    pageValues.add(page);
                } else break;

            pages.put(pageNumber, pageValues);
        }

        return pages;
    }

}
