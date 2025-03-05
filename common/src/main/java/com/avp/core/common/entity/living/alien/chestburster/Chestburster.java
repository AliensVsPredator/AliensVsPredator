package com.avp.core.common.entity.living.alien.chestburster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import com.avp.core.AVP;
import com.avp.core.common.MoveAnalysis;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.living.alien.Alien;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.gene.GeneKeys;
import com.avp.core.common.gene.behavior.GeneDecoders;
import com.avp.core.common.manager.GrowthManager;
import com.avp.core.common.util.AVPPredicates;
import com.avp.core.common.util.XenomorphGrowthUtil;
import com.avp.core.common.util.resin.ResinData;
import com.avp.core.common.util.resin.ResinManager;
import com.avp.core.common.util.resin.ResinProducer;

public class Chestburster extends Alien implements ResinProducer {

    public static AttributeSupplier.Builder createChestbursterAttributes() {
        var container = ConfigProperties.CHESTBURSTER_ATTRIBUTES;
        return container.applyFrom(AVP.STATS_CONFIG, Monster.createMonsterAttributes());
    }

    protected final MoveAnalysis moveAnalysis;

    private final ChestbursterAnimationDispatcher animationDispatcher;

    private final GrowthManager growthManager;

    private final ResinManager resinManager;

    public Chestburster(EntityType<? extends Chestburster> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new ChestbursterAnimationDispatcher(this);
        this.growthManager = new GrowthManager(this, XenomorphGrowthUtil.GROW_UP_CALLBACK)
            .setGrowOverTime(true)
            .setGrowthTimeReductionMultiplierProvider(
                () -> geneManager.get(GeneKeys.GROWTH_SPEED, GeneDecoders.GROWTH_SPEED)
            );
        this.moveAnalysis = new MoveAnalysis(this);
        this.resinManager = new ResinManager(this, createResinData())
            .setBonusResinProvider(
                () -> geneManager.get(GeneKeys.BONUS_RESIN_PRODUCTION, GeneDecoders.BONUS_RESIN_PRODUCTION).intValue()
            );
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, LivingEntity.class, 8, 1, 1.2, entity -> {
            if (!(entity instanceof Alien alien)) {
                return !AVPPredicates.IS_IMMORTAL.test(entity);
            }

            var isAlienAberrant = alien.isAberrant();
            var isAlienNetherAfflicted = alien.isNetherAfflicted();

            if (!Objects.equals(isAberrant(), isAlienAberrant) || !Objects.equals(isNetherAfflicted(), isAlienNetherAfflicted)) {
                return true;
            }

            var hiveSignature = hiveManager.signatureOrNull();
            var alienHiveSignature = alien.hiveManager().signatureOrNull();

            return hiveSignature != null && alienHiveSignature != null && !Objects.equals(hiveSignature, alienHiveSignature);
        }));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
    }

    @Override
    public void tick() {
        super.tick();
        moveAnalysis.tick();
        growthManager.tick();
        resinManager.tick();
    }

    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 8, 1, 750);
    }

    public void runPassiveAnimations() {
        var dispatcher = animationDispatcher;
        var isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();
        Runnable animFunction;

        // if (isUnderWater()) {
        // // TODO: idle swim
        // animFunction = dispatcher::swim;
        // } else

        if (isMovingOnGround) {
            animFunction = dispatcher::slowSlither;
        } else {
            animFunction = dispatcher::idle;
        }

        animFunction.run();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        growthManager.load(compoundTag);
        resinManager.load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        growthManager.save(compoundTag);
        resinManager.save(compoundTag);
    }

    @Override
    public ResinManager resinManager() {
        return resinManager;
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        SpawnEggItem spawnEggItem = null;

        if (isNetherAfflicted()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.NETHER_CHESTBURSTER.get());
        }

        if (isAberrant()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.ABERRANT_CHESTBURSTER.get());
        }

        return spawnEggItem == null ? super.getPickResult() : new ItemStack(spawnEggItem);
    }
}
