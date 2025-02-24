package com.itsschatten.yggdrasil.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

/**
 * Generic argument to convert a {@link String} in an {@link Enum} value.
 *
 * @param <E> The type for the Enum.
 */
// Fixme: Remove when stable
@SuppressWarnings("UnstableApiUsage")
public final class GenericEnumArgument<E extends Enum<E>> implements CustomArgumentType.Converted<E, String> {

    // The class for the enum, used in a EnumSet to get all values and to call Enum#valueOf.
    final Class<E> enumClass;

    /**
     * Constructs a new argument with the provided enum class.
     *
     * @param enumClass The Enum class to use.
     */
    private GenericEnumArgument(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    /**
     * Method that constructs a new argument with the provided enum class.
     *
     * @param enumClass The enum class to use.
     * @param <T>       The type for the enum.
     * @return Returns a new {@link GenericEnumArgument}
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T extends Enum<T>> @NotNull GenericEnumArgument<T> generic(final Class<T> enumClass) {
        return new GenericEnumArgument<>(enumClass);
    }

    // Lists all values from the provided enum for tab completion.
    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        EnumSet.allOf(enumClass).stream().map(op -> op.name().toLowerCase()).forEach(builder::suggest);
        return builder.buildFuture();
    }

    // Converts the provided nativeType, in this case a String, into the Enum value.
    @Override
    public @NotNull E convert(@NotNull String nativeType) throws CommandSyntaxException {
        try {
            return Enum.valueOf(enumClass, nativeType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(() -> "Failed to find '" + nativeType + "' in " + enumClass.getSimpleName() + ".").create();
        } catch (NullPointerException e) {
            throw new SimpleCommandExceptionType(() -> "Enum class is null while attempting to parse a value for it!").create();
        }
    }

    // The native type to use,
    // because Brigadier doesn't currently allow us
    // to register custom argument types and send them to the client.
    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
