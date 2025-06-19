package com.avp.mixin;

import com.alien.common.gameplay.entity.living.alien.parasite.Parasite;
import com.alien.common.model.alien.Host;
import com.alien.common.registry.InfectionRegistry;
import com.alien.common.util.EmbryoUtil;
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
    private static final String NBT_EMBRYO_GROWTH_TIME_IN_TICKS = "embryoGrowthTimeInTicks";

    @Unique
    private static final String NBT_EMBRYO_TYPE = "embryoType";

    @Unique
    private int embryoGrowthTimeInTicks;

    @Unique
    private EntityType<?> embryoType;

    public MixinLivingEntity_Host(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var self = LivingEntity.class.cast(this);
        EmbryoUtil.runEmbryoRoutines(self);
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        this.embryoGrowthTimeInTicks = compoundTag.getInt(NBT_EMBRYO_GROWTH_TIME_IN_TICKS);

        var resourceLocationString = compoundTag.getString(NBT_EMBRYO_TYPE);
        var resourceLocation = ResourceLocation.parse(resourceLocationString);
        var entityType = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);

        if (!entityType.equals(EntityType.PIG)) {
            this.embryoType = entityType;
        }
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        compoundTag.putInt(NBT_EMBRYO_GROWTH_TIME_IN_TICKS, embryoGrowthTimeInTicks);

        if (embryoType != null) {
            var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(embryoType);
            compoundTag.putString(NBT_EMBRYO_TYPE, resourceLocation.toString());
        }
    }

    @Override
    public void implantEmbryo(Parasite parasite) {
        var infectionOption = InfectionRegistry.get(getType(), parasite.getType());

        infectionOption.ifSome(infection -> {
            this.embryoType = infection.embryoType();
            // Assign the active genes from the parasite to the host's gene manager.
            parasite.getGeneManager().transfer(getOrCreateGeneManager(), false);

            var self = LivingEntity.class.cast(this);

            if (self instanceof Mob mob) {
                // Set persistence required since we don't want this mob to despawn while it is carrying an embryo.
                mob.setPersistenceRequired();
            }
        });
    }

    @Override
    public void removeEmbryo() {
        this.embryoType = null;
    }

    @Override
    public EntityType<?> getEmbryoType() {
        return embryoType;
    }

    @Override
    public int getEmbryoGrowthTimeInTicks() {
        return embryoGrowthTimeInTicks;
    }
}
