package com.avp.fabric.client.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.item.TempAVPBlockItems;

public class IndustrialCategory implements DisplayCategory<IndustrialDisplay> {

    public static final ResourceLocation TEXTURE = AVPResources.location("textures/gui/container/industrial_furnace_gui.png");

    public static final CategoryIdentifier<IndustrialDisplay> INDUSTRIAL_FURNACE =
        CategoryIdentifier.of(AVP.MOD_ID, "industrial_furnace_gui");

    @Override
    public CategoryIdentifier<? extends IndustrialDisplay> getCategoryIdentifier() {
        return INDUSTRIAL_FURNACE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.avp.industrial_furnace_block");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(TempAVPBlockItems.INDUSTRIAL_FURNACE_BLOCK.get().getDefaultInstance());
    }

    @Override
    public List<Widget> setupDisplay(IndustrialDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        var centerX = bounds.getCenterX();
        var centerY = bounds.getCenterY();
        var inputX = centerX - 28;
        var outputX = centerX + 28;
        var slotY = centerY - 9;

        // Input slot
        widgets.add(
            Widgets.createSlot(new Point(inputX, slotY))
                .entries(display.getInputEntries().getFirst())
                .markInput()
        );

        // Output slot
        widgets.add(
            Widgets.createSlot(new Point(outputX, slotY))
                .entries(display.getOutputEntries().getFirst())
                .markOutput()
        );

        // Progress arrow
        widgets.add(
            Widgets.createArrow(new Point(centerX - 9, slotY))
                .animationDurationTicks(40)
        );

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }
}
