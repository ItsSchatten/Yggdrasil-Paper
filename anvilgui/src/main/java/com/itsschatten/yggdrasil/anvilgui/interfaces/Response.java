package com.itsschatten.yggdrasil.anvilgui.interfaces;

import com.itsschatten.yggdrasil.StringUtil;
import com.itsschatten.yggdrasil.anvilgui.AnvilGUI;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
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

    @Contract(pure = true)
    static @NotNull Response updateTitle(String title) {
        Validate.notNull(title, "Title must not be null!");
        return (anvilGUI, player) -> anvilGUI.setTitle(title);
    }

    @Contract(pure = true)
    static @NotNull Response updateTitle(Component title) {
        Validate.notNull(title, "Title must not be null!");
        return (anvilGUI, player) -> anvilGUI.setTitle(title);
    }

    @Contract(pure = true)
    static @NotNull Response close() {
        return (anvilGUI, player) -> anvilGUI.closed();
    }

    @Contract(pure = true)
    static @NotNull Response openMenu(final Menu menu, final IMenuHolder holder) {
        return (anvilGUI, player) -> {
            Validate.notNull(menu, "Menu must not be null!");
            anvilGUI.closed();
            menu.displayTo(holder);
        };
    }

    @Contract(pure = true)
    static @NotNull Response run(Runnable runnable) {
        Validate.notNull(runnable, "Runnable must be null");
        return (anvilgui, player) -> runnable.run();
    }

}
