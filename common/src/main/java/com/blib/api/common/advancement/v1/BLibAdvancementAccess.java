package com.blib.api.common.advancement.v1;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record BLibAdvancementAccess(
    String modId,
    String group,
    String path
) {

    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(modId, group + "/" + path);
    }

    public String getTitleTranslationKey() {
        return "advancements." + group + "." + path + ".title";
    }

    public Component getTitleComponent() {
        return Component.translatable(getTitleTranslationKey());
    }

    public String getDescriptionTranslationKey() {
        return "advancements." + group + "." + path + ".description";
    }

    public Component getDescriptionComponent() {
        return Component.translatable(getDescriptionTranslationKey());
    }

    public void grant(ServerPlayer serverPlayer) {
        var advancementHolder = serverPlayer.server.getAdvancements().get(getResourceLocation());

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
}
