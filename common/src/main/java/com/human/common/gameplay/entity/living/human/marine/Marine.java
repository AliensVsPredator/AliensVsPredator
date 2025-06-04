package com.human.common.gameplay.entity.living.human.marine;

import com.human.common.gameplay.entity.living.human.AbstractHuman;
import com.human.common.gameplay.entity.living.human.marine.ai.MarineGOAP;
import com.human.common.registry.init.item.HumanGunItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.common.model.inventory.AVPInventory;
import com.avp.common.model.inventory.AVPInventoryBearer;
import com.avp.common.registry.init.item.AVPItems;

public class Marine extends AbstractHuman implements AVPInventoryBearer {

    private static final List<List<Item>> USABLE_ARMOR_ITEMS = List.of(
        List.of(
            Items.IRON_HELMET,
            Items.IRON_CHESTPLATE,
            Items.IRON_LEGGINGS,
            Items.IRON_BOOTS
        )
    );

    private static final List<Supplier<Item>> USABLE_WEAPON_ITEM_SUPPLIERS = List.of(
        HumanGunItems.M88MOD4_COMBAT_PISTOL,
        HumanGunItems.M37_12_SHOTGUN,
        HumanGunItems.F903WE_RIFLE,
        HumanGunItems.M41A_PULSE_RIFLE,
        HumanGunItems.M4RA_BATTLE_RIFLE,
        () -> Items.IRON_AXE,
        () -> Items.IRON_SWORD
    );

    private static final List<EquipmentSlot> ARMOR_EQUIPMENT_SLOTS = List.of(
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET
    );

    private final MarineAnimationDispatcher animationDispatcher;

    private final MarineGOAP goap;

    private final MarineInventory marineInventory;

    public Marine(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new MarineAnimationDispatcher(this);
        this.goap = new MarineGOAP(this);
        this.marineInventory = new MarineInventory(this);
    }

    public static AttributeSupplier.Builder createMarineAttributes() {
        return applyFrom(AVP.config.statsConfigs.MARINE_STATS, Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE));
    }

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
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        marineInventory.dropItems();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor level,
        @NotNull DifficultyInstance difficulty,
        @NotNull MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        addInitialWeapon();

        if (random.nextInt(100) <= 10) {
            marineInventory.addPersonalItem(new ItemStack(AVPItems.GRENADE.get()));
            addInitialArmor();
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public AVPInventory getInventory() {
        return marineInventory;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        marineInventory.load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        marineInventory.save(compoundTag);
    }

    private void addInitialArmor() {
        var randomArmorSetIndex = getRandom().nextInt(USABLE_ARMOR_ITEMS.size());
        var selectedArmor = USABLE_ARMOR_ITEMS.get(randomArmorSetIndex)
            .stream()
            .map(ItemStack::new)
            .toArray(ItemStack[]::new);

        for (var i = 0; i < ARMOR_EQUIPMENT_SLOTS.size(); i++) {
            marineInventory.addPersonalItem(selectedArmor[i]);
        }
    }

    private void addInitialWeapon() {
        marineInventory.addPersonalItem(
            new ItemStack(USABLE_WEAPON_ITEM_SUPPLIERS.get(random.nextInt(USABLE_WEAPON_ITEM_SUPPLIERS.size())).get())
        );
    }
}
