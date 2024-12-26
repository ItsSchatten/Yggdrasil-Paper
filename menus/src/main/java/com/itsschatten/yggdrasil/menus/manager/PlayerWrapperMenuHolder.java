package com.itsschatten.yggdrasil.menus.manager;

import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * This is a wrapper class that adds some extra methods to a player that are used in the Menu system in the Yggdrasil library.
 * <br/><br/>
 * This may honestly be a bit of bloat. Currently looking into getting a nicer way to do this instead of managing this via the plugin.
 *
 * @param base The base player.
 */
public record PlayerWrapperMenuHolder(Player base) implements IMenuHolder {

    @Override
    public @NotNull UUID getUniqueID() {
        return base.getUniqueId();
    }

    @Override
    public @NotNull Player getBase() {
        return base;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerWrapperMenuHolder that)) return false;
        return Objects.equals(getBase(), that.getBase());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getBase());
    }
}
