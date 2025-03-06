package com.avp.core.common.menu;

import com.avp.core.platform.service.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import com.avp.core.common.menu.armor_case.ArmorCaseMenu;

import java.util.function.Supplier;

public class MenuTypes {

    public static final Supplier<MenuType<ArmorCaseMenu>> ARMOR_CASE = register(ArmorCaseMenu::new, "armor_case");

    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(MenuType.MenuSupplier<T> supplier, String id) {
        return Services.REGISTRY.registerScreen(id, () -> new MenuType<>(supplier, FeatureFlagSet.of()));
    }

    public static void initialize() {}
}
