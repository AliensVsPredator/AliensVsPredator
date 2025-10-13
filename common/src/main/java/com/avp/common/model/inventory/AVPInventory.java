package com.avp.common.model.inventory;

import com.just.codec.Codec;
import com.just.codec.schema.CodecSchema;
import com.just.core.functional.result.Result;
import com.lib.common.util.codec.impl.MojangCodecs;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class AVPInventory {

    public static final Codec<AVPInventory> CODEC = new Codec<>() {

        @Override
        public <T> Result<AVPInventory, T> decode(CodecSchema<T> codecSchema, T input) {
            return codecSchema.getList(input).andThen(consumer -> {
                var listBuilder = new ArrayList<ItemStack>();
                var failed = new AtomicBoolean(false);

                consumer.accept(element -> {
                    var result = MojangCodecs.ITEM_STACK.decode(codecSchema, element);
                    if (result.isOk()) {
                        listBuilder.add(result.unwrap());
                    } else {
                        failed.set(true);
                    }
                });

                if (failed.get()) {
                    return Result.err(input);
                }

                return Result.ok(AVPInventory.fromItemStacks(listBuilder.toArray(ItemStack[]::new)));
            });
        }

        @Override
        public <T> T encode(CodecSchema<T> codecSchema, AVPInventory value) {
            return codecSchema.createList(
                Arrays.stream(value.getSerializedItemStacks())
                    .filter(Predicate.not(ItemStack::isEmpty))
                    .map(stack -> MojangCodecs.ITEM_STACK.encode(codecSchema, stack))
            );
        }
    };

    public static AVPInventory fromItemStacks(ItemStack[] stacks) {
        var inventory = new AVPInventory(stacks.length);
        for (int i = 0; i < stacks.length; i++) {
            var stack = stacks[i];
            if (stack != null && !stack.isEmpty()) {
                inventory.setItemStack(inventory.entries[i], stack.copy());
            }
        }
        return inventory;
    }

    private final Entry[] entries;

    private final Set<Entry> emptyEntries;

    private final Map<Item, Set<Entry>> itemToEntriesMap;

    public AVPInventory(int size) {
        this.emptyEntries = new ObjectArraySet<>();
        this.itemToEntriesMap = new HashMap<>();

        this.entries = new Entry[size];

        for (var i = 0; i < size; i++) {
            entries[i] = new Entry(ItemStack.EMPTY, i);
        }

        Collections.addAll(emptyEntries, entries);
    }

    public AddResult addItem(Item item) {
        return addItem(item, 1);
    }

    public AddResult addItem(Item item, int amount) {
        if (amount <= 0) {
            return AddResult.Success.INSTANCE;
        }

        var inserted = 0;
        var maxStackSize = item.getDefaultMaxStackSize();

        while (amount > 0) {
            var toInsert = Math.min(amount, maxStackSize);
            var result = addItemStack(new ItemStack(item, toInsert));

            if (result instanceof AddResult.Success) {
                inserted += toInsert;
                amount -= toInsert;
            } else if (result instanceof AddResult.Partial(var count)) {
                inserted += count;
                return new AddResult.Partial(inserted);
            } else {
                return inserted > 0
                    ? new AddResult.Partial(inserted)
                    : AddResult.InventoryFull.INSTANCE;
            }
        }

        return AddResult.Success.INSTANCE;
    }

    public AddResult addItemStack(ItemStack incomingStack) {
        if (incomingStack == null || incomingStack.isEmpty()) {
            return AddResult.Success.INSTANCE;
        }

        var remaining = incomingStack.getCount();
        var maxStackSize = incomingStack.getMaxStackSize();

        var matchingEntries = itemToEntriesMap.getOrDefault(incomingStack.getItem(), Set.of());

        // Merge with existing compatible stacks
        for (var entry : matchingEntries) {
            var stack = entry.itemStack;

            if (ItemStack.isSameItemSameComponents(stack, incomingStack)) {
                int canAdd = maxStackSize - stack.getCount();

                if (canAdd > 0) {
                    var toAdd = Math.min(remaining, canAdd);
                    stack.grow(toAdd);
                    remaining -= toAdd;

                    if (remaining <= 0) {
                        return AddResult.Success.INSTANCE;
                    }
                }
            }
        }

        var emptyEntries = Set.copyOf(this.emptyEntries);

        // Insert into empty slots
        for (var entry : emptyEntries) {
            var toInsert = Math.min(remaining, maxStackSize);
            setItemStack(entry, incomingStack.copyWithCount(toInsert));
            remaining -= toInsert;

            if (remaining <= 0) {
                return AddResult.Success.INSTANCE;
            }
        }

        var inserted = incomingStack.getCount() - remaining;

        if (inserted > 0) {
            return new AddResult.Partial(inserted);
        } else {
            return AddResult.InventoryFull.INSTANCE;
        }
    }

    public boolean hasItem(Item item) {
        return hasItem(item, 1);
    }

    public boolean hasItem(Item item, int amount) {
        if (amount <= 0) {
            return true;
        }

        var entries = itemToEntriesMap.getOrDefault(item, Set.of());

        for (var entry : entries) {
            var stack = entry.itemStack;

            if (stack.getItem() == item) {
                amount -= stack.getCount();

                if (amount <= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public RemoveResult removeItem(Item item) {
        return removeItem(item, 1);
    }

    public RemoveResult removeItem(Item item, int amount) {
        if (amount <= 0) {
            return RemoveResult.Success.INSTANCE;
        }

        // We create a copy here because entry.setItemStack can mutate the inventory.
        var entries = Set.copyOf(itemToEntriesMap.getOrDefault(item, Set.of()));

        if (entries.isEmpty()) {
            return RemoveResult.InventoryEmpty.INSTANCE;
        }

        var removed = 0;

        for (var entry : entries) {
            var stack = entry.itemStack;

            if (stack.getItem() != item) {
                continue;
            }

            var stackCount = stack.getCount();
            var toRemove = Math.min(stackCount, amount);
            shrinkEntryStack(entry, toRemove);
            removed += toRemove;
            amount -= toRemove;

            if (amount <= 0) {
                return RemoveResult.Success.INSTANCE;
            }
        }

        return removed > 0
            ? new RemoveResult.Partial(removed)
            : RemoveResult.InventoryEmpty.INSTANCE;
    }

    public RemoveResult removeItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return RemoveResult.Success.INSTANCE;
        }

        var remaining = itemStack.getCount();
        var removed = 0;

        var entries = Set.copyOf(itemToEntriesMap.getOrDefault(itemStack.getItem(), Set.of()));

        for (var entry : entries) {
            var stack = entry.itemStack;

            if (ItemStack.isSameItemSameComponents(stack, itemStack)) {
                var toRemove = Math.min(stack.getCount(), remaining);
                shrinkEntryStack(entry, toRemove);
                removed += toRemove;
                remaining -= toRemove;

                if (remaining <= 0) {
                    return RemoveResult.Success.INSTANCE;
                }
            }
        }

        return removed > 0
            ? new RemoveResult.Partial(removed)
            : RemoveResult.InventoryEmpty.INSTANCE;
    }

    public ItemStack removeItemStack(Entry entry) {
        var itemStack = entry.itemStack;
        setItemStack(entry, ItemStack.EMPTY);
        return itemStack;
    }

    public List<Entry> filterEntries(Predicate<Entry> predicate) {
        return Arrays.stream(entries)
            .filter(predicate)
            .toList();
    }

    public List<Entry> filterEntriesByItem(Predicate<Item> predicate) {
        return itemToEntriesMap.entrySet()
            .stream()
            .filter(entry -> predicate.test(entry.getKey()))
            .flatMap(entry -> entry.getValue().stream())
            .toList();
    }

    public List<Entry> filterEntriesByStack(Predicate<ItemStack> predicate) {
        return itemToEntriesMap.values()
            .stream()
            .flatMap(Set::stream)
            .filter(entry -> predicate.test(entry.itemStack))
            .toList();
    }

    public List<Item> filterItems(Predicate<Item> predicate) {
        return itemToEntriesMap.keySet()
            .stream()
            .filter(predicate)
            .toList();
    }

    public Set<Entry> selectEntries(Item item) {
        return itemToEntriesMap.getOrDefault(item, Set.of());
    }

    public void clear() {
        // Clear all item stacks to ItemStack.EMPTY.
        for (var entry : entries) {
            setItemStack(entry, ItemStack.EMPTY);
        }
    }

    public boolean isSlotEmpty(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= entries.length) {
            throw new IndexOutOfBoundsException("Invalid slot index: " + slotIndex);
        }

        return entries[slotIndex].itemStack.isEmpty();
    }

    public Entry getEntryInSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= entries.length) {
            throw new IndexOutOfBoundsException("Invalid slot index: " + slotIndex);
        }

        return entries[slotIndex];
    }

    public int getSize() {
        return entries.length;
    }

    public ItemStack[] getSerializedItemStacks() {
        var result = new ItemStack[entries.length];

        for (var i = 0; i < entries.length; i++) {
            result[i] = entries[i].copyItemStack();
        }

        return result;
    }

    private void shrinkEntryStack(Entry entry, int amount) {
        var stack = entry.itemStack;

        if (stack.isEmpty() || amount <= 0) {
            return;
        }

        // Store the old item before the stack potentially becomes air.
        var oldItem = stack.getItem();

        // Shrink the stack.
        stack.shrink(amount);

        if (stack.isEmpty()) {
            // Remove the entry from underneath its old item.
            evictEntryForItem(entry, oldItem);
            setItemStack(entry, ItemStack.EMPTY);
        }
    }

    private void setItemStack(Entry entry, ItemStack newStack) {
        var currentItemStack = entry.itemStack;

        if (!currentItemStack.isEmpty()) {
            var currentItem = currentItemStack.getItem();
            // Remove entry from underneath current item in map. We do this preemptively just in case the item changes.
            // Even if the item didn't change, the entry gets re-added later on in this method, so no big deal.
            evictEntryForItem(entry, currentItem);
        } else {
            emptyEntries.remove(entry);
        }

        entry.itemStack = newStack;

        if (!newStack.isEmpty()) {
            itemToEntriesMap.computeIfAbsent(newStack.getItem(), k -> new ObjectArraySet<>()).add(entry);
        } else {
            emptyEntries.add(entry);
        }
    }

    private void evictEntryForItem(Entry entry, Item item) {
        itemToEntriesMap.compute(item, ($1, nullableSet) -> {
            if (nullableSet != null) {
                nullableSet.remove(entry);

                if (nullableSet.isEmpty()) {
                    return null;
                }
            }

            return nullableSet;
        });
    }

    public sealed interface AddResult {

        enum Success implements AddResult {
            INSTANCE
        }

        record Partial(int count) implements AddResult {}

        enum InventoryFull implements AddResult {
            INSTANCE
        }
    }

    public sealed interface RemoveResult {

        enum Success implements RemoveResult {
            INSTANCE
        }

        record Partial(int count) implements RemoveResult {}

        enum InventoryEmpty implements RemoveResult {
            INSTANCE
        }
    }

    public class Entry {

        private final int slotIndex;

        private ItemStack itemStack;

        private Entry(ItemStack itemStack, int slotIndex) {
            this.itemStack = itemStack;
            this.slotIndex = slotIndex;
        }

        public <T> @Nullable T get(DataComponentType<T> dataComponentType) {
            return itemStack.get(dataComponentType);
        }

        public AVPInventory getInventory() {
            return AVPInventory.this;
        }

        public Item getItem() {
            return itemStack.getItem();
        }

        public int getItemCount() {
            return itemStack.getCount();
        }

        public int getUseDuration(LivingEntity livingEntity) {
            return itemStack.getUseDuration(livingEntity);
        }

        public int getSlotIndex() {
            return slotIndex;
        }

        @Override
        public String toString() {
            return "Entry[" + slotIndex + "] = " + itemStack;
        }

        public ItemStack copyItemStack() {
            return itemStack.copy();
        }
    }
}
