package com.blib.internal.mixin;

import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;

import com.blib.api.common.registry.v1.HolderExtension;

@Mixin(Holder.class)
public interface MixinHolder<T> extends HolderExtension<T> {}
