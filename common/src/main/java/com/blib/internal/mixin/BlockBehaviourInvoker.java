package com.blib.internal.mixin;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@ApiStatus.Internal
@Mixin(BlockBehaviour.class)
public interface BlockBehaviourInvoker {

    @Invoker("getSoundType")
    SoundType invokeGetSoundType(BlockState state);
}
