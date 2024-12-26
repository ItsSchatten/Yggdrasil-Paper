package com.itsschatten.yggdrasil.menus.buttons.premade;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.types.PageMenu;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuRunnable;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public final class PageNavigationButtonImpl extends Button {

    /**
     * The builder of for this button item.
     */
    private final ItemCreator.ItemCreatorBuilder item;

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

    public PageNavigationButtonImpl(ItemCreator.ItemCreatorBuilder item, int pageNumber, InventoryPosition position, MenuRunnable runnable) {
        this.item = item;
        this.pageNumber = pageNumber;
        this.position = position;
        this.runnable = runnable;
    }

    /**
     * {@inheritDoc}
     *
     * @return Returns the position for this button.
     */
    @Contract(pure = true)
    @Override
    public @NotNull InventoryPosition getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     *
     * @return Returns the built item from the builder.
     */
    @Override
    public ItemCreator createItem() {
        return this.item.build();
    }

    /**
     * {@inheritDoc}
     *
     * @param user  The {@link IMenuHolder} that clicked this button.
     * @param menu  The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public void onClicked(final IMenuHolder user, final Menu menu, final ClickType click) {
        Validate.isTrue(pageNumber > 0, "A page number cannot be 0 or less!");
        if (menu instanceof PageMenu pageMenu) {
            if (runnable != null && pageMenu.viewedPage() != pageNumber) runnable.run(user, menu, click);
            pageMenu.switchPage(pageNumber);
        }
    }

}
