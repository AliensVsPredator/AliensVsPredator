package org.avp.common.item.armor.material;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record AVPArmorMaterial(
    String registryName,
    int durabilityMultiplier,
    int[] protectionValues,
    int enchantmentValue,
    SoundEvent equipSound,
    Supplier<ItemLike> repairIngredientSupplier,
    float toughness,
    float knockbackResistance
) implements ArmorMaterial {

    private static final int[] BASE_DURABILITY = new int[] { 13, 15, 16, 11 };

    public static final class Builder {

        private final String registryName;

        private int durabilityMultiplier;

        private int[] protectionValues;

        private int enchantmentValue;

        private final SoundEvent equipSound;

        private final Supplier<ItemLike> repairIngredientSupplier;

        private float toughness;

        private float knockbackResistance;

        public Builder(String registryName, SoundEvent equipSound, Supplier<ItemLike> repairIngredientSupplier) {
            this.registryName = registryName;
            this.equipSound = equipSound;
            this.repairIngredientSupplier = repairIngredientSupplier;
        }

        public AVPArmorMaterial.Builder setDurabilityMultiplier(int durabilityMultiplier) {
            this.durabilityMultiplier = durabilityMultiplier;
            return this;
        }

        public AVPArmorMaterial.Builder setProtectionValues(int[] protectionValues) {
            this.protectionValues = protectionValues;
            return this;
        }

        public AVPArmorMaterial.Builder setEnchantmentValue(int enchantmentValue) {
            this.enchantmentValue = enchantmentValue;
            return this;
        }

        public AVPArmorMaterial.Builder setKnockbackResistance(float knockbackResistance) {
            this.knockbackResistance = knockbackResistance;
            return this;
        }

        public AVPArmorMaterial.Builder setToughness(float toughness) {
            this.toughness = toughness;
            return this;
        }

        public AVPArmorMaterial build() {
            return new AVPArmorMaterial(
                registryName,
                durabilityMultiplier,
                protectionValues,
                enchantmentValue,
                equipSound,
                repairIngredientSupplier,
                toughness,
                knockbackResistance
            );
        }
    }

    @Override
    public int getDurabilityForType(@NotNull ArmorItem.Type type) {
        return BASE_DURABILITY[type.getSlot().getIndex()] * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(@NotNull ArmorItem.Type type) {
        return protectionValues[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return Ingredient.of(repairIngredientSupplier.get());
    }

    @Override
    public @NotNull String getName() {
        return registryName;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
