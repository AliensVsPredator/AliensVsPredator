package com.blib.mod.client.render.entity;

import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import com.blib.mod.common.entity.TestGOAPMob;

/**
 * Thin {@link MobRenderer} for {@link TestGOAPMob} that reuses vanilla's cow model + texture. The vanilla
 * {@code CowRenderer} class is hard-typed to {@code Cow} which means we can't register it directly against an
 * {@code EntityType<TestGOAPMob>} without an unchecked cast — this subclass with the generic {@code CowModel<T>} keeps
 * the registration type-safe.
 */
@ApiStatus.Internal
public final class TestGOAPMobRenderer extends MobRenderer<TestGOAPMob, CowModel<TestGOAPMob>> {

    private static final ResourceLocation COW_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/cow/cow.png");

    private static final float SHADOW_RADIUS = 0.7F;

    public TestGOAPMobRenderer(EntityRendererProvider.Context context) {
        super(context, new CowModel<>(context.bakeLayer(ModelLayers.COW)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(TestGOAPMob entity) {
        return COW_TEXTURE;
    }
}
