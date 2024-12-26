package com.itsschatten.yggdrasil.items.manipulators;

import com.itsschatten.yggdrasil.items.MetaManipulator;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A prebuilt {@link MetaManipulator} that will add potion effects to a potion.
 */
public final class AddPotionEffectsManipulator implements MetaManipulator {

    /**
     * The effects to add to the potion.
     */
    private final List<PotionEffect> effects;

    /**
     * Constructs the manipulator.
     *
     * @param effects A list of {@link PotionEffect} to add to the potion.
     */
    public AddPotionEffectsManipulator(final List<PotionEffect> effects) {
        this.effects = effects;
    }

    /**
     * Constructs the manipulator.
     *
     * @param effects An array of {@link PotionEffect} to add to the potion.
     */
    public AddPotionEffectsManipulator(final PotionEffect... effects) {
        this.effects = new ArrayList<>();
        this.effects.addAll(Arrays.stream(effects).toList());
    }

    @Override
    public void apply(final ItemMeta meta) {
        if (meta instanceof final PotionMeta potionMeta) {
            effects.forEach((effect) -> potionMeta.addCustomEffect(effect, true));
        } else {
            throw new IllegalStateException("Cannot add potion effects to " + meta + " because it does not support effects.");
        }
    }

}
