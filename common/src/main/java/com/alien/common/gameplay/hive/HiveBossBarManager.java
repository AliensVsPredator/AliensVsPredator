package com.alien.common.gameplay.hive;

import com.alien.common.data.AlienAdvancements;
import com.alien.common.data.AlienVariantTypes;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.util.AlienPredicates;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.avp.AVP;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class HiveBossBarManager {

    public static final Map<AlienVariant, String> ALIEN_VARIANT_TO_TRANSLATABLE_STRING_MAP = Arrays.stream(AlienVariant.values())
        .collect(
            Collectors.toMap(
                Function.identity(),
                alienVariant -> "bossbar.avp.hive." + alienVariant.name().toLowerCase(Locale.US) + ".title"
            )
        );

    private static final Map<AlienVariant, Component> ALIEN_VARIANT_TO_COMPONENT_NAME_MAP = ALIEN_VARIANT_TO_TRANSLATABLE_STRING_MAP
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> Component.translatable(entry.getValue())
            )
        );

    private static final Predicate<EntityType<?>> XENOMORPH_PREDICATE = entityType -> entityType.is(AVPEntityTypeTags.XENOMORPHS);

    private final Hive hive;

    private final ServerBossEvent bossEvent;

    private int maximumSeenXenomorphCount;

    public HiveBossBarManager(Hive hive) {
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(
            ALIEN_VARIANT_TO_COMPONENT_NAME_MAP.get(hive.getVariant()),
            BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarOverlay.PROGRESS
        ).setDarkenScreen(AVP.config.hiveConfigs.HIVE_DARKEN_SCREEN);
        this.hive = hive;
    }

    public void tick() {
        updateBossBarProgress();
        updateBossBarColor();
        updateBossBarTitle();
        updateTrackingPlayers();
    }

    private void updateBossBarProgress() {
        // Get all xenomorphs that are loaded in the world right now.
        var loadedXenomorphCount = hive.getMembershipManager()
            .getMembersMatching(XENOMORPH_PREDICATE)
            .size();
        // Get all xenomorphs that are in reserves right now.
        var currentReserveXenomorphCount = hive.getReserveManager()
            .getCountMatching(XENOMORPH_PREDICATE);
        // Add the two counts together to get the total xenomorph count.
        var totalXenomorphCount = loadedXenomorphCount + currentReserveXenomorphCount;

        this.maximumSeenXenomorphCount = Math.max(maximumSeenXenomorphCount, totalXenomorphCount);

        bossEvent.setProgress(totalXenomorphCount / (float) maximumSeenXenomorphCount);
    }

    private void updateBossBarColor() {
        var alienVariantType = AlienVariantTypes.getFor(hive.getVariant());
        bossEvent.setColor(alienVariantType.bossBarColor());
    }

    private void updateBossBarTitle() {
        var variant = hive.getVariant();
        var component = ALIEN_VARIANT_TO_COMPONENT_NAME_MAP.get(variant);

        bossEvent.setName(component);
    }

    private void updateTrackingPlayers() {
        if (hive.ageInTicks() % 20 == 0) {
            removeNonTargetPlayers(bossEvent);
        }

        if (!hive.isAlive()) {
            bossEvent.removeAllPlayers();
        }
    }

    private void removeNonTargetPlayers(ServerBossEvent bossEvent) {
        var playersToRemove = bossEvent.getPlayers()
            .stream()
            .filter(player -> {
                if (!AlienPredicates.isValidTarget(player)) {
                    return true;
                }

                return !hive.getSpaceManager().isEntityWithinHive(player);
            })
            .toList();

        playersToRemove.forEach(bossEvent::removePlayer);
    }

    public void onHiveRemoved() {
        var level = hive.level();

        if (level.getDifficulty() != Difficulty.PEACEFUL && level instanceof ServerLevel serverLevel) {
            serverLevel.players()
                .stream()
                .filter(player -> hive.getSpaceManager().isEntityWithinHive(player))
                .forEach(AlienAdvancements.KILL_A_HIVE::grant);
        }

        bossEvent.removeAllPlayers();
    }

    public void trackPlayer(ServerPlayer player) {
        bossEvent.addPlayer(player);
    }

    public boolean isTrackingPlayers() {
        return !bossEvent.getPlayers().isEmpty();
    }
}
