package com.avp.common.hive.manager;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.avp.AVP;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.AlienVariant;
import com.avp.common.entity.living.alien.util.AlienPredicates;
import com.avp.common.hive.Hive;

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

    private final Hive hive;

    private final ServerBossEvent bossEvent;

    private int maximumSeenAlienCount;

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
        var currentAlienCount = hive.getMembershipManager()
            .getMembersMatching(entityType -> entityType.is(AVPEntityTypeTags.XENOMORPHS))
            .size();
        this.maximumSeenAlienCount = Math.max(maximumSeenAlienCount, currentAlienCount);

        bossEvent.setProgress(currentAlienCount / (float) maximumSeenAlienCount);
    }

    private void updateBossBarColor() {
        var color = switch (hive.getVariant()) {
            case NORMAL -> BossEvent.BossBarColor.GREEN;
            case NETHER -> BossEvent.BossBarColor.RED;
            case ABERRANT -> BossEvent.BossBarColor.YELLOW;
            case IRRADIATED -> BossEvent.BossBarColor.BLUE;
        };

        bossEvent.setColor(color);
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
        bossEvent.removeAllPlayers();
    }

    public void trackPlayer(ServerPlayer player) {
        bossEvent.addPlayer(player);
    }

    public boolean isTrackingPlayers() {
        return !bossEvent.getPlayers().isEmpty();
    }
}
