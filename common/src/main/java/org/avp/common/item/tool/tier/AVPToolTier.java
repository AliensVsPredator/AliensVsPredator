package org.avp.common.item.tool.tier;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.avp.api.GameObject;
import org.jetbrains.annotations.NotNull;

public record AVPToolTier(
    int uses,
    float speed,
    float attackDamageBonus,
    int harvestLevel,
    int enchantmentValue,
    GameObject<Item> repairItemGameObject
) implements Tier {
    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public int getLevel() {
        return harvestLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return Ingredient.of(repairItemGameObject.get());
    }
}
