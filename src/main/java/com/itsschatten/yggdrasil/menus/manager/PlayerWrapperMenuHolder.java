package com.itsschatten.yggdrasil.menus.manager;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This is a wrapper class that adds some extra methods to a player that are used in the Menu system in the Yggdrasil library.
 * <br/><br/>
 * This may honestly be a bit of bloat. Currently looking into getting a nicer way to do this instead of managing this via the plugin.
 */
public record PlayerWrapperMenuHolder(Player base) implements IMenuHolder {

    @Override
    public @NotNull UUID getUniqueID() {
        return base.getUniqueId();
    }

    @Override
    public Player getBase() {
        return base;
    }

    private @Nullable Menu obtainMenu(final String nbtTag) {
        if (base.hasMetadata(nbtTag)) {
            try {
                return (Menu) base().getMetadata(nbtTag).get(0).value();
            } catch (ClassCastException ex) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Menu getCurrentMenu() {
        return obtainMenu(Menu.CURRENT_TAG);
    }

    @Override
    public Menu getPreviousMenu() {
        return obtainMenu(Menu.PREVIOUS_TAG);
    }

    @Override
    public Menu getViewedMenu() {
        return obtainMenu(Menu.VIEWED_TAG);
    }

    @Override
    public void setViewedMenu(Menu menu) {
        base.setMetadata(Menu.VIEWED_TAG, new FixedMetadataValue(Utils.getInstance(), menu));
    }

    @Override
    public void removeCurrentMenu() {
        base.removeMetadata(Menu.CURRENT_TAG, Utils.getInstance());
    }

    @Override
    public void removePreviousMenu() {
        base.removeMetadata(Menu.PREVIOUS_TAG, Utils.getInstance());
    }

    @Override
    public void removeViewedMenu() {
        base.removeMetadata(Menu.VIEWED_TAG, Utils.getInstance());
    }

    @Override
    public void updateMenu(Menu menu) {
        if (base.hasMetadata(Menu.CURRENT_TAG)) {
            try {
                base.setMetadata(Menu.PREVIOUS_TAG, new FixedMetadataValue(Utils.getInstance(), getCurrentMenu()));
            } catch (ClassCastException ignored) {
            }
        }

        base.setMetadata(Menu.CURRENT_TAG, new FixedMetadataValue(Utils.getInstance(), menu));
    }
}
