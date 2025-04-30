package com.avp.fabric.client.screen;

import com.avp.AVP;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.avp.fabric.common.menu.IndustrialFurnaceMenu;
import com.avp.fabric.common.menu.IndustrialFurnaceRecipeBookComponent;

public class IndustrialFurnaceScreen extends AbstractFurnaceScreen<IndustrialFurnaceMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        AVP.MOD_ID,
        "textures/gui/container/industrial_furnace_gui.png"
    );

    private static final ResourceLocation LIT_PROGRESS_TEXTURE = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");

    private static final ResourceLocation BURN_PROGRESS_TEXTURE = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    public IndustrialFurnaceScreen(IndustrialFurnaceMenu handler, Inventory inventory, Component title) {
        super(handler, new IndustrialFurnaceRecipeBookComponent(), inventory, title, TEXTURE, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE);
    }
}
