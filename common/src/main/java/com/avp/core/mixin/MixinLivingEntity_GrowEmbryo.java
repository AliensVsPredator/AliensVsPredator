package com.avp.core.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

import com.avp.core.common.entity.living.Host;
import com.avp.core.common.entity.living.alien.Alien;
import com.avp.core.common.entity.living.alien.parasite.Parasite;
import com.avp.core.common.gene.GeneProviders;
import com.avp.core.common.lifecycle.registry.AlienInfectionRegistry;
import com.avp.core.common.manager.GeneManager;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_GrowEmbryo extends Entity implements Host {

    @Unique
    private static final String PARASITE_GROWTH_TIME_IN_TICKS_KEY = "parasiteGrowthTimeInTicks";

    @Unique
    private static final String PARASITE_SOURCE_TYPE_KEY = "parasiteSourceType";

    @Unique
    private GeneManager geneManager;

    @Unique
    private int parasiteGrowthTimeInTicks;

    @Unique
    private EntityType<?> parasiteSourceType;

    public MixinLivingEntity_GrowEmbryo(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        tickParasiteGrowth();
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        getOrCreateGeneManager().load(compoundTag);

        this.parasiteGrowthTimeInTicks = compoundTag.getInt(PARASITE_GROWTH_TIME_IN_TICKS_KEY);

        var resourceLocationString = compoundTag.getString(PARASITE_SOURCE_TYPE_KEY);
        var resourceLocation = ResourceLocation.parse(resourceLocationString);
        var entityType = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);

        if (!entityType.equals(EntityType.PIG)) {
            this.parasiteSourceType = entityType;
        }
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        getOrCreateGeneManager().save(compoundTag);

        compoundTag.putInt(PARASITE_GROWTH_TIME_IN_TICKS_KEY, parasiteGrowthTimeInTicks);

        if (parasiteSourceType != null) {
            var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(parasiteSourceType);
            compoundTag.putString(PARASITE_SOURCE_TYPE_KEY, resourceLocation.toString());
        }
    }

    @Unique
    private void tickParasiteGrowth() {
        var level = level();

        if (level.isClientSide) {
            return;
        }

        var self = LivingEntity.class.cast(this);

        if (self instanceof Player player && (player.isCreative() || player.isSpectator() || player.isInvulnerable())) {
            parasiteGrowthTimeInTicks = 0;
            parasiteSourceType = null;
            geneManager = null;
            return;
        }

        if (parasiteSourceType != null) {
            parasiteGrowthTimeInTicks++;

            // FIXME: Make time configurable
            if (parasiteGrowthTimeInTicks > TimeUnit.MINUTES.toSeconds(5) * 20) {
                var infectionOptional = AlienInfectionRegistry.get(getType(), parasiteSourceType);

                infectionOptional.ifPresent(infection -> {
                    var parasiteType = infection.parasiteType().get();
                    var parasite = parasiteType.create(level);

                    if (parasite != null) {
                        if (parasite instanceof Mob mob) {
                            mob.setPersistenceRequired();
                        }

                        if (parasite instanceof Alien alien) {
                            var geneManager = alien.geneManager();
                            geneManager.setAll(getOrCreateGeneManager().getAll());
                            alien.updateStateBasedOnGenetics();

                            // Transfer genetics from parasite source to parasite.
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

                        parasite.moveTo(position(), getYRot(), getXRot());
                        parasite.setYRot(getYRot());
                        parasite.setXRot(getXRot());

                        // TODO: Adjust parasite's base attributes based on genes.

                        level.addFreshEntity(parasite);
                    }
                });

                this.parasiteSourceType = null;
                kill();
            }
        } else {
            parasiteGrowthTimeInTicks = 0;
        }
    }

    @Override
    public GeneManager getOrCreateGeneManager() {
        if (geneManager == null) {
            this.geneManager = new GeneManager(this);
        }

        return geneManager;
    }

    @Override
    public int parasiteGrowthTimeInTicks() {
        return parasiteGrowthTimeInTicks;
    }

    @Override
    public EntityType<?> parasiteType() {
        return parasiteSourceType;
    }

    @Override
    public void injectEmbryo(Parasite parasite) {
        this.parasiteSourceType = parasite.getType();
        getOrCreateGeneManager().setAll(parasite.geneManager().getAll());

        var self = LivingEntity.class.cast(this);

        if (self instanceof Mob mob) {
            mob.setPersistenceRequired();
        }
    }
}
