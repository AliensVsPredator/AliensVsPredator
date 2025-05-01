package com.avp.fabric.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public class CreativeModeTabUtil {

    /* package-private */ static void accept(FabricItemGroupEntries entries, Supplier<? extends ItemLike> itemLikeSupplier) {
        accept(entries, itemLikeSupplier.get());
    }

    /* package-private */ static void accept(FabricItemGroupEntries entries, ItemLike itemLike) {
        entries.accept(itemLike);
    }
}
