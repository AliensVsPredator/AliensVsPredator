package org.avp.api.data.loot_table;

import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractAVPEntityLootTableProvider extends EntityLootSubProvider {

    private final Map<EntityType<?>, LootTable.Builder> map;

    protected AbstractAVPEntityLootTableProvider() {
        super(FeatureFlags.REGISTRY.allFlags());
        map = new HashMap<>();
    }

    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        this.generate();
        map.forEach(
            (entityType, lootTableBuilder) -> biConsumer.accept(entityType.getDefaultLootTable(), lootTableBuilder)
        );
    }

    @Override
    protected void add(@NotNull EntityType<?> entityType, LootTable.@NotNull Builder lootTableBuilder) {
        super.add(entityType, lootTableBuilder);
        map.putIfAbsent(entityType, lootTableBuilder);
    }
}
