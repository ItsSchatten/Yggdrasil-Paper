package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import com.itsschatten.yggdrasil.menus.utils.MenuRunnable;
import lombok.Builder;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

@Builder(builderClassName = "Builder")
public class ButtonImpl<T extends MenuHolder> extends Button<T> {

    final @NotNull InventoryPosition position;
    final @Nullable Collection<InventoryPosition> positions;

    final @Nullable String permission;

    final @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item;

    final @Nullable MenuRunnable<T> onClick;

    public ButtonImpl(@NotNull InventoryPosition position, @Nullable Collection<InventoryPosition> positions,
                      @Nullable String permission,
                      @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item,
                      @Nullable MenuRunnable<T> onClick) {
        this.position = position;
        this.positions = positions;
        this.permission = permission;
        this.item = item;
        this.onClick = onClick;
    }

    @Override
    public ItemCreator createItem() {
        return item.get().build();
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
