package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.ChestbursterRunnerModel;
import org.avp.common.entity.living.ChestbursterRunner;

public class ChestbursterRunnerRenderer extends GeoEntityRenderer<ChestbursterRunner> {

    public ChestbursterRunnerRenderer(EntityRendererProvider.Context context) {
        super(context, new ChestbursterRunnerModel());
    }
}
