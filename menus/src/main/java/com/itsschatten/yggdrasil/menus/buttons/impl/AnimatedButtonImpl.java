package com.itsschatten.yggdrasil.menus.buttons.impl;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.AnimatedButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import com.itsschatten.yggdrasil.menus.utils.MenuRunnable;
import lombok.Builder;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * An animated button, it animates.
 *
 * @param <T> The menu holder type.
 */
@Builder(builderClassName = "Builder")
public class AnimatedButtonImpl<T extends MenuHolder> extends AnimatedButton<T> {

    /**
     * The position for this button.
     */
    final @NotNull InventoryPosition position;

    /**
     * The positions for this button, in addition to {@link #position}
     */
    final @Nullable Collection<InventoryPosition> positions;

    /**
     * The permission to view this button.
     */
    final @Nullable String permission;

    /**
     * The ItemStack to draw initially.
     */
    final @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item;

    /**
     * The animation to draw.
     */
    final @Nullable Supplier<ItemCreator.ItemCreatorBuilder> animate;

    /**
     * The time, in ticks, to wait between animation frames.
     */
    final int updateTime;

    /**
     * The runnable to run when the holder clicks on the button.
     */
    final @Nullable MenuRunnable<T> onClick;

    /**
     * Constructs a new AnimatedButton.
     *
     * @param position   The position of the button.
     * @param positions  Additional positions for the button.
     * @param permission The permission to view this button.
     * @param item       The default item to draw.
     * @param animate    The animation for the button.
     * @param updateTime The time, in ticks, to wait between animation frames.
     * @param onClick    The runnable to run when the holder clicks on the button.
     */
    public AnimatedButtonImpl(@NotNull InventoryPosition position, @Nullable Collection<InventoryPosition> positions,
                              @Nullable String permission,
                              @NotNull Supplier<ItemCreator.ItemCreatorBuilder> item,
                              @Nullable Supplier<ItemCreator.ItemCreatorBuilder> animate,
                              int updateTime, @Nullable MenuRunnable<T> onClick) {
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
    public void onClicked(T user, Menu<T> menu, ClickType click) {
        if (this.onClick != null) {
            this.onClick.run(user, menu, click);
        }
    }

    /**
     * Builder.
     */
    public static class Builder<T extends MenuHolder> {

        /**
         * The position.
         *
         * @param position The position to set as default.
         * @return this builder for chaining.
         */
        public Builder<T> position(InventoryPosition position) {
            this.position = position;
            return this;
        }

        /**
         * Set the position, using coordinates.
         *
         * @param row    The row for the item.
         * @param column The colum for the item.
         * @return this builder for chaining.
         */
        public Builder<T> position(int row, int column) {
            return this.position(InventoryPosition.of(row, column));
        }

    }

}
