package com.lib.common.util;

import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import com.lib.common.gameplay.gene.GeneModifier;
import com.lib.common.gameplay.gene.GeneModifierKey;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import com.avp.AVP;

public class GeneSerializationUtil {

    public static void loadGeneMap(String geneMapKey, CompoundTag compoundTag, Map<GeneModifierKey, Double> geneMap) {
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

    public static void loadGeneModifiers(String geneListKey, CompoundTag compoundTag, Map<GeneModifierKey, Double> geneMap) {
        if (compoundTag.contains(geneListKey, CompoundTag.TAG_LIST)) {
            var listTag = compoundTag.getList(geneListKey, CompoundTag.TAG_COMPOUND);

            for (var i = 0; i < listTag.size(); i++) {
                var elementTag = listTag.getCompound(i);

                GeneBonusDataEntry.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, elementTag))
                    .resultOrPartial(AVP.LOGGER::error)
                    .ifPresent(entry -> geneMap.put(new GeneModifierKey(entry.id(), entry.operation()), entry.value()));
            }
        }
    }

    public static void saveGeneModifiers(String geneListKey, CompoundTag compoundTag, Map<GeneModifierKey, Double> geneMap) {
        var listTag = new ListTag();

        for (var entry : geneMap.entrySet()) {
            var geneEntry = new GeneBonusDataEntry(entry.getKey().resourceLocation(), entry.getKey().operation(), entry.getValue());
            GeneBonusDataEntry.CODEC.encodeStart(NbtOps.INSTANCE, geneEntry)
                .resultOrPartial(AVP.LOGGER::error)
                .ifPresent(listTag::add);
        }

        compoundTag.put(geneListKey, listTag);
    }
}
