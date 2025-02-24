package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.MenuTriggerButton;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import lombok.Builder;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Builder(builderClassName = "Builder")
public class MenuTriggerButtonImpl extends MenuTriggerButton {

    final @NotNull InventoryPosition position;
    final @Nullable Collection<InventoryPosition> positions;

    final @Nullable String permission;

    final @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item;

    final @NotNull BiFunction<IMenuHolder, ClickType, Menu> menu;

    public MenuTriggerButtonImpl(@NotNull InventoryPosition position, @Nullable Collection<InventoryPosition> positions,
                                 @Nullable String permission,
                                 @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item,
                                 @NotNull BiFunction<IMenuHolder, ClickType, Menu> menu) {
        this.position = position;
        this.positions = positions;
        this.permission = permission;
        this.item = item;
        this.menu = menu;
    }

    @Override
    public ItemCreator createItem() {
        return this.item.get().build();
    }

    @Override
    public Menu getMenu(IMenuHolder user, ClickType click) {
        return this.menu.apply(user, click);
    }

    @Override
    public @Nullable String getPermission() {
        return this.permission == null || this.permission.isBlank() ? null : this.permission;
    }

    @Override
    public @NotNull InventoryPosition getPosition() {
        return this.position;
    }

    @Override
    public @Nullable Collection<InventoryPosition> getPositions() {
        return this.positions;
    }

    public static class Builder {

        public Builder position(InventoryPosition position) {
            this.position = position;
            return this;
        }

        public Builder position(int row, int column) {
            return this.position(InventoryPosition.of(row, column));
        }

    }

}
