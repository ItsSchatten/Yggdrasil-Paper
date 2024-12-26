package com.itsschatten.yggdrasil.menus.buttons.premade;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.items.ItemOptions;
import com.itsschatten.yggdrasil.items.MetaManipulator;
import lombok.Builder;
import lombok.Singular;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Return button.
 */
@Builder(toBuilder = true)
public final class ReturnButton extends Button {

    /**
     * The {@link Menu} to return to when clicking the button.
     */
    private final Menu menuToReturn;

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
    private ItemOptions options = ItemOptions.HIDE_ALL_FLAGS;

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
        return ItemCreator.builder().material(material).display(name).lore(lore).options(options).manipulators(manipulators).build();
    }

    /**
     * {@inheritDoc}
     *
     * @param user The {@link IMenuHolder} that clicked this button.
     * @param menu The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public void onClicked(IMenuHolder user, @NotNull Menu menu, ClickType click) {
        menu.beforeReturn();
        menuToReturn.switchMenu(user, menu);
    }

    public static class ReturnButtonBuilder {

        public ReturnButtonBuilder position(final InventoryPosition position) {
            this.position = position;
            return this;
        }

        public ReturnButtonBuilder position(final int row, final int column) {
            return position(InventoryPosition.of(row, column));
        }

    }
    
}
