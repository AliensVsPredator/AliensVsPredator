package com.avp.fabric.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.avp.fabric.client.animation.entity.MarineAnimator;
import com.avp.fabric.client.render.layer.human.HumanOutfitLayer;
import com.avp.fabric.common.entity.living.human.marine.Marine;

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
