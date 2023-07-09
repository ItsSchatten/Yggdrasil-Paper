package com.itsschatten.yggdrasil.libs.menus.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.itsschatten.yggdrasil.libs.StringUtil;
import com.itsschatten.yggdrasil.libs.Utils;
import lombok.Builder;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Utility class to make fancy items.
 */
@Builder(toBuilder = true)
public final class ItemCreator {

    /**
     * The item that is going to be created.
     */
    private final ItemStack item;

    /**
     * The material that the item should be.
     */
    private final Material material;

    /**
     * The amount of the item.
     */
    @Builder.Default
    private final int amount = 1;

    /**
     * The name of the item.
     */
    private final String name;

    /**
     * The name of this item.
     */
    private final Component componentName;

    /**
     * A list of enchantments that should be applied to the item.
     */
    private final List<Enchant> enchants;

    /**
     * The lore of the item.
     */
    private final List<String> lore;

    /**
     * Hide all flags from the player.
     */
    private final boolean hideTags;

    /**
     * If true an enchantment will be applied to an item to make them glow.
     */
    private final boolean glow;

    /**
     * The item meta of the item.
     */
    private ItemMeta meta;

    /**
     * A list of flags that should be applied to an item.
     */
    private List<CreatorFlags> flags;

    /**
     * If true, the item will be unbreakable.
     */
    private boolean unbreakable;

    /**
     * Creates an item with a normal material.
     *
     * @param mat  The material of the item.
     * @param name The name of the item.
     * @param lore The lore of the item.
     * @return Returns an item creator.
     */
    public static ItemCreatorBuilder of(Material mat, String name, @NonNull String @NotNull ... lore) {
        for (int i = 0; i < lore.length; i++)
            lore[i] = "<gray>" + lore[i];

        return ItemCreator.builder().material(mat).name("<!i>" + name).lore(Arrays.asList(lore));
    }

    /**
     * Makes a copy of an item.
     *
     * @param copy The ItemStack that should be copied.
     * @return The copied item.
     */
    public static ItemCreatorBuilder of(ItemStack copy) {
        return ItemCreator.builder().item(copy);
    }

    /**
     * Makes an item.
     *
     * @param mat The material that the item should be.
     * @return The item builder.
     */
    public static ItemCreatorBuilder of(Material mat) {
        return ItemCreator.builder().material(mat);
    }

    /**
     * Generates a filler item with no name on the item.
     *
     * @param mat The material to use for this item.
     * @return An ItemCreator instance.
     */
    public static ItemCreator makeFillerItem(final Material mat) {
        return ItemCreator.of(mat).name("<white>").hideTags(true).build();
    }

