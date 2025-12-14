package com.blib.internal.mixin;

import com.blib.common.gameplay.model.HolderExtension;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Holder.class)
public interface MixinHolder<T> extends HolderExtension<T> {}
