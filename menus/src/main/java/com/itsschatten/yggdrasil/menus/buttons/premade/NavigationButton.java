package com.itsschatten.yggdrasil.menus.buttons.premade;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.items.ItemOptions;
import com.itsschatten.yggdrasil.items.MetaManipulator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import com.itsschatten.yggdrasil.menus.utils.MenuRunnable;
import lombok.Builder;
import lombok.Singular;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Builder
public final class NavigationButton<T extends MenuHolder> extends Button<T> {

    /**
     * The lore to apply to the button.
     */
    @Singular("lore")
    private List<String> lore;

    /**
     * The name that will appear on the item.
     */
    private String name;

    /**
     * The {@link Material} of the button.
     */
    private Material material;

    /**
     * The {@link ItemOptions} for the button.
     */
    @Builder.Default
    private ItemOptions options = ItemOptions.EMPTY;

    /**
     * The {@link MetaManipulator}s for the item.
     */
    @Singular
    private List<MetaManipulator> manipulators;

    /**
     * When the button is clicked, this runs.
     */
    private MenuRunnable runnable;

    /**
     * The {@link InventoryPosition} that this button will appear in a menu.
     */
    private InventoryPosition position;

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
        return ItemCreator.builder().material(material).name(name).lore(lore).options(options).manipulators(manipulators).build();
    }

    /**
     * {@inheritDoc}
     *
     * @param user  The {@link T} that clicked this button.
     * @param menu  The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public void onClicked(final T user, final Menu<T> menu, final ClickType click) {
        runnable.run(user, menu, click);
    }

    public static class NavigationButtonBuilder<T extends MenuHolder> {

        public NavigationButtonBuilder<T> position(final InventoryPosition position) {
            this.position = position;
            return this;
        }

        public NavigationButtonBuilder<T> position(final int row, final int column) {
            return position(InventoryPosition.of(row, column));
        }

    }

}
