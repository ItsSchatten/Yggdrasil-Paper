package com.itsschatten.yggdrasil.items.manipulators;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.items.ISkin;
import com.itsschatten.yggdrasil.items.ItemManipulator;
import com.itsschatten.yggdrasil.items.MetaManipulator;
import com.itsschatten.yggdrasil.items.SkinTexture;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.UUID;

/**
 * A prebuilt {@link MetaManipulator} that will change the skin on a player head item.
 */
public final class SkullManipulator implements ItemManipulator {

    /**
     * A {@link SkinTexture} instance.
     */
    private final SkinTexture texture;

    /**
     * The {@link UUID} of a player.
     */
    private final UUID uuid;

    /**
     * An {@link ISkin} value.
     */
    private final ISkin skin;

    /**
     * Set if we should always forcefully remove the name, only used when setting the UUID.
     *
     * @apiNote This is ignored if the manipulator is crated with a {@link SkinTexture} or an {@link ISkin} value.
     */
    private final boolean forceNameRemoval;

    /**
     * Constructs a SkullManipulator using a {@link UUID}.
     *
     * @param uuid The UUID of to use as the skin.
     */
    public SkullManipulator(final UUID uuid) {
        this.uuid = uuid;
        this.forceNameRemoval = false;
        this.texture = null;
        this.skin = null;
    }

    /**
     * Constructs a SkullManipulator using a {@link UUID} with the ability to override the name.
     *
     * @param uuid             The UUID of to use as the skin.
     * @param forceNameRemoval If the name should be removed from the skull data.
     */
    public SkullManipulator(final UUID uuid, final boolean forceNameRemoval) {
        this.uuid = uuid;
        this.forceNameRemoval = forceNameRemoval;
        this.texture = null;
        this.skin = null;
    }

    /**
     * Constructs a SkullManipulator using a {@link SkinTexture}.
     *
     * @param texture The texture to use for the texture properties.
     */
    public SkullManipulator(final SkinTexture texture) {
        this.texture = texture;
        this.forceNameRemoval = false;
        this.uuid = null;
        this.skin = null;
    }

    /**
     * Constructs a SkullManipulator using a {@link ISkin} instance.
     *
     * @param skin The skin to use.
     */
    public SkullManipulator(final ISkin skin) {
        this.skin = skin;
        this.forceNameRemoval = false;
        this.texture = null;
        this.uuid = null;
    }

    @Override
    public void apply(final @NotNull ItemStack item) {
        if (item.getItemMeta() instanceof final SkullMeta skullMeta) {
            if (this.skin != null) {
                final PlayerProfile profile = Bukkit.getServer().createProfile(this.skin.getUUID(), this.skin.getName());
                final PlayerTextures textures = profile.getTextures();

                try {
                    textures.setSkin(URI.create(this.skin.getTextureURL()).toURL(), this.skin.getSkinModel());
                } catch (MalformedURLException e) {
                    Utils.logError(e);
                    Utils.logError("Failed to get a proper URL for a skull!");
                    return;
                }

                profile.setTextures(textures);
                item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile()
                        // This is done to ensure a name is not provided on the skull, thus allowing use of meta#itemName(Component)
                        .name(this.skin.getName() == null || this.skin.getName().isBlank() ? null : this.skin.getName())
                        .addProperties(profile.getProperties()).build());
            } else if (this.texture != null) {
                final PlayerProfile profile = Bukkit.getServer().createProfile(this.texture.uuid(), this.texture.name());
                final PlayerTextures textures = profile.getTextures();

                try {
                    textures.setSkin(this.texture.uri().toURL(), this.texture.model());
                } catch (MalformedURLException e) {
                    Utils.logError(e);
                    Utils.logError("Failed to get a proper URL for a skull!");
                    return;
                }

                profile.setTextures(textures);
                // This is done to ensure a name is not provided on the skull, thus allowing use of meta#itemName(Component)
                item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile()
                        .name(this.texture.name() == null || this.texture.name().isBlank() ? null : this.texture.name())
                        .addProperties(profile.getProperties()).build());
            } else if (uuid != null) {

                // When a UUID is set, we assume it's for a specific reason, such as showing a player's head in a menu,
                // overriding and removing the name of the skull MAY mess with things. However, we also allow the name to
                // be removed.
                if (forceNameRemoval) {
                    item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile()
                            .name(null)
                            .addProperties(Bukkit.getOfflinePlayer(uuid).getPlayerProfile().getProperties()).build());
                } else {
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                    item.setItemMeta(skullMeta);
                }
            }
        } else {
            throw new IllegalStateException("Cannot update a skin on a non-skull item: " + item);
        }
    }

}
