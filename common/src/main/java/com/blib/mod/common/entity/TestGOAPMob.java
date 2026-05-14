package com.blib.mod.common.entity;

import com.just.ai.goap.StateKey;
import com.just.ai.goap.action.Action;
import com.just.ai.goap.condition.expression.Expressions;
import com.just.ai.goap.goal.Goal;
import com.just.ai.goap.graph.Graph;
import com.just.ai.goap.sensor.Sensors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.goap.v1.GOAPSensors;
import com.blib.api.common.goap.v1.action.impl.WanderAction;

/**
 * Reference GOAP-driven test mob — extends {@link Cow} so it gets vanilla Cow's model + texture for free, then disables
 * vanilla AI ({@code registerGoals} is overridden to a no-op) and exposes a small {@link Graph} via the
 * {@code GOAPUser} contract instead.
 * <p>
 * The graph is intentionally minimal: a handful of pre-built sensors so the {@code World State} tab in the engine's
 * GOAP details panel has visible data, and one wander action / goal pair that produces a non-empty plan list. Spawn
 * with {@code /summon blib:test_goap_mob ~ ~ ~}, then select it in the engine viewport.
 */
@ApiStatus.Internal
public final class TestGOAPMob extends Cow {

    /** Derived state the wander goal wants satisfied. The wander action's effect flips it to {@code true}. */
    private static final StateKey.Derived<Boolean> HAS_WANDERED = StateKey.derived("has_wandered");

    /** Graph instance shared across all test mobs — sensors / actions / goals are stateless so this is safe. */
    private static final Graph<LivingEntity> GOAP_GRAPH = buildGraph();

    public TestGOAPMob(EntityType<? extends Cow> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10.0)
            .add(Attributes.MOVEMENT_SPEED, 0.22);
    }

    @Override
    protected void registerGoals() {
        // Intentionally empty — vanilla Cow goals would compete with GOAP for navigation control. The GOAP agent
        // attached by MixinLivingEntity_GOAPUser drives all behavior.
    }

    /**
     * Hand back our graph for the {@code GOAPUser} contract (mixed into all {@link LivingEntity} subclasses at
     * runtime). No {@code @Override} because the mixin's method isn't visible at compile time — but the JVM dispatch
     * resolves to this implementation on {@code TestGOAPMob} instances. The mixin auto-ticks the agent against this
     * every server tick, so we don't need to override {@link #tick()}.
     */
    public Graph<LivingEntity> blib$getGOAPGraphOrNull() {
        return GOAP_GRAPH;
    }

    private static Graph<LivingEntity> buildGraph() {
        return Graph.<LivingEntity>builder()
            // Sensors — populate the world-state map the GOAP debug panel reads. Mix of pre-built ones from
            // GOAPSensors and a couple of custom ones for visibility into the mob's own state.
            .addSensor(GOAPSensors.IS_ON_GROUND)
            .addSensor(GOAPSensors.IS_IN_LAVA)
            .addSensor(GOAPSensors.IS_ON_FIRE)
            .addSensor(GOAPSensors.IS_UNDERWATER)
            .addSensor(GOAPSensors.HEALTH_RATIO)
            .addSensor(Sensors.map(StateKey.sensed("tick_count"), (LivingEntity e) -> e.tickCount))
            .addSensor(Sensors.map(StateKey.sensed("y_pos"), (LivingEntity e) -> e.getBlockY()))
            // Goal: keep wandering. Replanned every time it's satisfied because the agent is configured with
            // ReplanPolicies.ifNoActivePlans() in MixinLivingEntity_GOAPUser → cycles indefinitely.
            .addGoal(
                Goal.builder("wander_indefinitely")
                    .addDesiredCondition(HAS_WANDERED, Expressions.Boolean.isTrue())
                    .build()
            )
            // Action: delegates to BLib's WanderAction. Effect on HAS_WANDERED makes the planner pick this action
            // to satisfy the goal.
            .addAction(
                Action.<LivingEntity>builder("wander_around")
                    .withCost(1.0f)
                    .addEffect(HAS_WANDERED, true)
                    .withPerformCallback(TestGOAPMob::performWander)
                    .build()
            )
            .build();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Action.Signal performWander(Action.Context<? extends LivingEntity> ctx) {
        if (!(ctx.getActor() instanceof PathfinderMob)) {
            return Action.Signal.ABORT;
        }
        // WanderAction is parameterized on PathfinderMob, but our graph is typed on LivingEntity (forced by the
        // mixin's GOAPUser<LivingEntity> erasure). The actor check above guarantees the cast is safe at runtime.
        var pathfinderCtx = (Action.Context) ctx;
        return WanderAction.perform(pathfinderCtx, 8, 4, 1.0, WanderAction::onFinish);
    }
}
