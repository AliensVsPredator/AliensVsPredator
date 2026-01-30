package com.blib.internal.client.model;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import com.blib.api.client.model.v1.AzBakedModel;

public abstract class AzBakedModelFactory {

    public abstract AzBakedModel constructGeoModel(GeometryTree geometryTree);

    public abstract AzBone constructBone(
        BoneStructure boneStructure,
        ModelProperties properties,
        @Nullable AzBone parent
    );

    public abstract GeoCube constructCube(Cube cube, ModelProperties properties, AzBone bone);

    public GeoQuad[] buildQuads(
        UVUnion uvUnion,
        VertexSet vertices,
        Cube cube,
        float textureWidth,
        float textureHeight,
        boolean mirror
    ) {
        GeoQuad[] quads = new GeoQuad[6];

        quads[0] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.WEST);
        quads[1] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.EAST);
        quads[2] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.NORTH);
        quads[3] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.SOUTH);
        quads[4] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.UP);
        quads[5] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.DOWN);

        return quads;
    }

    public GeoQuad buildQuad(
        VertexSet vertices,
        Cube cube,
        UVUnion uvUnion,
        float textureWidth,
        float textureHeight,
        boolean mirror,
        Direction direction
    ) {
        if (!uvUnion.isBoxUV()) {
            FaceUV faceUV = uvUnion.faceUV().fromDirection(direction);

            if (faceUV == null)
                return null;

            return GeoQuad.build(
                vertices.verticesForQuad(direction, false, mirror || cube.mirror() == Boolean.TRUE),
                faceUV.uv(),
                faceUV.uvSize(),
                faceUV.uvRotation(),
                textureWidth,
                textureHeight,
                mirror,
                direction
            );
        }

        double[] uv = cube.uv().boxUVCoords();
        double[] uvSize = cube.size();
        Vec3 uvSizeVec = new Vec3(Math.floor(uvSize[0]), Math.floor(uvSize[1]), Math.floor(uvSize[2]));
        double[][] uvData = switch (direction) {
            case WEST -> new double[][] {
                new double[] { uv[0] + uvSizeVec.z + uvSizeVec.x, uv[1] + uvSizeVec.z },
                new double[] { uvSizeVec.z, uvSizeVec.y }
            };
            case EAST -> new double[][] {
                new double[] { uv[0], uv[1] + uvSizeVec.z },
                new double[] { uvSizeVec.z, uvSizeVec.y }
            };
            case NORTH -> new double[][] {
                new double[] { uv[0] + uvSizeVec.z, uv[1] + uvSizeVec.z },
                new double[] { uvSizeVec.x, uvSizeVec.y }
            };
            case SOUTH -> new double[][] {
                new double[] { uv[0] + uvSizeVec.z + uvSizeVec.x + uvSizeVec.z, uv[1] + uvSizeVec.z },
                new double[] { uvSizeVec.x, uvSizeVec.y }
            };
            case UP -> new double[][] {
                new double[] { uv[0] + uvSizeVec.z, uv[1] },
                new double[] { uvSizeVec.x, uvSizeVec.z }
            };
            case DOWN -> new double[][] {
                new double[] { uv[0] + uvSizeVec.z + uvSizeVec.x, uv[1] + uvSizeVec.z },
                new double[] { uvSizeVec.x, -uvSizeVec.z }
            };
        };

        return GeoQuad.build(
            vertices.verticesForQuad(direction, true, mirror || cube.mirror() == Boolean.TRUE),
            uvData[0],
            uvData[1],
            FaceUV.Rotation.NONE,
            textureWidth,
            textureHeight,
            mirror,
            direction
        );
    }
}
