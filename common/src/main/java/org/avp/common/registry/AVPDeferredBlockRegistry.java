package org.avp.common.registry;

import net.minecraft.world.level.block.Block;
import org.avp.api.Holder;
import org.avp.api.Tuple;
import org.avp.api.block.BlockData;
import org.avp.common.service.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AVPDeferredBlockRegistry extends AVPDeferredRegistry<Block> {

    private static final List<Tuple<Holder<Block>, BlockData>> DATA_ENTRIES = new ArrayList<>();

    public static List<Tuple<Holder<Block>, BlockData>> getDataEntries() {
        return DATA_ENTRIES;
    }

    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        var blockData = blockDataBuilder.build();
        var holder = createHolder(registryName, blockData::create);
        entries.add(holder);
        DATA_ENTRIES.add(new Tuple<>(holder, blockData));
        return holder;
    }

    @Override
    protected Holder<Block> createHolder(String registryName, Supplier<Block> supplier) {
        return Services.BLOCK_SERVICE.createHolder(registryName, supplier);
    }

    @Override
    public final void register() {
        entries.forEach(Services.BLOCK_SERVICE::register);
    }
}
