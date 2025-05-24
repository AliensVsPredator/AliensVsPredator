package com.avp.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.avp.common.entity.living.Host;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.parasite.Parasite;
import com.avp.common.entity.living.gene.GeneProviders;
import com.avp.common.entity.living.manager.GeneManager;
import com.avp.common.lifecycle.infection.AlienInfection;
import com.avp.common.lifecycle.registry.AlienInfectionRegistry;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_GrowEmbryo extends Entity implements Host {

    @Unique
    private static final String PARASITE_GROWTH_TIME_IN_TICKS_KEY = "parasiteGrowthTimeInTicks";

    @Unique
    private static final String PARASITE_TYPE_KEY = "parasiteType";

    @Unique
    private GeneManager geneManager;

    @Unique
    private int parasiteGrowthTimeInTicks;

    @Unique
    private EntityType<?> parasiteType;

    public MixinLivingEntity_GrowEmbryo(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        runParasiteRoutines();
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        getOrCreateGeneManager().load(compoundTag);

        this.parasiteGrowthTimeInTicks = compoundTag.getInt(PARASITE_GROWTH_TIME_IN_TICKS_KEY);

        var resourceLocationString = compoundTag.getString(PARASITE_TYPE_KEY);
        var resourceLocation = ResourceLocation.parse(resourceLocationString);
        var entityType = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);

        if (!entityType.equals(EntityType.PIG)) {
            this.parasiteType = entityType;
        }
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        getOrCreateGeneManager().save(compoundTag);

        compoundTag.putInt(PARASITE_GROWTH_TIME_IN_TICKS_KEY, parasiteGrowthTimeInTicks);

        if (parasiteType != null) {
            var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(parasiteType);
            compoundTag.putString(PARASITE_TYPE_KEY, resourceLocation.toString());
        }
    }

    @Unique
    private void runParasiteRoutines() {
        var level = level();

        if (level.isClientSide) {
            return;
        }

        var self = LivingEntity.class.cast(this);

        if (self instanceof Player player && (player.isCreative() || player.isSpectator() || player.isInvulnerable())) {
            parasiteGrowthTimeInTicks = 0;
            parasiteType = null;
            geneManager = null;
            return;
        }

        if (parasiteType != null) {
            tickParasiteGrowth(level, self);
        } else {
            parasiteGrowthTimeInTicks = 0;
        }
    }

    @Unique
    private void tickParasiteGrowth(Level level, LivingEntity self) {
        parasiteGrowthTimeInTicks++;

        // TODO: Make time configurable
        if (parasiteGrowthTimeInTicks <= TimeUnit.MINUTES.toSeconds(5) * 20) {
            return;
        }

        if (level.getDifficulty() != Difficulty.PEACEFUL) {
            AlienInfectionRegistry.get(getType(), parasiteType)
                .ifSome(alienInfection -> {
                    @SuppressWarnings("unchecked")
                    var typedInfection = (AlienInfection<LivingEntity, LivingEntity>) alienInfection;

                    giveBirth(level, self, typedInfection);
                });

            kill();
        }

        // Remove the parasite type no matter what.
        this.parasiteType = null;
        // Reset the parasite growth time (in ticks) no matter what.
        this.parasiteGrowthTimeInTicks = 0;
    }

    @Unique
    private void giveBirth(Level level, LivingEntity self, AlienInfection<LivingEntity, LivingEntity> alienInfection) {
        var embryoType = alienInfection.embryoType();
        var embryo = embryoType.create(level);

        if (embryo == null) {
            return;
        }

        if (embryo instanceof Mob mob) {
            mob.setPersistenceRequired();
        }

        if (embryo instanceof Alien alien) {
            applyGenesToParasite(self, alien);
        }

        embryo.moveTo(position(), getYRot(), getXRot());
        embryo.setYRot(getYRot());
        embryo.setXRot(getXRot());

        // TODO: This shouldn't be here at all.
        if (self instanceof Witch) {
            var effects = List.of(
                MobEffects.DAMAGE_BOOST,
                MobEffects.MOVEMENT_SPEED,
                MobEffects.REGENERATION,
                MobEffects.DIG_SPEED,
                MobEffects.JUMP
            );
            var randomEffect = effects.get(self.getRandom().nextInt(effects.size()));
            // TODO: Prefer genes over effects, effects can be removed by milk / other factors, genes can't.
            embryo.addEffect(new MobEffectInstance(randomEffect, Integer.MAX_VALUE, 0, false, false));
        }

        // Copies effects from previous entity to the next
        for (var effect : self.getActiveEffects()) {
            embryo.addEffect(new MobEffectInstance(effect));
        }

        // TODO: Adjust parasite's base attributes based on genes.

        level.addFreshEntity(embryo);
    }

    @Unique
    private void applyGenesToParasite(LivingEntity self, Alien alien) {
        var geneManager = alien.geneManager();
        geneManager.setAll(getOrCreateGeneManager().getAll());

        // Transfer genetics from parasite to embryo.
        var hostType = self.getType();
        var hostSpecificBonusGenesMap = GeneProviders.GENE_MAPS_BY_ENTITY_TYPE.get(hostType);

        if (hostSpecificBonusGenesMap != null) {
            hostSpecificBonusGenesMap.forEach(geneManager::add);
        }

        // Should be safe since this is a LivingEntity mixin, would only break if some other mod
        // is modifying getType to NOT return a living entity type.
        @SuppressWarnings("unchecked")
        var commonBonusGenesMap = GeneProviders.computeInheritedGeneAdditiveValues(
            (EntityType<? extends LivingEntity>) hostType
        );
        commonBonusGenesMap.forEach(geneManager::add);
    }

    @Override
    public GeneManager getOrCreateGeneManager() {
        if (geneManager == null) {
            this.geneManager = new GeneManager(this);
        }

        return geneManager;
    }

    @Override
    public EntityType<?> getParasiteType() {
        return parasiteType;
    }

    @Override
    public void injectEmbryo(Parasite parasite) {
        this.parasiteType = parasite.getType();
        getOrCreateGeneManager().setAll(parasite.geneManager().getAll());

        var self = LivingEntity.class.cast(this);

        if (self instanceof Mob mob) {
            mob.setPersistenceRequired();
        }
    }

    @Override
    public void clearParasiteType() {
        this.parasiteType = null;
    }
}
