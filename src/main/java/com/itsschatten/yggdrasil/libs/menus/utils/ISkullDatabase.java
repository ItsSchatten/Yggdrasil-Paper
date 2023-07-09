package com.itsschatten.yggdrasil.libs.menus.utils;

import org.bukkit.profile.PlayerTextures;

import java.util.UUID;

/**
 * A class that is used to store texture information for custom skulls.
 */
public interface ISkullDatabase {
    /**
     * @return The name for this skull item, default is null.
     */
    String getName();

    /**
     * <b>NOTE:</b> This <b>MUST</b> return a textures.minecraft.net URL!
     *
     * @return Return's the Minecraft textures URL for this skin.
     */
    String getTextureURL();

    /**
     * @return Default returns {@link PlayerTextures.SkinModel#CLASSIC}.
     */
    default PlayerTextures.SkinModel getSkinModel() {
        return PlayerTextures.SkinModel.CLASSIC;
    }

    /**
     * The UUID for this skull.
     * Should you want skulls to stack, ensure you set a consistent UUID for that skull.
     *
     * @return Be default a random UUID generated via {@link UUID#randomUUID()}.
     */
    default UUID getUUID() {
        return UUID.randomUUID();
    }
}
