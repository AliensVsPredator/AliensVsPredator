package com.lib.common.gameplay.entity.manager;

import com.lib.common.gameplay.gene.Gene;
import com.lib.common.gameplay.gene.GeneModifierKey;
import com.lib.common.gameplay.gene.GeneOperationType;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.avp.common.registry.AVPDeferredHolder;

public class GeneMap {

    private final Set<GeneModifierKey> dirtyKeys;

    private final Map<GeneModifierKey, Double> geneMap;

    public GeneMap() {
        this.dirtyKeys = new HashSet<>();
        this.geneMap = new Object2DoubleArrayMap<>();
    }

    public boolean hasGene(AVPDeferredHolder<Gene> geneHolder) {
        return hasGene(geneHolder.get());
    }

    public boolean hasGene(Gene gene) {
        var geneResourceLocation = gene.id();
        return geneMap.containsKey(new GeneModifierKey(geneResourceLocation, GeneOperationType.ADDITIVE))
            || geneMap.containsKey(new GeneModifierKey(geneResourceLocation, GeneOperationType.MULTIPLICATIVE));
    }

    public boolean hasGeneModifier(GeneModifierKey geneModifierKey) {
        return geneMap.containsKey(geneModifierKey);
    }

    public double getValue(Gene gene, GeneOperationType operation) {
        return getValueFromMap(gene, operation, geneMap);
    }

    public double getValue(AVPDeferredHolder<Gene> geneHolder, GeneOperationType operation) {
        return getValueFromMap(geneHolder, operation, geneMap);
    }

    public Map<GeneModifierKey, Double> getBackingMap() {
        return Collections.unmodifiableMap(geneMap);
    }

    public void putAll(Map<GeneModifierKey, Double> geneMap) {
        this.geneMap.putAll(geneMap);
        dirtyKeys.addAll(geneMap.keySet());
    }

    public void add(AVPDeferredHolder<Gene> geneHolder, GeneOperationType operation, double value) {
        add(geneHolder.get().id(), operation, value);
    }

    public void add(ResourceLocation resourceLocation, GeneOperationType operation, double value) {
        add(new GeneModifierKey(resourceLocation, operation), value);
    }

    public void add(GeneModifierKey geneModifierKey, double value) {
        addOrInsert(geneModifierKey, value, geneMap);
    }

    private void addOrInsert(
        GeneModifierKey geneModifierKey,
        double additiveValue,
        Map<GeneModifierKey, Double> geneMap
    ) {
        var oldValue = geneMap.get(geneModifierKey);
        var oldValueNotNull = oldValue == null ? 0 : oldValue;
        var newValue = oldValueNotNull + additiveValue;

        geneMap.put(geneModifierKey, newValue);

        var valueDidChange = !Objects.equals(oldValue, newValue);

        if (valueDidChange) {
            dirtyKeys.add(geneModifierKey);
        }
    }

    private Double getValueFromMap(AVPDeferredHolder<Gene> geneHolder, GeneOperationType operation, Map<GeneModifierKey, Double> geneMap) {
        return getValueFromMap(geneHolder.get(), operation, geneMap);
    }

    private Double getValueFromMap(Gene gene, GeneOperationType operation, Map<GeneModifierKey, Double> geneMap) {
        return geneMap.getOrDefault(new GeneModifierKey(gene.id(), operation), 0.0);
    }

    public void clear() {
        setDirty();
        geneMap.clear();
    }

    public Set<GeneModifierKey> getDirtyKeys() {
        return Collections.unmodifiableSet(dirtyKeys);
    }

    public void clearDirtyKeys() {
        dirtyKeys.clear();
    }

    public boolean isDirty() {
        return !dirtyKeys.isEmpty();
    }

    public void setDirty() {
        dirtyKeys.addAll(geneMap.keySet());
    }
}
