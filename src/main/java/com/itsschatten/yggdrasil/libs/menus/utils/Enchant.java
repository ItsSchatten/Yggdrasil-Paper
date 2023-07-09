package com.itsschatten.yggdrasil.libs.menus.utils;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;

/**
 * Utility class used in {@link ItemCreator} to quickly apply enchantments to items.
 */
@Getter
public class Enchant {
    /**
     * The {@link Enchantment} this should be.
     */
    private final Enchantment enchant;

    /**
     * The level for the enchantment.
     */
    private final int level;

    /**
     * Creates a new enchantment with a level of one.
     *
     * @param enchant The enchantment that you want applied.
     */
    public Enchant(Enchantment enchant) {
        this(enchant, 1);
    }

    /**
     * Create a new enchantment with a specified level.
     *
     * @param enchant The enchantment that you want applied.
     * @param level   The level that the enchantment will be.
     */
    public Enchant(Enchantment enchant, int level) {
        this.enchant = enchant;
        this.level = level;
    }

}
