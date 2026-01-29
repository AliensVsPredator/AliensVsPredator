package com.blib.azurelib.common.render.layer;

import com.blib.azurelib.common.model.AzBone;
import com.blib.azurelib.common.render.AzRendererPipelineContext;

public interface AzRenderLayer<K, T> {

    void preRender(AzRendererPipelineContext<K, T> context);

    void render(AzRendererPipelineContext<K, T> context);

    void renderForBone(AzRendererPipelineContext<K, T> context, AzBone bone);
}
