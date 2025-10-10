package com.alien.common.model.alien.variant;

import com.alien.common.gameplay.block.resin.vein.ResinVeinBlock;
import com.alien.common.gameplay.block.resin.vent.ResinVentBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import com.avp.common.registry.AVPDeferredHolder;

public record AlienVariantType(
    AlienVariant variant,

    // Blocks
    AVPDeferredHolder<Block> resin,
    AVPDeferredHolder<Block> resinNode,
    AVPDeferredHolder<ResinVeinBlock> resinVein,
    AVPDeferredHolder<ResinVentBlock> resinVent,
    AVPDeferredHolder<Block> resinWeb,

    // Block Tags
    TagKey<Block> resinBlockTag,
    TagKey<Block> resinReplaceableTag,

    // Items
    AVPDeferredHolder<Item> chitin,
    AVPDeferredHolder<Item> platedChitin,
    AVPDeferredHolder<Item> resinBall,

    // Game Events
    AVPDeferredHolder<GameEvent> cryForHelpEvent,
    @Nullable AVPDeferredHolder<GameEvent> eggPickupRequestEvent,
    AVPDeferredHolder<GameEvent> resinSpreadEvent,

    // Particle Types
    AVPDeferredHolder<SimpleParticleType> acidParticleType,

    // Miscellaneous
    BossEvent.BossBarColor bossBarColor,
    ChatFormatting chatColor,
    boolean canReproduce
) {}
