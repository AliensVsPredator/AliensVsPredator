package com.avp.core.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.avp.core.AVPResources;
import com.avp.core.common.menu.armor_case.ArmorCaseMenu;

public class ArmorCaseScreen extends AbstractContainerScreen<ArmorCaseMenu> {

    private static final ResourceLocation TEXTURE = AVPResources.location("textures/gui/container/armor_case.png");

    public ArmorCaseScreen(ArmorCaseMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        this.renderTooltip(guiGraphics, i, j);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        int k = (width - imageWidth) / 2;
        int l = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, k, l, 0, 0, imageWidth, imageHeight);
    }
}
