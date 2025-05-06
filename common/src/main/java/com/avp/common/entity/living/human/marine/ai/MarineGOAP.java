package com.avp.common.entity.living.human.marine.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Monster;

import com.avp.common.entity.ai.EntityGOAP;
import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.action.AttackAction;
import com.avp.common.entity.ai.action.AvoidAction;
import com.avp.common.entity.ai.action.EatFoodToHealAction;
import com.avp.common.entity.ai.action.EquipItemAction;
import com.avp.common.entity.ai.action.MoveToTargetEntityAction;
import com.avp.common.entity.ai.action.PickUpItemAction;
import com.avp.common.entity.ai.action.WanderToLandPosAction;
import com.avp.common.entity.ai.goal.EntertainedGoal;
import com.avp.common.entity.ai.goal.HealthyGoal;
import com.avp.common.entity.ai.goal.NoTargetGoal;
import com.avp.common.entity.ai.goal.PickUpFoodGoal;
import com.avp.common.entity.ai.sensor.combat.CombatResponseSensor;
import com.avp.common.entity.ai.sensor.combat.NearbyAttackTargetEntitiesSensor;
import com.avp.common.entity.ai.sensor.combat.NearestAttackTargetEntitySensor;
import com.avp.common.entity.ai.sensor.combat.NearestAttackTargetInRangeSensor;
import com.avp.common.entity.ai.sensor.entity.FoodTargetEntityInRangeSensor;
import com.avp.common.entity.ai.sensor.entity.NearbyEntitiesSensor;
import com.avp.common.entity.ai.sensor.entity.NearbyFoodItemEntitiesSensor;
import com.avp.common.entity.ai.sensor.entity.NearbyItemEntitiesSensor;
import com.avp.common.entity.ai.sensor.entity.NearbyLivingEntitiesSensor;
import com.avp.common.entity.ai.sensor.entity.NearestFoodItemEntitySensor;
import com.avp.common.entity.ai.sensor.inventory.InventorySensor;
import com.avp.common.entity.ai.sensor.stats.IsBoredSensor;
import com.avp.common.entity.ai.sensor.stats.IsHealthySensor;
import com.avp.common.entity.ai.util.CombatResponse;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.common.entity.living.human.marine.Marine;
import com.avp.common.goap.state.GOAPWorldState;
import com.avp.common.item.GunItem;

public class MarineGOAP extends EntityGOAP<Marine> {

    public MarineGOAP(Marine marine) {
        // Order matters for these.
        // TODO: Organize sensors automatically based on dependencies.
        addBaseRoutines(marine);
        addInventoryRoutines();
        addFoodPickupRoutines(marine);
        addCombatRoutines(marine);
        addSelfCareRoutines(marine);
    }

    // Base routines that most entities have, that aren't necessarily direct preconditions for actions.
    public void addBaseRoutines(Marine marine) {
        addSensor(NearbyEntitiesSensor.INSTANCE);
        addSensor(NearbyLivingEntitiesSensor.INSTANCE);
        addSensor(NearbyItemEntitiesSensor.INSTANCE);

        addSensor(new IsBoredSensor(marine.getRandom()));

        addAction(new WanderToLandPosAction<>(0.75));

        addGoal(new EntertainedGoal());
    }

    public void addInventoryRoutines() {
        // Sensors
        addSensor(new InventorySensor<>());
    }

    // Routines that enable the marine to target utility items and pick them up.
    public void addFoodPickupRoutines(Marine marine) {
        // Sensors
        addSensor(NearbyFoodItemEntitiesSensor.INSTANCE);
        addSensor(NearestFoodItemEntitySensor.INSTANCE);
        addSensor(new FoodTargetEntityInRangeSensor<>((self, distanceSqr) -> distanceSqr <= 2 * 2));

        // Goals
        addGoal(new PickUpFoodGoal());

        // Actions
        addAction(
            new MoveToTargetEntityAction<>(
                GOAPConstants.NEAREST_FOOD_ITEM_ENTITY,
                GOAPConstants.IS_FOOD_TARGET_ITEM_ENTITY_IN_RANGE,
                1.0,
                (self, distanceSqr) -> distanceSqr < 1
            )
        );
        addAction(
            new PickUpItemAction<>(
                GOAPConstants.NEAREST_FOOD_ITEM_ENTITY,
                GOAPConstants.IS_FOOD_TARGET_ITEM_ENTITY_IN_RANGE,
                worldState -> marine.getHealth() / marine.getMaxHealth()
            )
        );
    }

    // Routines that enable the marine to fight target entities.
    public void addCombatRoutines(Marine marine) {
        // Sensors
        addSensor(new NearbyAttackTargetEntitiesSensor<>(target -> target instanceof Monster));
        addSensor(NearestAttackTargetEntitySensor.INSTANCE);
        addSensor(
            new NearestAttackTargetInRangeSensor<>(((worldState, distanceSqr) -> isTargetInRange(marine, worldState, distanceSqr)))
        );
        addSensor(new CombatResponseSensor<>());

        // Goals
        // If the marine has a target, this goal is for the entity to get rid of the target, somehow.
        addGoal(new NoTargetGoal());

        // Actions
        // An action that allows the marine to move closer to the attack target so that the attack target is in range.
        addAction(
            new MoveToTargetEntityAction<>(
                GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY,
                GOAPConstants.IS_ATTACK_TARGET_ENTITY_IN_RANGE,
                1.0,
                (worldState, distanceSqr) -> isTargetInRange(marine, worldState, distanceSqr)
            )
        );

        // Melee attack action that the marine can choose if melee is preferable.
        addAction(new EquipItemAction<>(InteractionHand.MAIN_HAND, ItemType.meleeWeapon()));
        addAction(new AttackAction<>(CombatResponse.FightType.MELEE));

        // Ranged attack action that the marine can choose if range is preferable.
        addAction(new EquipItemAction<>(InteractionHand.MAIN_HAND, ItemType.rangedWeapon()));
        addAction(new MarineRangedAttackAction());

        // Avoid action to allow the marine to run away from an avoid target.
        addAction(new AvoidAction<>(8, 1.2F));
    }

    // Routines that allow the marine to try and heal itself and/or help the marine avoid danger.
    public void addSelfCareRoutines(Marine marine) {
        // Sensors
        addSensor(new IsHealthySensor<>(0.5F));

        // Goals
        addGoal(new HealthyGoal());

        // Actions
        // Marines need to equip food in order to eat it.
        addAction(new EquipItemAction<>(InteractionHand.OFF_HAND, ItemType.food()));
        // Marines must eat the equipped food in order to heal.
        addAction(new EatFoodToHealAction<>(marine.getMaxHealth() * 0.2F));
    }

    private boolean isTargetInRange(Marine marine, GOAPWorldState worldState, double distanceSqr) {
        return switch (worldState.getOrDefault(GOAPConstants.MAIN_HAND_ITEM_TYPE, ItemType.none())) {
            case ItemType.MeleeWeapon meleeWeapon -> distanceSqr <= 2 * 2;
            case ItemType.RangedWeapon rangedWeapon -> {
                if (marine.getMainHandItem().getItem() instanceof GunItem gunItem) {
                    var fireMode = gunItem.getGunConfig().getDefaultFireMode();
                    var range = fireMode.range();
                    yield distanceSqr < range * range;
                }

                yield distanceSqr < 16 * 16;
            }
            case ItemType.Food food -> false;
            case ItemType.None none -> false;
            case ItemType.Other other -> false;
        };
    }
}
