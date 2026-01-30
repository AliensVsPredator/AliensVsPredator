package com.blib.api.client.render.v1.layer;

import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.internal.client.model.AzBone;

public interface AzRenderLayer<K, T> {

    void preRender(AzRendererPipelineContext<K, T> context);

    void render(AzRendererPipelineContext<K, T> context);

    void renderForBone(AzRendererPipelineContext<K, T> context, AzBone bone);
}
