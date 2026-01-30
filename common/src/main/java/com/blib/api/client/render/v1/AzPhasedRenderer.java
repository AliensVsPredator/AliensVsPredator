package com.blib.api.client.render.v1;

public interface AzPhasedRenderer<K, T> {

    void preRender(AzRendererPipelineContext<K, T> context, boolean isReRender);

    void postRender(AzRendererPipelineContext<K, T> context, boolean isReRender);
}
