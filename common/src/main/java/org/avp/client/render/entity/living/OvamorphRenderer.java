package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.AVPResources;
import org.avp.common.entity.living.Ovamorph;
import org.avp.common.util.TimeUtils;

public class OvamorphRenderer extends GeoEntityRenderer<Ovamorph> {

    private static final ResourceLocation TEXTURE_HALLOWEEN = AVPResources.entityTextureLocation("ovamorph_halloween");

    public OvamorphRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("ovamorph", GeoModelType.ENTITY));
        this.withScale(1.75F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Ovamorph entity) {
        return TimeUtils.isHalloween() ? TEXTURE_HALLOWEEN : super.getTextureLocation(entity);
    }
}
