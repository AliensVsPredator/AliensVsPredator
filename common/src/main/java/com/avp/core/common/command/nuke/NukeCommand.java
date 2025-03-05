package com.avp.core.common.command.nuke;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;

import java.time.Duration;

import com.avp.core.AVP;
import com.avp.core.common.explosion.Explosion;
import com.avp.core.common.explosion.nuke.NuclearExplosionEffects;
import com.avp.core.common.util.ExplosionUtil;
import com.avp.core.server.ServerScheduler;

public class NukeCommand {

    private static final String COMMAND_NAME = "nuke";

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(COMMAND_NAME)
            .executes(context -> {
                ServerScheduler.schedule(() -> {
                    var explosion = createNuclearExplosion(context);
                    explosion.explode();
                }, Duration.ofSeconds(1));

                return 1;
            });
    }

    private static Explosion createNuclearExplosion(CommandContext<CommandSourceStack> context) {
        var level = context.getSource().getLevel();
        var center = context.getSource().getPosition();
        var progressTracker = new ExplosionProgressTracker();
        var nuclearExplosionEffects = new NuclearExplosionEffects();
        var radius = 16 * 8;
        var maxKnockback = 5;

        return Explosion.builder(level, center)
            .withRadius(Direction.Plane.HORIZONTAL, radius)
            .withRadius(Direction.UP, radius / 2)
            .withRadius(Direction.DOWN, 16 * 2)
            .onExplosionStart(() -> {
                progressTracker.startTimer();

                var entities = ExplosionUtil.getEntitiesInRadius(level, center, radius);

                for (var entity : entities) {
                    var distance = entity.distanceToSqr(center);
                    var damage = ExplosionUtil.computeDamage(radius, 5, 1000, distance);

                    entity.igniteForSeconds(15);
                    entity.hurt(level.damageSources().explosion(null), (float) damage);
                    ExplosionUtil.applyKnockback(center, radius, entity, maxKnockback, distance);
                }
            })
            .onBlockSample(($, pos) -> {
                nuclearExplosionEffects.apply($, pos);
                progressTracker.incrementBlockDestroyCounter();
            })
            .onExplosionFinish(() -> {
                progressTracker.stopTimer();

                var timeTakenInMillis = progressTracker.timeTaken();
                var timeTakenInTicks = timeTakenInMillis / 50;

                AVP.LOGGER.info(
                    "Explosion @ {} completed in {}ms ({} ticks), destroying {} blocks!",
                    center,
                    timeTakenInMillis,
                    timeTakenInTicks,
                    progressTracker.blocksDestroyed()
                );
            })
            .build();
    }
}
