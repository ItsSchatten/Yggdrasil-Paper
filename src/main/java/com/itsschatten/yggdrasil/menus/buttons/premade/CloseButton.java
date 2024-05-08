package com.itsschatten.yggdrasil.menus.buttons.premade;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.ItemCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
        user.getBase().closeInventory(InventoryCloseEvent.Reason.PLAYER);
        if (type == ClickType.NUMBER_KEY) {
            Bukkit.getScheduler().runTaskLater(Utils.getInstance(), () -> user.getBase().updateInventory(), 15);
        }
    }
}
