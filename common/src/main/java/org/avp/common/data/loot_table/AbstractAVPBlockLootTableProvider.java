package org.avp.common.data.loot_table;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.avp.common.AVPConstants;

public abstract class AbstractAVPBlockLootTableProvider extends BlockLootSubProvider {

    protected AbstractAVPBlockLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        this.generate();
        Set<ResourceLocation> blockLootTableResourceLocations = new HashSet<>();

        for (Block block : getKnownBlocks()) {
            if (block.isEnabled(this.enabledFeatures)) {
                ResourceLocation blockLootTableResourceLocation = block.getLootTable();
                if (
                    blockLootTableResourceLocation != BuiltInLootTables.EMPTY && blockLootTableResourceLocations.add(
                        blockLootTableResourceLocation
                    )
                ) {
                    LootTable.Builder lootTableBuilder = this.map.remove(blockLootTableResourceLocation);
                    if (lootTableBuilder == null) {
                        throw new IllegalStateException(
                            String.format(
                                Locale.ROOT,
                                "Missing loot table '%s' for '%s'",
                                blockLootTableResourceLocation,
                                BuiltInRegistries.BLOCK.getKey(block)
                            )
                        );
                    }

                    biConsumer.accept(blockLootTableResourceLocation, lootTableBuilder);
                }
            }
        }
    }

    public @NotNull Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream()
            .filter(
                block -> Optional.of(BuiltInRegistries.BLOCK.getKey(block))
                    .filter(key -> key.getNamespace().equals(AVPConstants.MOD_ID))
                    .isPresent()
            )
            .collect(Collectors.toSet());
    }
}
