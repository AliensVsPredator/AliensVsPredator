package com.avp.common.entity.living.human.marine;

import com.bvanseg.just.functional.option.Option;
import com.xlib.NBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.avp.common.util.AVPInventory;
import com.avp.common.util.ItemUtil;

public class MarineInventory implements AVPInventory, InventoryCarrier, NBTSerializable {

    private static final String PERSONAL_INVENTORY_KEY = "personalInventory";

    private static final String PRIMARY_INVENTORY_KEY = "primaryInventory";

    private final Marine marine;

    // This inventory is for items the marine can use that should not be dropped (i.e. items the marine spawns with).
    private final SimpleContainer personalInventory;

    // This inventory is for items the marine can pick up. Its contents are dropped on death.
    private final SimpleContainer primaryInventory;

    public MarineInventory(Marine marine) {
        this.marine = marine;
        this.personalInventory = new SimpleContainer(9);
        this.primaryInventory = new SimpleContainer(27);
    }

    @Override
    public @NotNull SimpleContainer getInventory() {
        return primaryInventory;
    }

    @Override
    public boolean canAddItem(ItemStack itemStack) {
        return primaryInventory.canAddItem(itemStack);
    }

    @Override
    public void forEach(Consumer<ItemStack> consumer) {
        personalInventory.getItems().forEach(consumer);
        primaryInventory.getItems().forEach(consumer);
    }

    @Override
    public void pickUpItem(ItemEntity itemEntity) {
        InventoryCarrier.pickUpItem(marine, this, itemEntity);
    }

    @Override
    public void removeItemType(Item item, int amount) {
        primaryInventory.removeItemType(item, amount);
    }

    @Override
    public Stream<ItemStack> stream() {
        return Stream.concat(personalInventory.getItems().stream(), primaryInventory.getItems().stream());
    }

    // Returns the remaining amount of items that weren't added to the marine's inventory.
    public ItemStack addPersonalItem(ItemStack itemStack) {
        return personalInventory.addItem(itemStack);
    }

    public void dropItems() {
        primaryInventory.removeAllItems()
            .stream()
            .map(itemStack -> ItemUtil.drop(marine, itemStack, true, false))
            .flatMap(Option::toStream)
            .forEach(itemEntity -> marine.level().addFreshEntity(itemEntity));
    }

    @Override
    public void load(CompoundTag compoundTag) {
        var level = marine.level();
        personalInventory.fromTag(compoundTag.getList(PERSONAL_INVENTORY_KEY, Tag.TAG_COMPOUND), level.registryAccess());
        primaryInventory.fromTag(compoundTag.getList(PRIMARY_INVENTORY_KEY, Tag.TAG_COMPOUND), level.registryAccess());
    }

    @Override
    public void save(CompoundTag compoundTag) {
        var level = marine.level();
        compoundTag.put(PERSONAL_INVENTORY_KEY, personalInventory.createTag(level.registryAccess()));
        compoundTag.put(PRIMARY_INVENTORY_KEY, primaryInventory.createTag(level.registryAccess()));
    }
}
