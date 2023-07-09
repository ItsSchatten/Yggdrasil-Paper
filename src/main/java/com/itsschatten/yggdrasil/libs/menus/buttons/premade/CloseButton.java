package com.itsschatten.yggdrasil.libs.menus.buttons.premade;

import com.itsschatten.yggdrasil.libs.Utils;
import com.itsschatten.yggdrasil.libs.menus.Menu;
import com.itsschatten.yggdrasil.libs.menus.buttons.Button;
import com.itsschatten.yggdrasil.libs.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.libs.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.libs.menus.utils.ItemCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Close button.
 */
@RequiredArgsConstructor
public final class CloseButton extends Button {
    @Getter
    private final String[] lore;

    @Getter
    private final String name;

    @Getter
    private final Material material;

    private final InventoryPosition position;

    @Contract(pure = true)
    @Override
    public @Nullable InventoryPosition getPosition() {
        return position;
    }

    @Override
    public ItemCreator createItem() {
        return ItemCreator.of(getMaterial()).amount(1).name(getName()).lore(Arrays.stream(getLore()).toList()).hideTags(true).build();
    }

    @Override
    public void onClicked(@NotNull IMenuHolder user, Menu menu, ClickType type) {
        user.getBase().closeInventory();
        if (type == ClickType.NUMBER_KEY) {
            Bukkit.getScheduler().runTaskLater(Utils.getInstance(), () -> user.getBase().updateInventory(), 15);
        }
    }
}
