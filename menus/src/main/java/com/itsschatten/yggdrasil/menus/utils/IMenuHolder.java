package com.itsschatten.yggdrasil.menus.utils;


import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a holder for a Menu.
 */
public interface IMenuHolder {

    /**
     * Get the {@link UUID} from the {@link #getBase() base player}.
     *
     * @return The {@link UUID} of the base {@link Player}.
     */
    @NotNull
    default UUID getUniqueID() {
        return getBase().getUniqueId();
    }

    /**
     * Get the base {@link Player} of this {@link IMenuHolder}.
     *
     * @return The base {@link Player}.
     */
    @NotNull
    Player getBase();

    /**
     * This is only ever "active" when {@link #getBase()} has a {@link Menu} open.
     *
     * @return This holder's currently active menu.
     * @see MenuListeners
     */
    @Nullable
    default Menu getCurrentMenu() {
        return obtainMenu(Menu.CURRENT_TAG);
    }

    /**
     * This is set whenever a {@link Menu} is closed.
     *
     * @return This holder's previous menu.
     * @see MenuListeners
     */
    @Nullable
    default Menu getPreviousMenu() {
        return obtainMenu(Menu.PREVIOUS_TAG);
    }

    /**
     * This is set whenever a player begins viewing another holder's open {@link Menu}.
     * <P>This is reset whenever they close this menu and should never have a persistent value.</P>
     *
     * @return This holder's currently active viewing menu.
     * @see MenuListeners
     */
    @Nullable
    default Menu getViewedMenu() {
        return obtainMenu(Menu.VIEWED_TAG);
    }

    /**
     * Sets the {@link Menu} that this holder is viewing.
     *
     * @param menu The {@link Menu} to set.
     * @see MenuListeners
     */
    default void setViewedMenu(Menu menu) {
        getBase().setMetadata(Menu.VIEWED_TAG, new FixedMetadataValue(Utils.getInstance(), menu));
    }

    /**
     * Remove's this holder's current {@link Menu}.
     *
     * @see MenuListeners
     */
    default void removeCurrentMenu() {
        getBase().removeMetadata(Menu.CURRENT_TAG, Utils.getInstance());
    }

    /**
     * Remove's this holder's previous {@link Menu}.
     *
     * @see MenuListeners
     */
    default void removePreviousMenu() {
        getBase().removeMetadata(Menu.PREVIOUS_TAG, Utils.getInstance());
    }

    /**
     * Removes this holder's currently viewed {@link Menu}.
     *
     * @see MenuListeners
     */
    default void removeViewedMenu() {
        getBase().removeMetadata(Menu.VIEWED_TAG, Utils.getInstance());
    }

    /**
     * Update his holder's current {@link Menu} to the one provided.
     * <br>
     * If the holder currently has an active {@link Menu}, it is set as previous.
     *
     * @param menu The {@link Menu} that should be set as the active menu.
     * @see MenuListeners
     */
    default void updateMenu(final Menu menu) {
        if (getBase().hasMetadata(Menu.CURRENT_TAG)) {
            try {
                getBase().setMetadata(Menu.PREVIOUS_TAG, new FixedMetadataValue(Utils.getInstance(), getCurrentMenu()));
            } catch (ClassCastException ignored) {
            }
        }

        getBase().setMetadata(Menu.CURRENT_TAG, new FixedMetadataValue(Utils.getInstance(), menu));
    }

    /**
     * Get the {@link Menu} based on the provided {@link String}.
     *
     * @param nbtTag The tag to search the metadata for.
     * @return Returns a possibly {@code null} {@link Menu} instance.
     */
    default @Nullable Menu obtainMenu(final String nbtTag) {
        if (getBase().hasMetadata(nbtTag)) {
            try {
                return (Menu) getBase().getMetadata(nbtTag).getFirst().value();
            } catch (ClassCastException ex) {
                return null;
            }
        }
        return null;
    }

    /**
     * Utility method to quickly send messages to the base of this class.
     *
     * @param message  A single message to send.
     * @param messages An additional array of messages to send to the base.
     */
    default void tell(final String message, final String... messages) {
        Utils.tell(getBase(), message, messages);
    }

    /**
     * Utility method to quickly send {@link Component}s to the base of this class.
     *
     * @param message  The single {@link Component} to send.
     * @param messages An additional array of {@link Component}s to send.
     */
    default void tell(final Component message, final Component... messages) {
        Utils.tell(getBase(), message, messages);
    }

}
