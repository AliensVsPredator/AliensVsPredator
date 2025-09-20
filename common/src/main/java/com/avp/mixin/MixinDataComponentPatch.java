package com.avp.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

import com.avp.common.model.DataComponentPatchAccessor;

@Mixin(DataComponentPatch.class)
public interface MixinDataComponentPatch extends DataComponentPatchAccessor {

    @Override
    @Accessor("map")
    Reference2ObjectMap<DataComponentType<?>, Optional<?>> avp$getMap();

    @Invoker("<init>")
    static DataComponentPatch avp$construct(Reference2ObjectMap<DataComponentType<?>, Optional<?>> map) {
        throw new UnsupportedOperationException("Invoker not transformed.");
    }
}
