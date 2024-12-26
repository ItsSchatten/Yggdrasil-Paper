package com.itsschatten.yggdrasil.items;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Represents options for the {@link ItemCreator}. Used to determine how an item will appear or interact with the world.
 */
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public final class ItemOptions {

    /**
     * Empty options.
     */
    public static ItemOptions EMPTY = ItemOptions.builder().build();

    /**
     * Preset options for filler items.
     */
    public static ItemOptions FILLER = ItemOptions.builder().hideToolTip(true).build();

    /**
     * Preset options for making an item glow.
     */
    public static ItemOptions GLOW = ItemOptions.builder().glow(true).build();

    /**
     * Preset options for hiding all flags on the item.
     *
     * @implNote Due to the way Paper handles ItemFlags, flags that may not be appropriate for the item will be set.
     * I.E., if the item doesn't contain any enchantments but the flag for hiding enchantments is supplied, that flag will not be set on the item.
     */
    public static ItemOptions HIDE_ALL_FLAGS = ItemOptions.builder().itemFlags(Arrays.stream(ItemFlag.values()).collect(Collectors.toSet())).build();

    /**
     * Flags to hide options on an item.
     *
     * @see ItemFlag
     */
    private Set<ItemFlag> itemFlags;

    // It is recommended to check the ItemMeta methods to determine if a variable should be a primitive or a value-based class.

    /**
     * If the item's 'enchantment glint override' should be set, if assigned {@code null} the override will not be set.
     */
    private Boolean glow;

    /**
     * The Minecraft rarity of this item, all items will have a default rarity assigned. Rarity alters the color of the item and display names if no color is supplied.
     *
     * @see ItemRarity
     */
    private ItemRarity rarity;

    /**
     * If the item should have its tooltip hidden when hovered in an inventory.
     */
    private boolean hideToolTip;

    /**
     * If the item should be unbreakable.
     */
    private boolean unbreakable;

    /**
     * If the item should be immune to burning in fire or lava.
     */
    private Tag<DamageType> resistantTo;

    /**
     * The model for this item.
     */
    private Key model;

    // TODO: Other components.
    // This needs a list.

    /**
     * Equipable component for this item.
     */
    private UnaryOperator<EquippableComponent> equipable;

    /**
     * Get the options from a provided {@link ItemStack}.
     *
     * @param stack The {@link ItemStack} to get the options from.
     * @return Returns the options of the provided {@link ItemStack}.
     */
    public static ItemOptions fromItemStack(final @NotNull ItemStack stack) {
        final ItemMeta meta = stack.getItemMeta();
        if (meta == null) return EMPTY;

        final ItemOptionsBuilder builder = ItemOptions.builder();

        if (meta.hasItemModel()) builder.model(meta.getItemModel());
        if (meta.hasEnchantmentGlintOverride()) builder.glow(meta.getEnchantmentGlintOverride());
        if (meta.hasRarity()) builder.rarity(meta.getRarity());

        return builder
                .hideToolTip(meta.isHideTooltip())
                .unbreakable(meta.isUnbreakable())
                .resistantTo(meta.getDamageResistant())
                .itemFlags(meta.getItemFlags())
                .equipable((equip) -> meta.getEquippable())
                .build();
    }

    /**
     * Applies these options to the provided {@link ItemMeta}.
     *
     * @param meta The {@link ItemMeta} to apply these options to.
     * @see ItemCreator#make()
     */
    @ApiStatus.Internal
    public void apply(final ItemMeta meta) {
        if (meta == null) return;

        meta.setHideTooltip(hideToolTip);
        meta.setUnbreakable(unbreakable);
        meta.setDamageResistant(resistantTo);

        if (equipable != null) meta.setEquippable(equipable.apply(meta.getEquippable()));
        if (model != null) meta.setItemModel(NamespacedKey.fromString(model.asString()));

        if (glow != null) meta.setEnchantmentGlintOverride(glow);

        if (rarity != null) meta.setRarity(rarity);

        if (itemFlags != null && !itemFlags.isEmpty()) {
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }
    }

    /**
     * The builder for an {@link ItemOptions}.
     */
    public static class ItemOptionsBuilder {

        public ItemOptionsBuilder itemFlags(Set<ItemFlag> itemFlags) {
            this.itemFlags = itemFlags;
            return this;
        }

        public ItemOptionsBuilder itemFlags(Collection<ItemFlag> itemFlags) {
            this.itemFlags = new HashSet<>(itemFlags);
            return this;
        }

        public ItemOptionsBuilder itemFlags(ItemFlag... itemFlags) {
            this.itemFlags = Arrays.stream(itemFlags).collect(Collectors.toSet());
            return this;
        }
    }

}
