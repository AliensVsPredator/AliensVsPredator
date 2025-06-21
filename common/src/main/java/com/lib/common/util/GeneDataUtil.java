package com.lib.common.util;

import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import com.lib.common.gameplay.gene.GeneModifierKey;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneDataUtil {

    public static List<GeneBonusDataEntry> toList(Map<GeneModifierKey, Double> geneMap) {
        return geneMap.entrySet()
            .stream()
            .map(entry -> new GeneBonusDataEntry(entry.getKey().resourceLocation(), entry.getKey().operation(), entry.getValue()))
            .toList();
    }

    public static Map<GeneModifierKey, Double> toMap(List<GeneBonusDataEntry> geneBonusDataEntries) {
        return geneBonusDataEntries.stream()
            .collect(Collectors.toMap(GeneBonusDataEntry::toKey, GeneBonusDataEntry::value));
    }
}
