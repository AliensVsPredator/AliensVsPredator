package com.lib.common.gameplay.entity.manager;

import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.util.GeneSerializationUtil;
import net.minecraft.nbt.CompoundTag;

public class GeneContainer implements NBTSerializable {

    private static final String NBT_GENE_MODIFIERS = "geneModifiers";

    private static final String NBT_DORMANT_GENE_MODIFIERS = "dormantGeneModifiers";

    private final GeneMap geneMap;

    private final GeneMap dormantGeneMap;

    public GeneContainer() {
        this.geneMap = new GeneMap();
        this.dormantGeneMap = new GeneMap();
    }

    public GeneMap getActiveGeneMap() {
        return geneMap;
    }

    public GeneMap getDormantGeneMap() {
        return dormantGeneMap;
    }

    public void transfer(GeneContainer other, boolean activateDormantGenes) {
        other.geneMap.putAll(geneMap.getBackingMap());

        if (activateDormantGenes) {
            dormantGeneMap.getBackingMap().forEach(other.getActiveGeneMap()::add);
        } else {
            other.dormantGeneMap.putAll(dormantGeneMap.getBackingMap());
        }
    }

    public void clear() {
        geneMap.clear();
        dormantGeneMap.clear();
    }

    @Override
    public void load(CompoundTag compoundTag) {
        geneMap.clear();
        dormantGeneMap.clear();

        GeneSerializationUtil.loadGeneModifiers(NBT_GENE_MODIFIERS, compoundTag, geneMap);
        GeneSerializationUtil.loadGeneModifiers(NBT_DORMANT_GENE_MODIFIERS, compoundTag, dormantGeneMap);

        // Mark all keys as dirty post-load in case the data changed.
        geneMap.setDirty();
    }

    @Override
    public void save(CompoundTag compoundTag) {
        GeneSerializationUtil.saveGeneModifiers(NBT_GENE_MODIFIERS, compoundTag, geneMap);
        GeneSerializationUtil.saveGeneModifiers(NBT_DORMANT_GENE_MODIFIERS, compoundTag, dormantGeneMap);
    }
}
