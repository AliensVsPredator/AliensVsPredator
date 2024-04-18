package org.avp.common.creative_tab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.api.Tuple;
import org.avp.common.AVPConstants;
import org.avp.common.block.*;
import org.avp.common.item.*;
import org.avp.common.registry.AVPDeferredBlockRegistry;
import org.avp.common.registry.AVPDeferredRegistry;
import org.avp.common.service.Services;

public final class AVPCreativeModeTabs extends AVPDeferredRegistry<CreativeModeTab> {

    public static final AVPCreativeModeTabs INSTANCE = new AVPCreativeModeTabs();

    private void createBuilderHolder(String registryName, Supplier<CreativeModeTab.Builder> creativeModeTabBuilderSupplier) {
        var name = String.format("tab.%s.%s", AVPConstants.MOD_ID, registryName);
        createHolder(
            name,
            () -> creativeModeTabBuilderSupplier.get()
                .title(Component.translatable(name))
                .build()
        );
    }

    private static Collection<ItemStack> itemsToItemStacks(Collection<Holder<Item>> holderList) {
        return holderList.stream()
            .map(Holder::get)
            .map(Item::getDefaultInstance)
            .toList();
    }

    private static Collection<ItemStack> blocksToItemStacks(List<Holder<Block>> holderList) {
        return holderList.stream()
            .map(Holder::get)
            .map(Block::asItem)
            .map(Item::getDefaultInstance)
            .toList();
    }

    @Override
    protected Holder<CreativeModeTab> createHolder(String registryName, Supplier<CreativeModeTab> supplier) {
        var holder = Services.CREATIVE_MODE_TAB_SERVICE.createHolder(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    @Override
    public void register() {
        createBuilderHolder(
            "armor",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .icon(AVPArmorItems.INSTANCE.veritaniumHelmet.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPArmorItems.INSTANCE.getEntries())
                    )
                )
        );

        createBuilderHolder(
            "blocks",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                .icon(AVPTempleBlocks.INSTANCE.brick.get().asItem()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        blocksToItemStacks(AVPDeferredBlockRegistry.getDataEntries().stream().map(Tuple::first).toList())
                    )
                )
        );

        createBuilderHolder(
            "food",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 2)
                .icon(AVPFoodItems.INSTANCE.doritos.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPFoodItems.INSTANCE.getEntries())
                    )
                )
        );

        createBuilderHolder(
            "electronics",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 3)
                .icon(AVPElectronicItems.INSTANCE.cpu.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPElectronicItems.INSTANCE.getEntries())
                    )
                )
        );

        createBuilderHolder(
            "entities",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 4)
                .icon(Items.EGG::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPSpawnEggItems.INSTANCE.getEntries())
                    )
                )
        );

        createBuilderHolder(
            "items",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 5)
                .icon(AVPItems.INSTANCE.royalJelly.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPItems.INSTANCE.getEntries())
                    )
                )
        );

        createBuilderHolder(
            "tools",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
                .icon(AVPToolItems.INSTANCE.veritaniumPickaxe.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPToolItems.INSTANCE.getEntries())
                    )
                )
        );

        createBuilderHolder(
            "weapons",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 1)
                .icon(AVPWeaponItems.INSTANCE.grenadeM40.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> {
                        output.acceptAll(
                            itemsToItemStacks(AVPAmmunitionPartItems.INSTANCE.getEntries())
                        );
                        output.acceptAll(
                            itemsToItemStacks(AVPWeaponBlueprintItems.INSTANCE.getEntries())
                        );
                        output.acceptAll(
                            itemsToItemStacks(AVPBulletItems.INSTANCE.getEntries())
                        );
                        output.acceptAll(
                            itemsToItemStacks(AVPWeaponItems.INSTANCE.getEntries())
                        );
                        output.acceptAll(
                            itemsToItemStacks(AVPWeaponPartItems.INSTANCE.getEntries())
                        );
                    }
                )
        );

        // Register all the holders we just created.
        entries.forEach(Services.CREATIVE_MODE_TAB_SERVICE::register);
    }

    private AVPCreativeModeTabs() {}
}
