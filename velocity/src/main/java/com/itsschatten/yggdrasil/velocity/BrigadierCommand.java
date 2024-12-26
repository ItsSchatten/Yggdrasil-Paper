package com.itsschatten.yggdrasil.velocity;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Wrapper class to make a Brigadier Command.
 *
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public abstract class BrigadierCommand implements Comparable<BrigadierCommand> {

    /**
     * The description of the command.
     */
    private final String description;

    /**
     * Aliases for the command.
     *
     * @apiNote All aliases should be lowercase.
     * @see com.mojang.brigadier.builder.LiteralArgumentBuilder#redirect(CommandNode)
     */
    private final Collection<String> aliases;

    /**
     * The sub-commands for this command.
     *
     * @implNote Sub-commands added to other sub-commands will not be registered into the parent sub-command.
     */
    private final List<BrigadierCommand> subcommands;

    /**
     * The permission for this command.
     * <p>
     * -- SETTER --
     * Set the permission
     * required for this command.
     * <p>
     * You can use {@link #permissionWithPrefix(String)} to quickly add a prefix to your permission.
     * <br>
     * Set to {@code ""} to create the permission before registration.
     * <b>Note:</b> This will not work on a <b>parent</b> command, only on sub-commands.
     * Parent commands will be registered with no permission required.
     * -- GETTER --
     * The permission of the command.
     *
     * @return By default, returns {@code ""}
     */
    @Setter
    @Accessors(fluent = true, makeFinal = true)
    private String permission = "";

    /**
     * Builds the description and aliases for this command.
     *
     * @param description The description for the command.
     * @param aliases     The aliases for the command.
     */
    protected BrigadierCommand(String description, List<String> aliases) {
        this.description = description;
        this.aliases = new ArrayList<>(aliases);
        this.subcommands = new ArrayList<>();
    }

    /**
     * Builds the description and aliases for this command.
     *
     * @param description The description for the command.
     * @param aliases     The aliases for the command.
     */
    protected BrigadierCommand(String description, Collection<String> aliases) {
        this.description = description;
        this.aliases = new ArrayList<>(aliases);
        this.subcommands = new ArrayList<>();
    }

    /**
     * Builds the description and aliases for this command.
     *
     * @param description The description for the command.
     * @param aliases     The aliases for the command.
     */
    protected BrigadierCommand(String description, String... aliases) {
        this.description = description;
        this.aliases = new ArrayList<>(List.of(aliases));
        this.subcommands = new ArrayList<>();
    }

    /**
     * Builds the description and provides no aliases for this command.
     *
     * @param description The description for the command.
     */
    protected BrigadierCommand(String description) {
        this.description = description;
        this.aliases = null;
        this.subcommands = new ArrayList<>();
    }

    /**
     * Builds an empty description and aliases for this command.
     *
     * @param aliases The aliases for the command.
     */
    protected BrigadierCommand(List<String> aliases) {
        this.description = "";
        this.aliases = new ArrayList<>(aliases);
        this.subcommands = new ArrayList<>();
    }

    /**
     * Builds an empty description and aliases for this command.
     *
     * @param aliases The aliases for the command.
     */
    protected BrigadierCommand(Collection<String> aliases) {
        this.description = "";
        this.aliases = new ArrayList<>(aliases);
        this.subcommands = new ArrayList<>();
    }

    /**
     * Builds an empty description and aliases for this command.
     *
     * @param aliases The aliases for the command.
     */
    protected BrigadierCommand(String... aliases) {
        this.description = "";
        this.aliases = new ArrayList<>(List.of(aliases));
        this.subcommands = new ArrayList<>();
    }

    /**
     * Builds a command with no description and no aliases.
     */
    protected BrigadierCommand() {
        this.description = "";
        this.aliases = null;
        this.subcommands = new ArrayList<>();
    }

    @Override
    public int compareTo(@NotNull BrigadierCommand o) {
        return this.command().getLiteral().compareTo(o.command().getLiteral());
    }

    /**
     * Method to return the command's main literal with a provided prefix.
     *
     * @param prefix The prefix for the permission.
     * @return Return's the built permission.
     */
    public final @NotNull String permissionWithPrefix(final @NotNull String prefix) {
        return (prefix.endsWith(".") ? prefix.toLowerCase() : prefix.toLowerCase() + ".") + command().getLiteral().toLowerCase();
    }

    /**
     * Additional requirements to use the command.
     *
     * @return Returns a boolean value, default to {@code true} if not overridden.
     */
    public Predicate<CommandSource> requirements() {
        return (source) -> true;
    }

    /**
     * The requirements to execute the command.
     *
     * @return Returns the full requirements for the command, combining {@link #requirements()} and a permission check.
     */
    @Contract(pure = true)
    protected final @NotNull Predicate<CommandSource> fullRequirements() {
        return (source) -> requirements().test(source) && (permission.isBlank() || source.hasPermission(permission));
    }

    /**
     * Adds a subcommand to be registered for this command.
     * <p>Note: This method does not actually register the command onto the command map, it only adds it as a possible command argument for a leading "parent" command.
     * <br/>
     * PS: Subcommands should not be used for normal arguments such as integers and such, that should be handled in a normal {@link com.mojang.brigadier.builder.ArgumentBuilder#then(CommandNode)} call.
     * <br/>
     * PPS: There is no way to remove a command once it has been registered.</p>
     *
     * @param subcommand The command to "register" as a subcommand.
     */
    public final void addSubCommand(BrigadierCommand subcommand) {
        subcommands.add(subcommand);
    }

    /**
     * Adds a list of subcommands to be registered for this command.
     * <p>Note: This method does not actually register the command onto the command map, it only adds it as a possible command argument for a leading "parent" command.
     * <br/>
     * PS: Subcommands should not be used for normal arguments such as integers and such, that should be handled in a normal {@link com.mojang.brigadier.builder.ArgumentBuilder#then(CommandNode)} call.
     * <br/>
     * PPS: There is no way to remove a command once it has been registered.</p>
     *
     * @param subcommand The command to "register" as a subcommand.
     */
    public final void addSubCommand(Collection<BrigadierCommand> subcommand) {
        subcommands.addAll(subcommand);
    }

    /**
     * Colorized version of {@link #commandDescription()}.
     *
     * @return Returns a colorized version of {@link #commandDescription()}
     */
    public @NotNull Component descriptionComponent() {
        return StringUtil.color(commandDescription());
    }

    /**
     * The command description that can be colorized.
     *
     * @return If unset, defaults to {@link #description()}
     */
    public @NotNull String commandDescription() {
        return description;
    }

    /**
     * Returns a sorted {@link List} of all registered {@link BrigadierCommand sub commands} description {@link String strings}.
     *
     * @return Returns a sorted list, generated from a stream.
     */
    public final List<String> subCommandDescriptions() {
        return subcommands.stream().sorted().map(BrigadierCommand::description).filter(description -> !description.isEmpty()).toList();
    }

    /**
     * Returns a sorted {@link List} of all registered {@link BrigadierCommand sub commands} description {@link Component components}.
     *
     * @return Returns a sorted list, generated from a stream.
     */
    public final List<Component> subcommandDescriptionComponents() {
        return subcommands.stream().sorted().map(BrigadierCommand::descriptionComponent).filter(component -> {
            if (component instanceof TextComponent text) {
                return !text.content().isEmpty();
            }

            return false;
        }).toList();
    }

    /**
     * Call this method to register this command.
     *
     * @param registrar The registrar to register the command to.
     */
    public final void register(@NotNull ProxyServer registrar) {
        final com.velocitypowered.api.command.BrigadierCommand toRegister = new com.velocitypowered.api.command.BrigadierCommand(this.build());
        final CommandMeta.Builder meta = registrar.getCommandManager().metaBuilder(toRegister).plugin(Utils.getInstance());

        if (aliases != null) {
            meta.aliases(this.aliases.toArray(new String[0]));
        }

        registrar.getCommandManager().register(meta.build(), toRegister);
    }

    /**
     * The actual command to register, this uses <a href="https://github.com/Mojang/brigadier">Mojang's Brigadier</a> so the format is the same as that.
     * <p>
     * This method will also "register" all subcommands that are "loaded" for this command.
     * <br/>
     * This method is mainly used internally and may not have a use outside of registration.
     * </p>
     *
     * @see com.mojang.brigadier.builder.LiteralArgumentBuilder
     * @see com.mojang.brigadier.builder.ArgumentBuilder
     * @see com.mojang.brigadier.builder.RequiredArgumentBuilder
     * @see com.mojang.brigadier.builder.LiteralArgumentBuilder
     * @see #command()
     */
    public final LiteralCommandNode<CommandSource> build() {
        final LiteralArgumentBuilder<CommandSource> builder = command();

        // Check if we have subcommands, and then add them if we have any to load.
        if (!subcommands.isEmpty()) {
            subcommands.forEach(subcommand -> {
                // Add a command permission if it doesn't exist.
                if (subcommand.permission().isBlank() && !this.permission().isBlank()) {
                    subcommand.permission(this.permission() + "." + subcommand.command().getLiteral());
                }

                // Add a subcommand to this command.
                builder.then(subcommand.command().requires(subcommand.fullRequirements()));
            });
        }

        return builder.requires(this.fullRequirements()).build();
    }

    /**
     * The command builder to register later, this uses <a href="https://github.com/Mojang/brigadier">Mojang's Brigadier</a> so the format is the same as that.
     *
     * @implNote You should use {@link #requirements()} to add additional requirements instead of passing in a '.requires'.
     * Permission checks will fail otherwise.
     * @see com.mojang.brigadier.builder.LiteralArgumentBuilder
     * @see com.mojang.brigadier.builder.ArgumentBuilder
     * @see com.mojang.brigadier.builder.RequiredArgumentBuilder
     * @see com.mojang.brigadier.builder.LiteralArgumentBuilder
     * @see #build()
     */
    public abstract LiteralArgumentBuilder<CommandSource> command();

    /**
     * Creates a new {@link LiteralArgumentBuilder} of the required name.
     *
     * @param name The literal name.
     * @return a new {@link LiteralArgumentBuilder}.
     */
    public static @NotNull LiteralArgumentBuilder<CommandSource> literalBuilder(final String name) {
        Preconditions.checkNotNull(name, "'name' must not be null");
        Preconditions.checkArgument(name.indexOf(' ') == -1, "'name' may not contain any spaces!");

        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a new {@link com.mojang.brigadier.builder.RequiredArgumentBuilder} of the required name and {@link ArgumentType}.
     *
     * @param name         The argument name.
     * @param argumentType The argument type.
     * @param <T>          The {@link ArgumentType} required type.
     * @return a new {@link RequiredArgumentBuilder}.
     */
    public static <T> @NotNull RequiredArgumentBuilder<CommandSource, T> argumentBuilder(final @NotNull String name, @NotNull final ArgumentType<T> argumentType) {
        Preconditions.checkNotNull(name, "'name' must not be null!");
        Preconditions.checkNotNull(argumentType, "'argumentType' must not be null!");

        return RequiredArgumentBuilder.argument(name, argumentType);
    }
}
