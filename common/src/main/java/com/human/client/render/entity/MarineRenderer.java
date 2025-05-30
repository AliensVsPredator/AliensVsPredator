package com.human.client.render.entity;

import com.human.client.animation.entity.MarineAnimator;
import com.human.client.render.layer.human.HumanOutfitLayer;
import com.human.common.gameplay.entity.living.human.marine.Marine;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MarineRenderer extends AbstractHumanRenderer<Marine> {

    public MarineRenderer(EntityRendererProvider.Context context) {
        super(
            AbstractHumanRenderer.<Marine>createConfig()
                .setAnimatorProvider(MarineAnimator::new)
                .addRenderLayer(new HumanOutfitLayer<>()),
            context
        );
    }
}