    /**
     * Makes a skull of a player.
     *
     * @param owner The owner of the skull's {@link UUID}.
     * @return Returns this class, sets the item meta.
     */
    public ItemCreator setSkull(UUID owner) {
        Validate.notNull(material);
        Validate.isTrue(material == Material.PLAYER_HEAD);

        final ItemStack is = makeForMenu();
        final SkullMeta meta = (SkullMeta) is.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));

        this.meta = meta;

        return this;
    }

    /**
     * Set a skull's texture.
     *
     * @param skull The {@link ISkullDatabase} for this skull.
     * @return The new {@link ItemCreator} with the skull data.
     */
    public ItemCreator setSkull(@NotNull ISkullDatabase skull) {
        final ItemStack stack = makeForMenu();
        final SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
        assert skullMeta != null;

        final PlayerProfile profile = Bukkit.getServer().createProfile(skull.getUUID(), skull.getName());
        final PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(skull.getTextureURL()), skull.getSkinModel());
        } catch (MalformedURLException e) {
            Utils.logError(e);
            Utils.logError("Failed to get a proper URL for a skull!");
        }
        profile.setTextures(textures);
        skullMeta.setPlayerProfile(profile);

        this.meta = skullMeta;
        return this;
    }

    /**
     * Set a skulls skin based on the url provided.
     *
     * @param uuid  The {@link UUID} for this skull, used in {@link PlayerProfile}.
     * @param name  The name for that should be supplied to {@link PlayerProfile}
     * @param url   The url for the skull.
     * @param model The skin model to use for this skull.
     * @return A new ItemCreator with updated meta.
     */
    public @NotNull ItemCreator setSkull(final UUID uuid, final String name, @NotNull String url, final PlayerTextures.SkinModel model) {
        final ItemStack stack = makeForMenu();
        final SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
        assert skullMeta != null;
        final PlayerProfile profile = Bukkit.getServer().createProfile(uuid, name);
        final PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(url), model);
        } catch (MalformedURLException e) {
            Utils.logError(e);
            Utils.logError("Failed to get a proper URL for a skull!");
        }
        profile.setTextures(textures);
        skullMeta.setPlayerProfile(profile);
        this.meta = skullMeta;
        return this;
    }

    /**
     * Add data to the item's {@link org.bukkit.persistence.PersistentDataContainer}
     *
     * @param tag   The tag to add.
     * @param type  The type of data this tag stores.
     * @param value The value we are setting.
     * @param <T>   The primary type.
     * @param <Z>   The type retrieved when stored.
     * @return A new ItemCreator with updated data.
     */
    public <T, Z> ItemCreator addData(final String tag, final PersistentDataType<T, Z> type, final Z value) {
        final ItemStack stack = makeForMenu();
        final ItemMeta meta = stack.getItemMeta();
        assert meta != null;

        meta.getPersistentDataContainer().set(Utils.makeKey(tag), type, value);
        this.meta = meta;
        return this;
    }

    /**
     * Check if the item contains a tag.
     *
     * @param tag  The tag we are checking.
     * @param type The type for this data.
     * @param <T>  The primary data type.
     * @param <Z>  Type retrieved.
     * @return True if the item contains this tag.
     */
    public <T, Z> boolean checkTag(final String tag, final PersistentDataType<T, Z> type) {
        final ItemStack stack = makeForMenu();
        final ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        return meta.getPersistentDataContainer().has(Utils.makeKey(tag), type);
    }

    /**
     * Get the tag from the item.
     *
     * @param tag  The tag we should get.
     * @param type The type for the tag.
     * @param <T>  The primary type.
     * @param <Z>  Type retrieved when gotten.
     * @return Get the object from the tag.
     */
    public <T, Z> Z getTag(final String tag, final PersistentDataType<T, Z> type) {
        final ItemStack stack = makeForMenu();
        final ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        return meta.getPersistentDataContainer().get(Utils.makeKey(tag), type);
    }

    /**
     * Color a piece of leather armor.
     *
     * @param color The color.
     * @return The class to make and set the item.
     */
    public ItemCreator setArmorColor(final Color color) {
        Validate.isTrue(material != null);
        Validate.isTrue(material.name().contains("LEATHER_"), "You can only set the dye color on leather armor!");

        final ItemStack is = makeForMenu();
        final LeatherArmorMeta armorMeta = (LeatherArmorMeta) is.getItemMeta();
        assert armorMeta != null;
        armorMeta.setColor(color);

        this.meta = armorMeta;
        return this;
    }

    /**
     * Color a piece of leather armor.
     *
     * @param hex The hex color for the color.
     * @return The class to make and set the item.
     */
    public ItemCreator setArmorColor(final String hex) {
        final TextColor clr = TextColor.fromHexString(hex);
        if (clr == null) return setArmorColor(Color.BLACK);
        return setArmorColor(Color.fromRGB(clr.value()));
    }

    /**
     * Set the color of a potion.
     *
     * @param color The color we should set.
     * @return The ItemCreator with updated meta.
     */
    public ItemCreator setPotionColor(final @NotNull java.awt.Color color) {
        Validate.notNull(material);
        Validate.isTrue(material == Material.POTION);

        final ItemStack stack = makeForMenu();
        final PotionMeta meta = (PotionMeta) stack.getItemMeta();
        assert meta != null;

        meta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
        this.meta = meta;
        return this;
    }

    /**
     * Set the color of a potion based on a HEX string.
     *
     * @param color The color we should set.
     * @return The ItemCreator with updated meta.
     */
    public ItemCreator setPotionColor(final String color) {
        final TextColor clr = TextColor.fromHexString(color);
        if (clr == null) {
            return setPotionColor(java.awt.Color.BLACK);
        }
        return setPotionColor(new java.awt.Color(clr.value()));
    }

    /**
     * Adds a potion effect to a potion.
     *
     * @param effect The effect to add.
     * @return The ItemCreator with updated meta.
     */
    public ItemCreator addPotionEffect(final PotionEffect effect) {
        Validate.notNull(material);
        Validate.isTrue(material == Material.POTION);

        final ItemStack stack = makeForMenu();
        final PotionMeta meta = (PotionMeta) stack.getItemMeta();
        assert meta != null;

        meta.addCustomEffect(effect, true);
        this.meta = meta;
        return this;
    }

    /**
     * Makes the item and makes it unbreakable.
     *
     * @return The make method to create the item.
     */
    public @NotNull ItemStack makeUnbreakable() {
        unbreakable = true;

        return make();
    }

    /**
     * Makes an item for a menu specifically.
     *
     * @return The new fancy item.
     */
    public @NotNull ItemStack makeForMenu() {
        if (item == null) {
            Objects.requireNonNull(material, "Material cannot equal null.");
        }

        final ItemStack is = item != null ? item.clone() : new ItemStack(material);
        is.setAmount(amount);

        return getGeneratedItemStack(is);
    }

    @NotNull
    private ItemStack getGeneratedItemStack(ItemStack is) {
        final ItemMeta makerMeta = meta != null ? meta.clone() : is.getItemMeta();

        flags = flags == null ? new ArrayList<>() : new ArrayList<>(flags);
        if (makerMeta != null) {
            if (glow) {
                if (is.getType().getEquipmentSlot() != EquipmentSlot.HAND || is.getType().getEquipmentSlot() != EquipmentSlot.OFF_HAND)
                    makerMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
                else
                    makerMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);

                flags.add(CreatorFlags.HIDE_ENCHANTS);
            }

            if (enchants != null) {
                for (final Enchant enchant : enchants)
                    makerMeta.addEnchant(enchant.getEnchant(), enchant.getLevel(), true);
            }

            if (name != null) {
                makerMeta.displayName(StringUtil.color("<!i><white>" + name));
            } else if (componentName != null) {
                makerMeta.displayName(StringUtil.color("<!i><white>").append(componentName));
            } else if (makerMeta.displayName() != null){
                makerMeta.displayName(Objects.requireNonNull(makerMeta.displayName()).decoration(TextDecoration.ITALIC, false));
            }

            applyLoreAndItemFlags(makerMeta);

            if (assignItemMeta(is, makerMeta)) return is;
        }
        return is;
    }

    /**
     * Makes the item.
     *
     * @return The made item.
     */
    public @NotNull ItemStack make() {
        final ItemStack is = makeForMenu();
        final ItemMeta makerMeta = meta != null ? meta.clone() : is.getItemMeta();

        if (makerMeta != null) {
            final List<Component> lore = makerMeta.lore() == null ? new ArrayList<>() : makerMeta.lore();

            if (lore != null && lore.size() != 0) {
                makerMeta.lore(lore);
            }

            if (assignItemMeta(is, makerMeta)) return is;
        }
        return is;
    }

    /**
     * Assigns some {@link ItemMeta} to an ItemStack.
     *
     * @param is        The {@link ItemStack} that the ItemMeta should be assigned to.
     * @param makerMeta The ItemMeta that should be applied.
     * @return True if an ItemStack is a {@link Material#PLAYER_HEAD} or some sort of dyeable leather item.
     */
    private boolean assignItemMeta(@NotNull ItemStack is, ItemMeta makerMeta) {
        if (is.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) makerMeta;
            is.setItemMeta(skullMeta);
            return true;
        }

        if (is.getType().name().contains("LEATHER_")) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) makerMeta;

            if (unbreakable) {
                armorMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
                armorMeta.setUnbreakable(unbreakable);
            }
            is.setItemMeta(armorMeta);
            return true;
        }

        is.setItemMeta(makerMeta);
        return false;
    }

    /**
     * Applies the lore and {@link CreatorFlags} to the {@link ItemStack}
     *
     * @param makerMeta The {@link ItemMeta} for the create method.
     */
    private void applyLoreAndItemFlags(final ItemMeta makerMeta) {
        if (makerMeta == null)
            return;

        final List<Component> coloredLore = new ArrayList<>();
        if (lore != null) {
            lore.forEach((line) -> coloredLore.add(StringUtil.color("<!i><gray>" + line.replace("\\s", " "))));
        }

        if (coloredLore.size() > 0) {
            makerMeta.lore(coloredLore);
        }

        if (unbreakable) {
            flags.add(CreatorFlags.HIDE_ATTRIBUTES);
            flags.add(CreatorFlags.HIDE_UNBREAKABLE);

            makerMeta.setUnbreakable(true);
        }

        if (hideTags) {
            for (final CreatorFlags f : CreatorFlags.values()) {
                if (!flags.contains(f))
                    flags.add(f);
            }
        }

        try {
            final List<ItemFlag> bukkitFlags = new ArrayList<>();

            for (final CreatorFlags flag : flags)
                bukkitFlags.add(ItemFlag.valueOf(flag.toString()));

            makerMeta.addItemFlags(bukkitFlags.toArray(new ItemFlag[0]));
        } catch (Throwable ignored) {
        }
    }

    /**
     * Flags that we can use to hide things on items.
     */
    public enum CreatorFlags {
        /**
         * Hides the enchantment names on items.
         */
        HIDE_ENCHANTS,
        /**
         * Hides the attributes of an item, like the damage a sword does or the damage reduction of armor.
         */
        HIDE_ATTRIBUTES,
        /**
         * Hides that the equipment is unbreakable.
         */
        HIDE_UNBREAKABLE,
        /**
         * Hides that the item is dyed.
         */
        HIDE_DYE,
        /**
         * Hides what the tool can destroy.
         */
        HIDE_DESTROYS,
        /**
         * Hides what the block can be placed on.
         */
        HIDE_PLACED_ON,
        /**
         * Hides the potion effects that are shown on items (i.e. Speed II 3:00) and other things.
         */
        HIDE_POTION_EFFECTS
    }

    /**
     * Fancy builder classes.
     */
    public static class ItemCreatorBuilder {
        // This is simply here to satisfy any IDE issues,
        // this is generated via Lombok.
    }

}
