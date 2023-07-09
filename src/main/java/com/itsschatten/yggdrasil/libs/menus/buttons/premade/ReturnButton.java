package com.itsschatten.yggdrasil.libs.menus.buttons.premade;

import com.itsschatten.yggdrasil.libs.menus.Menu;
import com.itsschatten.yggdrasil.libs.menus.buttons.Button;
import com.itsschatten.yggdrasil.libs.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.libs.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.libs.menus.utils.ItemCreator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Return button.
 */
@RequiredArgsConstructor
@AllArgsConstructor
public final class ReturnButton extends Button {

    private final Menu menuToReturn;
    private final InventoryPosition position;
    private boolean makeNewInstance = false;
    private Material material;
    private String name;
    private String[] lore;

    @Contract(pure = true)
    @Override
    public @Nullable InventoryPosition getPosition() {
        return position;
    }

    @Override
    public ItemCreator createItem() {
        return ItemCreator.of(material, name).lore(Arrays.stream(lore).collect(Collectors.toList())).hideTags(true).build();
    }

    @Override
    public void onClicked(IMenuHolder user, @NotNull Menu menu, ClickType type) {
        menu.beforeReturn();

        if (makeNewInstance) {
            menuToReturn.newInstance().switchMenu(user, menu);
        } else {
            menuToReturn.switchMenu(user, menu);
        }
    }

}
