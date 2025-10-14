package com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire.sensor;

import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import com.avp.common.model.inventory.AVPInventory;
import com.avp.common.model.inventory.AVPInventoryHolder;

public class WaterBucketInInventorySensor {

    public static final StateKey.Sensed<Option<AVPInventory.Entry>> KEY = StateKey.sensed("water_bucket_in_inventory");

    public static <T extends LivingEntity & AVPInventoryHolder> @NotNull Map<StateKey<?>, Option<AVPInventory.Entry>> sense(
        T livingEntityWithInventory,
        ReadableWorldState worldState
    ) {
        var waterBucketEntries = livingEntityWithInventory.getInventory().selectEntries(Items.WATER_BUCKET);
        var waterBucketEntryOption = waterBucketEntries.isEmpty()
            ? Option.<AVPInventory.Entry>none()
            : Option.ofNullable(waterBucketEntries.stream().findFirst().orElse(null));
        return Map.of(KEY, waterBucketEntryOption);
    }

}
