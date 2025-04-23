package com.itsschatten.yggdrasil.items;

import org.bukkit.profile.PlayerTextures;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A class that is used to store texture information for skins. Mainly used for skulls.
 */
public interface ISkin {

    /**
     * Get the name for this skin.
     *
     * @return The name for this skin, default is null.
     */
    @Nullable
    @Pattern("^[!-~]{0,16}$")
    default String getName() {
        return "";
    }

    /**
     * Get the URL for the texture, must be a 'textures.minecraft.net'.
     * <br>
     * <b>NOTE:</b> This <b>MUST</b> return a textures.minecraft.net URL!
     *
     * @return Return's the Minecraft textures URL for this skin.
     */
    @NotNull
    @Pattern("^http(s)?://textures.minecraft.net/.*$")
    String getTextureURL();

    /**
     * The model of this skin, if only used for skulls this can be left to {@link PlayerTextures.SkinModel#CLASSIC}.
     *
     * @return Default returns {@link PlayerTextures.SkinModel#CLASSIC}.
     */
    @NotNull
    default PlayerTextures.SkinModel getSkinModel() {
        return PlayerTextures.SkinModel.CLASSIC;
    }

    /**
     * The {@link UUID} for this skin.
     * Should you want skulls to stack, ensure you set a consistent UUID for that skull.
     *
     * @return A UUID instance.
     */
    @NotNull
    UUID getUUID();

}
