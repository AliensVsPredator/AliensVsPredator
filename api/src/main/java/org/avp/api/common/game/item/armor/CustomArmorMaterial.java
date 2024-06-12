package org.avp.api.common.game.item.armor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.avp.api.common.registry.holder.BLHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public record CustomArmorMaterial(
    String registryName,
    int durabilityMultiplier,
    Map<ArmorItem.Type, Integer> protectionValues,
    int enchantmentValue,
    BLHolder<SoundEvent> equipSoundHolder,
    Supplier<ItemLike> repairIngredientSupplier,
    float toughness,
    float knockbackResistance
) implements ArmorMaterial {

    private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = Map.ofEntries(
        Map.entry(ArmorItem.Type.HELMET, 13),
        Map.entry(ArmorItem.Type.CHESTPLATE, 16),
        Map.entry(ArmorItem.Type.LEGGINGS, 15),
        Map.entry(ArmorItem.Type.BOOTS,11)
    );

    public static final class Builder {

        private final String registryName;

        private int durabilityMultiplier;

        private Map<ArmorItem.Type, Integer> protectionValues;

        private int enchantmentValue;

        private final BLHolder<SoundEvent> equipSoundHolder;

        private final Supplier<ItemLike> repairIngredientSupplier;

        private float toughness;

        private float knockbackResistance;

        public Builder(String registryName, BLHolder<SoundEvent> equipSoundHolder, Supplier<ItemLike> repairIngredientSupplier) {
            this.registryName = registryName;
            this.equipSoundHolder = equipSoundHolder;
            this.repairIngredientSupplier = repairIngredientSupplier;
        }

        public CustomArmorMaterial.Builder setDurabilityMultiplier(int durabilityMultiplier) {
            this.durabilityMultiplier = durabilityMultiplier;
            return this;
        }

        public CustomArmorMaterial.Builder setProtectionValues(Map<ArmorItem.Type, Integer> protectionValues) {
            var helmet = Objects.requireNonNull(protectionValues.get(ArmorItem.Type.HELMET));
            var chestplate = Objects.requireNonNull(protectionValues.get(ArmorItem.Type.CHESTPLATE));
            var leggings = Objects.requireNonNull(protectionValues.get(ArmorItem.Type.LEGGINGS));
            var boots = Objects.requireNonNull(protectionValues.get(ArmorItem.Type.BOOTS));

            if (
                // Helmets, boots and leggings must not provide more armor value than a chestplate.
                helmet > chestplate ||
                boots > chestplate ||
                leggings > chestplate ||
                // Helmets and boots must not provide more armor value than leggings.
                helmet > leggings ||
                boots > leggings
            ) {
                throw new IllegalArgumentException("Invalid armor value(s)! Helmet: " + helmet + ", Chestplate: " + chestplate + ", Leggings: " + leggings + ", Boots: " + boots);
            }

            this.protectionValues = protectionValues;
            return this;
        }

        public CustomArmorMaterial.Builder setEnchantmentValue(int enchantmentValue) {
            this.enchantmentValue = enchantmentValue;
            return this;
        }

        public CustomArmorMaterial.Builder setKnockbackResistance(float knockbackResistance) {
            this.knockbackResistance = knockbackResistance;
            return this;
        }

        public CustomArmorMaterial.Builder setToughness(float toughness) {
            this.toughness = toughness;
            return this;
        }

        public CustomArmorMaterial build() {
            return new CustomArmorMaterial(
                registryName,
                durabilityMultiplier,
                protectionValues,
                enchantmentValue,
                equipSoundHolder,
                repairIngredientSupplier,
                toughness,
                knockbackResistance
            );
        }
    }

    @Override
    public int getDurabilityForType(@NotNull ArmorItem.Type type) {
        return BASE_DURABILITY.get(type) * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(@NotNull ArmorItem.Type type) {
        return protectionValues.get(type);
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return equipSoundHolder.get();
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
