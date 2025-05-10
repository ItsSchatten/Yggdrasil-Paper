package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.buttons.CommandButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

@Builder(builderClassName = "Builder")
public class CommandButtonImpl<T extends MenuHolder> extends CommandButton<T> {

    final @NotNull InventoryPosition position;
    final @Nullable Collection<InventoryPosition> positions;

    final @Nullable String permission;

    final @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item;

    final @NotNull String command;

    final boolean console;
    final boolean closeAfter;

    public CommandButtonImpl(@NotNull InventoryPosition position, @Nullable Collection<InventoryPosition> positions,
                             @Nullable String permission,
                             @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item,
                             @NotNull String command, boolean console, boolean closeAfter) {
        this.position = position;
        this.positions = positions;
        this.permission = permission;
        this.item = item;
        this.command = command;
        this.console = console;
        this.closeAfter = closeAfter;
    }

    @Override
    public boolean closeOnExecute() {
        return this.closeAfter;
    }

    @Override
    public ItemCreator createItem() {
        return this.item.get().build();
    }

    @Override
    public boolean executeByConsole() {
        return this.console;
    }

    @Override
    public @NotNull String getCommand() {
        return this.command;
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
