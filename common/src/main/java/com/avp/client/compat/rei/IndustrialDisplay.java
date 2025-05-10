package com.avp.client.compat.rei;

import com.avp.common.recipe.IndustrialFurnaceRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class IndustrialDisplay extends BasicDisplay {

    public IndustrialDisplay(RecipeHolder<IndustrialFurnaceRecipe> recipe) {
        super(
            List.of(EntryIngredients.ofIngredient(recipe.value().getIngredients().getFirst())),
            List.of(EntryIngredient.of(EntryStacks.of(recipe.value().getResultItem(null))))
        );
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return IndustrialCategory.INDUSTRIAL_FURNACE;
    }
}
