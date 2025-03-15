package com.avp.common.entity.living.human.marine;

import com.avp.AVP;
import com.avp.common.ai.goal.combat.DelayedAttackGoal;
import com.avp.common.ai.goal.combat.UseItemGoal;
import com.avp.common.config.ConfigProperties;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.human.AbstractHumanMob;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import com.avp.common.manager.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MarineMob extends AbstractHumanMob {

    private final MarineAnimationDispatcher animationDispatcher;

    public MarineMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new MarineAnimationDispatcher(this);
        this.outfitManager = new MarineOutfitManager(this);
        this.beardManager = new BeardManager(this, 3);
        this.hairManager = new HairManager(this, 5, 5, 5);
        this.eyeManager = new EyeManager(this, 5, 5);
        this.skinManager = new SkinManager(this, 6, 6);
    }

    public static AttributeSupplier.Builder createMarineAttributes() {
        var container = ConfigProperties.MARINE_ATTRIBUTES;
        return container.applyFrom(AVP.STATS_CONFIG, Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE));
    }

    @Override
    protected void runPassiveAnimations() {}

    @Override
    public void runAttackAnimations() {
        animationDispatcher.rightShoot();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new DelayedAttackGoal(this, 1.0, true, 5, this::runAttackAnimations));
        targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(AbstractHumanMob.class));
        targetSelector.addGoal(
                2,
                new NearestAttackableTargetGoal<>(
                        this,
                        LivingEntity.class,
                        false,
                        target -> (this.getLastAttacker() != null && this.getLastAttacker().is(target)) || target instanceof Alien
                )
        );
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        setItemSlot(EquipmentSlot.MAINHAND, makeInitialWeapon());
        if (random.nextInt( 100 ) <= 10) {
            setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(AVPItems.GRENADE));
            this.makeInitialArmor();
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private void makeInitialArmor() {
        var selectedArmor = List.of(
//                        List.of(
//                                ArmorItems.TACTICAL_HELMET,
//                                ArmorItems.TACTICAL_CHESTPLATE,
//                                ArmorItems.TACTICAL_LEGGINGS,
//                                ArmorItems.TACTICAL_BOOTS
//                        ),
//                        List.of(
//                                ArmorItems.TACTICAL_CAMO_HELMET,
//                                ArmorItems.TACTICAL_CAMO_CHESTPLATE,
//                                ArmorItems.TACTICAL_CAMO_LEGGINGS,
//                                ArmorItems.TACTICAL_CAMO_BOOTS
//                        )
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

        var armorSlots = List.of(
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        );

        for (var i = 0; i < armorSlots.size(); i++) {
            setItemSlot(armorSlots.get(i), selectedArmor[i]);
        }
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
