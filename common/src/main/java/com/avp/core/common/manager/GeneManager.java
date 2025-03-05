package com.avp.core.common.manager;

import it.unimi.dsi.fastutil.objects.Object2ByteArrayMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.Map;

import com.avp.core.common.gene.GeneKey;
import com.avp.core.common.gene.behavior.GeneDecoder;
import com.avp.core.common.level.saveddata.GenePaletteLevelData;

public class GeneManager {

    private static final String GENE_KEY = "genes";

    private final Entity entity;

    private final Map<GeneKey, Byte> geneKeyToValueMap;

    public GeneManager(Entity entity) {
        this.entity = entity;
        this.geneKeyToValueMap = new Object2ByteArrayMap<>();
    }

    public <T> T get(GeneKey geneKey, GeneDecoder<T> geneDecoder) {
        var rawGeneValue = geneKeyToValueMap.getOrDefault(geneKey, (byte) 0);
        return geneDecoder.decode(rawGeneValue);
    }

    public boolean isMaximized(GeneKey geneKey) {
        var rawGeneValue = geneKeyToValueMap.getOrDefault(geneKey, (byte) 0);
        return rawGeneValue == Byte.MAX_VALUE;
    }

    public boolean isMinimized(GeneKey geneKey) {
        var rawGeneValue = geneKeyToValueMap.getOrDefault(geneKey, (byte) 0);
        return rawGeneValue == Byte.MIN_VALUE;
    }

    public void add(GeneKey geneKey, int value) {
        geneKeyToValueMap.compute(geneKey, ($, oldValue) -> {
            var oldValueNotNull = oldValue == null ? 0 : oldValue;
            return (byte) Math.clamp(value + oldValueNotNull, Byte.MIN_VALUE, Byte.MAX_VALUE);
        });
    }

    public void set(GeneKey geneKey, int value) {
        var clampedValue = Math.clamp(value, Byte.MIN_VALUE, Byte.MAX_VALUE);
        geneKeyToValueMap.put(geneKey, (byte) clampedValue);
    }

    public Map<GeneKey, Byte> getAll() {
        return Collections.unmodifiableMap(geneKeyToValueMap);
    }

    public void setAll(Map<GeneKey, Byte> geneKeyToValueMap) {
        this.geneKeyToValueMap.putAll(geneKeyToValueMap);
    }

    public void minimize(GeneKey geneKey) {
        set(geneKey, Byte.MIN_VALUE);
    }

    public void maximize(GeneKey geneKey) {
        set(geneKey, Byte.MAX_VALUE);
    }

    public void load(CompoundTag compoundTag) {
        var level = entity.level();
        var genePaletteOptional = GenePaletteLevelData.getOrCreate(level);

        genePaletteOptional.ifPresent(genePalette -> {
            var geneArray = compoundTag.getByteArray(GENE_KEY);

            for (int i = 0; i < geneArray.length; i += 2) {
                var id = geneArray[i];
                var value = geneArray[i + 1];
                var geneKey = genePalette.getKeyOrNull(id);

                if (geneKey != null) {
                    geneKeyToValueMap.put(geneKey, value);
                }
            }
        });
    }

    public void save(CompoundTag compoundTag) {
        var level = entity.level();
        var genePaletteOptional = GenePaletteLevelData.getOrCreate(level);

        genePaletteOptional.ifPresent(genePalette -> {
            var geneArray = new byte[geneKeyToValueMap.size() * 2];

            var i = 0;

            for (var entry : geneKeyToValueMap.entrySet()) {
                var key = entry.getKey();
                var value = entry.getValue();
                var id = genePalette.getId(key);
                geneArray[i] = id;
                geneArray[i + 1] = value;
                i += 2;
            }

            compoundTag.putByteArray(GENE_KEY, geneArray);
        });
    }
}
