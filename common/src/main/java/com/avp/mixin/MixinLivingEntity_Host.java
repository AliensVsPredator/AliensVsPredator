package com.avp.mixin;

import com.alien.common.gameplay.entity.living.alien.parasite.Parasite;
import com.alien.common.model.alien.Host;
import com.alien.common.registry.InfectionRegistry;
import com.alien.common.util.AlienEmbryoUtil;
import com.lib.common.gameplay.entity.manager.GeneContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_Host extends Entity implements Host {

    @Unique
    private static final String NBT_PARASITE_GENES = "parasiteGenes";

    @Unique
    private static final String NBT_EMBRYO_GROWTH_TIME_IN_TICKS = "embryoGrowthTimeInTicks";

    @Unique
    private static final String NBT_EMBRYO_TYPE = "embryoType";

    @Unique
    private int embryoGrowthTimeInTicks;

    @Unique
    private EntityType<?> embryoType;

    @Unique
    private GeneContainer parasiteGeneContainer;

    public MixinLivingEntity_Host(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var self = LivingEntity.class.cast(this);
        AlienEmbryoUtil.runAlienEmbryoRoutines(self);
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        this.embryoGrowthTimeInTicks = compoundTag.getInt(NBT_EMBRYO_GROWTH_TIME_IN_TICKS);

        var resourceLocationString = compoundTag.getString(NBT_EMBRYO_TYPE);
        var resourceLocation = ResourceLocation.parse(resourceLocationString);
        var entityType = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);

        // TODO: Fix this, don't check for a pig.
        if (!entityType.equals(EntityType.PIG)) {
            setEmbryoType(entityType);
        }

        if (compoundTag.contains(NBT_PARASITE_GENES)) {
            var tag = compoundTag.getCompound(NBT_PARASITE_GENES);
            getOrCreateParasiteGeneContainer().load(tag);
        }
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        compoundTag.putInt(NBT_EMBRYO_GROWTH_TIME_IN_TICKS, embryoGrowthTimeInTicks);

        if (embryoType != null) {
            var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(embryoType);
            compoundTag.putString(NBT_EMBRYO_TYPE, resourceLocation.toString());
        }

        var tag = new CompoundTag();
        getOrCreateParasiteGeneContainer().save(tag);
        compoundTag.put(NBT_PARASITE_GENES, tag);
    }

    @Override
    public void implantEmbryo(Parasite parasite) {
        var infectionOption = InfectionRegistry.get(getType(), parasite.getType());

        infectionOption.ifSome(infection -> {
            setEmbryoType(infection.embryoType());
            // Assign the active genes from the parasite to the embryo's gene container.
            parasite.getGeneManager()
                .getGeneContainer()
                .transfer(getOrCreateParasiteGeneContainer(), false);

            var self = LivingEntity.class.cast(this);

            if (self instanceof Mob mob) {
                // Set persistence required since we don't want this mob to despawn while it is carrying an embryo.
                mob.setPersistenceRequired();
            }
        });
    }

    @Override
    public GeneContainer getOrCreateParasiteGeneContainer() {
        if (parasiteGeneContainer == null) {
            this.parasiteGeneContainer = new GeneContainer();
        }

        return parasiteGeneContainer;
    }

    @Override
    public EntityType<?> getEmbryoType() {
        return embryoType;
    }

    @Override
    public void setEmbryoType(EntityType<?> embryoType) {
        this.embryoType = embryoType;
    }

    @Override
    public int getEmbryoGrowthTimeInTicks() {
        return embryoGrowthTimeInTicks;
    }

    @Override
    public void setEmbryoGrowthTimeInTicks(int embryoGrowthTimeInTicks) {
        this.embryoGrowthTimeInTicks = embryoGrowthTimeInTicks;
    }
}
