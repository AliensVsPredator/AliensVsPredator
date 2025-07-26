package com.lib.common.data;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import com.avp.AVPResources;

public record AdvancementAccess(
    String group,
    String path
) {

    public ResourceLocation getResourceLocation() {
        return AVPResources.location(group + "/" + path);
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
