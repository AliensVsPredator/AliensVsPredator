package com.human.common.gameplay.entity.living.human.marine;

import com.human.common.gameplay.entity.living.human.AbstractHuman;
import com.human.common.gameplay.entity.living.human.marine.ai.MarineGOAPFactory;
import com.human.common.registry.init.item.HumanGunItems;
import com.just.core.functional.option.Option;
import com.just.goap.GOAP;
import com.lib.common.gameplay.goap.GOAPUser;
import com.lib.common.gameplay.util.ItemUtil;
import com.lib.common.util.codec.schema.CodecSchemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.common.model.inventory.AVPInventory;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPItems;

public class Marine extends AbstractHuman implements GOAPUser<Marine> {

    private static final String NBT_INVENTORY = "inventory";

    @Deprecated
    private static final String NBT_PERSONAL_INVENTORY = "personalInventory";

    @Deprecated
    private static final String NBT_PRIMARY_INVENTORY = "primaryInventory";

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

    private final AVPInventory inventory;

    public Marine(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new MarineAnimationDispatcher(this);
        this.inventory = new AVPInventory(27);
    }

    @Override
    public @Nullable GOAP<Marine> createGOAP() {
        return MarineGOAPFactory.create();
    }

    @Override
    public void runAttackAnimations() {
        animationDispatcher.rightShoot();
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        Arrays.stream(inventory.getSerializedItemStacks())
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
        addInitialWeapon();
        inventory.addItem(AVPItems.GRENADE.get());

        addInitialArmor();

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand interactionHand) {
        var itemStack = player.getItemInHand(interactionHand);

        if (itemStack.getItem() instanceof ArmorItem) {
            if (!level().isClientSide) {
                var item = new ItemStack(itemStack.getItem(), 1);
                item.applyComponents(itemStack.getComponents());
                itemStack.consume(1, player);
                inventory.addItem(item.getItem());
            }

            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        return super.mobInteract(player, interactionHand);
    }

    public AVPInventory getInventory() {
        return inventory;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        if (compoundTag.contains(NBT_PERSONAL_INVENTORY)) {
            var personalInventory = new SimpleContainer(9);
            personalInventory.fromTag(compoundTag.getList(NBT_PERSONAL_INVENTORY, Tag.TAG_COMPOUND), level().registryAccess());
            personalInventory.getItems().forEach(inventory::addItemStack);
        }

        if (compoundTag.contains(NBT_PRIMARY_INVENTORY)) {
            var primaryInventory = new SimpleContainer(27);
            primaryInventory.fromTag(compoundTag.getList(NBT_PRIMARY_INVENTORY, Tag.TAG_COMPOUND), level().registryAccess());
            primaryInventory.getItems().forEach(inventory::addItemStack);
        }

        if (compoundTag.contains(NBT_INVENTORY)) {
            AVPInventory.CODEC.decode(CodecSchemas.NBT, compoundTag.get(NBT_INVENTORY))
                .inspectErr(tag -> AVP.LOGGER.error("Failed to load tag '{}'. Tag: {}", NBT_INVENTORY, tag))
                .ifOk(loadedInventory -> Arrays.stream(loadedInventory.getSerializedItemStacks()).forEach(inventory::addItemStack));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put(NBT_INVENTORY, AVPInventory.CODEC.encode(CodecSchemas.NBT, inventory));
    }

    private void addInitialArmor() {
        var randomArmorSetIndex = getRandom().nextInt(DEFAULT_ARMOR_SETS.size());
        var selectedArmor = DEFAULT_ARMOR_SETS.get(randomArmorSetIndex)
            .stream()
            .map(itemSupplier -> new ItemStack(itemSupplier.get()))
            .toArray(ItemStack[]::new);

        for (var i = 0; i < ARMOR_EQUIPMENT_SLOTS.size(); i++) {
            inventory.addItemStack(selectedArmor[i]);
        }
    }

    private void addInitialWeapon() {
        inventory.addItemStack(
            new ItemStack(USABLE_WEAPON_ITEM_SUPPLIERS.get(random.nextInt(USABLE_WEAPON_ITEM_SUPPLIERS.size())).get())
        );
    }
}
