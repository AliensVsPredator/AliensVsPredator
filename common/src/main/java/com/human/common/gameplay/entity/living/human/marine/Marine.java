package com.human.common.gameplay.entity.living.human.marine;

import com.human.common.gameplay.entity.living.human.AbstractHuman;
import com.human.common.gameplay.entity.living.human.marine.ai.MarineGOAP;
import com.human.common.registry.init.item.HumanGunItems;
import com.lib.common.gameplay.goap.GOAPUser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
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
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPItems;

public class Marine extends AbstractHuman implements AVPInventoryBearer, GOAPUser<MarineGOAP> {

    private static final List<List<Supplier<Item>>> DEFAULT_ARMOR_SETS = List.of(
        List.of(
            AVPArmorItems.TACTICAL_HELMET,
            AVPArmorItems.TACTICAL_CHESTPLATE,
            AVPArmorItems.TACTICAL_LEGGINGS,
            AVPArmorItems.TACTICAL_BOOTS
        ),
        List.of(
            AVPArmorItems.TACTICAL_CAMO_HELMET,
            AVPArmorItems.TACTICAL_CAMO_CHESTPLATE,
            AVPArmorItems.TACTICAL_CAMO_LEGGINGS,
            AVPArmorItems.TACTICAL_CAMO_BOOTS
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

    public static AttributeSupplier.Builder createMarineAttributes() {
        return applyFrom(AVP.config.statsConfigs.MARINE_STATS, Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE));
    }

    private final MarineAnimationDispatcher animationDispatcher;

    private final MarineInventory marineInventory;

    public Marine(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new MarineAnimationDispatcher(this);
        this.marineInventory = new MarineInventory(this);
    }

    @Override
    public @Nullable MarineGOAP createGOAP() {
        return new MarineGOAP(this);
    }

    @Override
    public void runAttackAnimations() {
        animationDispatcher.rightShoot();
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
        marineInventory.addPersonalItem(new ItemStack(AVPItems.GRENADE.get()));

        addInitialArmor();

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
        var randomArmorSetIndex = getRandom().nextInt(DEFAULT_ARMOR_SETS.size());
        var selectedArmor = DEFAULT_ARMOR_SETS.get(randomArmorSetIndex)
            .stream()
            .map(itemSupplier -> new ItemStack(itemSupplier.get()))
            .toArray(ItemStack[]::new);

        for (var i = 0; i < ARMOR_EQUIPMENT_SLOTS.size(); i++) {
            // TODO: Once goap ai is improved, add armor to marine's inventory and let them equip it.
            // marineInventory.addPersonalItem(selectedArmor[i]);
            var itemStack = selectedArmor[i];
            // TODO: This cast isn't necessarily safe.
            var armorItem = (ArmorItem) itemStack.getItem();
            setItemSlot(armorItem.getEquipmentSlot(), itemStack);
        }
    }

    private void addInitialWeapon() {
        marineInventory.addPersonalItem(
            new ItemStack(USABLE_WEAPON_ITEM_SUPPLIERS.get(random.nextInt(USABLE_WEAPON_ITEM_SUPPLIERS.size())).get())
        );
    }
}
