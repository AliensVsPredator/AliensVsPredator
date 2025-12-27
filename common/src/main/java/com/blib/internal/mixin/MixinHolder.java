package com.blib.internal.mixin;

import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;

import com.blib.common.gameplay.model.HolderExtension;

@Mixin(Holder.class)
public interface MixinHolder<T> extends HolderExtension<T> {}
