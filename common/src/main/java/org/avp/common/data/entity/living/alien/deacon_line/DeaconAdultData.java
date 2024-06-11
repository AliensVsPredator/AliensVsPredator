package org.avp.common.data.entity.living.alien.deacon_line;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootTable;
import org.avp.api.data.entity.AttributeSupplierUtil;
import org.avp.api.data.entity.EntityData;
import org.avp.api.data.entity.EntitySoundData;
import org.avp.api.data.entity.EntitySpawnData;
import org.avp.api.registry.entity.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.api.registry.holder.BLHolder;
import org.avp.common.data.entity.AVPEntitySpeedConstants;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.common.game.entity.living.alien.deacon_line.DeaconAdult;

import java.util.List;
import java.util.Optional;

public class DeaconAdultData extends EntityData<DeaconAdult> {

    public static final DeaconAdultData INSTANCE = new DeaconAdultData();

    @Override
    protected BLHolder<EntityType<DeaconAdult>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "deacon_adult",
            0x8896A5,
            0x495256,
            EntityType.Builder.of(DeaconAdult::new, MobCategory.MONSTER)
                .sized(0.98F, 2.48F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AttributeSupplierUtil.builder()
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.MAX_HEALTH, 120)
                .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DEACON_ADULT_SPEED)
                .build()
        );
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.ACID_BLEEDERS,
            AVPEntityTypeTags.ALIENS,
            AVPEntityTypeTags.MONSTERS,
            AVPEntityTypeTags.SMALL_GUNS_IMMUNE
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
