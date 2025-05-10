package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.AnimatedMenuTriggerButton;
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
public class AnimatedMenuTriggerButtonImpl<T extends MenuHolder> extends AnimatedMenuTriggerButton<T> {

    final @NotNull InventoryPosition position;
    final @Nullable Collection<InventoryPosition> positions;

    final @Nullable String permission;

    final @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item;
    final @Nullable Supplier<ItemCreator.ItemCreatorBuilder> animate;

    final int updateTime;

    final @NotNull BiFunction<T, ClickType, Menu<T>> menu;

    public AnimatedMenuTriggerButtonImpl(@NotNull InventoryPosition position, @Nullable Collection<InventoryPosition> positions,
                                         @Nullable String permission,
                                         @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item,
                                         @Nullable Supplier<ItemCreator.ItemCreatorBuilder> animate,
                                         int updateTime, @NotNull BiFunction<T, ClickType, Menu<T>> menu) {
        this.position = position;
        this.positions = positions;
        this.permission = permission;
        this.item = item;
        this.animate = animate;
        this.updateTime = updateTime;
        this.menu = menu;
    }

    @Override
    public ItemCreator createItem() {
        return this.item.get().build();
    }

    @Override
    public ItemCreator animate() {
        return this.animate == null ? super.animate() : this.animate.get().build();
    }

    @Override
    public long getUpdateTime() {
        return this.updateTime < 0 ? super.getUpdateTime() : this.updateTime;
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

    @Override
    public @NotNull Menu<T> getMenu(T user, ClickType click) {
        return this.menu.apply(user, click);
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
