package com.lib.common.gameplay.entity.manager;

import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.GeneModifierKey;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.GeneRegistry;
import com.lib.common.util.GeneDataUtil;
import com.lib.common.util.GeneSerializationUtil;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Collections;
import java.util.Map;

import com.avp.common.network.packet.S2CSyncGenesPayload;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class GeneManager implements NBTSerializable {

    private static final String NBT_GENE_MAP = "geneMap";

    private static final String NBT_DORMANT_GENE_MAP = "dormantGeneMap";

    private static final String NBT_GENE_MODIFIERS = "geneModifiers";

    private static final String NBT_DORMANT_GENE_MODIFIERS = "dormantGeneModifiers";

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

            // TODO: This doesn't handle genes that go missing from the map. Remove all modifiers then re-apply them.
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

            syncToClient();

            this.isDirty = false;
        }
    }

    public void syncToClient() {
        var server = ((ServerLevel) entity.level()).getServer();
        var payload = new S2CSyncGenesPayload(entity.getId(), GeneDataUtil.toList(geneMap));
        Services.SERVER_NETWORKING.sendToAllClients(server, payload);
    }

    public void transfer(GeneManager other, boolean activateDormantGenes) {
        other.putActiveGenes(getActiveGenes());

        if (activateDormantGenes) {
            getDormantGenes().forEach(other::addActiveGene);
        } else {
            other.putDormantGenes(getDormantGenes());
        }
    }

    public void clearActiveGenes() {
        geneMap.clear();
    }

    public boolean hasGene(AVPDeferredHolder<Gene> geneHolder) {
        var geneResourceLocation = geneHolder.get().id();
        return geneMap.containsKey(new GeneModifierKey(geneResourceLocation, GeneOperationType.ADDITIVE))
            || geneMap.containsKey(new GeneModifierKey(geneResourceLocation, GeneOperationType.MULTIPLICATIVE));
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

    public void addActiveGene(AVPDeferredHolder<Gene> geneHolder, GeneOperationType operation, double value) {
        addActiveGene(geneHolder.get().id(), operation, value);
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
        return geneMap.getOrDefault(new GeneModifierKey(geneHolder.get().id(), operation), 0.0);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        geneMap.clear();
        dormantGeneMap.clear();

        // TODO: Remove these two lines prior to release of 0.2.0.
        GeneSerializationUtil.loadGeneMap(NBT_GENE_MAP, compoundTag, geneMap);
        GeneSerializationUtil.loadGeneMap(NBT_DORMANT_GENE_MAP, compoundTag, geneMap);

        GeneSerializationUtil.loadGeneModifiers(NBT_GENE_MODIFIERS, compoundTag, geneMap);
        GeneSerializationUtil.loadGeneModifiers(NBT_DORMANT_GENE_MODIFIERS, compoundTag, dormantGeneMap);

        this.isDirty = true;
    }

    @Override
    public void save(CompoundTag compoundTag) {
        GeneSerializationUtil.saveGeneModifiers(NBT_GENE_MODIFIERS, compoundTag, geneMap);
        GeneSerializationUtil.saveGeneModifiers(NBT_DORMANT_GENE_MODIFIERS, compoundTag, dormantGeneMap);
    }
}
