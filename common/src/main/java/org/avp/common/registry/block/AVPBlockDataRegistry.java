package org.avp.common.registry.block;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.avp.api.common.data.block.BlockDataContainer;
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.service.Services;
import org.avp.common.data.block.EngineerShipBlockSetDataContainer;
import org.avp.common.data.block.IndustrialBlockSetDataContainer;
import org.avp.common.data.block.IndustrialGlassBlockSetDataContainer;
import org.avp.common.data.block.PaddingBlockSetDataContainer;
import org.avp.common.data.block.PlasticBlockSetDataContainer;
import org.avp.common.data.block.TempleBlockDataContainer;
import org.avp.common.data.block.UnidentifiedBlockSetDataContainer;
import org.avp.common.data.block.YautjaShipBlockSetDataContainer;
import org.avp.common.data.block.metal.MetalAluminumBlockSetDataContainer;
import org.avp.common.data.block.metal.MetalCopperBlockSetDataContainer;
import org.avp.common.data.block.metal.MetalFerroaluminumBlockSetDataContainer;
import org.avp.common.data.block.metal.MetalGoldBlockSetDataContainer;
import org.avp.common.data.block.metal.MetalIronBlockSetDataContainer;
import org.avp.common.data.block.metal.MetalNetheriteBlockSetDataContainer;
import org.avp.common.data.block.metal.MetalOrioniteBlockDataContainer;
import org.avp.common.data.block.metal.MetalSteelBlockSetDataContainer;
import org.avp.common.data.block.metal.MetalTitaniumBlockSetDataContainer;
import org.avp.common.data.block.ore.OreBauxiteBlockDataContainer;
import org.avp.common.data.block.ore.OreCobaltBlockDataContainer;
import org.avp.common.data.block.ore.OreLithiumBlockDataContainer;
import org.avp.common.data.block.ore.OreMonaziteBlockDataContainer;
import org.avp.common.data.block.ore.OreSilicaBlockDataContainer;
import org.avp.common.data.block.ore.OreTitaniumBlockDataContainer;
import org.avp.common.data.block.raw.NeodymiumBlockDataContainer;
import org.avp.common.data.block.raw.RawBauxiteBlockDataContainer;
import org.avp.common.data.block.raw.RawCobaltBlockDataContainer;
import org.avp.common.data.block.raw.RawLithiumBlockDataContainer;
import org.avp.common.data.block.raw.RawSilicaBlockDataContainer;
import org.avp.common.data.block.raw.RawTitaniumBlockDataContainer;

public class AVPBlockDataRegistry {

    public static final AVPBlockDataRegistry INSTANCE = new AVPBlockDataRegistry();

    private final Map<String, SingleBlockDataContainer.Holder> entries = new LinkedHashMap<>();

    public Collection<SingleBlockDataContainer.Holder> getEntries() {
        return Collections.unmodifiableCollection(entries.values());
    }

    public void addEntry(BlockDataContainer blockDataContainer) {
        if (blockDataContainer instanceof SingleBlockDataContainer.Holder holder) {
            entries.put(holder.getRegistryName(), holder);
            holder.getVariants().forEach(this::addEntry);
        } else if (blockDataContainer instanceof ExtendedBlockDataContainer extendedBlockDataContainer) {
            extendedBlockDataContainer.getVariants().forEach(this::addEntry);
        }
    }

    public void register() {
        registerDataEntries();
        getEntries().forEach(entry -> Services.BLOCK_SERVICE.register(entry.getHolder()));
    }

    private void registerDataEntries() {
        // Engineer Ship
        addEntry(EngineerShipBlockSetDataContainer.INSTANCE);

        // Industrial
        addEntry(IndustrialBlockSetDataContainer.INSTANCE);

        // Industrial Glass
        addEntry(IndustrialGlassBlockSetDataContainer.INSTANCE);

        // Metals
        addEntry(MetalAluminumBlockSetDataContainer.INSTANCE);
        addEntry(MetalCopperBlockSetDataContainer.INSTANCE);
        addEntry(MetalFerroaluminumBlockSetDataContainer.INSTANCE);
        addEntry(MetalGoldBlockSetDataContainer.INSTANCE);
        addEntry(MetalIronBlockSetDataContainer.INSTANCE);
        addEntry(MetalNetheriteBlockSetDataContainer.INSTANCE);
        addEntry(MetalOrioniteBlockDataContainer.INSTANCE);
        addEntry(MetalSteelBlockSetDataContainer.INSTANCE);
        addEntry(MetalTitaniumBlockSetDataContainer.INSTANCE);

        // Ores
        addEntry(OreBauxiteBlockDataContainer.INSTANCE);
        addEntry(OreCobaltBlockDataContainer.INSTANCE);
        addEntry(OreLithiumBlockDataContainer.INSTANCE);
        addEntry(OreMonaziteBlockDataContainer.INSTANCE);
        addEntry(OreSilicaBlockDataContainer.INSTANCE);
        addEntry(OreTitaniumBlockDataContainer.INSTANCE);

        // Padding
        addEntry(PaddingBlockSetDataContainer.INSTANCE);

        // Plastic
        addEntry(PlasticBlockSetDataContainer.INSTANCE);

        // Raw Blocks
        addEntry(NeodymiumBlockDataContainer.INSTANCE);
        addEntry(RawBauxiteBlockDataContainer.INSTANCE);
        addEntry(RawCobaltBlockDataContainer.INSTANCE);
        addEntry(RawLithiumBlockDataContainer.INSTANCE);
        addEntry(RawSilicaBlockDataContainer.INSTANCE);
        addEntry(RawTitaniumBlockDataContainer.INSTANCE);

        // Temple
        addEntry(TempleBlockDataContainer.INSTANCE);

        // Unidentified
        addEntry(UnidentifiedBlockSetDataContainer.INSTANCE);

        // Yautja Ship
        addEntry(YautjaShipBlockSetDataContainer.INSTANCE);
    }
}
