package org.avp.common.entity.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootTable;
import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.attribute.AVPEntityAttributesBindingRegistry;
import org.avp.common.entity.living.Praetorian;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.tag.AVPEntityTypeTags;

import java.util.List;
import java.util.Optional;

public class PraetorianData extends EntityData<Praetorian> {

    public static final PraetorianData INSTANCE = new PraetorianData();

    @Override
    protected Holder<EntityType<Praetorian>> createHolder() {
        return AVPSimpleDeferredEntityTypeRegistry.INSTANCE.createMobHolder(
            "praetorian",
            0x010202,
            0x363534,
            EntityType.Builder.of(Praetorian::new, MobCategory.MONSTER)
                .sized(0.98F, 2.48F)
        );
    }

    @Override
    protected Optional<AttributeSupplier> createAttributeSupplier() {
        return Optional.of(AVPEntityAttributesBindingRegistry.builder()
            .add(Attributes.ATTACK_DAMAGE, 12)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1)
            .add(Attributes.MAX_HEALTH, 100)
            .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.PRAETORIAN_SPEED)
            .build());
    }

    @Override
    protected List<TagKey<EntityType<?>>> createTags() {
        return List.of(
            AVPEntityTypeTags.ACID_BLEEDERS,
            AVPEntityTypeTags.ALIENS,
            AVPEntityTypeTags.HIVE_ALIENS,
            AVPEntityTypeTags.MONSTERS,
            AVPEntityTypeTags.PRODUCES_RESIN,
            AVPEntityTypeTags.ROYAL_ALIENS
        );
    }

    @Override
    protected Optional<LootTable.Builder> createLootTable() {
        return Optional.empty();
    }
}
