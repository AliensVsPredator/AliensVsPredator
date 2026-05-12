package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Server → client: full snapshot of the server-authoritative {@code LimbDefinitionRegistry} tier-2 state. Fired on each
 * data-pack reload to every online player, and on player join (delayed by 20 ticks so the connection is fully
 * established).
 * <p>
 * The wire format is a flat list of {@code (entityTypeId, [(limbId, categoryId, fatal), ...])} entries — one per entity
 * type with at least one declared limb. The client repopulates {@code LimbDefinitionRegistry}'s tier-2 with synthetic
 * {@link com.blib.api.common.dismemberment.v1.LimbDefinition}s whose {@code spawnOffsetProvider} is a no-op (clients
 * never call it; the server computes the spawn position before the limb entity is created).
 */
public record S2CLimbDefinitionsSyncPayload(List<EntityTypeLimbs> entries) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("limb_definitions_sync");

    public static final Type<S2CLimbDefinitionsSyncPayload> TYPE = new Type<>(PAYLOAD_ID);

    public record LimbEntry(
        ResourceLocation limbId,
        ResourceLocation categoryId,
        boolean fatal
    ) {

        public static final StreamCodec<LimbEntry> CODEC = RecordStreamCodec.of(
            BLibCodecs.Stream.RESOURCE_LOCATION,
            LimbEntry::limbId,
            BLibCodecs.Stream.RESOURCE_LOCATION,
            LimbEntry::categoryId,
            StreamCodecs.BOOLEAN,
            LimbEntry::fatal,
            LimbEntry::new
        );
    }

    public record EntityTypeLimbs(
        ResourceLocation entityTypeId,
        List<LimbEntry> limbs
    ) {

        public static final StreamCodec<EntityTypeLimbs> CODEC = RecordStreamCodec.of(
            BLibCodecs.Stream.RESOURCE_LOCATION,
            EntityTypeLimbs::entityTypeId,
            LimbEntry.CODEC.asList(),
            EntityTypeLimbs::limbs,
            EntityTypeLimbs::new
        );
    }

    public static final StreamCodec<S2CLimbDefinitionsSyncPayload> CODEC = RecordStreamCodec.of(
        EntityTypeLimbs.CODEC.asList(),
        S2CLimbDefinitionsSyncPayload::entries,
        S2CLimbDefinitionsSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** Build a payload from the current server-side {@link LimbDefinitionRegistry} merged snapshot. */
    public static S2CLimbDefinitionsSyncPayload snapshotFromRegistry() {
        var snapshot = LimbDefinitionRegistry.snapshotAll();
        var entries = new ArrayList<EntityTypeLimbs>(snapshot.size());
        for (var bucket : snapshot.entrySet()) {
            var perEntity = new ArrayList<LimbEntry>(bucket.getValue().size());
            for (var def : bucket.getValue().values()) {
                perEntity.add(new LimbEntry(def.id(), def.category().id(), def.fatal()));
            }
            entries.add(new EntityTypeLimbs(bucket.getKey(), perEntity));
        }
        return new S2CLimbDefinitionsSyncPayload(entries);
    }
}
