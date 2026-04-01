package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.internal.common.faction.BLibFactionData;

public class Faction<T extends FactionData> {

    private final ResourceLocation id;

    private final ResourceLocation typeId;

    private final FactionMembership membership;

    private final BLibFactionData internalData;

    @ApiStatus.Internal
    public Faction(
        ResourceLocation id,
        ResourceLocation typeId,
        FactionMembership membership,
        BLibFactionData internalData
    ) {
        this.id = id;
        this.typeId = typeId;
        this.membership = membership;
        this.internalData = internalData;
    }

    public ResourceLocation id() {
        return id;
    }

    public ResourceLocation typeId() {
        return typeId;
    }

    public String name() {
        return internalData.name();
    }

    public void setName(String name) {
        internalData.setName(name);
    }

    public int color() {
        return internalData.color();
    }

    public void setColor(int color) {
        internalData.setColor(color);
    }

    public ClaimVisibility claimVisibility() {
        return internalData.claimVisibility();
    }

    public void setClaimVisibility(ClaimVisibility claimVisibility) {
        internalData.setClaimVisibility(claimVisibility);
    }

    public FactionMembership membership() {
        return membership;
    }

    @SuppressWarnings("unchecked")
    public @Nullable T data() {
        return (T) internalData.modData();
    }

    public boolean isType(TagKey<FactionDataType<?>> tag) {
        var holder = BLibBuiltInRegistries.FACTION_DATA_TYPES.getHolder(typeId);

        return holder.isPresent() && holder.get().is(tag);
    }

    @ApiStatus.Internal
    public BLibFactionData internalData() {
        return internalData;
    }
}
