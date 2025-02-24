package com.itsschatten.yggdrasil.menus.buttons.premade;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.buttons.impl.PageNavigationButtonImpl;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuRunnable;
import lombok.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public final class PageNavigationButton {

    /**
     * The builder of for this button item.
     */
    private final @NotNull Function<Integer, Supplier<ItemCreator.ItemCreatorBuilder>> item;

    /**
     * The builder for when this button is active.
     */
    private final @Nullable Function<Integer, Supplier<ItemCreator.ItemCreatorBuilder>> active;

    /**
     * The page number to switch to.
     */
    private final int pageNumber;

    /**
     * The {@link InventoryPosition} that this button will appear in a menu.
     */
    private final InventoryPosition position;

    /**
     * A runnable that can be executed on the menu, run before switching pages.
     */
    private final MenuRunnable runnable;

    @Builder(toBuilder = true)
    public PageNavigationButton(@NotNull Function<Integer, Supplier<ItemCreator.ItemCreatorBuilder>> item,
                                @Nullable Function<Integer, Supplier<ItemCreator.ItemCreatorBuilder>> active,
                                int pageNumber, InventoryPosition position, MenuRunnable runnable) {
        this.item = item;
        this.active = active;
        this.pageNumber = pageNumber;
        this.position = position;
        this.runnable = runnable;
    }

    /**
     * Makes the actual button.
     *
     * @return Returns a new {@link PageNavigationButtonImpl}.
     */
    @Contract("_ -> new")
    public @NotNull PageNavigationButtonImpl make(int page) {
        if (page == pageNumber && active != null) {
            return new PageNavigationButtonImpl(active.apply(page), pageNumber, position, runnable);
        }

        return new PageNavigationButtonImpl(item.apply(page), pageNumber, position, runnable);
    }

    public static class PageNavigationButtonBuilder {

        public PageNavigationButtonBuilder position(final InventoryPosition position) {
            this.position = position;
            return this;
        }

        public PageNavigationButtonBuilder position(final int row, final int column) {
            return position(InventoryPosition.of(row, column));
        }

    }

}
