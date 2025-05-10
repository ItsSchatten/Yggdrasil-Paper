package com.itsschatten.yggdrasil.menus.buttons.premade;

import com.itsschatten.yggdrasil.menus.buttons.SimpleButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.items.ItemOptions;
import com.itsschatten.yggdrasil.items.MetaManipulator;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import lombok.Builder;
import lombok.Singular;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * The info button.
 */
@Builder
public final class InfoButton<T extends MenuHolder> extends SimpleButton<T> {

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

    public static class InfoButtonBuilder<T extends MenuHolder> {

        public InfoButtonBuilder<T> position(final InventoryPosition position) {
            this.position = position;
            return this;
        }

        public InfoButtonBuilder<T> position(final int row, final int column) {
            return position(InventoryPosition.of(row, column));
        }

    }

}
