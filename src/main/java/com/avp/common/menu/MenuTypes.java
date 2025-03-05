package com.avp.common.menu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import com.avp.AVPResources;
import com.avp.common.menu.armor_case.ArmorCaseMenu;

public class MenuTypes {

    public static final MenuType<ArmorCaseMenu> ARMOR_CASE = register(ArmorCaseMenu::new, "armor_case");

    public static <T extends AbstractContainerMenu> MenuType<T> register(MenuType.MenuSupplier<T> supplier, String id) {
        var resourceLocation = AVPResources.location(id);
        var menuType = new MenuType<>(supplier, FeatureFlagSet.of());
        return Registry.register(BuiltInRegistries.MENU, resourceLocation, menuType);
    }

    public static void initialize() {}
}
