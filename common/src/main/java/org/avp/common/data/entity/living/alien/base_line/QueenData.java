package org.avp.common.data.entity.living.alien.base_line;

import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootTable;
import org.avp.api.data.entity.AttributeSupplierUtil;
import org.avp.api.data.entity.EntityData;
import org.avp.api.data.entity.EntitySoundData;
import org.avp.api.data.entity.EntitySpawnData;
import org.avp.api.registry.entity.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.api.registry.holder.BLHolder;
import org.avp.common.config.AVPConfig;
import org.avp.common.data.entity.AVPEntitySpeedConstants;
import org.avp.common.data.loot_table.entity.QueenLootTable;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.common.game.entity.living.alien.base_line.Queen;
import org.avp.common.game.sound.AVPSoundEventRegistry;
import org.avp.common.util.AlienSounds;

import java.util.List;
import java.util.Optional;

public class QueenData extends EntityData<Queen> {

    public static final QueenData INSTANCE = new QueenData();

    @Override
    protected BLHolder<EntityType<Queen>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "queen",
            0x010202,
            0x363534,
            EntityType.Builder.of(Queen::new, MobCategory.MONSTER)
                .sized(1.98F, 3.98F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(
            AttributeSupplierUtil.builder()
                .add(Attributes.ATTACK_DAMAGE, 24)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.MAX_HEALTH, 300)
                .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.QUEEN_SPEED)
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
            AVPEntityTypeTags.ROYAL_ALIENS,
            AVPEntityTypeTags.HEAVY_GUNS_IMMUNE
        );
    }

    @Override
    protected Optional<EntitySoundData> createSoundData() {
        return Optional.of(
            new EntitySoundData(
                AVPSoundEventRegistry.INSTANCE.entityQueen.ambient(),
                AlienSounds.createSoundEventSelector(AVPSoundEventRegistry.INSTANCE.entityQueen.hurt()),
                AVPSoundEventRegistry.INSTANCE.entityQueen.death()
            )
        );
    }

    @Override
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.of(QueenLootTable.LOOT_TABLE);
    }

    @Override
    protected Optional<EntitySpawnData<?>> createSpawnData() {
        return Optional.of(
            new EntitySpawnData<>(
                BiomeTags.IS_OVERWORLD,
                5,
                1,
                1,
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ($, accessor, spawnType, pos, randSrc) -> pos.getY() <= AVPConfig.Aliens.MAX_Y_LEVEL_FOR_QUEEN_SPAWNS &&
                    Queen.checkQueenSpawnRules(getHolder().get(), accessor, spawnType, pos, randSrc)
            )
        );
    }
}
