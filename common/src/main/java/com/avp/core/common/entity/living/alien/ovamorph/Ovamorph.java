package com.avp.core.common.entity.living.alien.ovamorph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.core.AVP;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.constant.HealthConstants;
import com.avp.core.common.entity.constant.KnockbackResistanceConstants;
import com.avp.core.common.entity.living.alien.Alien;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.manager.HatchManager;
import com.avp.core.common.util.AVPPredicates;
import com.avp.core.common.util.AlienVariantUtil;

public class Ovamorph extends Alien implements Shearable {

    private static final EntityDataAccessor<Boolean> HATCHED = SynchedEntityData.defineId(Ovamorph.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Byte> MAX_SPAWN_COUNT = SynchedEntityData.defineId(Ovamorph.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<Boolean> ROOTED = SynchedEntityData.defineId(Ovamorph.class, EntityDataSerializers.BOOLEAN);

    private static final String IS_ROOTED_KEY = "isRooted";

    public static AttributeSupplier.Builder createOvamorphAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.ATTACK_DAMAGE, 0)
            .add(Attributes.KNOCKBACK_RESISTANCE, KnockbackResistanceConstants.OVAMORPH_KNOCKBACK_RESISTANCE)
            .add(Attributes.MAX_HEALTH, HealthConstants.OVAMORPH_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, 0);
    }

    private final OvamorphAnimationDispatcher animationDispatcher;

    private final HatchManager hatchManager;

    public Ovamorph(EntityType<? extends Ovamorph> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new OvamorphAnimationDispatcher(this);
        this.hatchManager = new HatchManager(this, HATCHED, MAX_SPAWN_COUNT, 3 * 20, 3 * 20);
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HATCHED, false);
        builder.define(MAX_SPAWN_COUNT, (byte) 1);
        builder.define(ROOTED, true);
    }

    @Override
    public void tick() {
        super.tick();
        hatchManager.tick();
    }

    public void tryHatch() {
        if (!level().isClientSide && !hatchManager.hatched()) {
            hatchManager.hatch();
            animationDispatcher.open();
        }
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand interactionHand) {
        if (!level().isClientSide) {
            var itemStack = player.getItemInHand(interactionHand);
            var resinBallItem = AlienVariantUtil.getResinBallFor(this);

            if (itemStack.is(AVPItems.RAW_ROYAL_JELLY.get())) {
                if (hatchManager.hatched()) {
                    hatchManager.restore();
                    player.getItemInHand(interactionHand).shrink(1);
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.CONSUME;
                }
            } else if (isRooted() && itemStack.is(Items.SHEARS)) {
                shear(SoundSource.PLAYERS);
                gameEvent(GameEvent.SHEAR, player);
                itemStack.hurtAndBreak(1, player, getSlotForHand(interactionHand));
                return InteractionResult.SUCCESS;
            } else if (!isRooted() && itemStack.is(resinBallItem)) {
                level().playSound(null, this, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
                setRooted(true);
                itemStack.shrink(1);
            }
        }

        return super.mobInteract(player, interactionHand);
    }

    @Override
    public void shear(@NotNull SoundSource soundSource) {
        setRooted(false);
        level().playSound(null, this, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);
        var resinBallItem = AlienVariantUtil.getResinBallFor(this);

        var itemEntity = this.spawnAtLocation(resinBallItem, 1);

        if (itemEntity != null) {
            itemEntity.setDeltaMovement(
                itemEntity.getDeltaMovement()
                    .add(
                        (random.nextFloat() - random.nextFloat()) * 0.1F,
                        random.nextFloat() * 0.05F,
                        (random.nextFloat() - random.nextFloat()) * 0.1F
                    )
            );
        }
    }

    @Override
    public boolean readyForShearing() {
        return isRooted();
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float damage) {
        var isHurt = super.hurt(damageSource, damage);

        if (!level().isClientSide && isHurt && damageSource.getEntity() != null) {
            tryHatch();
        }

        return isHurt;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            super.doPush(entity);
            return;
        }

        if (AVPPredicates.isFreeHost(this, entity)) {
            tryHatch();
        }

        super.doPush(entity);
    }

    @Override
    protected boolean canBleedAcid() {
        return !hatchManager.hatched();
    }

    @Override
    public boolean isPushedByFluid() {
        return !isRooted();
    }

    @Override
    public boolean isPushable() {
        return !isRooted();
    }

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired() || !isRooted();
    }

    @Override
    protected boolean canHeal() {
        return !hatchManager.hatched() && super.canHeal();
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.STATS_CONFIG.properties().getOrThrow(ConfigProperties.OVAMORPH_ATTRIBUTES.healthRegenPerSecond());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        hatchManager.load(compoundTag);

        if (compoundTag.contains(IS_ROOTED_KEY)) {
            setRooted(compoundTag.getBoolean(IS_ROOTED_KEY));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        hatchManager.save(compoundTag);

        compoundTag.putBoolean(IS_ROOTED_KEY, isRooted());
    }

    public HatchManager hatchManager() {
        return hatchManager;
    }

    public boolean isRooted() {
        return entityData.get(ROOTED);
    }

    public void setRooted(boolean isRooted) {
        entityData.set(ROOTED, isRooted);
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        SpawnEggItem spawnEggItem = null;

        if (isNetherAfflicted()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.NETHER_OVAMORPH.get());
        }

        if (isAberrant()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.ABERRANT_OVAMORPH.get());
        }

        return spawnEggItem == null ? super.getPickResult() : new ItemStack(spawnEggItem);
    }
}
