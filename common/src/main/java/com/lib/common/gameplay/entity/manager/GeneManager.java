package com.lib.common.gameplay.entity.manager;

import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.GeneModifierKey;
import com.lib.common.gameplay.gene.GeneRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class GeneManager implements NBTSerializable {

    private final LivingEntity entity;

    private final GeneContainer geneContainer;

    public GeneManager(LivingEntity entity) {
        this.entity = entity;
        this.geneContainer = new GeneContainer();
    }

    public void tick() {
        var activeGeneMap = geneContainer.getActiveGeneMap();

        if (entity.level().isClientSide || !activeGeneMap.isDirty()) {
            return;
        }

        // Do NOT move these to after the gene effects being applied!
        var oldMaxHealth = entity.getMaxHealth();
        var wasFullHealth = entity.getHealth() == oldMaxHealth;

        activeGeneMap.getDirtyKeys()
            .forEach(this::applyGeneEffects);

        var maxHealthAttributeInstance = entity.getAttribute(Attributes.MAX_HEALTH);

        if (maxHealthAttributeInstance != null) {
            var newMax = maxHealthAttributeInstance.getValue();
            var currentHealth = entity.getHealth();

            if (wasFullHealth || currentHealth > newMax) {
                entity.setHealth((float) newMax);
            }
        }

        activeGeneMap.clearDirtyKeys();
    }

    private void applyGeneEffects(GeneModifierKey dirtyGeneModifierKey) {
        var activeGeneMap = geneContainer.getActiveGeneMap();
        var id = dirtyGeneModifierKey.resourceLocation();
        var gene = GeneRegistry.getValueOrNull(id);

        if (gene == null) {
            return;
        }

        switch (gene) {
            case Gene.Attribute attribute -> {
                var attributeInstance = entity.getAttribute(attribute.attributeHolder());

                if (attributeInstance != null) {
                    var hasGeneModifier = activeGeneMap.hasGeneModifier(dirtyGeneModifierKey);

                    if (hasGeneModifier) {
                        applyGeneAttributeBonus(dirtyGeneModifierKey, attributeInstance);
                    } else {
                        // Container no longer has the gene modifier, so remove the attribute modifier.
                        attributeInstance.removeModifier(id);
                    }
                }
            }
            case Gene.Simple ignored -> { /* NO-OP */ }
        }
    }

    private void applyGeneAttributeBonus(GeneModifierKey geneModifierKey, AttributeInstance attributeInstance) {
        var id = geneModifierKey.resourceLocation();
        var gene = GeneRegistry.getValueOrNull(id);
        var bonusValue = geneContainer.getActiveGeneMap().getValue(gene, geneModifierKey.operation());
        var finalBonusValue = switch (geneModifierKey.operation()) {
            case ADDITIVE -> bonusValue;
            case MULTIPLICATIVE -> attributeInstance.getBaseValue() * bonusValue;
        };
        var modifier = new AttributeModifier(id, finalBonusValue, AttributeModifier.Operation.ADD_VALUE);

        attributeInstance.addOrReplacePermanentModifier(modifier);
    }

    public GeneContainer getGeneContainer() {
        return geneContainer;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        geneContainer.load(compoundTag);
    }

    @Override
    public void save(CompoundTag compoundTag) {
        geneContainer.save(compoundTag);
    }
}
