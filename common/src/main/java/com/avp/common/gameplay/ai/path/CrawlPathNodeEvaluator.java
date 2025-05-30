package com.avp.common.gameplay.ai.path;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.NotNull;

public class CrawlPathNodeEvaluator extends WalkNodeEvaluator {

    @Override
    public void prepare(@NotNull PathNavigationRegion pathNavigationRegion, @NotNull Mob mob) {
        super.prepare(pathNavigationRegion, mob);
        this.entityHeight = Mth.floor((mob.getBbHeight() / 2) + 1.0F);
    }
}
