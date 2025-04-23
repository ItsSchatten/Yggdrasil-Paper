package com.itsschatten.yggdrasil.items;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
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

    // Most of these components are likely unnecessary and don't do anything on items.
    private final static Set<DataComponentType> ALL_COMPONENTS = new HashSet<>(Arrays.asList(
            //<editor-fold desc="All Components." defaultstate="collapsed">
            DataComponentTypes.MAX_STACK_SIZE,
            DataComponentTypes.MAX_DAMAGE,
            DataComponentTypes.DAMAGE,
            DataComponentTypes.UNBREAKABLE,
            DataComponentTypes.CUSTOM_NAME,
            DataComponentTypes.ITEM_NAME,
            DataComponentTypes.ITEM_MODEL,
            DataComponentTypes.LORE,
            DataComponentTypes.RARITY,
            DataComponentTypes.ENCHANTMENTS,
            DataComponentTypes.CAN_PLACE_ON,
            DataComponentTypes.CAN_BREAK,
            DataComponentTypes.ATTRIBUTE_MODIFIERS,
            DataComponentTypes.CUSTOM_MODEL_DATA,
            DataComponentTypes.TOOLTIP_DISPLAY,
            DataComponentTypes.REPAIR_COST,
            DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE,
            DataComponentTypes.INTANGIBLE_PROJECTILE,
            DataComponentTypes.FOOD,
            DataComponentTypes.CONSUMABLE,
            DataComponentTypes.USE_REMAINDER,
            DataComponentTypes.USE_COOLDOWN,
            DataComponentTypes.DAMAGE_RESISTANT,
            DataComponentTypes.TOOL,
            DataComponentTypes.WEAPON,
            DataComponentTypes.ENCHANTABLE,
            DataComponentTypes.EQUIPPABLE,
            DataComponentTypes.REPAIRABLE,
            DataComponentTypes.GLIDER,
            DataComponentTypes.TOOLTIP_STYLE,
            DataComponentTypes.DEATH_PROTECTION,
            DataComponentTypes.BLOCKS_ATTACKS,
            DataComponentTypes.STORED_ENCHANTMENTS,
            DataComponentTypes.DYED_COLOR,
            DataComponentTypes.MAP_COLOR,
            DataComponentTypes.MAP_ID,
            DataComponentTypes.MAP_DECORATIONS,
            DataComponentTypes.MAP_POST_PROCESSING,
            DataComponentTypes.CHARGED_PROJECTILES,
            DataComponentTypes.BUNDLE_CONTENTS,
            DataComponentTypes.POTION_CONTENTS,
            DataComponentTypes.POTION_DURATION_SCALE,
            DataComponentTypes.SUSPICIOUS_STEW_EFFECTS,
            DataComponentTypes.WRITABLE_BOOK_CONTENT,
            DataComponentTypes.WRITTEN_BOOK_CONTENT,
            DataComponentTypes.TRIM,
            DataComponentTypes.INSTRUMENT,
            DataComponentTypes.PROVIDES_TRIM_MATERIAL,
            DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER,
            DataComponentTypes.JUKEBOX_PLAYABLE,
            DataComponentTypes.PROVIDES_BANNER_PATTERNS,
            DataComponentTypes.RECIPES,
            DataComponentTypes.LODESTONE_TRACKER,
            DataComponentTypes.FIREWORK_EXPLOSION,
            DataComponentTypes.FIREWORKS,
            DataComponentTypes.PROFILE,
            DataComponentTypes.NOTE_BLOCK_SOUND,
            DataComponentTypes.BANNER_PATTERNS,
            DataComponentTypes.BASE_COLOR,
            DataComponentTypes.POT_DECORATIONS,
            DataComponentTypes.CONTAINER,
            DataComponentTypes.CONTAINER_LOOT,
            DataComponentTypes.BREAK_SOUND,
            DataComponentTypes.VILLAGER_VARIANT,
            DataComponentTypes.WOLF_VARIANT,
            DataComponentTypes.WOLF_SOUND_VARIANT,
            DataComponentTypes.WOLF_COLLAR,
            DataComponentTypes.FOX_VARIANT,
            DataComponentTypes.SALMON_SIZE,
            DataComponentTypes.PARROT_VARIANT,
            DataComponentTypes.TROPICAL_FISH_PATTERN,
            DataComponentTypes.TROPICAL_FISH_BASE_COLOR,
            DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR,
            DataComponentTypes.MOOSHROOM_VARIANT,
            DataComponentTypes.RABBIT_VARIANT,
            DataComponentTypes.PIG_VARIANT,
            DataComponentTypes.COW_VARIANT,
            DataComponentTypes.CHICKEN_VARIANT,
            DataComponentTypes.FROG_VARIANT,
            DataComponentTypes.HORSE_VARIANT,
            DataComponentTypes.PAINTING_VARIANT,
            DataComponentTypes.LLAMA_VARIANT,
            DataComponentTypes.AXOLOTL_VARIANT,
            DataComponentTypes.CAT_VARIANT,
            DataComponentTypes.CAT_COLLAR,
            DataComponentTypes.SHEEP_COLOR,
            DataComponentTypes.SHULKER_COLOR
            //</editor-fold>
    ));

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
     * @implNote Due to the large number of components in the "{@link #ALL_COMPONENTS}"
     * list you should not use this variable and instead opt for explicit definition of
     * components that should be hidden.
     * @deprecated Deprecated in favor of using per-component hiding due to a large number of components in the list.
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.1.3")
    @Deprecated(since = "2.1.2", forRemoval = true)
    public static ItemOptions HIDE_ALL_FLAGS = ItemOptions.builder().hiddenComponents(ALL_COMPONENTS).build();

    /**
     * Flags to hide options on an item.
     *
     * @see DataComponentType
     * @see DataComponentTypes
     */
    private Set<DataComponentType> hiddenComponents;

    // It is recommended to check the ItemMeta methods to determine if a variable should be a primitive or a value-based class.

    /**
     * If the item's 'enchantment glint override' should be set, if assigned {@code null} the override will be removed.
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

    /**
     * Data for a custom model if one is applied.
     */
    private CustomModelData.Builder modelData;

    // TODO: Other common components.
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

        if (stack.hasData(DataComponentTypes.CUSTOM_MODEL_DATA)) {
            final CustomModelData data = stack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
            builder.modelData(data);
        }

        return builder
                .hideToolTip(meta.isHideTooltip())
                .unbreakable(meta.isUnbreakable())
                .resistantTo(meta.getDamageResistant())
                .hiddenComponents(stack.getData(DataComponentTypes.TOOLTIP_DISPLAY).hiddenComponents())
                .equipable((equip) -> meta.getEquippable())
                .build();
    }

    /**
     * Applies these options to the provided {@link ItemMeta}.
     *
     * @param itemStack The {@link ItemMeta} to apply these options to.
     * @see ItemCreator#make()
     */
    @ApiStatus.Internal
    public void apply(final ItemStack itemStack) {
        if (itemStack == null) return;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        meta.setHideTooltip(hideToolTip);
        meta.setUnbreakable(unbreakable);
        meta.setDamageResistant(resistantTo);

        if (equipable != null) meta.setEquippable(equipable.apply(meta.getEquippable()));
        if (model != null) meta.setItemModel(NamespacedKey.fromString(model.asString()));
        if (modelData != null) {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();
            final CustomModelData data = this.modelData.build();

            component.setFlags(data.flags());
            component.setFloats(data.floats());
            component.setColors(data.colors());
            component.setStrings(data.strings());

            meta.setCustomModelDataComponent(component);
        }

        if (glow != null) meta.setEnchantmentGlintOverride(glow);

        if (rarity != null) meta.setRarity(rarity);

        itemStack.setItemMeta(meta);
        if (hiddenComponents != null && !hiddenComponents.isEmpty()) {
            itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hiddenComponents(hiddenComponents).build());
        }
    }


    /**
     * The builder for an {@link ItemOptions}.
     */
    public static class ItemOptionsBuilder {

        public ItemOptionsBuilder modelData(final CustomModelData.Builder modelData) {
            this.modelData = modelData;
            return this;
        }

        public ItemOptionsBuilder modelData(final @NotNull CustomModelData data) {
            this.modelData = CustomModelData.customModelData().addColors(data.colors()).addFlags(data.flags()).addFloats(data.floats()).addStrings(data.strings());
            return this;
        }

        public ItemOptionsBuilder modelData(final @NotNull UnaryOperator<CustomModelData.Builder> data) {
            this.modelData = data.apply(this.modelData == null ? CustomModelData.customModelData() : this.modelData);
            return this;
        }

        public ItemOptionsBuilder hiddenComponents(Set<DataComponentType> dataComponents) {
            this.hiddenComponents = dataComponents;
            return this;
        }

        public ItemOptionsBuilder hiddenComponents(Collection<DataComponentType> dataComponents) {
            this.hiddenComponents = new HashSet<>(dataComponents);
            return this;
        }

        public ItemOptionsBuilder hiddenComponents(DataComponentType... dataComponents) {
            this.hiddenComponents = Arrays.stream(dataComponents).collect(Collectors.toSet());
            return this;
        }

        public ItemOptionsBuilder hiddenComponent(DataComponentType dataComponent) {
            if (this.hiddenComponents == null) {
                this.hiddenComponents = new HashSet<>();
            }
            this.hiddenComponents.add(dataComponent);
            return this;
        }

    }

}
