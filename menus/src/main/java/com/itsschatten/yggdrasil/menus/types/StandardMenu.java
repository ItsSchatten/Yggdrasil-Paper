package com.itsschatten.yggdrasil.menus.types;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.premade.CloseButton;
import com.itsschatten.yggdrasil.menus.buttons.premade.InfoButton;
import com.itsschatten.yggdrasil.menus.buttons.premade.ReturnButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Standard menu, nothing special about this one.
 */
public abstract class StandardMenu<T extends MenuHolder> extends Menu<T> {

    /**
     * The parent of this menu.
     * --- GETTER ---
     * Gets the parent of the menu.
     *
     * @return A possibly {@code null} {@link Menu} instance.
     */
    @Nullable
    @Getter
    private final Menu<T> parent;

    /**
     * Standard implementation of {@link StandardMenu}.
     *
     * @param parent The parent (or previous) {@link StandardMenu}.
     */
    public StandardMenu(@Nullable final Menu<T> parent, String title, int size) {
        super(size, title);
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void formInventory() {
        registerPreMadeButtons();
        drawExtra();
        drawButtons();
    }

    /**
     * Used to draw extra things to the menu that aren't necessarily menu buttons.
     */
    public void drawExtra() {
    }

    /**
     * {@inheritDoc}
     *
     * @param position The position to check.
     * @return Returns {@code true} if, and only if, a {@link Button} was found to be in the same position as the one provided.
     */
    @Override
    @ApiStatus.Internal
    public boolean isSlotTakenByButton(InventoryPosition position) {
        // We filter any buttons the player doesn't have permission to view, as those technically don't exist.
        // Then we find any buttons that equal the provided position.
        // In honesty, the permission check is kinda redundant, as there is no "weight"
        // to button registering, instead just when the button is registered.
        return buttons().stream()
                .filter(button -> button.getPermission() != null && !holder().hasPermission(button.getPermission()))
                .anyMatch((button) -> button.getPosition().equals(position));
    }

    // TODO: unfinalize?
    @Override
    public final Button<T> getButton(ItemStack stack, InventoryPosition position) {
        return getButtonImpl(stack, position, buttons());
    }

    /**
     * Registers all pre-made buttons and sets them in the inventory if required.
     */
    protected final void registerPreMadeButtons() {
        if (addInfo() && getInfoButton() != null) {
            registerButtons(getInfoButton().build());
        }

        if (addClose() && getCloseButton() != null) {
            registerButtons(getCloseButton().build());
        }

        if (addReturn() && getReturnButton() != null) {
            Objects.requireNonNull(parent, "Parent cannot be 'null' if the menu wants to add a return button!");
            registerButtons(getReturnButton().build());
        }

    }

    // Pre-made button things.

    /**
     * The {@link CloseButton}.
     *
     * @return Returns a {@link CloseButton} builder class.
     * @see CloseButton#builder()
     */
    @Nullable
    public CloseButton.CloseButtonBuilder<T> getCloseButton() {
        return CloseButton.<T>builder()
                .material(Material.BARRIER)
                .name("<red>Close")
                .position(InventoryPosition.of(rows() - 1, columns() - 1));
    }

    /**
     * The {@link ReturnButton}.
     *
     * @return Returns a {@link ReturnButton} builder class.
     * @see ReturnButton#builder()
     */
    @Nullable
    public ReturnButton.ReturnButtonBuilder<T> getReturnButton() {
        return ReturnButton.<T>builder()
                .menuToReturn(parent)
                .material(Material.ARROW)
                .name("<yellow>< Return to " + (parent != null ? parent.getTitle() : ""))
                .position(InventoryPosition.of(rows() - 1, columns() - 2));
    }

    /**
     * The {@link InfoButton}.
     *
     * @return Returns the {@link InfoButton}.
     * @see InfoButton#builder()
     */
    @Nullable
    public InfoButton.InfoButtonBuilder<T> getInfoButton() {
        return InfoButton.<T>builder()
                .material(Material.NETHER_STAR)
                .name("<yellow>Information")
                .lore(getInfo())
                .position(InventoryPosition.of(rows() - 1, columns() - 9));
    }

    /**
     * The lore for the info button.
     *
     * @return Nothing by default.
     */
    public List<String> getInfo() {
        return List.of();
    }

    /**
     * Should the close button be added to the menu?
     *
     * @return <code>false</code> by default.
     */
    public boolean addClose() {
        return false;
    }

    /**
     * Should the return button be added to the menu?
     *
     * @return If the Menu's parent is not {@code null} the button is shown.
     */
    public boolean addReturn() {
        return parent != null;
    }

    /**
     * Should the info button be added to the menu?
     *
     * @return If the lore for the button is not null and the length is greater than 0, the button will be shown.
     */
    public boolean addInfo() {
        return getInfo() != null && !getInfo().isEmpty();
    }

}
