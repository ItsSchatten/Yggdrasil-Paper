package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.DynamicButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import com.itsschatten.yggdrasil.menus.utils.MenuRunnable;
import lombok.Builder;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

@Builder(builderClassName = "Builder")
public class DynamicButtonImpl<T extends MenuHolder> extends DynamicButton<T> {

    final @NotNull InventoryPosition position;
    final @Nullable Set<InventoryPosition> positions;

    final @Nullable String permission;

    final @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item;
    final @Nullable Supplier<ItemCreator.ItemCreatorBuilder> update;

    final int updateTime;

    final @Nullable MenuRunnable<T> onClick;

    public DynamicButtonImpl(@NotNull InventoryPosition position, @Nullable Set<InventoryPosition> positions,
                             @Nullable String permission,
                             @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item,
                             @Nullable Supplier<ItemCreator.ItemCreatorBuilder> update,
                             int updateTime, @Nullable MenuRunnable<T> onClick) {
        this.position = position;
        this.positions = positions;
        this.permission = permission;
        this.item = item;
        this.update = update;
        this.updateTime = updateTime;
        this.onClick = onClick;
    }

    @Override
    public ItemCreator createItem() {
        return this.item.get().build();
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
    public void onClicked(T user, Menu<T> menu, ClickType click) {
        if (this.onClick != null) {
            this.onClick.run(user, menu, click);
        }
    }

    @Override
    public ItemCreator updateStack() {
        return this.update == null ? super.updateStack() : this.update.get().build();
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
