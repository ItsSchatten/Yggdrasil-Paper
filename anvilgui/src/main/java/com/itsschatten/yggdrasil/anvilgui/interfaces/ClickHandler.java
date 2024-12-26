package com.itsschatten.yggdrasil.anvilgui.interfaces;

import com.itsschatten.yggdrasil.anvilgui.Snapshot;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * A functional interface whose function is {@link #apply(Object, Object)}.
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface ClickHandler extends BiFunction<Integer, Snapshot, CompletableFuture<List<Response>>> {
}
