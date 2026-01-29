package com.blib.azurelib.common.model.factory.primitive;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import com.blib.azurelib.common.cache.object.GeoVertex;

public record VertexSet(
    GeoVertex bottomLeftBack,
    GeoVertex bottomRightBack,
    GeoVertex topLeftBack,
    GeoVertex topRightBack,
    GeoVertex topLeftFront,
    GeoVertex topRightFront,
    GeoVertex bottomLeftFront,
    GeoVertex bottomRightFront
) {

    public VertexSet(Vec3 origin, Vec3 vertexSize, double inflation) {
        this(
            new GeoVertex(origin.x - inflation, origin.y - inflation, origin.z - inflation),
            new GeoVertex(origin.x - inflation, origin.y - inflation, origin.z + vertexSize.z + inflation),
            new GeoVertex(origin.x - inflation, origin.y + vertexSize.y + inflation, origin.z - inflation),
            new GeoVertex(
                origin.x - inflation,
                origin.y + vertexSize.y + inflation,
                origin.z + vertexSize.z + inflation
            ),
            new GeoVertex(
                origin.x + vertexSize.x + inflation,
                origin.y + vertexSize.y + inflation,
                origin.z - inflation
            ),
            new GeoVertex(
                origin.x + vertexSize.x + inflation,
                origin.y + vertexSize.y + inflation,
                origin.z + vertexSize.z + inflation
            ),
            new GeoVertex(origin.x + vertexSize.x + inflation, origin.y - inflation, origin.z - inflation),
            new GeoVertex(
                origin.x + vertexSize.x + inflation,
                origin.y - inflation,
                origin.z + vertexSize.z + inflation
            )
        );
    }

    public GeoVertex[] quadWest() {
        return new GeoVertex[] { this.topRightBack, this.topLeftBack, this.bottomLeftBack, this.bottomRightBack };
    }

    public GeoVertex[] quadEast() {
        return new GeoVertex[] {
            this.topLeftFront,
            this.topRightFront,
            this.bottomRightFront,
            this.bottomLeftFront
        };
    }

    public GeoVertex[] quadNorth() {
        return new GeoVertex[] { this.topLeftBack, this.topLeftFront, this.bottomLeftFront, this.bottomLeftBack };
    }

    public GeoVertex[] quadSouth() {
        return new GeoVertex[] {
            this.topRightFront,
            this.topRightBack,
            this.bottomRightBack,
            this.bottomRightFront
        };
    }

    public GeoVertex[] quadUp() {
        return new GeoVertex[] { this.topRightBack, this.topRightFront, this.topLeftFront, this.topLeftBack };
    }

    public GeoVertex[] quadDown() {
        return new GeoVertex[] {
            this.bottomLeftBack,
            this.bottomLeftFront,
            this.bottomRightFront,
            this.bottomRightBack
        };
    }

    public GeoVertex[] verticesForQuad(Direction direction, boolean boxUv, boolean mirror) {
        return switch (direction) {
            case WEST -> mirror ? quadEast() : quadWest();
            case EAST -> mirror ? quadWest() : quadEast();
            case NORTH -> quadNorth();
            case SOUTH -> quadSouth();
            case UP -> mirror && !boxUv ? quadDown() : quadUp();
            case DOWN -> mirror && !boxUv ? quadUp() : quadDown();
        };
    }
}
