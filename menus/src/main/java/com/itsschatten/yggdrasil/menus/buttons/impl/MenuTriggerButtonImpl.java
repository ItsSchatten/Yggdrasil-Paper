package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.MenuTriggerButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import lombok.Builder;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Builder(builderClassName = "Builder")
public class MenuTriggerButtonImpl<T extends MenuHolder> extends MenuTriggerButton<T> {

    final @NotNull InventoryPosition position;
    final @Nullable Collection<InventoryPosition> positions;

    final @Nullable String permission;

    final @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item;

    final @NotNull BiFunction<T, ClickType, Menu<T>> menu;

    public MenuTriggerButtonImpl(@NotNull InventoryPosition position, @Nullable Collection<InventoryPosition> positions,
                                 @Nullable String permission,
                                 @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item,
                                 @NotNull BiFunction<T, ClickType, Menu<T>> menu) {
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
    public Menu<T> getMenu(T user, ClickType click) {
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

    public static class Builder<T extends MenuHolder> {

        public Builder<T> position(InventoryPosition position) {
            this.position = position;
            return this;
        }

        public Builder<T> position(int row, int column) {
            return this.position(InventoryPosition.of(row, column));
        }

    }

}
