package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.registry.v1.BLibBuiltInRegistries;

public class Faction<T extends FactionData> {

    private final ResourceLocation id;

    private final ResourceLocation typeId;

    private final FactionRelationships relationships;

    private @Nullable T data;

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public Faction(ResourceLocation id, ResourceLocation typeId, FactionRelationships relationships, @Nullable FactionData data) {
        this.id = id;
        this.typeId = typeId;
        this.relationships = relationships;
        this.data = (T) data;
    }

    public ResourceLocation id() {
        return id;
    }

    public ResourceLocation typeId() {
        return typeId;
    }

    public FactionRelationships relationships() {
        return relationships;
    }

    public @Nullable T data() {
        return data;
    }

    public boolean isType(TagKey<FactionDataType<?>> tag) {
        var holder = BLibBuiltInRegistries.FACTION_DATA_TYPES.getHolder(typeId);

        return holder.isPresent() && holder.get().is(tag);
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public void setData(FactionData data) {
        this.data = (T) data;
    }
}
