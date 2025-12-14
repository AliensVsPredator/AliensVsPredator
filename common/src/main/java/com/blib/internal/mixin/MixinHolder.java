package com.blib.internal.mixin;

import com.blib.common.gameplay.model.HolderExtension;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;

@ApiStatus.Internal
@Mixin(Holder.class)
public interface MixinHolder<T> extends HolderExtension<T> {}
