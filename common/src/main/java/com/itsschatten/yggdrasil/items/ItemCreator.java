package com.itsschatten.yggdrasil.items;

import com.itsschatten.yggdrasil.StringUtil;
import lombok.Builder;
import lombok.Singular;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A utility class to create {@link ItemStack} using a Builder.
 */
@Builder(toBuilder = true)
public final class ItemCreator {

    /**
     * The material for the item.
     */
    @NotNull
    private Material material;

    /**
     * The stack size of the item.
     */
    @Builder.Default
    private int amount = 1;

    /**
     * The name of the item.
     */
    private Component name;

    /**
     * The display name of the item.
     */
    private Component display;

    /**
     * The lore that appears when hovered for the item.
     */
    private List<Component> lore;

    /**
     * Options for the item.
     */
    private ItemOptions options;

    /**
     * {@link Manipulator}s, used to alter the ItemMeta or the ItemStack before the rest of this builder is applied.
     */
    @Singular
    private List<Manipulator<?>> manipulators;

    /**
     * Starting {@link ItemMeta}.
     */
    private ItemMeta meta;

    /**
     * Quickly make a {@link ItemCreatorBuilder} with the provided {@link Material}.
     *
     * @param material The {@link Material} for the {@link ItemStack} being built.
     * @return Returns an {@link ItemCreatorBuilder} instance.
     */
    public static ItemCreatorBuilder of(final Material material) {
        return ItemCreator.builder().material(material);
    }

    /**
     * Quickly make a {@link ItemCreatorBuilder} with the provided {@link ItemStack}.
     *
     * @param stack The {@link ItemStack} to build the builder with.
     * @return Returns an {@link ItemCreatorBuilder} instance.
     */
    public static ItemCreatorBuilder of(final @NotNull ItemStack stack) {
        final ItemMeta meta = stack.getItemMeta();
        if (meta == null) return ItemCreator.builder().material(stack.getType());

        return ItemCreator.builder().material(stack.getType()).meta(meta);
    }

    /**
     * Creates the {@link ItemStack}.
     *
     * @return Returns the built {@link ItemStack}, built using the values of this class.
     */
    public @NotNull ItemStack make() {
        final ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = this.meta != null ? this.meta : stack.getItemMeta();

        if (meta != null) {
            for (final Manipulator<?> manipulator : this.manipulators) {
                if (manipulator instanceof final ItemManipulator item) {
                    item.apply(stack);
                    // We update the meta here to avoid "lagging" changes or changes that don't appear do actually do anything.
                    // Such as using ItemStack#setData not being reflected because the meta differs, better to keep constant.
                    meta = stack.getItemMeta();
                } else if (manipulator instanceof final MetaManipulator metaManipulator) {
                    metaManipulator.apply(meta);
                } else {
                    throw new IllegalArgumentException("Unsupported manipulator found: " + manipulator);
                }
            }

            if (this.display != null) meta.displayName(this.display);
            if (this.name != null) meta.itemName(this.name);
            if (this.lore != null) meta.lore(this.lore);

            if (this.options != null) this.options.apply(meta);

            stack.setItemMeta(meta);
        } else {
            throw new IllegalStateException("ItemMeta in an ItemCreator make method must not be null!");
        }

        return stack;
    }

    /**
     * The builder class for {@link ItemCreator}.
     */
    public static class ItemCreatorBuilder {
        public ItemCreatorBuilder name(final Component component) {
            this.name = component;
            return this;
        }

        public ItemCreatorBuilder name(final String name) {
            return name(StringUtil.color(name));
        }

        public ItemCreatorBuilder display(final Component component) {
            this.display = component;
            return this;
        }

        public ItemCreatorBuilder display(final String display) {
            // We want to FORCE names to not be italic by default, so we disable that here.
            // The end user can still override this by making it italic.
            return display(StringUtil.color("<!i>" + display));
        }

        public ItemCreatorBuilder lore(final @NotNull List<Component> lore) {
            this.lore = lore.stream().map(component -> component.colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList();
            return this;
        }

        public ItemCreatorBuilder lore(final Component... lore) {
            if (this.lore == null) {
                this.lore = new ArrayList<>();
            }

            this.lore.addAll(Stream.of(lore).map(component -> component.colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList());
            return this;
        }

        public ItemCreatorBuilder lore(final String... lore) {
            if (this.lore == null) {
                this.lore = new ArrayList<>();
            }

            this.lore.addAll(Stream.of(lore).map(string -> StringUtil.color("<!i><gray>" + string)).toList());
            return this;
        }

        public ItemCreatorBuilder lore(final @NotNull Collection<String> lore) {
            this.lore = lore.stream().map(string -> StringUtil.color("<!i><gray>" + string)).toList();
            return this;
        }

        public ItemCreatorBuilder options(final ItemOptions options) {
            this.options = options;
            return this;
        }

        public ItemCreatorBuilder options(final ItemOptions.@NotNull ItemOptionsBuilder builder) {
            this.options = builder.build();
            return this;
        }

        /**
         * Returns this builder as a {@link Supplier}.
         *
         * @return Returns {@code this} as a Supplier.
         */
        public Supplier<ItemCreatorBuilder> supplier() {
            return () -> this;
        }

    }

}
