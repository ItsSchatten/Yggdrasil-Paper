package com.itsschatten.yggdrasil.wands;

import com.itsschatten.yggdrasil.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A selection wand.
 */
public abstract class Wand {

    /**
     * Used to add the wand to our set, so we can access it.
     */
    public Wand() {
        if (WandUtils.getWands() == null) {
            throw new NullPointerException("Can't register a wand without registering initializing them first!");
        }

        WandUtils.getWands().add(this);
        Utils.debugLog("Registered a wand at '" + getClass().getName() + "'!");
    }

    /**
     * The permission needed to use the wand.
     *
     * @return The permission string.
     */
    public abstract String permission();

    /**
     * Called when our wand sets both points.
     *
     * @param firstLocation  The first {@link Location} set.
     * @param secondLocation The second {@link Location} set.
     * @param player         The {@link Player} that set the locations.
     */
    public abstract void onComplete(final Location firstLocation, final Location secondLocation, final Player player);

    /**
     * Called when a point is selected.
     *
     * @param location  The {@link Location} selected.
     * @param player    The {@link Player} that set the location.
     * @param secondPos If this selection is the second position. (set internally)
     */
    public abstract void onSelect(final Location location, final Player player, final boolean secondPos);

    /**
     * The wand's {@link ItemStack}.
     *
     * @return The ItemStack.
     */
    public abstract ItemStack getItemStack();

    /**
     * The type of wand this is.
     *
     * @return One of {@link WandType the wand types}.
     * @see WandType
     */
    public WandType getType() {
        return WandType.SINGLE_SELECTION;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}