package com.itsschatten.yggdrasil.menus.buttons.premade;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.items.ItemOptions;
import com.itsschatten.yggdrasil.items.MetaManipulator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import lombok.Singular;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The close button.
 */
@lombok.Builder
public final class CloseButton<T extends MenuHolder> extends Button<T> {

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
    @lombok.Builder.Default
    private ItemOptions options = ItemOptions.EMPTY;

    /**
     * The {@link MetaManipulator}s for the item.
     */
    @Singular
    private List<MetaManipulator> manipulators;

    /**
     * The {@link InventoryPosition} that this button will appear in a menu.
     */
    private InventoryPosition position;

    /**
     * {@inheritDoc}
     *
     * @return Returns the position for this button.
     */
    @Override
    @NotNull
    public InventoryPosition getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     *
     * @return Returns the built item from the builder.
     */
    @Override
    public ItemCreator createItem() {
        return ItemCreator.builder().material(material).amount(1).name(name).lore(lore).options(options).manipulators(manipulators).build();
    }

    /**
     * {@inheritDoc}
     *
     * @param user  The {@link T} that clicked this button.
     * @param menu  The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public void onClicked(final @NotNull T user, final Menu<T> menu, final ClickType click) {
        user.player().closeInventory(InventoryCloseEvent.Reason.PLAYER);
        if (click == ClickType.NUMBER_KEY) {
            Bukkit.getScheduler().runTaskLater(Utils.getInstance(), () -> user.player().updateInventory(), 15);
        }
    }

    public static class CloseButtonBuilder<T extends MenuHolder> {

        public CloseButtonBuilder<T> position(final InventoryPosition position) {
            this.position = position;
            return this;
        }

        public CloseButtonBuilder<T> position(final int row, final int column) {
            return position(InventoryPosition.of(row, column));
        }

    }

}
