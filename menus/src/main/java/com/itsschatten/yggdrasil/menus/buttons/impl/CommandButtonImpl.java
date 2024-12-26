package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.buttons.CommandButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@Builder(builderClassName = "Builder")
public class CommandButtonImpl extends CommandButton {

    final @NotNull InventoryPosition position;
    final @Nullable Collection<InventoryPosition> positions;

    final @Nullable String permission;

    final @NotNull ItemCreator.ItemCreatorBuilder item;

    final @NotNull String command;

    final boolean console;
    final boolean closeAfter;

    public CommandButtonImpl(@NotNull InventoryPosition position, @Nullable Collection<InventoryPosition> positions, @Nullable String permission, @NotNull ItemCreator.ItemCreatorBuilder item, @NotNull String command, boolean console, boolean closeAfter) {
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
        return this.item.build();
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