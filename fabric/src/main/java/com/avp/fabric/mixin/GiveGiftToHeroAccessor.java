package com.avp.fabric.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(GiveGiftToHero.class)
public interface GiveGiftToHeroAccessor {

    @Accessor("GIFTS")
    static Map<VillagerProfession, ResourceKey<LootTable>> getGifts() {
        throw new AssertionError();
    }
}
