package com.blib.azurelib.common.animation.impl;

import net.minecraft.world.item.ItemStack;

import java.util.UUID;

import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.animation.AzAnimatorConfig;
import com.blib.azurelib.common.util.client.RenderUtils;
import com.blib.azurelib.core.molang.MolangParser;
import com.blib.azurelib.core.molang.MolangQueries;

public abstract class AzItemAnimator extends AzAnimator<UUID, ItemStack> {

    protected AzItemAnimator() {
        super();
    }

    protected AzItemAnimator(AzAnimatorConfig config) {
        super(config);
    }

    @Override
    protected void applyMolangQueries(ItemStack animatable, double animTime, float partialTicks) {
        super.applyMolangQueries(animatable, animTime, partialTicks);

        var parser = MolangParser.INSTANCE;

        parser.setMemoizedValue(
            MolangQueries.ITEM_CURRENT_DURABILITY,
            () -> animatable.getDamageValue() / (float) animatable.getMaxDamage()
        );
        parser.setMemoizedValue(
            MolangQueries.ITEM_IS_ENCHANTED,
            () -> RenderUtils.booleanToFloat(!animatable.isEnchanted())
        );
    }
}
