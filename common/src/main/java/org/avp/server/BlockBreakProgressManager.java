package org.avp.server;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

import org.avp.api.Tuple;

public class BlockBreakProgressManager {

    public static final Map<BlockPos, Tuple<Long, Float>> BLOCK_BREAK_PROGRESS_MAP = new HashMap<>();

    private BlockBreakProgressManager() {
        throw new UnsupportedOperationException();
    }
}
