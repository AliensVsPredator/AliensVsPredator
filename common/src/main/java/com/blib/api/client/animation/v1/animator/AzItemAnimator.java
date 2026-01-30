package com.blib.api.client.animation.v1.animator;

import net.minecraft.world.item.ItemStack;

import java.util.UUID;

import com.blib.internal.client.render.util.RenderUtil;
import com.blib.internal.common.molang.MolangParser;
import com.blib.internal.common.molang.MolangQueries;

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
            () -> RenderUtil.booleanToFloat(!animatable.isEnchanted())
        );
    }
}
