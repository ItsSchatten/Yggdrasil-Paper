package com.itsschatten.yggdrasil.anvilgui;

/**
 * Wrapper class to wrap the magic values assigned to an Anvil inventories slot.
 * <br/>
 * See also: <a href="https://github.com/WesJD/AnvilGUI/blob/master/api/src/main/java/net/wesjd/anvilgui/AnvilGUI.java#L834">AnvilGUI.Slot</a>
 *
 * @since 1.0.0
 */
public final class Slot {

    private static final int[] values = new int[]{Slot.INPUT_LEFT, Slot.INPUT_RIGHT, Slot.OUTPUT};

    /**
     * The slot on the far left, where the first input is inserted. An {@link org.bukkit.inventory.ItemStack} is always inserted
     * here to be renamed
     */
    public static final int INPUT_LEFT = 0;

    /**
     * Not used, but in a real anvil you are able to put the second item you want to combine here
     */
    public static final int INPUT_RIGHT = 1;

    /**
     * The output slot, where an item is put when two items are combined from {@link #INPUT_LEFT} and
     * {@link #INPUT_RIGHT} or {@link #INPUT_LEFT} is renamed
     */
    public static final int OUTPUT = 2;

    /**
     * Get all anvil slot values
     *
     * @return The array containing all possible anvil slots
     */
    public static int[] values() {
        return values;
    }
}

