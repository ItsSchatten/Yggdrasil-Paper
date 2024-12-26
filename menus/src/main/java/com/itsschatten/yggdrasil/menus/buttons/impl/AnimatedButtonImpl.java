package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.AnimatedButton;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuRunnable;
import lombok.Builder;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@Builder(builderClassName = "Builder")
public class AnimatedButtonImpl extends AnimatedButton {

    final @NotNull InventoryPosition position;
    final @Nullable Collection<InventoryPosition> positions;

    final @Nullable String permission;

    final @NotNull ItemCreator.ItemCreatorBuilder item;
    final @Nullable ItemCreator.ItemCreatorBuilder animate;

    final int updateTime;

    final @Nullable MenuRunnable onClick;

    public AnimatedButtonImpl(@NotNull InventoryPosition position, @Nullable Collection<InventoryPosition> positions, @Nullable String permission, @NotNull  ItemCreator.ItemCreatorBuilder item, @Nullable ItemCreator.ItemCreatorBuilder animate, int updateTime, @Nullable MenuRunnable onClick) {
        this.position = position;
        this.positions = positions;
        this.permission = permission;
        this.item = item;
        this.animate = animate;
        this.updateTime = updateTime;
        this.onClick = onClick;
    }

    @Override
    public ItemCreator createItem() {
        return this.item.build();
    }

    @Override
    public ItemCreator animate() {
        return this.animate == null ? super.animate() : this.animate.build();
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
    public void onClicked(IMenuHolder user, Menu menu, ClickType click) {
        if (this.onClick != null) {
            this.onClick.run(user, menu, click);
        }
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
