package com.itsschatten.yggdrasil.items;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.bukkit.profile.PlayerTextures;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * A skin texture.
 *
 * @param uuid    The {@link UUID} for the skin texture, used to generate a {@link com.destroystokyo.paper.profile.PlayerProfile}.
 * @param texture The texture {@link String} for this skin.
 * @param name    The name to use in the generated {@link com.destroystokyo.paper.profile.PlayerProfile}.
 * @param model   The model to use for this skin.
 */
public record SkinTexture(@NotNull UUID uuid, @NotNull String texture, @Subst("") @Nullable @Pattern("^[!-~]{0,16}$") String name,
                          @NotNull PlayerTextures.SkinModel model) {

    /**
     * A skin texture, defaulting to use the {@link org.bukkit.profile.PlayerTextures.SkinModel#CLASSIC} model.
     *
     * @param uuid    The {@link UUID} for the skin texture, used to generate a {@link com.destroystokyo.paper.profile.PlayerProfile}.
     * @param texture The texture {@link String} for this skin.
     * @param name    The name to use in the generated {@link com.destroystokyo.paper.profile.PlayerProfile}.
     */
    public SkinTexture(@NotNull UUID uuid, @Subst("") @Nullable String name, @NotNull String texture) {
        this(uuid, texture, name, PlayerTextures.SkinModel.CLASSIC);
    }

    /**
     * A skin texture, with an empty string as the name.
     *
     * @param uuid    The {@link UUID} for the skin texture, used to generate a {@link com.destroystokyo.paper.profile.PlayerProfile}.
     * @param texture The texture {@link String} for this skin.
     * @param model   The model to use for this skin.
     */
    public SkinTexture(@NotNull UUID uuid, @NotNull String texture, PlayerTextures.SkinModel model) {
        this(uuid, texture, "", model);
    }

    /**
     * A skin texture, with an empty string as the name and using the {@link org.bukkit.profile.PlayerTextures.SkinModel#CLASSIC} model.
     *
     * @param uuid    The {@link UUID} for the skin texture, used to generate a {@link com.destroystokyo.paper.profile.PlayerProfile}.
     * @param texture The texture {@link String} for this skin.
     */
    public SkinTexture(@NotNull UUID uuid, @NotNull String texture) {
        this(uuid, texture, "", PlayerTextures.SkinModel.CLASSIC);
    }

    @Contract("_, _ -> new")
    public static @NotNull SkinTexture of(final UUID uuid, final String texture) {
        return new SkinTexture(uuid, texture);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull SkinTexture of(final UUID uuid, final String texture, final PlayerTextures.SkinModel model) {
        return new SkinTexture(uuid, texture, model);
    }

    /**
     * Returns the URI from the {@link #texture} String.
     *
     * @return Returns a newly created {@link URI}, pointing to "http(s)://textures.minecraft.net".
     */
    public @NotNull URI uri() {
        if (texture.startsWith("http://textures.minecraft.net") || texture.startsWith("https://textures.minecraft.net")) {
            return URI.create(texture);
        }

        try {
            final byte[] base = Base64.getDecoder().decode(texture);
            final String textureString = new String(base, StandardCharsets.UTF_8);
            final JsonObject json = new Gson().fromJson(textureString, JsonObject.class);

            return URI.create(json.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString());
        } catch (IllegalArgumentException ignored) {
            try {
                final JsonObject json = new Gson().fromJson(texture, JsonObject.class);
                return URI.create(json.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString());
            } catch (JsonSyntaxException ex) {
                return URI.create("https://textures.minecraft.net/texture/" + texture);
            }
        } catch (JsonSyntaxException ex) {
            return URI.create("https://textures.minecraft.net/texture/" + texture);
        }
    }

}
