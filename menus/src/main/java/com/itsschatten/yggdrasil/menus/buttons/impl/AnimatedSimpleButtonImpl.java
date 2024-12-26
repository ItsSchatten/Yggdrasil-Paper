package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.buttons.AnimatedSimpleButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@Builder(builderClassName = "Builder")
public class AnimatedSimpleButtonImpl extends AnimatedSimpleButton {

    final @NotNull InventoryPosition position;
    final @Nullable Collection<InventoryPosition> positions;

    final @Nullable String permission;

    final @NotNull ItemCreator.ItemCreatorBuilder item;
    final @Nullable ItemCreator.ItemCreatorBuilder animate;

    final int updateTime;

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
        return this.updateTime < 0 ? super.getUpdateTime() : updateTime;
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
