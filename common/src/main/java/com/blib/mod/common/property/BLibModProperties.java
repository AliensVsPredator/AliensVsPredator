package com.blib.mod.common.property;

import com.blib.api.common.property.v1.BLibPropertyKey;
import com.blib.api.common.property.v1.serializer.BLibPropertySerializers;

public class BLibModProperties {

    public static class Debug {

        private static final BLibPropertyKey.Parent DEBUG = BLibPropertyKey.parent("debug");

        public static class Render {

            private static final BLibPropertyKey.Parent RENDER = DEBUG.child("render");

            public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                RENDER.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                false
            );

            public static class ChunkBorder {

                private static final BLibPropertyKey.Parent CHUNK_BORDER = RENDER.child("chunk_border");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    CHUNK_BORDER.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class ChunkRender {

                private static final BLibPropertyKey.Parent CHUNK_RENDER = RENDER.child("chunk_render");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    CHUNK_RENDER.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class CollisionBox {

                private static final BLibPropertyKey.Parent COLLISION_BOX = RENDER.child("collision_box");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    COLLISION_BOX.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class Goal {

                private static final BLibPropertyKey.Parent GOAL = RENDER.child("goal");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    GOAL.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class Goap {

                private static final BLibPropertyKey.Parent GOAP = RENDER.child("goap");

                public static final BLibModProperty<Integer> WORLD_STATE_PAGE_SIZE = new BLibModProperty<>(
                    GOAP.leaf("world_state_page_size", BLibPropertySerializers.INT),
                    30
                );

                public static final BLibModProperty<Integer> DEDICATED_TICK_INTERVAL = new BLibModProperty<>(
                    GOAP.leaf("dedicated_tick_interval", BLibPropertySerializers.INT),
                    10
                );
            }

            public static class HeightMap {

                private static final BLibPropertyKey.Parent HEIGHT_MAP = RENDER.child("height_map");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    HEIGHT_MAP.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class Light {

                private static final BLibPropertyKey.Parent LIGHT = RENDER.child("light");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    LIGHT.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class Neighbors {

                private static final BLibPropertyKey.Parent NEIGHBORS = RENDER.child("neighbors");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    NEIGHBORS.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class Path {

                private static final BLibPropertyKey.Parent PATH = RENDER.child("path");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    PATH.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class SolidFace {

                private static final BLibPropertyKey.Parent SOLID_FACE = RENDER.child("solid_face");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    SOLID_FACE.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class SkyLight {

                private static final BLibPropertyKey.Parent SKY_LIGHT = RENDER.child("sky_light");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    SKY_LIGHT.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class Structures {

                private static final BLibPropertyKey.Parent STRUCTURES = RENDER.child("structures");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    STRUCTURES.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class Water {

                private static final BLibPropertyKey.Parent WATER = RENDER.child("water");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    WATER.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }

            public static class WorldGenAttempt {

                private static final BLibPropertyKey.Parent WORLD_GEN_ATTEMPT = RENDER.child("world_gen_attempt");

                public static final BLibModProperty<Boolean> ENABLED = new BLibModProperty<>(
                    WORLD_GEN_ATTEMPT.leaf("enabled", BLibPropertySerializers.BOOLEAN),
                    false
                );
            }
        }

    }

    private BLibModProperties() {
        throw new UnsupportedOperationException();
    }
}
