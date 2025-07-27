package com.avp.common.model;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentType;

import java.util.Optional;

public interface DataComponentPatchAccessor {

    Reference2ObjectMap<DataComponentType<?>, Optional<?>> avp$getMap();
}
