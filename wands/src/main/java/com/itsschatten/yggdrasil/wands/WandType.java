package com.itsschatten.yggdrasil.wands;

import java.util.UUID;

/**
 * The type of wand.
 */
public enum WandType {

    /**
     * Allows the ability to immediately execute code after the selection is completed.
     * <p><b>Once code execution is complete, the selection is cleared.</b></p>
     * <p>This is the default wand type.</p>
     *
     * @implNote Once code execution is completed, the selection for the player will be cleared.
     * @see WandUtils#clearSelection(UUID)
     * @see WandUtils#getSelectionLocations(UUID)
     */
    SINGLE_SELECTION,

    /**
     * Allows the wand to be in a "continuous" selection state.
     * <p>Continuous selection wands allow the ability to obtain the selection area, while {@link #SINGLE_SELECTION} do not.</p>
     *
     * @implNote This wand's selection is continuous, it will not be cleared unless told to do so.
     * @see WandUtils#clearSelection(UUID)
     * @see WandUtils#getSelectionLocations(UUID)
     */
    CONTINUOUS_SELECTION

}
