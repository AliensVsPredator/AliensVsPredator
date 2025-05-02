package com.avp.common.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public class CreativeModeTabUtil {

    /* package-private */ static void accept(CreativeModeTab.Output output, Supplier<? extends ItemLike> itemLikeSupplier) {
        accept(output, itemLikeSupplier.get());
    }

    /* package-private */ static void accept(CreativeModeTab.Output output, ItemLike itemLike) {
        output.accept(itemLike);
    }
}
