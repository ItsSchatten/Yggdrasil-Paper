package com.itsschatten.yggdrasil.resolvers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
import net.kyori.adventure.text.minimessage.internal.parser.node.ValueNode;
import net.kyori.adventure.text.minimessage.tag.Inserting;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tree.Node;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

/**
 * Used to alternate colors in a string.
 * <p>
 * Accepted as {@code <alt:color:color>} or {@code <alternate:color:color>}.
 * </p>
 *
 * <p>This is based upon the {@link net.kyori.adventure.text.minimessage.tag.standard.GradientTag}, with some slight modifications.</p>
 * @since 2.0.0
 */
@ApiStatus.Internal
public final class AlternateResolver implements Modifying, Examinable {

    // Hex starting character.
    private static final char HEX = '#';

    // Aliases for some colors.
    private static final Map<String, TextColor> COLOR_ALIASES = new HashMap<>();

    static {
        COLOR_ALIASES.put("dark_grey", NamedTextColor.DARK_GRAY);
        COLOR_ALIASES.put("grey", NamedTextColor.GRAY);
    }

    // A 'flattener' to convert a component tree to a linear string for display.
    private static final ComponentFlattener LENGTH_CALCULATOR = ComponentFlattener.builder()
            .mapper(TextComponent.class, TextComponent::content)
            .unknownMapper(x -> "_") // every unknown component gets a single color
            .build();

    // Have we visited?
    private boolean visited;
    // The size; from the codePointCount.
    private int size = 0;

    private int disableApplyingColorDepth = -1;

    // Actual resolver instance.
    public static final TagResolver RESOLVER = TagResolver.resolver(new HashSet<>(Set.of("alt", "alternate")), AlternateResolver::create);

    // The index in the colors.
    private int index = 0;

    // Our colors.
    private final TextColor[] colors;

    /**
     * Constructs this resolver.
     *
     * @param colors The {@link TextColor}s to use.
     */
    public AlternateResolver(final @NotNull List<TextColor> colors) {
        if (colors.isEmpty()) {
            this.colors = new TextColor[]{TextColor.color(0xffffff), TextColor.color(0x000000)};
        } else {
            this.colors = colors.toArray(new TextColor[0]);
        }
    }

    /**
     * Initializes this resolver.
     */
    private void init() {
        this.index = 0;
    }

    /**
     * Creates the tag.
     *
     * @param args    The arguments for this tag.
     * @param context The context.
     * @return Returns a {@link Tag} instance.
     */
    @Contract("_, _ -> new")
    private static @NotNull Tag create(@NotNull ArgumentQueue args, Context context) {
        double phase;
        final List<TextColor> textColors;
        if (args.hasNext()) {
            textColors = new ArrayList<>();
            while (args.hasNext()) {
                final Argument arg = args.pop();
                // last argument? maybe this is the phase?
                if (!args.hasNext()) {
                    final OptionalDouble possiblePhase = arg.asDouble();
                    if (possiblePhase.isPresent()) {
                        phase = possiblePhase.getAsDouble();
                        if (phase < -1d || phase > 1d) {
                            throw context.newException(String.format("Gradient phase is out of range (%s). Must be in the range [-1.0, 1.0] (inclusive).", phase), args);
                        }
                        break;
                    }
                }

                final TextColor parsedColor = resolveColor(arg.value(), context);
                textColors.add(parsedColor);
            }

            if (textColors.size() == 1) {
                throw context.newException("Invalid gradient, not enough colors. Gradients must have at least two colors.", args);
            }
        } else {
            textColors = Collections.emptyList();
        }

        return new AlternateResolver(textColors);
    }

