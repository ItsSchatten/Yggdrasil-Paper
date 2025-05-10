package com.itsschatten.yggdrasil.anvilgui.interfaces;

import com.itsschatten.yggdrasil.StringUtil;
import com.itsschatten.yggdrasil.anvilgui.AnvilGUI;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * A functional interface whose function is {@link #accept(Object, Object)}
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface Response extends BiConsumer<AnvilGUI, Player> {

    /**
     * Replaces the input text.
     *
     * @param text The text to replace.
     * @return Returns a new {@link Response}.
     */
    @Contract(pure = true)
    static @NotNull Response replaceInputText(String text) {
        Validate.notNull(text, "Text must not be null!");
        return (anvilGUI, player) -> {
            ItemStack item = anvilGUI.inventory().getItem(2);
            if (item != null) {
                item = anvilGUI.inventory().getItem(0);
            }

            if (item == null) {
                throw new IllegalStateException("replaceInputText may only be used if the OUTPUT or INPUT_LEFT are not empty.");
            } else {
                final ItemStack cloned = item.clone();
                final ItemMeta meta = cloned.getItemMeta();
                meta.displayName(StringUtil.color(text));
                cloned.setItemMeta(meta);
                anvilGUI.inventory().setItem(0, cloned);
            }
        };
    }

    /**
     * Closes the AnvilGUI.
     *
     * @return Returns {@link AnvilGUI#closed()}
     */
    @Contract(pure = true)
    static @NotNull Response close() {
        return (anvilGUI, player) -> anvilGUI.closed();
    }

    /**
     * Opens a new {@link Menu}
     *
     * @param menu   The menu to open.
     * @param holder The holder to open the menu for.
     * @param <T>    The generic holder.
     * @return Returns the new {@link Response} to open the new menu, it also closes the {@link AnvilGUI}.
     */
    @Contract(pure = true)
    static @NotNull <T extends MenuHolder> Response openMenu(final Menu<T> menu, final T holder) {
        return (anvilGUI, player) -> {
            Validate.notNull(menu, "Menu must not be null!");
            anvilGUI.closed();
            menu.displayTo(holder);
        };
    }

    /**
     * Run something.
     *
     * @param runnable The runnable to run.
     * @return Returns a new {@link Response} that runs the Runnable.
     */
    @Contract(pure = true)
    static @NotNull Response run(Runnable runnable) {
        Validate.notNull(runnable, "Runnable must be null");
        return (anvilgui, player) -> runnable.run();
    }

}
