package com.avp.common.block.entity.resin_node;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.util.NBTSerializable;

public class ResinSpreader implements NBTSerializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResinSpreader.class);

    private static final String CURSORS_KEY = "cursors";

    // The maximum number of cursors that can be controlled by a resin spreader at any given time.
    private static final int CURSOR_LIMIT = 32;

    // The maximum charge value that a cursor can maintain.
    private static final int MAX_CHARGE = 1000;

    public static ResinSpreader create() {
        return new ResinSpreader(AVPBlockTags.RESIN_REPLACEABLE, 10);
    }

    private final TagKey<Block> replaceableBlocks;

    private final int chargeDecayRate;

    private List<ChargeCursor> cursors;

    private ResinSpreader(TagKey<Block> tagKey, int chargeDecayRate) {
        this.replaceableBlocks = tagKey;
        this.chargeDecayRate = chargeDecayRate;
        this.cursors = new ArrayList<>();
    }

    public TagKey<Block> replaceableBlocks() {
        return this.replaceableBlocks;
    }

    public int chargeDecayRate() {
        return this.chargeDecayRate;
    }

    public void addCursors(BlockPos blockPos, int totalCharge) {
        while (totalCharge > 0) {
            var chargeForCursor = Math.min(totalCharge, MAX_CHARGE);
            addCursor(new ChargeCursor(blockPos, chargeForCursor));
            totalCharge -= chargeForCursor;
        }
    }

    private void addCursor(ChargeCursor chargeCursor) {
        if (cursors.size() < CURSOR_LIMIT) {
            // If we can add more cursors, then do so.
            cursors.add(chargeCursor);
        }
    }

    public void updateCursors(LevelAccessor levelAccessor, BlockPos nodePos, RandomSource randomSource) {
        if (this.cursors.isEmpty()) {
            // No cursors to update, so return.
            return;
        }

        // Cursors that get retained/kept will be added to this list.
        var retainedCursors = new ArrayList<ChargeCursor>();
        // Caches low-charge cursors by block position, preferring the one with the smallest charge when multiple
        // cursors occupy the same position.
        var cursorsByPos = new HashMap<BlockPos, ChargeCursor>();

        for (var cursor : cursors) {
            // Update the cursor.
            cursor.update(levelAccessor, nodePos, randomSource, this);

            if (cursor.charge > 0) {
                // The cursor still has charge left to it after updating.
                var cursorPos = cursor.getPos();
                var cachedCursor = cursorsByPos.get(cursorPos);

                if (cachedCursor == null) {
                    // If there is no cursor at this position yet, then add the cursor under this position and continue.
                    cursorsByPos.put(cursorPos, cursor);
                    retainedCursors.add(cursor);
                } else if (cursor.charge + cachedCursor.charge <= MAX_CHARGE) {
                    // The current cursor is overlapping in position with the cached cursor, and their combined charge
                    // is under the maximum. We can safely combine them into a single cursor at the given position.
                    cachedCursor.mergeWith(cursor);
                } else {
                    // The current cursor could not be combined with the cached cursor, so retain it.
                    retainedCursors.add(cursor);

                    if (cursor.charge < cachedCursor.charge) {
                        // The current cursor's charge is less than the cached cursor's charge. The point of the map
                        // is to combine cursors with low charge where possible, so we want to store the current
                        // cursor since it has a weaker charge here.
                        cursorsByPos.put(cursorPos, cursor);
                    }
                }
            }
        }

        this.cursors = retainedCursors;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(CURSORS_KEY, 9)) {
            cursors.clear();
            var list = ChargeCursor.CODEC
                .listOf()
                .parse(new Dynamic<>(NbtOps.INSTANCE, compoundTag.getList(CURSORS_KEY, 10)))
                .resultOrPartial(LOGGER::error)
                .orElseGet(ArrayList::new);

            var cursorCount = Math.min(list.size(), CURSOR_LIMIT);

            for (var i = 0; i < cursorCount; i++) {
                addCursor(list.get(i));
            }
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        ChargeCursor.CODEC
            .listOf()
            .encodeStart(NbtOps.INSTANCE, cursors)
            .resultOrPartial(LOGGER::error)
            .ifPresent(tag -> compoundTag.put(CURSORS_KEY, tag));
    }
}
