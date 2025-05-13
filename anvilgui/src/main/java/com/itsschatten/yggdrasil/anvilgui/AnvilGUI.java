package com.itsschatten.yggdrasil.anvilgui;

import com.itsschatten.yggdrasil.StringUtil;
import com.itsschatten.yggdrasil.anvilgui.interfaces.ClickHandler;
import com.itsschatten.yggdrasil.anvilgui.interfaces.Response;
import com.itsschatten.yggdrasil.menus.utils.CloseReason;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class and much of the AnvilGUI API has been modeled after <a href="https://github.com/WesJD/AnvilGUI/tree/master">AnvilGUI</a>.
 *
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class AnvilGUI {

    /**
     * The AnvilGUI's that are open.
     */
    @Getter
    @Accessors(fluent = true)
    private static final Map<Inventory, AnvilGUI> OPEN_ANVILS = new HashMap<>();

    /**
     * The listener for this class, registered dynamically.
     */
    private final InventoryListener listener = new InventoryListener();

    // Generic things to set.

    /**
     * The plugin to assign to this AnvilGUI instance.
     */
    @NotNull
    private final Plugin plugin;

    /**
     * The {@link Player} that the inventory was opened for.
     */
    private final Player player;

    /**
     * The {@link MenuHolder} that the inventory was opened for.
     */
    private final MenuHolder holder;

    /**
     * The initial items in the inventory, set in the builder under the names {@code inputLeft}, {@code inputRight}, and {@code output}
     */
    private final ItemStack[] initialItems;

    /**
     * If the inventory should prevent normal closing.
     */
    private final boolean preventClosing;

    /**
     * The slots that may be interacted with in the inventory, this defaults to nothing.
     */
    private final Set<Integer> interactableSlots;

    // Executors and Handlers.

    /**
     * The main executor for the inventory, defaults to the server's main thread.
     */
    private final Executor executor;

    /**
     * What happens when the inventory is closed.
     */
    private final Consumer<Snapshot> onClose;

    /**
     * What happens when you click an item in the inventory.
     */
    private final ClickHandler clickHandler;

    /**
     * If the Anvil is allowed to process multiple click handles concurrently, this default to false.
     */
    private final boolean concurrentClickHandlers;

    /**
     * The response after clicking.
     */
    private final Response response;
    /**
     * The text that appears on the input item.
     */
    private final Component itemText;
    /**
     * The title of the inventory.
     */
    private final Component title;

    // Instance variables.
    /**
     * The actual {@link Inventory} instance.
     */
    private Inventory inventory;

    /**
     * The {@link org.bukkit.inventory.InventoryView} for this anvil.
     *
     * @see AnvilView
     */
    private AnvilView view;

    /**
     * If this anvil is open or not.
     */
    private boolean open;

    /**
     * Builds an AnvilGUI from the {@link AnvilGUIBuilder}.
     *
     * @param plugin                  The {@link Plugin} instance to use to register tasks and such.
     * @param player                  The {@link Player} this AnvilGUI is for, may be {@code null} if holder is set.
     * @param holder                  The {@link MenuHolder} this AnvilGUI is for, may be {@code null} if the {@link Player} is set.
     * @param inputLeft               The first input {@link ItemStack}, defaults to paper if {@code null}.
     * @param inputRight              The second input slot.
     * @param output                  The output item.
     * @param preventClosing          If this AnvilGUI should prevent closing without an input.
     * @param interactableSlots       A {@link Set} of slots that may be interacted with.
     * @param executor                The main {@link Executor} for the AnvilGUI.
     * @param onClose                 What should be executed when the anvil is closed.
     * @param clickHandler            What should be executed when an item is clicked.
     * @param concurrentClickHandlers If we should allow the click handlers to run concurrently if they are async.
     * @param response                What should be returned in response for this anvil.s
     * @param title                   The title of the inventory.
     * @param itemText                The text that appears on the input item will not be used if the first input item is not null.
     */
    @Builder
    public AnvilGUI(@NotNull Plugin plugin, Player player, MenuHolder holder, ItemStack inputLeft, ItemStack inputRight, ItemStack output, boolean preventClosing, Set<Integer> interactableSlots, Executor executor, Consumer<Snapshot> onClose, ClickHandler clickHandler, boolean concurrentClickHandlers, Response response, Component title, Component itemText) {
        this.plugin = plugin;
        this.player = player;
        this.holder = holder;
        this.executor = executor;
        this.onClose = onClose;
        this.clickHandler = clickHandler;
        this.concurrentClickHandlers = concurrentClickHandlers;
        this.response = response;
        this.title = title;
        this.itemText = itemText;
        this.initialItems = new ItemStack[]{inputLeft, inputRight, output};
        this.preventClosing = preventClosing;
        this.interactableSlots = Collections.unmodifiableSet(interactableSlots == null ? new HashSet<>() : interactableSlots);
    }

    /**
     * Opens the inventory and configures anything that needs to be configured.
     */
    public void openInventory() {
        Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);

        if (this.player != null) {
            this.view = MenuType.ANVIL.create(this.player, title);
            this.player.openInventory(view);
            this.inventory = view.getTopInventory();
        } else if (this.holder != null) {
            this.view = MenuType.ANVIL.create(this.holder.player(), title);
            this.holder.player().openInventory(view);
            this.inventory = view.getTopInventory();
        } else {
            throw new UnsupportedOperationException("Both player and holder are null! The menu cannot be opened.");
        }

        for (int i = 0; i < initialItems.length; i++) {
            inventory.setItem(i, initialItems[i]);
        }

        this.open = true;
        OPEN_ANVILS.put(this.inventory, this);
    }

    /**
     * Method to close the inventory.
     */
    public void closed() {
        closed(true);
    }

    /**
     * Closes the inventory, calls {@link #onClose}, and unregisters the listener.
     */
    public void closed(boolean close) {
        if (!this.open) return;

        this.open = false;

        if (close) {
            if (this.player != null) {
                this.player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            } else if (this.holder != null) {
                this.holder.player().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            }
        }

        HandlerList.unregisterAll(this.listener);

        if (this.onClose != null) {
            this.onClose.accept(Snapshot.fromBuilt(this));
        }

        OPEN_ANVILS.remove(this.inventory);
    }

    /**
     * The builder.
     */
    public static class AnvilGUIBuilder {

        /**
         * Set the title for the GUI.
         *
         * @param title The component title for this GUI.
         * @return this builder for chaining.
         */
        public AnvilGUIBuilder title(Component title) {
            this.title = title;
            return this;
        }

        /**
         * Set the title for this GUI.
         *
         * @param title The title to set, as a String. This is later converted to a {@link Component} using {@link StringUtil#color(String)}
         * @return this builder for chaining.
         */
        public AnvilGUIBuilder title(String title) {
            this.title = StringUtil.color(title);
            return this;
        }

        /**
         * The text that appears on the first item in the inventory.
         *
         * @param text The {@link Component} to set the name of the item to.
         * @return this builder for chaining.
         */
        public AnvilGUIBuilder itemText(Component text) {
            this.itemText = text;
            return this;
        }

        /**
         * The text that appears on the first item in the inventory.
         *
         * @param text The String to set the name of the item to.
         * @return this builder for chaining.
         */
        public AnvilGUIBuilder itemText(String text) {
            this.itemText = Component.text(text);
            return this;
        }

        /**
         * What should happen when an item is clicked.
         *
         * @param click The function to execute when clicking.
         * @return this builder for chaining.
         */
        public AnvilGUIBuilder onClick(BiFunction<Integer, Snapshot, List<Response>> click) {
            this.clickHandler = (slot, snapshot) -> CompletableFuture.completedFuture(click.apply(slot, snapshot));
            return this;
        }

        /**
         * What should happen when an item is clicked, but this is executed asynchronously.
         *
         * @param onClick The {@link ClickHandler}.
         * @return this builder for chaining.
         */
        public AnvilGUIBuilder onClickAsync(ClickHandler onClick) {
            this.clickHandler = onClick;
            return this;
        }

        /**
         * Open the Anvil for a {@link MenuHolder}.
         *
         * @param holder The holder to open this anvil for.
         * @return this builder for chaining.
         */
        public AnvilGUI open(final MenuHolder holder) {
            Validate.notNull(holder, "Player cannot be null!");

            if (holder.getCurrentMenu() != null) {
                holder.getCurrentMenu().closeReason(CloseReason.ANVIL);
            }

            configure();

            final AnvilGUI gui = new AnvilGUI(this.plugin, null, holder, this.inputLeft, this.inputRight, this.output,
                    this.preventClosing, this.interactableSlots, this.executor, this.onClose, this.clickHandler, this.concurrentClickHandlers,
                    this.response, this.title, this.itemText);
            gui.openInventory();
            return gui;
        }

        /**
         * Open this Anvil for a {@link Player}.
         *
         * @param player The player to open this anvil for
         * @return this builder for chaining.
         */
        public AnvilGUI open(final Player player) {
            Validate.notNull(player, "Player cannot be null!");

            configure();

            final AnvilGUI gui = new AnvilGUI(this.plugin, player, null, this.inputLeft, this.inputRight, this.output,
                    this.preventClosing, this.interactableSlots, this.executor, this.onClose, this.clickHandler, this.concurrentClickHandlers,
                    this.response, this.title, this.itemText);
            gui.openInventory();
            return gui;
        }

        /**
         * Utility method to configure default values.
         */
        private void configure() {
            Validate.notNull(this.plugin, "Plugin cannot be null!");
            Validate.notNull(this.clickHandler, "Click handler cannot be null!");

            if (this.itemText != null) {
                if (this.inputLeft == null) {
                    this.inputLeft = new ItemStack(Material.PAPER);
                }

                final ItemMeta meta = this.inputLeft.getItemMeta();
                meta.displayName(itemText);
                this.inputLeft.setItemMeta(meta);
            } else {
                if (this.inputLeft == null) {
                    this.inputLeft = new ItemStack(Material.PAPER);

                    final ItemMeta meta = this.inputLeft.getItemMeta();
                    meta.displayName(Component.empty());
                    this.inputLeft.setItemMeta(meta);
                }
            }

            if (this.executor == null) {
                this.executor = (task) -> Bukkit.getScheduler().runTask(this.plugin, task);
            }
        }

    }

}
