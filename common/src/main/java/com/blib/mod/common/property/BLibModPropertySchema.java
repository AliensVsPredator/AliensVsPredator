package com.blib.mod.common.property;

import com.blib.api.common.property.v1.BLibPropertySchema;

public class BLibModPropertySchema {

    static final BLibPropertySchema SCHEMA = BLibPropertySchema.builder()
        .withPropertyValueAlignment(true)
        .addComment("Master toggle for all debug renderers.")
        .addProperty(BLibModProperties.Debug.Render.ENABLED.key(), BLibModProperties.Debug.Render.ENABLED.defaultValue())
        .addComment("Render chunk border debug info.")
        .addProperty(
            BLibModProperties.Debug.Render.ChunkBorder.ENABLED.key(),
            BLibModProperties.Debug.Render.ChunkBorder.ENABLED.defaultValue()
        )
        .addComment("Render chunk debug info.")
        .addProperty(
            BLibModProperties.Debug.Render.ChunkRender.ENABLED.key(),
            BLibModProperties.Debug.Render.ChunkRender.ENABLED.defaultValue()
        )
        .addComment("Render collision box debug info.")
        .addProperty(
            BLibModProperties.Debug.Render.CollisionBox.ENABLED.key(),
            BLibModProperties.Debug.Render.CollisionBox.ENABLED.defaultValue()
        )
        .addComment("Render entity goal selector debug info.")
        .addProperty(BLibModProperties.Debug.Render.Goal.ENABLED.key(), BLibModProperties.Debug.Render.Goal.ENABLED.defaultValue())
        .addComment("Number of world state entries per page in the GOAP debug HUD.")
        .addProperty(
            BLibModProperties.Debug.Render.Goap.WORLD_STATE_PAGE_SIZE.key(),
            BLibModProperties.Debug.Render.Goap.WORLD_STATE_PAGE_SIZE.defaultValue()
        )
        .addComment("Tick interval for GOAP debug updates on dedicated servers.")
        .addProperty(
            BLibModProperties.Debug.Render.Goap.DEDICATED_TICK_INTERVAL.key(),
            BLibModProperties.Debug.Render.Goap.DEDICATED_TICK_INTERVAL.defaultValue()
        )
        .addComment("Render height map debug info.")
        .addProperty(
            BLibModProperties.Debug.Render.HeightMap.ENABLED.key(),
            BLibModProperties.Debug.Render.HeightMap.ENABLED.defaultValue()
        )
        .addComment("Render light debug info.")
        .addProperty(BLibModProperties.Debug.Render.Light.ENABLED.key(), BLibModProperties.Debug.Render.Light.ENABLED.defaultValue())
        .addComment("Render neighbor updates debug info.")
        .addProperty(
            BLibModProperties.Debug.Render.Neighbors.ENABLED.key(),
            BLibModProperties.Debug.Render.Neighbors.ENABLED.defaultValue()
        )
        .addComment("Render entity pathfinding debug info.")
        .addProperty(BLibModProperties.Debug.Render.Path.ENABLED.key(), BLibModProperties.Debug.Render.Path.ENABLED.defaultValue())
        .addComment("Render A* search exploration debug info (explored nodes, corridor, surface directions).")
        .addProperty(
            BLibModProperties.Debug.Render.PathSearch.ENABLED.key(),
            BLibModProperties.Debug.Render.PathSearch.ENABLED.defaultValue()
        )
        .addComment("Render sky light section debug info.")
        .addProperty(BLibModProperties.Debug.Render.SkyLight.ENABLED.key(), BLibModProperties.Debug.Render.SkyLight.ENABLED.defaultValue())
        .addComment("Render solid face debug info.")
        .addProperty(
            BLibModProperties.Debug.Render.SolidFace.ENABLED.key(),
            BLibModProperties.Debug.Render.SolidFace.ENABLED.defaultValue()
        )
        .addComment("Render structure debug info.")
        .addProperty(
            BLibModProperties.Debug.Render.Structures.ENABLED.key(),
            BLibModProperties.Debug.Render.Structures.ENABLED.defaultValue()
        )
        .addComment("Render water debug info.")
        .addProperty(BLibModProperties.Debug.Render.Water.ENABLED.key(), BLibModProperties.Debug.Render.Water.ENABLED.defaultValue())
        .addComment("Render world gen attempt debug info.")
        .addProperty(
            BLibModProperties.Debug.Render.WorldGenAttempt.ENABLED.key(),
            BLibModProperties.Debug.Render.WorldGenAttempt.ENABLED.defaultValue()
        )
        .addBlankLine()
        .build();

    private BLibModPropertySchema() {
        throw new UnsupportedOperationException();
    }
}
