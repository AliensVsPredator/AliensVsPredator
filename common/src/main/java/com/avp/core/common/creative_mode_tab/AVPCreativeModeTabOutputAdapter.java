package com.avp.core.common.creative_mode_tab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AVPCreativeModeTabOutputAdapter implements CreativeModeTab.Output {

    private final CreativeModeTab.Output output;

    public AVPCreativeModeTabOutputAdapter(CreativeModeTab.Output output) {
        this.output = output;
    }

    public void accept(@NotNull Supplier<? extends ItemLike> itemLikeSupplier) {
        output.accept(itemLikeSupplier.get());
    }

    @Override
    public void accept(@NotNull ItemStack itemStack, @NotNull CreativeModeTab.TabVisibility tabVisibility) {
        output.accept(itemStack, tabVisibility);
    }
}
