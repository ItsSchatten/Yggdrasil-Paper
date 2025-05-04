package com.itsschatten.yggdrasil.anvilgui;

import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A snapshot of the AnvilGUI.
 *
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
public final class Snapshot {

    /**
     * The first item in the inventory.
     */
    private final ItemStack inputLeft;

    /**
     * The second item in the inventory.
     */
    private final ItemStack inputRight;

    /**
     * The output of the inventory.
     */
    private final ItemStack output;

    /**
     * The view of the Anvil.
     */
    private final AnvilView anvil;

    /**
     * The player that the AnvilGUI is built for, possibly {@code null}.
     */
    private final Player player;

    /**
     * A {@link MenuHolder} for the AnvilGUI, possibly {@code null}.
     */
    private final MenuHolder holder;

    /**
     * Builds a snapshot with a {@link Player}.
     *
     * @param inputLeft  The first item.
     * @param inputRight The second item.
     * @param output     The output item.
     * @param anvil      The {@link AnvilView} of the provided inventory.
     * @param player     The {@link Player} for the AnvilGUI.
     */
    public Snapshot(ItemStack inputLeft, ItemStack inputRight, ItemStack output, AnvilView anvil, Player player) {
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
        this.output = output;
        this.anvil = anvil;
        this.player = player;
        this.holder = null;
    }

    /**
     * Builds a snapshot with a {@link MenuHolder}.
     *
     * @param inputLeft  The first item.
     * @param inputRight The second item.
     * @param output     The output item.
     * @param anvil      The {@link AnvilView} of the provided inventory.
     * @param holder     The {@link MenuHolder} for the AnvilGUI.
     */
    public Snapshot(ItemStack inputLeft, ItemStack inputRight, ItemStack output, AnvilView anvil, MenuHolder holder) {
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
        this.output = output;
        this.anvil = anvil;
        this.player = null;
        this.holder = holder;
    }

    /**
     * Return the provided {@link ItemStack} if not null, otherwise return an ItemStack with {@link Material#AIR}.
     *
     * @param itemStack The {@link ItemStack} to check for nullity.
     * @return Always returns an {@link ItemStack}, if the provided ItemStack is null the material will be {@link Material#AIR}
     */
    @Contract("null -> new; !null -> param1")
    private static @NotNull ItemStack returnItemOrDefault(final ItemStack itemStack) {
        return itemStack == null ? new ItemStack(Material.AIR) : itemStack;
    }

    /**
     * Builds a {@link Snapshot} from a fully built {@link AnvilGUI}.
     *
     * @param gui The {@link AnvilGUI} instance to build a snapshot from.
     * @return Returns a {@link Snapshot}.
     */
    @Contract("_ -> new")
    static @NotNull Snapshot fromBuilt(final @NotNull AnvilGUI gui) {
        final Inventory inventory = gui.inventory();

        if (gui.player() != null) {
            return new Snapshot(returnItemOrDefault(inventory.getItem(Slot.INPUT_LEFT)), returnItemOrDefault(inventory.getItem(Slot.INPUT_RIGHT)), returnItemOrDefault(inventory.getItem(Slot.OUTPUT)), gui.view(), gui.player());
        } else {
            return new Snapshot(returnItemOrDefault(inventory.getItem(Slot.INPUT_LEFT)), returnItemOrDefault(inventory.getItem(Slot.INPUT_RIGHT)), returnItemOrDefault(inventory.getItem(Slot.OUTPUT)), gui.view(), gui.holder());
        }
    }

    /**
     * Get the text from the {@link AnvilView}.
     *
     * @return Returns either the anvil rename text or an empty string.
     */
    public @NotNull String text() {
        return this.anvil != null ? anvil.getRenameText() : "";
    }
}
