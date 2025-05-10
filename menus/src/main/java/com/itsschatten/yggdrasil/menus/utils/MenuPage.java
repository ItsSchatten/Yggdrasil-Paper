package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.premade.PageNavigationButton;
import com.itsschatten.yggdrasil.menus.types.PageMenu;
import lombok.Builder;
import lombok.Singular;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a page in a {@link PageMenu}.
 */
@Builder
public record MenuPage<T extends MenuHolder>(PageNavigationButton navButton, @Singular List<Button<T>> buttons) {

    public void register(final @NotNull PageMenu<T> menu) {
        if (menu.hasPreviousPage()) menu.clearPreviousPage();

        menu.registerPageButtons(buttons);
    }

    /**
     * Get all button positions.
     *
     * @return Returns a list of {@link InventoryPosition} based on the provided buttons.
     */
    public List<InventoryPosition> positions() {
        return buttons.stream().map(Button::getPosition).collect(Collectors.toList());
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "MenuPage[" +
                "navButton=" + navButton + ", " +
                "buttons=" + buttons + ']';
    }

}