    /**
     * Resolves the Text colors.
     *
     * @param colorName The name of the color, or the hex.
     * @param ctx       Context used for exception logging.
     * @return Returns the {@link TextColor}.
     * @throws ParsingException Thrown if unable to parse the color.
     */
    static @NotNull TextColor resolveColor(final @NotNull String colorName, final @NotNull Context ctx) throws ParsingException {
        final TextColor color;
        if (COLOR_ALIASES.containsKey(colorName)) {
            color = COLOR_ALIASES.get(colorName);
        } else if (colorName.charAt(0) == HEX) {
            color = TextColor.fromHexString(colorName);
        } else {
            color = NamedTextColor.NAMES.value(colorName);
        }

        if (color == null) {
            throw ctx.newException(String.format("Unable to parse a color from '%s'. Please use named colours or hex (#RRGGBB) colors.", colorName));
        }
        return color;
    }

    /**
     * @return Returns a stream of examinable properties.
     */
    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("colors", this.colors)
        );
    }

    /**
     * Advances the color index forward.
     */
    protected void advanceColor() {
        this.index++;
    }

    /**
     * @return The size; from code point count.
     * @see String#codePointCount(int, int)
     */
    protected final int size() {
        return this.size;
    }

    /**
     * {@inheritDoc}
     *
     * @param current the current element in the subtree
     * @param depth   depth in the tree this node is at
     */
    @Override
    public final void visit(final @NotNull Node current, final int depth) {
        if (this.visited) {
            throw new IllegalStateException("Color changing tag instances cannot be re-used, return a new one for each resolve");
        }

        if (current instanceof ValueNode) {
            final String value = ((ValueNode) current).value();
            this.size += value.codePointCount(0, value.length());
        } else if (current instanceof TagNode) {
            final TagNode tag = (TagNode) current;
            if (tag.tag() instanceof Inserting) {
                // ComponentTransformation.apply() returns the value of the component placeholder
                LENGTH_CALCULATOR.flatten(((Inserting) tag.tag()).value(), s -> this.size += s.codePointCount(0, s.length()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void postVisit() {
        // init
        this.visited = true;
        this.init();
    }

    /**
     * {@inheritDoc}
     *
     * @param current the current component
     * @param depth   the depth of the tree the current component is at
     * @return
     */
    @Override
    public Component apply(@NotNull Component current, int depth) {
        if ((this.disableApplyingColorDepth != -1 && depth > this.disableApplyingColorDepth) || current.style().color() != null) {
            if (this.disableApplyingColorDepth == -1 || depth < this.disableApplyingColorDepth) {
                this.disableApplyingColorDepth = depth;
            }
            // This component has its own color applied, which overrides ours
            // We still want to keep track of where we are though if this is text
            if (current instanceof TextComponent) {
                final String content = ((TextComponent) current).content();
                final int len = content.codePointCount(0, content.length());
                for (int i = 0; i < len; i++) {
                    // increment our color index
                    this.advanceColor();
                }
            }
            return current.children(Collections.emptyList());
        }

        this.disableApplyingColorDepth = -1;
        if (current instanceof final TextComponent textComponent && !((TextComponent) current).content().isEmpty()) {
            final String content = textComponent.content();

            final TextComponent.Builder parent = Component.text();

            // apply
            final int[] holder = new int[1];
            for (final PrimitiveIterator.OfInt it = content.codePoints().iterator(); it.hasNext(); ) {
                holder[0] = it.nextInt();
                final Component comp = Component.text(new String(holder, 0, 1), current.style().color(this.color()));
                this.advanceColor();
                parent.append(comp);
            }

            return parent.build();
        } else if (!(current instanceof TextComponent)) {
            final Component ret = current.children(Collections.emptyList()).colorIfAbsent(this.color());
            this.advanceColor();
            return ret;
        }

        return Component.empty().mergeStyle(current);
    }

    /**
     * @return Returns the next color based on the position.
     */
    protected TextColor color() {
        // from [0, this.colors.length - 1], select the position in the alternation
        // we will wrap around to preserve an even cycle as would be seen with non-zero phases
        final double position = this.index;
        final int lowUnClamped = (int) Math.floor(position);
        final int low = lowUnClamped % this.colors.length;

        return this.colors[low];
    }

}
