package com.blib.internal.common.capture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Builds the auto-generated {@code StructureTemplatePool} JSON for a jigsaw capture. Each sub-piece is added as a
 * {@code single_pool_element} with weight 1 and {@code rigid} projection so the assembled output is a byte-identical
 * reproduction of the captured layout. The seam jigsaw blocks placed by {@link JigsawSeamInjector} provide the
 * deterministic connectivity that makes vanilla's jigsaw resolver pick the right neighbor at each cut.
 * <p>
 * Output shape matches what {@code StructureTemplatePool.DIRECT_CODEC} reads:
 *
 * <pre>{@code
 * { "fallback": "minecraft:empty",
 *   "elements": [
 *     { "weight": 1, "element": { "element_type": "minecraft:single_pool_element",
 *                                 "location": "<project>:<piece>",
 *                                 "processors": "minecraft:empty_processor_list",
 *                                 "projection": "rigid" } },
 *     ...
 *   ] }
 * }</pre>
 */
@ApiStatus.Internal
public final class CapturePoolJsonBuilder {

    private CapturePoolJsonBuilder() {}

    public static JsonObject buildPool(List<ResourceLocation> pieceIds) {
        var pool = new JsonObject();
        pool.addProperty("fallback", "minecraft:empty");
        var elements = new JsonArray();
        for (var pieceId : pieceIds) {
            elements.add(buildElement(pieceId));
        }
        pool.add("elements", elements);
        return pool;
    }

    private static JsonObject buildElement(ResourceLocation pieceId) {
        var entry = new JsonObject();
        entry.addProperty("weight", 1);
        var element = new JsonObject();
        element.addProperty("element_type", "minecraft:single_pool_element");
        element.addProperty("location", pieceId.toString());
        element.addProperty("processors", "minecraft:empty");
        element.addProperty("projection", "rigid");
        entry.add("element", element);
        return entry;
    }
}
