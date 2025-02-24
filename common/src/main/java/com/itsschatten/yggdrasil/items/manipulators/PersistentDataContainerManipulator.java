package com.itsschatten.yggdrasil.items.manipulators;

import com.itsschatten.yggdrasil.items.MetaManipulator;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A prebuilt {@link MetaManipulator} that can change an Item's {@link PersistentDataContainer} from it's ItemMeta.
 */
public final class PersistentDataContainerManipulator implements MetaManipulator {

    /**
     * The consumer that performs the operations on the data container.
     */
    final Consumer<PersistentDataContainer> operator;

    /**
     * Constructor.
     *
     * @param operator The {@link Consumer} instance.
     */
    private PersistentDataContainerManipulator(Consumer<PersistentDataContainer> operator) {
        this.operator = operator;
    }

    /**
     * Makes a new {@link PersistentDataContainerManipulator} with the provided consumer.
     *
     * @param operator The {@link Consumer} used to manipulate the item's {@link PersistentDataContainer}.
     * @return Returns a new instance of {@code this}.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull PersistentDataContainerManipulator of(Consumer<PersistentDataContainer> operator) {
        return new PersistentDataContainerManipulator(operator);
    }


    @Override
    public void apply(@NotNull ItemMeta itemMeta) {
        operator.accept(itemMeta.getPersistentDataContainer());
    }

}
