package org.avp.common.data.entity.living.alien.beluga_line;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

import org.avp.api.common.data.entity.AttributeSupplierUtil;
import org.avp.api.common.data.entity.EntityData;
import org.avp.api.common.data.entity.EntitySoundData;
import org.avp.api.common.data.entity.EntitySpawnData;
import org.avp.api.common.registry.entity.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.data.entity.AVPEntitySpeedConstants;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.common.game.entity.living.alien.beluga_line.Octohugger;

public class OctohuggerData extends EntityData<Octohugger> {

    public static final OctohuggerData INSTANCE = new OctohuggerData();

    @Override
    protected BLHolder<EntityType<Octohugger>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "octohugger",
            0xC2BCC8,
            0xC09CAE,
            EntityType.Builder.of(Octohugger::new, MobCategory.MONSTER)
                .sized(0.25F, 0.75F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AttributeSupplierUtil.builder()
                .add(Attributes.ATTACK_DAMAGE, 0.5)
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.OCTOHUGGER_SPEED)
                .build()
        );
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.ACID_BLEEDERS,
            AVPEntityTypeTags.ALIENS,
            AVPEntityTypeTags.HIVE_ALIENS,
            AVPEntityTypeTags.MONSTERS,
            AVPEntityTypeTags.PARASITES
        );
    }

    @Override
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.empty();
    }

    @Override
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.empty();
    }

    @Override
    protected Optional<EntitySpawnData<?>> createSpawnData() {
        return Optional.empty();
    }
}
