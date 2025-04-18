package com.avp.common.entity.living.human.marine;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import com.avp.AVP;
import com.avp.common.entity.living.human.AbstractHuman;
import com.avp.common.entity.living.human.marine.ai.MarineGOAP;
import com.avp.common.item.AVPItems;
import com.avp.common.util.ItemUtil;

public class Marine extends AbstractHuman implements InventoryCarrier {

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

    private static final String INVENTORY_KEY = "inventory";

    private final MarineAnimationDispatcher animationDispatcher;

    private final MarineGOAP goap;

    private final SimpleContainer inventory;

    public Marine(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new MarineAnimationDispatcher(this);
        this.goap = new MarineGOAP(this);
        this.inventory = new SimpleContainer(27);
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
    protected void dropEquipment() {
        super.dropEquipment();
        // Drops the marine's equipment when they die.
        inventory.removeAllItems()
            .stream()
            .map(itemStack -> ItemUtil.drop(this, itemStack, true, false))
            .flatMap(Option::toStream)
            .forEach(itemEntity -> level().addFreshEntity(itemEntity));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor level,
        @NotNull DifficultyInstance difficulty,
        @NotNull MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        inventory.addItem(makeInitialWeapon());

        if (random.nextInt(100) <= 10) {
            inventory.addItem(new ItemStack(AVPItems.GRENADE));
            makeInitialArmor();
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        compoundTag.put(INVENTORY_KEY, inventory.createTag(level().registryAccess()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        inventory.fromTag(compoundTag.getList(INVENTORY_KEY, Tag.TAG_COMPOUND), level().registryAccess());
    }

    @Override
    public @NotNull SimpleContainer getInventory() {
        return inventory;
    }

    private void makeInitialArmor() {
        var selectedArmor = List.of(
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
            inventory.addItem(selectedArmor[i]);
        }
    }

    private ItemStack makeInitialWeapon() {
        var randomIndex = random.nextInt(USABLE_WEAPON_ITEMS.size());
        var randomWeaponItem = USABLE_WEAPON_ITEMS.get(randomIndex);

        return new ItemStack(randomWeaponItem);
    }
}
