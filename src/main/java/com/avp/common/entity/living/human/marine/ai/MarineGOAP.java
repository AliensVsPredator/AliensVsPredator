package com.avp.common.entity.living.human.marine.ai;

import net.minecraft.world.entity.monster.Monster;

import com.avp.common.entity.ai.EntityGOAP;
import com.avp.common.entity.ai.action.AvoidAction;
import com.avp.common.entity.ai.action.EatFoodToHealAction;
import com.avp.common.entity.ai.action.EquipRangedWeaponAction;
import com.avp.common.entity.ai.action.MeleeAttackAction;
import com.avp.common.entity.ai.action.MoveCloserToAttackTargetEntityAction;
import com.avp.common.entity.ai.action.MoveCloserToFoodItemAction;
import com.avp.common.entity.ai.action.PickUpFoodAction;
import com.avp.common.entity.ai.goal.HealthyGoal;
import com.avp.common.entity.ai.goal.NoTargetGoal;
import com.avp.common.entity.ai.goal.PickUpFoodGoal;
import com.avp.common.entity.ai.sensor.combat.AttackTargetInRangeSensor;
import com.avp.common.entity.ai.sensor.combat.CombatResponseSensor;
import com.avp.common.entity.ai.sensor.combat.NearbyAttackTargetEntitiesSensor;
import com.avp.common.entity.ai.sensor.combat.NearestAttackTargetEntitySensor;
import com.avp.common.entity.ai.sensor.entity.FoodTargetEntityInRangeSensor;
import com.avp.common.entity.ai.sensor.entity.NearbyEntitiesSensor;
import com.avp.common.entity.ai.sensor.entity.NearbyFoodItemEntitiesSensor;
import com.avp.common.entity.ai.sensor.entity.NearbyItemEntitiesSensor;
import com.avp.common.entity.ai.sensor.entity.NearbyLivingEntitiesSensor;
import com.avp.common.entity.ai.sensor.entity.NearestFoodItemEntitySensor;
import com.avp.common.entity.ai.sensor.inventory.HasFoodInInventorySensor;
import com.avp.common.entity.ai.sensor.inventory.HasFreeInventorySlotSensor;
import com.avp.common.entity.ai.sensor.inventory.HasRangedWeaponInInventorySensor;
import com.avp.common.entity.ai.sensor.inventory.MainHandItemTypeSensor;
import com.avp.common.entity.ai.sensor.stats.IsHealthySensor;
import com.avp.common.entity.living.human.marine.Marine;

public class MarineGOAP extends EntityGOAP<Marine> {

    public MarineGOAP(Marine marine) {
        // Order matters for these.
        // TODO: Organize sensors automatically based on dependencies.
        addBaseRoutines();
        addInventoryRoutines();
        addFoodPickupRoutines();
        addCombatRoutines();
        addSelfCareRoutines(marine);
    }

    // Base routines that most entities have, that aren't necessarily direct preconditions for actions.
    public void addBaseRoutines() {
        addSensor(NearbyEntitiesSensor.INSTANCE);
        addSensor(NearbyLivingEntitiesSensor.INSTANCE);
        addSensor(NearbyItemEntitiesSensor.INSTANCE);
    }

    public void addInventoryRoutines() {
        // Sensors
        addSensor(new HasFreeInventorySlotSensor<>());
        addSensor(new HasFoodInInventorySensor<>());
        addSensor(new MainHandItemTypeSensor<>());
        addSensor(new HasRangedWeaponInInventorySensor<>());
    }

    // Routines that enable the marine to target utility items and pick them up.
    public void addFoodPickupRoutines() {
        // Sensors
        addSensor(NearbyFoodItemEntitiesSensor.INSTANCE);
        addSensor(NearestFoodItemEntitySensor.INSTANCE);
        addSensor(new FoodTargetEntityInRangeSensor<>((self, distanceSqr) -> distanceSqr < 2));

        // Goals
        addGoal(new PickUpFoodGoal());

        // Actions
        addAction(new MoveCloserToFoodItemAction<>((self, distanceSqr) -> distanceSqr < 2));
        addAction(new PickUpFoodAction<>());
    }

    // Routines that enable the marine to fight target entities.
    public void addCombatRoutines() {
        // Sensors
        addSensor(new NearbyAttackTargetEntitiesSensor<>((self, target) -> target instanceof Monster));
        addSensor(NearestAttackTargetEntitySensor.INSTANCE);
        addSensor(new AttackTargetInRangeSensor<>((self, distanceSqr) -> distanceSqr < 16 * 16));
        addSensor(new CombatResponseSensor<>());

        // Goals
        // If the marine has a target, this goal is for the entity to get rid of the target, somehow.
        addGoal(new NoTargetGoal());

        // Actions
        // An action that allows the marine to move closer to the attack target so that the attack target is in range.
        addAction(new MoveCloserToAttackTargetEntityAction<>(1.0));
        // Melee attack action that the marine can choose if melee is preferable.
        addAction(new MeleeAttackAction<>());
        // Ranged attack action that the marine can choose if range is preferable.
        addAction(new EquipRangedWeaponAction<>());
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
        addAction(new EatFoodToHealAction<>(marine.getMaxHealth() * 0.2F));
    }
}
