package org.avp.common.data.entity.living.alien.base_line;

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
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.data.entity.AVPEntitySpeedConstants;
import org.avp.common.data.loot_table.entity.XenomorphLootTable;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.common.game.entity.living.alien.base_line.Ultramorph;
import org.avp.common.game.sound.AVPSoundEventRegistry;
import org.avp.common.registry.entity.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.util.AlienSounds;

public class UltramorphData extends EntityData<Ultramorph> {

    public static final UltramorphData INSTANCE = new UltramorphData();

    @Override
    protected BLHolder<EntityType<Ultramorph>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "ultramorph",
            0x3E474E,
            0x696E76,
            EntityType.Builder.of(Ultramorph::new, MobCategory.MONSTER)
                .sized(0.98F, 1.98F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AttributeSupplierUtil.builder()
                .add(Attributes.ATTACK_DAMAGE, 5.5)
                .add(Attributes.MAX_HEALTH, 230)
                .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.ULTRAMORPH_SPEED)
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
            AVPEntityTypeTags.PRODUCES_RESIN,
            AVPEntityTypeTags.MEDIUM_GUNS_IMMUNE
        );
    }

    @Override
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.of(
            new EntitySoundData(
                AVPSoundEventRegistry.INSTANCE.entityUltramorph.ambient(),
                AlienSounds.createSoundEventSelector(AVPSoundEventRegistry.INSTANCE.entityUltramorph.hurt()),
                AVPSoundEventRegistry.INSTANCE.entityUltramorph.death()
            )
        );
    }

    @Override
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.of(XenomorphLootTable.LOOT_TABLE);
    }

    @Override
    protected Optional<EntitySpawnData<?>> createSpawnData() {
        return Optional.empty();
    }
}
