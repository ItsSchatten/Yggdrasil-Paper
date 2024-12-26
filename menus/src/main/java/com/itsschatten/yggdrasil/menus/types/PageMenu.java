package com.itsschatten.yggdrasil.menus.types;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuInventory;
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
public abstract class PageMenu extends StandardMenu {

    /**
     * The list of all registered page buttons, this is WILL change throughout the menu's life.
     */
    private final List<Button> registeredPageButtons = new ArrayList<>();

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
    private MenuPage currentPage;

    /**
     * The previously viewed page.
     */
    private MenuPage previousPage;

    /**
     * Constructs a PageMenu.
     *
     * @param parent      The parent for this menu, used to return to later.
     * @param defaultItem The item that will replace previously placed page buttons.
     */
    public PageMenu(@Nullable Menu parent, ItemCreator defaultItem) {
        super(parent);
        this.defaultItem = defaultItem == null ? ItemCreator.of(Material.AIR).build() : defaultItem;
    }

    /**
     * The list of {@link MenuPage}.
     *
     * @return Returns a {@link List} of {@link MenuPage}s.
     */
    public abstract @NotNull List<MenuPage> makePages();

    /**
     * A "constant" value of pages.
     *
     * @return Returns {@link #makePages()}.
     */
    public final @NotNull List<MenuPage> pages() {
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
    public final void registerPageButtons(Collection<Button> buttons) {
        registeredPageButtons.addAll(buttons);
    }

    /**
     * Registers an array of buttons.
     *
     * @param buttons The buttons to register.
     */
    public final void registerPageButtons(Button... buttons) {
        registeredPageButtons.addAll(Arrays.stream(buttons).collect(Collectors.toSet()));
    }

    /**
     * Check to see if there provided {@link InventoryPosition} is taken by a page button.
     * <br>
     * Modeled after {@link Menu#isSlotTakenByButton(InventoryPosition)}
     *
     * @param position The position to check.
     * @return <code>true</code> if the position is occupied by a registered button.
     * @see Menu#isSlotTakenByButton(InventoryPosition)
     */
    public boolean isSlotTakenByPageButton(InventoryPosition position) {
        // Loop registered page buttons and check if the position is taken.
        for (final Button button : this.registeredPageButtons) {
            // Check if we have permission for the button to appear.
            if (button.getPermission() != null && !viewer.getBase().hasPermission(button.getPermission()))
                continue;

            if (button.getPosition().equals(position)) return true;
        }
        return false;
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
        final List<MenuPage> pages = this.pages();
        final MenuPage menuPage = pages.get(viewedPage - 1);

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
     * @return Returns {@link #getButtonImpl(ItemStack, List)}.
     */
    public final Button getPageButton(final ItemStack stack) {
        if (stack == null) return null;

        return getButtonImpl(stack, registeredPageButtons);
    }

    /**
     * {@inheritDoc}
     *
     * @return The fully drawn inventory.
     */
    @Override
    public @NotNull MenuInventory formInventory() {
        final MenuInventory inv = super.formInventory();

        drawPage();
        return inv;
    }
}
