package com.blib.api.common.advancement.v1;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record BLibAdvancement(
    String modId,
    String group,
    String path,
    ResourceLocation resourceLocation,
    String titleTranslationKey,
    String descriptionTranslationKey
) {

    public BLibAdvancement(String modId, String group, String path) {
        this(
            modId,
            group,
            path,
            ResourceLocation.fromNamespaceAndPath(modId, group + "/" + path),
            "advancements." + group + "." + path + ".title",
            "advancements." + group + "." + path + ".description"
        );
    }

    public Component titleComponent() {
        return Component.translatable(titleTranslationKey);
    }

    public Component descriptionComponent() {
        return Component.translatable(descriptionTranslationKey);
    }

    public @Nullable AdvancementHolder getHolder(ServerPlayer serverPlayer) {
        return serverPlayer.server.getAdvancements().get(resourceLocation);
    }

    public boolean isGranted(ServerPlayer serverPlayer) {
        var advancementHolder = getHolder(serverPlayer);

        if (advancementHolder == null) {
            return false;
        }

        var progress = serverPlayer.getAdvancements().getOrStartProgress(advancementHolder);

        return progress.isDone();
    }

    public void grant(ServerPlayer serverPlayer) {
        var advancementHolder = getHolder(serverPlayer);

        if (advancementHolder == null) {
            return;
        }

        var advancements = serverPlayer.getAdvancements();
        var progress = advancements.getOrStartProgress(advancementHolder);

        if (!progress.isDone()) {
            for (var criterionKey : progress.getRemainingCriteria()) {
                advancements.award(advancementHolder, criterionKey);
            }
        }
    }

    public void revoke(ServerPlayer serverPlayer) {
        var advancementHolder = getHolder(serverPlayer);

        if (advancementHolder == null) {
            return;
        }

        var advancements = serverPlayer.getAdvancements();
        var progress = advancements.getOrStartProgress(advancementHolder);

        if (progress.hasProgress()) {
            for (var criterionKey : progress.getCompletedCriteria()) {
                advancements.revoke(advancementHolder, criterionKey);
            }
        }
    }
}
