package com.avp.common.entity.living.human.marine;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import com.avp.AVP;
import com.avp.common.ai.goal.combat.DelayedAttackGoal;
import com.avp.common.entity.living.human.AbstractHuman;
import com.avp.common.entity.living.human.marine.ai.MarineGOAP;
import com.avp.common.item.AVPItems;
import com.avp.common.manager.*;

public class Marine extends AbstractHuman {

    private static final List<Item> USABLE_WEAPON_ITEMS = List.of(
        AVPItems.M88MOD4_COMBAT_PISTOL,
        AVPItems.M37_12_SHOTGUN,
        AVPItems.F903WE_RIFLE,
        AVPItems.M41A_PULSE_RIFLE,
        AVPItems.M4RA_BATTLE_RIFLE
    );

    private static final List<EquipmentSlot> ARMOR_EQUIPMENT_SLOTS = List.of(
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET
    );

    private final MarineAnimationDispatcher animationDispatcher;

    private final MarineGOAP goap;

    public Marine(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new MarineAnimationDispatcher(this);
        this.goap = new MarineGOAP(this);
    }

    public static AttributeSupplier.Builder createMarineAttributes() {
        return applyFrom(AVP.config.statsConfigs.MARINE_STATS, Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE));
    }

    @Override
    protected void runPassiveAnimations() {}

    @Override
    public void runAttackAnimations() {
        animationDispatcher.rightShoot();
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            goap.update(this);
        }

        if (this.getTarget() != null && this.getTarget() instanceof Marine) {
            this.setTarget(null);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new DelayedAttackGoal(this, 1.0, true, 5, this::runAttackAnimations));
        targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(AbstractHuman.class));
        targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                false,
                target -> {
                    if (target instanceof Monster) {
                        return true;
                    }

                    // TODO: More sophisticated checks here.

                    return false;
                }
            )
        );
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor level,
        @NotNull DifficultyInstance difficulty,
        @NotNull MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        setItemSlot(EquipmentSlot.MAINHAND, makeInitialWeapon());

        if (random.nextInt(100) <= 10) {
            setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(AVPItems.GRENADE));
            this.makeInitialArmor();
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private void makeInitialArmor() {
        var selectedArmor = List.of(
            // List.of(
            // ArmorItems.TACTICAL_HELMET,
            // ArmorItems.TACTICAL_CHESTPLATE,
            // ArmorItems.TACTICAL_LEGGINGS,
            // ArmorItems.TACTICAL_BOOTS
            // ),
            // List.of(
            // ArmorItems.TACTICAL_CAMO_HELMET,
            // ArmorItems.TACTICAL_CAMO_CHESTPLATE,
            // ArmorItems.TACTICAL_CAMO_LEGGINGS,
            // ArmorItems.TACTICAL_CAMO_BOOTS
            // )
            List.of(
                Items.IRON_HELMET,
                Items.IRON_CHESTPLATE,
                Items.IRON_LEGGINGS,
                Items.IRON_BOOTS
            ),
            List.of(
                Items.IRON_HELMET,
                Items.IRON_CHESTPLATE,
                Items.IRON_LEGGINGS,
                Items.IRON_BOOTS
            )
        )
            .get(this.getRandom().nextIntBetweenInclusive(0, 1))
            .stream()
            .map(ItemStack::new)
            .toArray(ItemStack[]::new);

        for (var i = 0; i < ARMOR_EQUIPMENT_SLOTS.size(); i++) {
            setItemSlot(ARMOR_EQUIPMENT_SLOTS.get(i), selectedArmor[i]);
        }
    }

    private ItemStack makeInitialWeapon() {
        var randomIndex = random.nextInt(USABLE_WEAPON_ITEMS.size());
        var randomWeaponItem = USABLE_WEAPON_ITEMS.get(randomIndex);
        return new ItemStack(randomWeaponItem);
    }
}
