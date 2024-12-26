package com.itsschatten.yggdrasil.items;

/**
 * A functional interface executed with {@link #apply(T)}.
 * <br>
 * Manipulators are called after all other meta options have been updated and set, it is recommended to use {@link ItemOptions} and the methods within {@link ItemCreator}.
 */
@FunctionalInterface
public interface Manipulator<T> {

    /**
     * Applies updates to the provided type.
     *
     * @param t The value to mutate.
     */
    void apply(T t);

}
