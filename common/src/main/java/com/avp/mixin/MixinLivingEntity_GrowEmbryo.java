package com.avp.mixin;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.parasite.Parasite;
import com.alien.common.model.alien.Host;
import com.alien.common.model.lifecycle.infection.Infection;
import com.alien.common.registry.GeneBonusDataRegistry;
import com.alien.common.registry.InfectionRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_GrowEmbryo extends Entity implements Host {

    @Unique
    private static final String PARASITE_GROWTH_TIME_IN_TICKS_KEY = "parasiteGrowthTimeInTicks";

    @Unique
    private static final String PARASITE_TYPE_KEY = "parasiteType";

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
            setGeneManager(null);
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

        // TODO: Use data pack values here.
        if (parasiteGrowthTimeInTicks <= TimeUnit.MINUTES.toSeconds(5) * 20) {
            return;
        }

        if (level.getDifficulty() != Difficulty.PEACEFUL) {
            InfectionRegistry.get(getType(), parasiteType)
                .ifSome(infection -> giveBirth(level, self, infection));

            kill();
        }

        // Remove the parasite type no matter what.
        this.parasiteType = null;
        // Reset the parasite growth time (in ticks) no matter what.
        this.parasiteGrowthTimeInTicks = 0;
    }

    @Unique
    private void giveBirth(Level level, LivingEntity self, Infection infection) {
        var embryoType = infection.embryoType();
        var embryo = embryoType.create(level);

        if (embryo == null) {
            return;
        }

        if (embryo instanceof Mob mob) {
            mob.setPersistenceRequired();
        }

        if (embryo instanceof Alien alien) {
            applyGenesToEmbryo(self, alien);
            alien.setHostType(self.getType());
        }

        embryo.moveTo(position(), getYRot(), getXRot());
        embryo.setYRot(getYRot());
        embryo.setXRot(getXRot());

        if (embryo instanceof LivingEntity livingEmbryo) {
            // TODO: The genes are assigned once here, but if they're removed they don't appear on the embryo again.
            // Copies effects from previous entity to the next
            for (var effect : self.getActiveEffects()) {
                livingEmbryo.addEffect(new MobEffectInstance(effect.getEffect(), Integer.MAX_VALUE, effect.getAmplifier(), false, false));
            }
        }

        level.addFreshEntity(embryo);
    }

    @Unique
    private void applyGenesToEmbryo(LivingEntity self, Alien embryo) {
        var alienGeneManager = embryo.getGeneManager();

        // Transfer genes.
        getOrCreateGeneManager().transfer(alienGeneManager, true);

        // Transfer genes from host to embryo.
        var hostType = self.getType();
        var hostSpecificBonusGenesMap = GeneBonusDataRegistry.getOrDefault(hostType);
        hostSpecificBonusGenesMap.forEach(alienGeneManager::addActiveGene);
    }

    @Override
    public EntityType<?> getParasiteType() {
        return parasiteType;
    }

    @Override
    public void injectEmbryo(Parasite parasite) {
        this.parasiteType = parasite.getType();
        // Assign the active genes from the parasite to the host's gene manager.
        parasite.getGeneManager().transfer(getOrCreateGeneManager(), false);

        var self = LivingEntity.class.cast(this);

        if (self instanceof Mob mob) {
            // Set persistence required since we don't want this mob to despawn while it is carrying an embryo.
            mob.setPersistenceRequired();
        }
    }

    @Override
    public void clearParasiteType() {
        this.parasiteType = null;
    }
}
