package com.blib.api.common.data_component.v1;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;

import java.util.Optional;

import com.blib.internal.mixin.MixinDataComponentPatch_Accessor;

public interface DataComponentPatchAccessor {

    static DataComponentPatch blib$construct(Reference2ObjectMap<DataComponentType<?>, Optional<?>> map) {
        return MixinDataComponentPatch_Accessor.blib$construct(map);
    }

    Reference2ObjectMap<DataComponentType<?>, Optional<?>> blib$getMap();
}
