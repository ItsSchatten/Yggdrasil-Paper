package com.itsschatten.yggdrasil.menus.types;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import com.itsschatten.yggdrasil.menus.utils.MenuPage;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A menu that can show different pages based on the page viewed without the need to open a new menu each time.
 */
public abstract class PageMenu<T extends MenuHolder> extends StandardMenu<T> {

    /**
     * The list of all registered page buttons, this is WILL change throughout the menu's life.
     */
    private final List<Button<T>> registeredPageButtons = new ArrayList<>();

    /**
     * The default {@link ItemStack}, designated by {@link ItemCreator}, that will be placed in previous page button places.
     */
    @NotNull
    private final ItemCreator defaultItem;

    /**
     * The page that is currently being viewed.
     * -- GETTER --
     * Get the currently viewed page.
     *
     * @return An integer representing the currently viewed page.
     */
    @Getter
    @Accessors(fluent = true)
    private int viewedPage = 1;

    /**
     * The currently viewed page.
     */
    private MenuPage<T> currentPage;

    /**
     * The previously viewed page.
     */
    private MenuPage<T> previousPage;

    /**
     * Constructs a PageMenu.
     *
     * @param parent      The parent for this menu, used to return to later.
     * @param defaultItem The item that will replace previously placed page buttons.
     */
    public PageMenu(@Nullable Menu<T> parent, String title, int size, ItemCreator defaultItem) {
        super(parent, title, size);
        this.defaultItem = defaultItem == null ? ItemCreator.of(Material.AIR).build() : defaultItem;
    }

    /**
     * The list of {@link MenuPage}.
     *
     * @return Returns a {@link List} of {@link MenuPage}s.
     */
    public abstract @NotNull List<MenuPage<T>> makePages();

    /**
     * A "constant" value of pages.
     *
     * @return Returns {@link #makePages()}.
     */
    public final @NotNull List<MenuPage<T>> pages() {
        return makePages();
    }

    /**
     * Returns if we have a previous page or not.
     *
     * @return Returns {@code true} if {@link #previousPage} is not {@code null}.
     */
    public final boolean hasPreviousPage() {
        return this.previousPage != null;
    }

    /**
     * Registers a collection of buttons.
     *
     * @param buttons The buttons to register.
     */
    public final void registerPageButtons(Collection<Button<T>> buttons) {
        registeredPageButtons.addAll(buttons);
    }

    /**
     * Registers an array of buttons.
     *
     * @param buttons The buttons to register.
     */
    @SafeVarargs
    public final void registerPageButtons(Button<T>... buttons) {
        registeredPageButtons.addAll(Arrays.stream(buttons).collect(Collectors.toSet()));
    }

    /**
     * {@inheritDoc}
     *
     * @param position
     * @return Returns {@code true} if, and only if, a {@link Button} was found to be in the same position as the one provided.
     */
    @Override
    public boolean isSlotTakenByButton(InventoryPosition position) {
        return registeredPageButtons.stream()
                .filter(button -> button.getPermission() == null || holder().hasPermission(button.getPermission()))
                .anyMatch(button -> button.getPosition().equals(position))
                || super.isSlotTakenByButton(position);
    }

    /**
     * Clear the previous page's items and the list of registered buttons.
     */
    public final void clearPreviousPage() {
        this.previousPage.positions().forEach(pos -> forceSet(pos, defaultItem));
        registeredPageButtons.clear();
    }

    /**
     * Switches to the provided page.
     *
     * @param page The page to switch to.
     * @throws IllegalStateException Thrown if the page number is invalid.
     */
    @ApiStatus.Internal
    public final void switchPage(final int page) {
        if (page > pages().size() || page < 1) {
            throw new IllegalStateException("Invalid page number: " + page + ". Must be between 1 and " + pages().size());
        }

        // We can ignore changing to the same page.
        if (viewedPage == page) {
            return;
        }

        this.viewedPage = page;
        drawPage();
    }

    /**
     * Refresh the currently viewed page.
     */
    public final void refreshPage() {
        drawPage();
    }

    /**
     * Draws the page to the menu.
     */
    private void drawPage() {
        final List<MenuPage<T>> pages = this.pages();
        final MenuPage<T> menuPage = pages.get(viewedPage - 1);

        if (this.currentPage != null && this.currentPage != menuPage)
            this.previousPage = this.currentPage;

        this.currentPage = menuPage;

        menuPage.register(this);
        pages.forEach(page -> registerPageButtons(page.navButton().make(viewedPage)));
        drawListOfButtons(registeredPageButtons);
    }

    /**
     * Get a {@link Button} from the {@link #registeredPageButtons} based on the {@link ItemStack} provided.
     *
     * @param stack    The item stack clicked.
     * @param position The position clicked.
     * @return Returns {@link #getButtonImpl(ItemStack, InventoryPosition, List)}.
     */
    public final Button<T> getPageButton(final ItemStack stack, InventoryPosition position) {
        if (stack == null) return null;

        return getButtonImpl(stack, position, registeredPageButtons);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void formInventory() {
        super.formInventory();
        drawPage();
    }
}
