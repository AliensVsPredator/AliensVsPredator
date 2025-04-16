package com.avp.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.avp.client.animation.entity.MarineAnimator;
import com.avp.client.render.layer.human.HumanOutfitLayer;
import com.avp.common.entity.living.human.marine.Marine;

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
