package com.itsschatten.yggdrasil.items.manipulators;

import com.itsschatten.yggdrasil.items.MetaManipulator;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

/**
 * A prebuilt {@link MetaManipulator} that will alter the color of dyeable items.
 */
public final class ColorManipulator implements MetaManipulator {

    /**
     * The color to dye the item to.
     */
    private final Color color;

    /**
     * Constructs a new ColorManipulator.
     *
     * @param color The {@link Color} to dye to item.
     */
    public ColorManipulator(final Color color) {
        this.color = color;
    }

    /**
     * Constructs a new ColorManipulator.
     *
     * @param hex A hexadecimal representation of a string.
     */
    public ColorManipulator(final @NotNull String hex) {
        this(Color.fromRGB(Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16)));
    }

    /**
     * Constructs a new ColorManipulator.
     *
     * @param rgb An integer representation of a color, consisting of red, green, and blue.
     */
    public ColorManipulator(final int rgb) {
        this(Color.fromRGB(rgb));
    }

    /**
     * Constructs a new ColorManipulator.
     *
     * @param red   The red value for the color.
     * @param green The green value for the color.
     * @param blue  The blue value for the color.
     */
    public ColorManipulator(final int red, final int green, final int blue) {
        this(Color.fromRGB(red, green, blue));
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        switch (meta) {
            case final MapMeta mapMeta -> mapMeta.setColor(color);
            case final LeatherArmorMeta leather -> leather.setColor(color);
            case final PotionMeta potion -> potion.setColor(color);

            default -> throw new IllegalStateException("Unexpected ItemMeta: " + meta);
        }
    }

}
