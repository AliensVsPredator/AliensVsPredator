package org.avp.common.item.armor.material;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.avp.api.Holder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public record AVPArmorMaterial(
    String registryName,
    int durabilityMultiplier,
    int[] protectionValues,
    int enchantmentValue,
    Holder<SoundEvent> equipSoundHolder,
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

        private final Holder<SoundEvent> equipSoundHolder;

        private final Supplier<ItemLike> repairIngredientSupplier;

        private float toughness;

        private float knockbackResistance;

        public Builder(String registryName, Holder<SoundEvent> equipSoundHolder, Supplier<ItemLike> repairIngredientSupplier) {
            this.registryName = registryName;
            this.equipSoundHolder = equipSoundHolder;
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
                equipSoundHolder,
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

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        AVPArmorMaterial that = (AVPArmorMaterial) object;
        return durabilityMultiplier == that.durabilityMultiplier && enchantmentValue == that.enchantmentValue && Float.compare(
            toughness,
            that.toughness
        ) == 0 && Float.compare(knockbackResistance, that.knockbackResistance) == 0 && Objects.equals(registryName, that.registryName)
            && Arrays.equals(protectionValues, that.protectionValues) && Objects.equals(equipSoundHolder, that.equipSoundHolder) && Objects.equals(
                repairIngredientSupplier,
                that.repairIngredientSupplier
            );
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(
            registryName,
            durabilityMultiplier,
            enchantmentValue,
            equipSoundHolder,
            repairIngredientSupplier,
            toughness,
            knockbackResistance
        );
        result = 31 * result + Arrays.hashCode(protectionValues);
        return result;
    }

    @Override
    public String toString() {
        return "AVPArmorMaterial{" +
            "registryName='" + registryName + '\'' +
            ", durabilityMultiplier=" + durabilityMultiplier +
            ", protectionValues=" + Arrays.toString(protectionValues) +
            ", enchantmentValue=" + enchantmentValue +
            ", equipSoundHolder=" + equipSoundHolder +
            ", repairIngredientSupplier=" + repairIngredientSupplier +
            ", toughness=" + toughness +
            ", knockbackResistance=" + knockbackResistance +
            '}';
    }
}
