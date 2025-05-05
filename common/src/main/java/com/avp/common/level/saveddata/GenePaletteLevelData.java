package com.avp.common.level.saveddata;

import com.bvanseg.just.functional.option.Option;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import com.avp.common.entity.living.gene.GeneKey;

public class GenePaletteLevelData extends SavedData {

    private static final String DATA_NAME = "gene_palette_data";

    private static final String GENE_PALETTE_KEY = "genePalette";

    private static final String LAST_ID_KEY = "lastId";

    private final BiMap<GeneKey, Byte> geneKeyToIdMap;

    private byte lastId;

    private GenePaletteLevelData() {
        this(HashBiMap.create(), (byte) 0);
    }

    private GenePaletteLevelData(BiMap<GeneKey, Byte> geneKeyToIdMap, byte lastId) {
        this.geneKeyToIdMap = geneKeyToIdMap;
        this.lastId = lastId;
    }

    public byte getId(GeneKey geneKey) {
        return geneKeyToIdMap.computeIfAbsent(geneKey, ($) -> {
            setDirty();
            return lastId++;
        });
    }

    public Option<GeneKey> getKey(Byte id) {
        return Option.ofNullable(geneKeyToIdMap.inverse().get(id));
    }

    public static GenePaletteLevelData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var map = HashBiMap.<GeneKey, Byte>create();
        var paletteTag = compoundTag.getCompound(GENE_PALETTE_KEY);
        var lastId = compoundTag.getByte(LAST_ID_KEY);

        for (var key : paletteTag.getAllKeys()) {
            var geneKey = new GeneKey(ResourceLocation.parse(key));
            var id = paletteTag.getByte(key);
            map.put(geneKey, id);
        }

        return new GenePaletteLevelData(map, lastId);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var tag = new CompoundTag();

        geneKeyToIdMap.forEach((key, value) -> tag.putByte(key.resourceLocation().toString(), value));

        compoundTag.put(GENE_PALETTE_KEY, tag);
        compoundTag.putByte(LAST_ID_KEY, lastId);

        return compoundTag;
    }

    public static Option<GenePaletteLevelData> getOrCreate(Level level) {
        return level.isClientSide
            ? Option.none()
            : Option.some(level.getServer())
                // overworld could be null here, so we map which safely handles null as Option.none.
                .map(MinecraftServer::overworld)
                .map(serverLevel -> serverLevel.getDataStorage().computeIfAbsent(GenePaletteLevelData.factory(), DATA_NAME));
    }

    public static SavedData.Factory<GenePaletteLevelData> factory() {
        return new SavedData.Factory<>(GenePaletteLevelData::new, GenePaletteLevelData::load, null);
    }
}
