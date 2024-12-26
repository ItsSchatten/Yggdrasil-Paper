package com.itsschatten.yggdrasil.items.manipulators;

import com.itsschatten.yggdrasil.items.ItemManipulator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import lombok.Builder;
import lombok.Singular;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Builder
public record ModelDataManipulator(List<Float> floats, @Singular List<Boolean> flags,
                                   @Singular List<String> strings,
                                   @Singular List<Color> colors) implements ItemManipulator {

    @Override
    public void apply(@NotNull ItemStack itemStack) {
        final CustomModelData.Builder builder = CustomModelData.customModelData();

        if (floats != null && !floats.isEmpty()) {
            builder.addFloats(floats);
        }

        if (flags != null && !flags.isEmpty()) {
            builder.addFlags(flags);
        }

        if (strings != null && !strings.isEmpty()) {
            builder.addStrings(strings);
        }

        if (colors != null && !colors.isEmpty()) {
            builder.addColors(colors);
        }

        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, builder.build());
    }

    static class ModelDataManipulatorBuilder {

        public ModelDataManipulatorBuilder addFloat(Float floatValue) {
            if (this.floats == null) this.floats = new java.util.ArrayList<>();
            this.floats.add(floatValue);
            return this;
        }

        public ModelDataManipulatorBuilder clearFloats() {
            if (this.floats != null) this.floats.clear();
            return this;
        }

    }

}
