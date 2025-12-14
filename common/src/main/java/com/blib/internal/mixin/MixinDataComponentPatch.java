package com.blib.internal.mixin;

import com.blib.common.gameplay.model.DataComponentPatchAccessor;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(DataComponentPatch.class)
public interface MixinDataComponentPatch extends DataComponentPatchAccessor {

    @Override
    @Accessor("map")
    Reference2ObjectMap<DataComponentType<?>, Optional<?>> blib$getMap();

    @Invoker("<init>")
    static DataComponentPatch blib$construct(Reference2ObjectMap<DataComponentType<?>, Optional<?>> map) {
        throw new UnsupportedOperationException("Invoker not transformed.");
    }
}
