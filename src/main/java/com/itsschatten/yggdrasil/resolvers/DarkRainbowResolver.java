package com.itsschatten.yggdrasil.resolvers;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
import net.kyori.adventure.text.minimessage.internal.parser.node.ValueNode;
import net.kyori.adventure.text.minimessage.tag.Inserting;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tree.Node;
import net.kyori.adventure.util.ShadyPines;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * All implementations are yoinked from {@link net.kyori.adventure.text.minimessage.tag.standard.AbstractColorChangingTag}.
 *
 * <p>This tag is an alternate implementation of the default RainbowTag implemented into MiniMessage,
 * the differences is the colors are darker comparatively to the default tag, this will still supply the same Gradiant style
 * as the Rainbow tag.</p>
 *
 * @since 2.0.0
 */
public class DarkRainbowResolver implements Modifying, Examinable {
    // Character to designate the reverse of this rainbow.
    private static final String REVERSE = "!";
    // The tag name.
    private static final String DARK_RAINBOW = "dark_rainbow";

    // The colors.
    private TextColor[] RAINBOW_COLORS = new TextColor[]{
            TextColor.fromHexString("#ff0000"),
            TextColor.fromHexString("#ffa500"),
            TextColor.fromHexString("#ffff00"),
            TextColor.fromHexString("#009000"),
            TextColor.fromHexString("#0000ff"),
            TextColor.fromHexString("#4b0082"),
            TextColor.fromHexString("#ee82ee"),
    };

    private static final ComponentFlattener LENGTH_CALCULATOR = ComponentFlattener.builder()
            .mapper(TextComponent.class, TextComponent::content)
            .unknownMapper(x -> "_") // every unknown component gets a single color
            .build();

    // Resolver instance.
    public static final TagResolver RESOLVER = TagResolver.resolver(DARK_RAINBOW, DarkRainbowResolver::create);

    // Have we visited?
    private boolean visited;

    // The size.
    @Getter
    @Accessors(fluent = true)
    private int size = 0;

    private int disableApplyingColorDepth = -1;

    private int index = 0;
    private int colorIndex = 0;

    private float factorStep = 0;
    private float phase;
    private final boolean reversed;

    @Override
    public void visit(@NotNull Node current, int depth) {
        if (this.visited) {
            throw new IllegalStateException("Color changing tag instances cannot be reused, return a new one for each resolve.");
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

    @Override
    public void postVisit() {
        this.visited = true;
        this.init();
    }

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
        if (current instanceof final TextComponent textComponent && ((TextComponent) current).content().length() > 0) {
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

    @Contract("_, _ -> new")
    static @NotNull Tag create(final @NotNull ArgumentQueue args, final Context ctx) {
        boolean reversed = false;
        float phase = 0;

        if (args.hasNext()) {
            if (args.hasNext()) {
                String value = args.pop().value();
                if (value.startsWith(REVERSE)) {
                    reversed = true;
                    value = value.substring(REVERSE.length());
                }
                if (value.length() > 0) {
                    try {
                        phase = Integer.parseInt(value);
                    } catch (final NumberFormatException ex) {
                        throw ctx.newException("Expected phase, got " + value);
                    }
                }
            }
        }

        return new DarkRainbowResolver(reversed, phase);
    }

    private DarkRainbowResolver(final boolean reversed, final float phase) {
        this.reversed = reversed;
        this.phase = phase;

        if (reversed) {
            final List<TextColor> reversedColors = new ArrayList<>(List.of(RAINBOW_COLORS));
            Collections.reverse(reversedColors);
            this.RAINBOW_COLORS = reversedColors.toArray(new TextColor[0]);
        }

    }

    protected void init() {
        int sectorLength = this.size() / (RAINBOW_COLORS.length - 1);
        if (sectorLength < 1) {
            sectorLength = 1;
        }
        this.factorStep = 1.0f / (sectorLength + this.index);
        this.phase = this.phase * sectorLength;
        this.index = 0;
    }

    protected void advanceColor() {
        // color switch needed?
        this.index++;
        if (this.factorStep * this.index > 1) {
            this.colorIndex++;
            this.index = 0;
        }
    }

    protected TextColor color() {
        float factor = this.factorStep * (this.index + this.phase);
        // loop around if needed
        if (factor > 1) {
            factor = 1 - (factor - 1);
        }

        if (this.reversed && this.RAINBOW_COLORS.length % 2 != 0) {
            // flip the gradient segment to allow looping phase -1 through 1
            return TextColor.lerp(factor, this.RAINBOW_COLORS[this.colorIndex + 1], this.RAINBOW_COLORS[this.colorIndex]);
        } else {
            return TextColor.lerp(factor, this.RAINBOW_COLORS[this.colorIndex], this.RAINBOW_COLORS[this.colorIndex + 1]);
        }
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("phase", this.phase),
                ExaminableProperty.of("colors", this.RAINBOW_COLORS)
        );
    }

    @Override
    public boolean equals(final @Nullable Object other) {
        if (this == other) return true;
        if (other == null || this.getClass() != other.getClass()) return false;
        final DarkRainbowResolver that = (DarkRainbowResolver) other;
        return this.index == that.index
                && this.colorIndex == that.colorIndex
                && ShadyPines.equals(that.factorStep, this.factorStep)
                && this.phase == that.phase && Arrays.equals(this.RAINBOW_COLORS, that.RAINBOW_COLORS);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(this.index, this.colorIndex, this.factorStep, this.phase);
        result = 31 * result + Arrays.hashCode(this.RAINBOW_COLORS);
        return result;
    }

}
