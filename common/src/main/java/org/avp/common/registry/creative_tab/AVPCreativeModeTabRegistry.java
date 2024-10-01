package org.avp.common.registry.creative_tab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.registry.AVPDeferredRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.Services;
import org.avp.common.AVPConstants;
import org.avp.common.data.block.TempleBlockDataContainer;
import org.avp.common.registry.block.AVPBlockDataRegistry;
import org.avp.common.registry.item.AVPAmmunitionPartItemRegistry;
import org.avp.common.registry.item.AVPArmorItemRegistry;
import org.avp.common.registry.item.AVPBulletItemRegistry;
import org.avp.common.registry.item.AVPElectronicItemRegistry;
import org.avp.common.registry.item.AVPFoodItemRegistry;
import org.avp.common.registry.item.AVPItemRegistry;
import org.avp.common.registry.item.AVPSpawnEggItemRegistry;
import org.avp.common.registry.item.AVPToolItemRegistry;
import org.avp.common.registry.item.AVPWeaponBlueprintItemRegistry;
import org.avp.common.registry.item.AVPWeaponItemRegistry;
import org.avp.common.registry.item.AVPWeaponPartItemRegistry;

public final class AVPCreativeModeTabRegistry extends AVPDeferredRegistry<CreativeModeTab> {

    public static final AVPCreativeModeTabRegistry INSTANCE = new AVPCreativeModeTabRegistry();

    private void createBuilderHolder(String registryName, Supplier<CreativeModeTab.Builder> creativeModeTabBuilderSupplier) {
        var name = String.format("tab.%s.%s", AVPConstants.MOD_ID, registryName);
        createHolder(
            name,
            () -> creativeModeTabBuilderSupplier.get()
                .title(Component.translatable(name))
                .build()
        );
    }

    private static Collection<ItemStack> itemsToItemStacks(Collection<BLHolder<Item>> holderList) {
        return holderList.stream()
            .map(BLHolder::get)
            .map(Item::getDefaultInstance)
            .toList();
    }

    private static Collection<ItemStack> blocksToItemStacks(List<BLHolder<Block>> holderList) {
        return holderList.stream()
            .map(BLHolder::get)
            .map(Block::asItem)
            .map(Item::getDefaultInstance)
            .toList();
    }

    @Override
    protected BLHolder<CreativeModeTab> createHolder(String registryName, Supplier<CreativeModeTab> supplier) {
        var holder = Services.CREATIVE_MODE_TAB_SERVICE.createHolder(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    @Override
    public void register() {
        createBuilderHolder(
            "armor",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .icon(AVPArmorItemRegistry.INSTANCE.veritanium.helmet().get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPArmorItemRegistry.INSTANCE.getValues())
                    )
                )
        );

        createBuilderHolder(
            "blocks",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                .icon(TempleBlockDataContainer.INSTANCE.base.getHolder().get().asItem()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> {
                        output.acceptAll(
                            blocksToItemStacks(
                                AVPBlockDataRegistry.INSTANCE.getEntries().stream().map(SingleBlockDataContainer.Holder::getHolder).toList()
                            )
                        );
                    }
                )
        );

        createBuilderHolder(
            "food",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 2)
                .icon(AVPFoodItemRegistry.INSTANCE.doritos.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPFoodItemRegistry.INSTANCE.getValues())
                    )
                )
        );

        createBuilderHolder(
            "electronics",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 3)
                .icon(AVPElectronicItemRegistry.INSTANCE.cpu.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPElectronicItemRegistry.INSTANCE.getValues())
                    )
                )
        );

        createBuilderHolder(
            "entities",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 4)
                .icon(Items.EGG::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPSpawnEggItemRegistry.INSTANCE.getValues())
                    )
                )
        );

        createBuilderHolder(
            "items",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 5)
                .icon(AVPItemRegistry.INSTANCE.royalJelly.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPItemRegistry.INSTANCE.getValues())
                    )
                )
        );

        createBuilderHolder(
            "tools",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
                .icon(AVPToolItemRegistry.INSTANCE.veritaniumPickaxe.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> output.acceptAll(
                        itemsToItemStacks(AVPToolItemRegistry.INSTANCE.getValues())
                    )
                )
        );

        createBuilderHolder(
            "weapons",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 1)
                .icon(AVPWeaponItemRegistry.INSTANCE.grenadeM40.get()::getDefaultInstance)
                .displayItems(
                    (itemDisplayParameters, output) -> {
                        output.acceptAll(
                            itemsToItemStacks(AVPAmmunitionPartItemRegistry.INSTANCE.getValues())
                        );
                        output.acceptAll(
                            itemsToItemStacks(AVPWeaponBlueprintItemRegistry.INSTANCE.getValues())
                        );
                        output.acceptAll(
                            itemsToItemStacks(AVPBulletItemRegistry.INSTANCE.getValues())
                        );
                        output.acceptAll(
                            itemsToItemStacks(AVPWeaponItemRegistry.INSTANCE.getValues())
                        );
                        output.acceptAll(
                            itemsToItemStacks(AVPWeaponPartItemRegistry.INSTANCE.getValues())
                        );
                    }
                )
        );

        // Register all the holders we just created.
        getValues().forEach(Services.CREATIVE_MODE_TAB_SERVICE::register);
    }

    private AVPCreativeModeTabRegistry() {}
}
