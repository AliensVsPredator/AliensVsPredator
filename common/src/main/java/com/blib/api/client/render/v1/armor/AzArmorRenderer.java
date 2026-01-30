package com.blib.api.client.render.v1.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.blib.api.client.animation.v1.animator.AzItemAnimator;
import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.api.client.render.v1.AzRendererConfig;
import com.blib.api.client.render.v1.armor.pipeline.AzArmorRendererPipeline;
import com.blib.internal.client.render.AzProvider;
import com.blib.mod.common.registry.init.BLibDataComponents;

public class AzArmorRenderer {

    private Entity entity;

    private final AzProvider<UUID, ItemStack> provider;

    private final AzArmorRendererPipeline rendererPipeline;

    @Nullable
    private AzItemAnimator reusedAzItemAnimator;

    public AzArmorRenderer(AzArmorRendererConfig config) {
        this.provider = new AzProvider<>(
            config::createAnimator,
            config::modelLocation,
            animator -> {
                if (animator.get(BLibDataComponents.AZ_ID.get()) != null) {
                    return UUID.randomUUID();
                }
                return animator.get(BLibDataComponents.AZ_ID.get());
            }
        );
        this.rendererPipeline = createPipeline(config);
    }

    protected AzArmorRendererPipeline createPipeline(AzRendererConfig config) {
        return new AzArmorRendererPipeline(config, this);
    }

    public void prepForRender(
        @Nullable Entity entity,
        ItemStack stack,
        @Nullable EquipmentSlot slot,
        @Nullable HumanoidModel<?> baseModel
    ) {
        if (entity == null || slot == null || baseModel == null) {
            return;
        }

        this.entity = entity;

        rendererPipeline.context().prepare(entity, stack, slot, baseModel);

        var model = provider.provideBakedModel(entity, stack);
        prepareAnimator(stack, model);
    }

    private void prepareAnimator(ItemStack stack, AzBakedModel model) {
        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzItemAnimator = (AzItemAnimator) provider.provideAnimator(entity, stack);
    }

    public @Nullable AzItemAnimator animator() {
        return reusedAzItemAnimator;
    }

    public AzProvider<UUID, ItemStack> provider() {
        return provider;
    }

    public AzArmorRendererPipeline rendererPipeline() {
        return rendererPipeline;
    }
}
