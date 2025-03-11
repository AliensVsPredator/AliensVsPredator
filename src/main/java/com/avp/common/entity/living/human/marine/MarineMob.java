package com.avp.common.entity.living.human.marine;

import com.avp.AVP;
import com.avp.common.config.ConfigProperties;
import com.avp.common.entity.living.human.AbstractHumanMob;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MarineMob extends AbstractHumanMob {

    private final MarineAnimationDispatcher animationDispatcher;

    public MarineMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new MarineAnimationDispatcher(this);
    }

    public static AttributeSupplier.Builder createMarineAttributes() {
        var container = ConfigProperties.MARINE_ATTRIBUTES;
        return container.applyFrom(AVP.STATS_CONFIG, Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE));
    }

    @Override
    protected void runPassiveAnimations() {}

    @Override
    public void runAttackAnimations() {
        //TODO: Attack animations
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        setItemSlot(EquipmentSlot.MAINHAND, makeInitialWeapon());
        setItemSlot(EquipmentSlot.HEAD, new ItemStack(ArmorItems.MK50_HELMET));
        setItemSlot(EquipmentSlot.CHEST, new ItemStack(ArmorItems.MK50_CHESTPLATE));
        setItemSlot(EquipmentSlot.LEGS, new ItemStack(ArmorItems.MK50_LEGGINGS));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private ItemStack makeInitialWeapon() {
        final var givenList = Arrays.asList(
                AVPItems.M88_MOD_4_COMBAT_PISTOL,
                AVPItems.M37_12_SHOTGUN,
                AVPItems.F903WE_RIFLE,
                AVPItems.M41A_PULSE_RIFLE,
                AVPItems.M4RA_BATTLE_RIFLE);
        final var randomIndex = random.nextInt(givenList.size());
        final var randomElement = givenList.get(randomIndex);
        return new ItemStack(randomElement);
    }
}
