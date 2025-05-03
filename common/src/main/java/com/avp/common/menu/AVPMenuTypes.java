package com.avp.common.menu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import com.avp.common.menu.armor_case.ArmorCaseMenu;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPMenuTypes {

    public static final AVPDeferredHolder<MenuType<ArmorCaseMenu>> ARMOR_CASE = register("armor_case", ArmorCaseMenu::new);

    public static final AVPDeferredHolder<MenuType<IndustrialFurnaceMenu>> INDUSTRIAL_FURNACE_MENU = register(
        "industrial_furnace_menu",
        IndustrialFurnaceMenu::new
    );

    public static <T extends AbstractContainerMenu> AVPDeferredHolder<MenuType<T>> register(String id, MenuType.MenuSupplier<T> supplier) {
        return Services.REGISTRY.register(BuiltInRegistries.MENU, id, () -> new MenuType<>(supplier, FeatureFlagSet.of()));
    }

    public static void initialize() {}
}
