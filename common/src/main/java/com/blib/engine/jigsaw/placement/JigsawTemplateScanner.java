package com.blib.engine.jigsaw.placement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.blib.api.common.worldgen.v1.StructureTemplateAccessor;

/**
 * Extracts the list of jigsaw blocks from a {@link StructureTemplate}'s first palette and caches the result. Each
 * lookup re-reads the palette but the parsed result is memoized in a {@link WeakHashMap} keyed by the template instance
 * — re-scans only happen when a template is actually re-loaded, not when the user moves the cursor.
 * <p>
 * Vanilla's NBT layout for a jigsaw block (mirroring {@link JigsawBlockEntity#load}):
 * <ul>
 * <li>{@code name} — the jigsaw's own identity for incoming-connection matching</li>
 * <li>{@code target} — the name this jigsaw expects to mate with</li>
 * <li>{@code joint} — {@code "aligned"} or {@code "rollable"}; defaults based on front-axis if absent</li>
 * </ul>
 * The {@code pool} field is captured here too but isn't currently used by the resolver (we trust the user's template
 * choice over a pool-membership check).
 */
@ApiStatus.Internal
public final class JigsawTemplateScanner {

    private static final Map<StructureTemplate, List<TemplateJigsawInfo>> cache = new WeakHashMap<>();

    private JigsawTemplateScanner() {}

    public static List<TemplateJigsawInfo> jigsawBlocks(StructureTemplate template) {
        var cached = cache.get(template);
        if (cached != null) {
            return cached;
        }

        var palettes = ((StructureTemplateAccessor) template).blib$getPalettes();
        if (palettes == null || palettes.isEmpty()) {
            cache.put(template, List.of());
            return List.of();
        }

        var blocks = palettes.get(0).blocks();
        var out = new ArrayList<TemplateJigsawInfo>();
        for (var info : blocks) {
            if (!info.state().is(Blocks.JIGSAW)) {
                continue;
            }
            var nbt = info.nbt();
            if (nbt == null) {
                continue;
            }

            var front = JigsawBlock.getFrontFacing(info.state());
            var top = JigsawBlock.getTopFacing(info.state());
            // Joint defaults match vanilla's JigsawBlockEntity.load: ALIGNED for horizontal-front jigsaws,
            // ROLLABLE for vertical-front. This matters for the alignment-math top-direction check — without the
            // default, "aligned" jigsaws missing the explicit "joint" tag would incorrectly be treated as rollable.
            var joint = JigsawBlockEntity.JointType
                .byName(nbt.getString("joint"))
                .orElseGet(
                    () -> front.getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE
                );

            out.add(
                new TemplateJigsawInfo(
                    info.pos().immutable(),
                    front,
                    top,
                    ResourceLocation.parse(nbt.getString("name")),
                    ResourceLocation.parse(nbt.getString("target")),
                    joint
                )
            );
        }

        var result = List.copyOf(out);
        cache.put(template, result);
        return result;
    }

    /** Drop the cache. Called from workspace close so we don't pin templates across sessions. */
    public static void clear() {
        cache.clear();
    }
}
