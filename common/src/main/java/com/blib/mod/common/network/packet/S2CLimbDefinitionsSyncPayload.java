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
import com.blib.api.common.dismemberment.v1.LimbPoseOption;
import com.blib.mod.BLib;

/**
 * Server → client: full snapshot of the server-authoritative {@code LimbDefinitionRegistry} tier-2 state. Fired on each
 * data-pack reload to every online player, and on player join (delayed by 20 ticks so the connection is fully
 * established).
 * <p>
 * The wire format carries resolved entity entries plus resolved template entries. The client repopulates
 * {@code LimbDefinitionRegistry}'s tier-2 with synthetic
 * {@link com.blib.api.common.dismemberment.v1.LimbDefinition}s whose {@code spawnOffsetProvider} is a no-op (clients
 * never call it; the server computes the spawn position before the limb entity is created).
 */
public record S2CLimbDefinitionsSyncPayload(List<EntityTypeLimbs> entries, List<TemplateLimbs> templates) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("limb_definitions_sync");

    public static final Type<S2CLimbDefinitionsSyncPayload> TYPE = new Type<>(PAYLOAD_ID);

    public record PoseEntry(
        String id,
        int weight
    ) {

        public static final StreamCodec<PoseEntry> CODEC = RecordStreamCodec.of(
            StreamCodecs.STRING_UTF8,
            PoseEntry::id,
            StreamCodecs.INT,
            PoseEntry::weight,
            PoseEntry::new
        );

        static PoseEntry fromOption(LimbPoseOption option) {
            return new PoseEntry(option.id(), option.weight());
        }
    }

    public record LimbEntry(
        ResourceLocation limbId,
        ResourceLocation categoryId,
        boolean fatal,
        List<PoseEntry> poses
    ) {

        public static final StreamCodec<LimbEntry> CODEC = RecordStreamCodec.of(
            BLibCodecs.Stream.RESOURCE_LOCATION,
            LimbEntry::limbId,
            BLibCodecs.Stream.RESOURCE_LOCATION,
            LimbEntry::categoryId,
            StreamCodecs.BOOLEAN,
            LimbEntry::fatal,
            PoseEntry.CODEC.asList(),
            LimbEntry::poses,
            LimbEntry::new
        );
    }

    public record EntityTypeLimbs(
        ResourceLocation entityTypeId,
        String parentTemplateId,
        List<LimbEntry> limbs
    ) {

        public static final StreamCodec<EntityTypeLimbs> CODEC = RecordStreamCodec.of(
            BLibCodecs.Stream.RESOURCE_LOCATION,
            EntityTypeLimbs::entityTypeId,
            StreamCodecs.STRING_UTF8,
            EntityTypeLimbs::parentTemplateId,
            LimbEntry.CODEC.asList(),
            EntityTypeLimbs::limbs,
            EntityTypeLimbs::new
        );
    }

    public record TemplateLimbs(
        ResourceLocation templateId,
        String parentTemplateId,
        List<LimbEntry> limbs
    ) {

        public static final StreamCodec<TemplateLimbs> CODEC = RecordStreamCodec.of(
            BLibCodecs.Stream.RESOURCE_LOCATION,
            TemplateLimbs::templateId,
            StreamCodecs.STRING_UTF8,
            TemplateLimbs::parentTemplateId,
            LimbEntry.CODEC.asList(),
            TemplateLimbs::limbs,
            TemplateLimbs::new
        );
    }

    public static final StreamCodec<S2CLimbDefinitionsSyncPayload> CODEC = RecordStreamCodec.of(
        EntityTypeLimbs.CODEC.asList(),
        S2CLimbDefinitionsSyncPayload::entries,
        TemplateLimbs.CODEC.asList(),
        S2CLimbDefinitionsSyncPayload::templates,
        S2CLimbDefinitionsSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** Build a payload from the current server-side {@link LimbDefinitionRegistry} merged snapshot. */
    public static S2CLimbDefinitionsSyncPayload snapshotFromRegistry() {
        var snapshot = LimbDefinitionRegistry.snapshotAll();
        var parents = LimbDefinitionRegistry.snapshotParents();
        var entries = new ArrayList<EntityTypeLimbs>(snapshot.size());
        for (var bucket : snapshot.entrySet()) {
            var perEntity = new ArrayList<LimbEntry>(bucket.getValue().size());
            for (var def : bucket.getValue().values()) {
                var poses = def.poses().stream().map(PoseEntry::fromOption).toList();
                perEntity.add(new LimbEntry(def.id(), def.category().id(), def.fatal(), poses));
            }
            entries.add(new EntityTypeLimbs(bucket.getKey(), parentString(parents.get(bucket.getKey())), perEntity));
        }

        var templateSnapshot = LimbDefinitionRegistry.snapshotTemplates();
        var templateParents = LimbDefinitionRegistry.snapshotTemplateParents();
        var templates = new ArrayList<TemplateLimbs>(templateSnapshot.size());
        for (var bucket : templateSnapshot.entrySet()) {
            var perTemplate = new ArrayList<LimbEntry>(bucket.getValue().size());
            for (var def : bucket.getValue().values()) {
                var poses = def.poses().stream().map(PoseEntry::fromOption).toList();
                perTemplate.add(new LimbEntry(def.id(), def.category().id(), def.fatal(), poses));
            }
            templates.add(new TemplateLimbs(bucket.getKey(), parentString(templateParents.get(bucket.getKey())), perTemplate));
        }
        return new S2CLimbDefinitionsSyncPayload(entries, templates);
    }

    private static String parentString(ResourceLocation parent) {
        return parent == null ? "" : parent.toString();
    }
}
