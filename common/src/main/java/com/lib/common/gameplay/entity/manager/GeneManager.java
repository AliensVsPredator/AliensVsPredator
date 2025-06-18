package com.lib.common.gameplay.entity.manager;

import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.GeneModifier;
import com.lib.common.gameplay.gene.GeneModifierKey;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.GeneRegistry;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Collections;
import java.util.Map;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.registry.AVPDeferredHolder;

public class GeneManager implements NBTSerializable {

    private static final String NBT_GENE_MAP = "geneMap";

    private static final String NBT_DORMANT_GENE_MAP = "dormantGeneMap";

    private final LivingEntity entity;

    private final Map<GeneModifierKey, Double> geneMap;

    private final Map<GeneModifierKey, Double> dormantGeneMap;

    private boolean isDirty;

    public GeneManager(LivingEntity entity) {
        this.entity = entity;
        this.geneMap = new Object2DoubleArrayMap<>();
        this.dormantGeneMap = new Object2DoubleArrayMap<>();
        this.isDirty = true;
    }

    public void tick() {
        if (entity.level().isClientSide) {
            return;
        }

        if (isDirty) {
            var oldMaxHealth = entity.getMaxHealth();
            var wasFullHealth = entity.getHealth() == oldMaxHealth;

            geneMap.forEach(((geneModifierKey, bonusValue) -> {
                var id = geneModifierKey.resourceLocation();
                var gene = GeneRegistry.getValueOrNull(id);

                if (gene != null) {
                    switch (gene) {
                        case Gene.Attribute attribute -> {
                            var attributeInstance = entity.getAttribute(attribute.attributeHolder());

                            if (attributeInstance != null) {
                                var finalBonusValue = switch (geneModifierKey.operation()) {
                                    case ADDITIVE -> bonusValue;
                                    case MULTIPLICATIVE -> attributeInstance.getBaseValue() * bonusValue;
                                };

                                var modifier = new AttributeModifier(id, finalBonusValue, AttributeModifier.Operation.ADD_VALUE);

                                attributeInstance.addOrReplacePermanentModifier(modifier);
                            }
                        }
                        case Gene.Simple ignored -> { /* NO-OP */ }
                    }
                }
            }));

            var maxHealthAttributeInstance = entity.getAttribute(Attributes.MAX_HEALTH);

            if (maxHealthAttributeInstance != null) {
                var newMax = maxHealthAttributeInstance.getValue();
                var currentHealth = entity.getHealth();

                if (wasFullHealth || currentHealth > newMax) {
                    entity.setHealth((float) newMax);
                }
            }

            this.isDirty = false;
        }
    }

    public void transfer(GeneManager other, boolean activateDormantGenes) {
        other.putActiveGenes(getActiveGenes());

        if (activateDormantGenes) {
            getDormantGenes().forEach(other::addActiveGene);
        } else {
            other.putDormantGenes(getDormantGenes());
        }
    }

    public double getActiveGeneValue(AVPDeferredHolder<Gene> geneHolder, GeneOperationType operation) {
        return getGeneFromMap(geneHolder, operation, geneMap);
    }

    public double getDormantGeneValue(AVPDeferredHolder<Gene> geneHolder, GeneOperationType operation) {
        return getGeneFromMap(geneHolder, operation, dormantGeneMap);
    }

    public Map<GeneModifierKey, Double> getActiveGenes() {
        return Collections.unmodifiableMap(geneMap);
    }

    public Map<GeneModifierKey, Double> getDormantGenes() {
        return Collections.unmodifiableMap(dormantGeneMap);
    }

    public void putActiveGenes(Map<GeneModifierKey, Double> geneMap) {
        this.geneMap.putAll(geneMap);

        this.isDirty = true;
    }

    public void putDormantGenes(Map<GeneModifierKey, Double> geneMap) {
        this.dormantGeneMap.putAll(geneMap);
    }

    public void addActiveGene(ResourceLocation resourceLocation, GeneOperationType operation, double value) {
        addActiveGene(new GeneModifierKey(resourceLocation, operation), value);
    }

    public void addActiveGene(GeneModifierKey geneModifierKey, double value) {
        addGeneToMap(geneModifierKey, value, geneMap, true);
    }

    public void addDormantGene(ResourceLocation resourceLocation, GeneOperationType operation, double value) {
        addDormantGene(new GeneModifierKey(resourceLocation, operation), value);
    }

    public void addDormantGene(GeneModifierKey geneModifierKey, double value) {
        addGeneToMap(geneModifierKey, value, dormantGeneMap, false);
    }

    private void addGeneToMap(GeneModifierKey geneModifierKey, double value, Map<GeneModifierKey, Double> geneMap, boolean markDirty) {
        geneMap.compute(geneModifierKey, ($, oldValue) -> {
            var oldValueNotNull = oldValue == null ? 0 : oldValue;
            return oldValueNotNull + value;
        });

        if (markDirty) {
            this.isDirty = true;
        }
    }

    private Double getGeneFromMap(AVPDeferredHolder<Gene> geneHolder, GeneOperationType operation, Map<GeneModifierKey, Double> geneMap) {
        var resourceLocation = AVPResources.location(geneHolder.get().id());
        return geneMap.getOrDefault(new GeneModifierKey(resourceLocation, operation), 0.0);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        geneMap.clear();
        dormantGeneMap.clear();

        loadGeneMap(NBT_GENE_MAP, compoundTag, geneMap);
        loadGeneMap(NBT_DORMANT_GENE_MAP, compoundTag, dormantGeneMap);

        this.isDirty = true;
    }

    @Override
    public void save(CompoundTag compoundTag) {
        saveGeneMap(NBT_GENE_MAP, compoundTag, geneMap);
        saveGeneMap(NBT_DORMANT_GENE_MAP, compoundTag, dormantGeneMap);
    }

    private static void loadGeneMap(String geneMapKey, CompoundTag compoundTag, Map<GeneModifierKey, Double> geneMap) {
        if (compoundTag.contains(geneMapKey, CompoundTag.TAG_COMPOUND)) {
            var geneMapTag = compoundTag.getCompound(geneMapKey);

            for (var key : geneMapTag.getAllKeys()) {
                try {
                    var id = ResourceLocation.parse(key);

                    GeneModifier.CODEC.parse(
                        new Dynamic<>(NbtOps.INSTANCE, geneMapTag.getCompound(key))
                    )
                        .resultOrPartial(
                            AVP.LOGGER::error
                        )
                        .ifPresent(geneModifier -> geneMap.put(new GeneModifierKey(id, geneModifier.operation()), geneModifier.value()));
                } catch (Exception e) {
                    e.printStackTrace();
                    // Log or handle malformed resource locations
                }
            }
        }
    }

    private static void saveGeneMap(String geneMapKey, CompoundTag compoundTag, Map<GeneModifierKey, Double> geneMap) {
        var geneMapTag = new CompoundTag();

        for (var entry : geneMap.entrySet()) {
            var geneModifier = new GeneModifier(entry.getKey().operation(), entry.getValue());
            GeneModifier.CODEC.encodeStart(NbtOps.INSTANCE, geneModifier)
                .resultOrPartial(
                    AVP.LOGGER::error
                )
                .ifPresent(tag -> geneMapTag.put(entry.getKey().resourceLocation().toString(), tag));
        }

        compoundTag.put(geneMapKey, geneMapTag);
    }
}
