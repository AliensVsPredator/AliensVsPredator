package com.blib.internal.client.posteffect;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.client.mod.v1.BLibClientMod;
import com.blib.api.client.shader.v1.BLibPostEffect;
import com.blib.api.client.shader.v1.BLibPostEffectSpec;

@ApiStatus.Internal
public final class BLibPostEffectImpl implements BLibPostEffect {

    private final BLibClientMod mod;

    private final BLibPostEffectSpec spec;

    private @Nullable ShaderInstance shaderInstance;

    BLibPostEffectImpl(BLibClientMod mod, BLibPostEffectSpec spec) {
        this.mod = mod;
        this.spec = spec;
    }

    @Override
    public ResourceLocation id() {
        return spec.id();
    }

    @Override
    public boolean isActive() {
        return spec.enabledWhen().getAsBoolean();
    }

    @Override
    public BLibPostEffectSpec spec() {
        return spec;
    }

    public BLibClientMod mod() {
        return mod;
    }

    public @Nullable ShaderInstance shaderInstance() {
        return shaderInstance;
    }

    public void setShaderInstance(@Nullable ShaderInstance instance) {
        this.shaderInstance = instance;
    }
}
