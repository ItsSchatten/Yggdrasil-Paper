package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A holder for a {@link Menu}.
 */
public class MenuHolder {

    /**
     * Constant to get a player's current menu.
     */
    public static final String CURRENT_TAG = "current_menu";

    /**
     * Constant to get a player's previous menu.
     */
    public static final String PREVIOUS_TAG = "previous_menu";

    /**
     * Constant to get a player's viewed menu.
     */
    public static final String VIEWED_TAG = "viewed_menu";

    /**
     * The player that this class is wrapping.
     */
    @Getter
    @Accessors(fluent = true)
    private final @NotNull Player player;

    /**
     * Constructs a new MenuHolder.
     *
     * @param player The base player for this holder.
     */
    public MenuHolder(final @NotNull Player player) {
        this.player = player;
    }

    /**
     * Quickly creates a new {@link MenuHolder} with the provided player or gets their "cached" value.
     *
     * @param player The {@link Player} to wrap.
     * @return Returns a new {@link MenuHolder}.
     * @implNote It is not recommended to use this method if you plan on making your own class extending MenuHolder, you should instead cache things yourself.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull MenuHolder wrap(final @NotNull Player player) {
        final List<MetadataValue> value = player.getMetadata("menu_holder");

        if (value.isEmpty() || value.getFirst().value() == null) {
            // Make a new holder and then set the value to the player.
            final MenuHolder holder = new MenuHolder(player);
            player.setMetadata("menu_holder", new FixedMetadataValue(Utils.getInstance(), holder));
            return holder;
        }

        // Shouldn't be null at this point.
        return (MenuHolder) Objects.requireNonNull(value.getFirst().value());
    }

    /**
     * Gets the value of a specific permission, if set.
     * <br/>
     * If no override is set for this object, it will return the default.
     *
     * @param permission The permission to check.
     * @return Returns the value of the provided permission.
     */
    public final boolean hasPermission(final String permission) {
        return player.hasPermission(permission);
    }

    /**
     * Gets the value of a specific permission, if set.
     * <br/>
     * If no override is set for this object, it will return the default.
     *
     * @param permission The permission to check.
     * @return Returns the value of the provided permission.
     */
    public final boolean hasPermission(final Permission permission) {
        return player.hasPermission(permission);
    }

    /**
     * This is only ever "active" when {@link #player} has a {@link Menu} open.
     *
     * @return This holder's currently active menu.
     * @see MenuListeners
     */
    @Nullable
    public final Menu<? extends MenuHolder> getCurrentMenu() {
        return obtainMenu(CURRENT_TAG);
    }

    /**
     * This is set whenever a {@link Menu} is closed.
     *
     * @return This holder's previous menu.
     * @see MenuListeners
     */
    @Nullable
    public final Menu<? extends MenuHolder> getPreviousMenu() {
        return obtainMenu(PREVIOUS_TAG);
    }

    /**
     * This is set whenever a player begins viewing another holder's open {@link Menu}.
     * <P>This is reset whenever they close this menu and should never have a persistent value.</P>
     *
     * @return This holder's currently active viewing menu.
     * @see MenuListeners
     */
    @Nullable
    public final Menu<? extends MenuHolder> getViewedMenu() {
        return obtainMenu(VIEWED_TAG);
    }

    /**
     * Sets the {@link Menu} that this holder is viewing.
     *
     * @param menu The {@link Menu} to set.
     * @see MenuListeners
     */
    public final void setViewedMenu(Menu<? extends MenuHolder> menu) {
        player.setMetadata(VIEWED_TAG, new FixedMetadataValue(Utils.getInstance(), menu));
    }

    /**
     * Remove's this holder's current {@link Menu}.
     *
     * @see MenuListeners
     */
    public final void removeCurrentMenu() {
        player.removeMetadata(CURRENT_TAG, Utils.getInstance());
    }

    /**
     * Remove's this holder's previous {@link Menu}.
     *
     * @see MenuListeners
     */
    public final void removePreviousMenu() {
        player.removeMetadata(PREVIOUS_TAG, Utils.getInstance());
    }

    /**
     * Removes this holder's currently viewed {@link Menu}.
     *
     * @see MenuListeners
     */
    public final void removeViewedMenu() {
        player.removeMetadata(VIEWED_TAG, Utils.getInstance());
    }

    /**
     * Update his holder's current {@link Menu} to the one provided.
     * <br>
     * If the holder currently has an active {@link Menu}, it is set as previous.
     *
     * @param menu The {@link Menu} that should be set as the active menu.
     * @see MenuListeners
     */
    public final void updateMenu(final Menu<? extends MenuHolder> menu) {
        if (player.hasMetadata(CURRENT_TAG)) {
            try {
                player.setMetadata(PREVIOUS_TAG, new FixedMetadataValue(Utils.getInstance(), getCurrentMenu()));
            } catch (ClassCastException ignored) {
            }
        }

        player.setMetadata(CURRENT_TAG, new FixedMetadataValue(Utils.getInstance(), menu));
    }

    /**
     * Get the {@link Menu} based on the provided {@link String}.
     *
     * @param nbtTag The tag to search the metadata for.
     * @return Returns a possibly {@code null} {@link Menu} instance.
     */
    private @Nullable Menu<? extends MenuHolder> obtainMenu(final String nbtTag) {
        if (player.hasMetadata(nbtTag)) {
            try {
                return (Menu<? extends MenuHolder>) player.getMetadata(nbtTag).getFirst().value();
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
    public final void tell(final String message, final String... messages) {
        Utils.tell(player, message, messages);
    }

    /**
     * Utility method to quickly send {@link Component}s to the base of this class.
     *
     * @param message  The single {@link Component} to send.
     * @param messages An additional array of {@link Component}s to send.
     */
    public final void tell(final Component message, final Component... messages) {
        Utils.tell(player, message, messages);
    }

    /**
     * Utility method to quickly send a translatable message to the base of this class.
     *
     * @param key       The translation key from a resource pack. I.E. "inventory.anvil.title"
     * @param arguments The arguments to replace as a string. If there are any in the translation string.
     */
    public final void translate(String key, String... arguments) {
        Utils.translate(player, key, arguments);
    }

    /**
     * Utility method to quickly send a translatable message to the base of this class.
     *
     * @param key       The translation key from a resource pack. I.E. "inventory.anvil.title"
     * @param arguments The arguments to replace as a component. If there are any in the translation string.
     */
    public final void translate(String key, Component... arguments) {
        Utils.translate(player, key, arguments);
    }

    /**
     * Utility method to quickly send a translatable message to the base of this class.
     *
     * @param key The translation key from a resource pack. I.E. "inventory.anvil.title"
     */
    public final void translate(String key) {
        Utils.translate(player, key);
    }

    /**
     * {@inheritDoc}
     *
     * @param o The object to check if equals.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MenuHolder that)) return false;
        return Objects.equals(player, that.player);
    }

    /**
     * {@inheritDoc}
     *
     * @return Returns a hashcode for this class.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(player);
    }
}
