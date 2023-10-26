package com.itsschatten.yggdrasil.menus.buttons.premade;

import com.itsschatten.yggdrasil.menus.buttons.SimpleButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.ItemCreator;
import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The info button.
 */
@Getter
public final class InfoButton extends SimpleButton {

    private final Material material;
    private final String name;
    private final InventoryPosition position;
    private final List<String> lore;

    /**
     * Info button constructor.
     * @param material The material for the info button
     * @param name The name of the info button
     * @param position The position for the info button.
     * @param lore The lore of the info button.
     */
    public InfoButton(@Nullable final Material material, @Nullable final String name, final InventoryPosition position, final String... lore) {
        this.material = material == null ? Material.NETHER_STAR : material;
        this.name = name == null ? "<yellow>Information" : name;
        this.position = position;
        this.lore = new ArrayList<>();
        this.lore.addAll(List.of(lore));
    }

    @Contract(pure = true)
    @Override
    public @Nullable InventoryPosition getPosition() {
        return position;
    }

    @Override
    public ItemCreator createItem() {
        return ItemCreator.of(material).amount(1).name(getName()).lore(getLore()).hideTags(true).build();
    }

}
