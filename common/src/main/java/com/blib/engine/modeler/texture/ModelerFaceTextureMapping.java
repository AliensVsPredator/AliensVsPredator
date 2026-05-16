package com.blib.engine.modeler.texture;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;

@ApiStatus.Internal
public final class ModelerFaceTextureMapping {

    private static final float EPSILON = 1.0e-6f;

    private ModelerFaceTextureMapping() {}

    public static @Nullable LoadedTexture textureForFace(ModelerScene scene, ModelerCube cube, ModelerCube.Face face) {
        var uv = cube.faceUv(face);
        if (uv != null) {
            var source = uv.textureSource();
            if (source != null) {
                for (var texture : scene.textures) {
                    if (source.equals(texture.sourceResource())) {
                        return texture;
                    }
                }
                return scene.activeTexture;
            }
            return scene.activeTexture;
        }
        return cube.hasPerFaceUv ? null : scene.activeTexture;
    }

    public static @Nullable UvQuad uvQuad(ModelerCube cube, ModelerCube.Face face) {
        var imported = cube.faceUv(face);
        if (imported != null) {
            return importedUvQuad(imported);
        }
        if (cube.hasPerFaceUv) {
            return null;
        }
        return boxUvQuad(cube, face);
    }

    public static FaceGeometry faceGeometry(ModelerCube cube, ModelerCube.Face face) {
        var inflate = (float) cube.inflate;
        var x0 = (float) cube.origin.x - inflate;
        var y0 = (float) cube.origin.y - inflate;
        var z0 = (float) cube.origin.z - inflate;
        var x1 = x0 + (float) cube.size.x + 2 * inflate;
        var y1 = y0 + (float) cube.size.y + 2 * inflate;
        var z1 = z0 + (float) cube.size.z + 2 * inflate;
        return switch (face) {
            case EAST -> new FaceGeometry(
                new Vector3f(x1, y0, z0),
                new Vector3f(x1, y1, z0),
                new Vector3f(x1, y1, z1),
                new Vector3f(x1, y0, z1),
                new Vector3f(1, 0, 0)
            );
            case WEST -> new FaceGeometry(
                new Vector3f(x0, y0, z1),
                new Vector3f(x0, y1, z1),
                new Vector3f(x0, y1, z0),
                new Vector3f(x0, y0, z0),
                new Vector3f(-1, 0, 0)
            );
            case UP -> new FaceGeometry(
                new Vector3f(x0, y1, z0),
                new Vector3f(x0, y1, z1),
                new Vector3f(x1, y1, z1),
                new Vector3f(x1, y1, z0),
                new Vector3f(0, 1, 0)
            );
            case DOWN -> new FaceGeometry(
                new Vector3f(x0, y0, z1),
                new Vector3f(x0, y0, z0),
                new Vector3f(x1, y0, z0),
                new Vector3f(x1, y0, z1),
                new Vector3f(0, -1, 0)
            );
            case SOUTH -> new FaceGeometry(
                new Vector3f(x1, y0, z1),
                new Vector3f(x1, y1, z1),
                new Vector3f(x0, y1, z1),
                new Vector3f(x0, y0, z1),
                new Vector3f(0, 0, 1)
            );
            case NORTH -> new FaceGeometry(
                new Vector3f(x0, y0, z0),
                new Vector3f(x0, y1, z0),
                new Vector3f(x1, y1, z0),
                new Vector3f(x1, y0, z0),
                new Vector3f(0, 0, -1)
            );
        };
    }

    public static @Nullable Pixel pixelAt(ModelerCube cube, ModelerCube.Face face, Vec3 localPoint, int textureWidth, int textureHeight) {
        return pixelAt(cube, face, localPoint, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    public static @Nullable Pixel pixelAt(
        ModelerCube cube,
        ModelerCube.Face face,
        Vec3 localPoint,
        double uvSheetWidth,
        double uvSheetHeight,
        int imageWidth,
        int imageHeight
    ) {
        var uv = uvQuad(cube, face);
        if (uv == null) {
            return null;
        }
        var point = facePoint(faceGeometry(cube, face), localPoint);
        if (point == null) {
            return null;
        }
        return uv.pixelAt(point.a(), point.b(), uvSheetWidth, uvSheetHeight, imageWidth, imageHeight);
    }

    private static @Nullable FacePoint facePoint(FaceGeometry geometry, Vec3 localPoint) {
        var p = new Vector3f((float) localPoint.x, (float) localPoint.y, (float) localPoint.z);
        var fromOrigin = p.sub(geometry.v0(), new Vector3f());
        var aAxis = geometry.aAxis();
        var bAxis = geometry.bAxis();
        var aLenSq = aAxis.lengthSquared();
        var bLenSq = bAxis.lengthSquared();
        if (aLenSq < EPSILON || bLenSq < EPSILON) {
            return null;
        }
        var a = fromOrigin.dot(aAxis) / aLenSq;
        var b = fromOrigin.dot(bAxis) / bLenSq;
        return new FacePoint(clamp01(a), clamp01(b));
    }

    private static UvQuad boxUvQuad(ModelerCube cube, ModelerCube.Face face) {
        var u = (float) cube.uvOriginU;
        var v = (float) cube.uvOriginV;
        var sx = (float) Math.floor(cube.size.x);
        var sy = (float) Math.floor(cube.size.y);
        var sz = (float) Math.floor(cube.size.z);
        var mirror = cube.mirrorUv;
        var eastU = mirror ? u + sz + sx : u;
        var westU = mirror ? u : u + sz + sx;
        return switch (face) {
            case EAST -> boxFaceQuad(eastU, v + sz, sz, sy, mirror);
            case WEST -> boxFaceQuad(westU, v + sz, sz, sy, mirror);
            case UP -> boxFaceQuad(u + sz, v, sx, sz, mirror);
            case DOWN -> boxFaceQuad(u + sz + sx, v + sz, sx, -sz, mirror);
            case SOUTH -> boxFaceQuad(u + 2 * sz + sx, v + sz, sx, sy, mirror);
            case NORTH -> boxFaceQuad(u + sz, v + sz, sx, sy, mirror);
        };
    }

    private static UvQuad boxFaceQuad(float uPix, float vPix, float uSize, float vSize, boolean mirror) {
        var uA = mirror ? uPix : uPix + uSize;
        var uB = mirror ? uPix + uSize : uPix;
        var vT = vPix;
        var vB = vPix + vSize;
        return new UvQuad(uA, vB, uA, vT, uB, vT, uB, vB);
    }

    private static UvQuad importedUvQuad(ModelerCube.FaceUv uv) {
        var c0 = importedCorner(uv, 1);
        var c1 = importedCorner(uv, 0);
        var c2 = importedCorner(uv, 3);
        var c3 = importedCorner(uv, 2);
        return new UvQuad(c0.u(), c0.v(), c1.u(), c1.v(), c2.u(), c2.v(), c3.u(), c3.v());
    }

    private static UvPoint importedCorner(ModelerCube.FaceUv uv, int index) {
        var shifted = Math.floorMod(index + uv.rotation() / 90, 4);
        var u0 = (float) uv.u();
        var v0 = (float) uv.v();
        var u1 = (float) (uv.u() + uv.width());
        var v1 = (float) (uv.v() + uv.height());
        var u = shifted == 0 || shifted == 1 ? u0 : u1;
        var v = shifted == 0 || shifted == 3 ? v0 : v1;
        return new UvPoint(u, v);
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public record FaceGeometry(
        Vector3f v0,
        Vector3f v1,
        Vector3f v2,
        Vector3f v3,
        Vector3f normal
    ) {

        public Vector3f aAxis() {
            return v3.sub(v0, new Vector3f());
        }

        public Vector3f bAxis() {
            return v1.sub(v0, new Vector3f());
        }

        public Vector3f point(float a, float b, float offset) {
            return new Vector3f(v0)
                .add(new Vector3f(aAxis()).mul(a))
                .add(new Vector3f(bAxis()).mul(b))
                .add(new Vector3f(normal).mul(offset));
        }
    }

    public record UvQuad(
        float u0,
        float v0,
        float u1,
        float v1,
        float u2,
        float v2,
        float u3,
        float v3
    ) {

        public float uAt(float a, float b) {
            return interpolate(u0, u1, u2, u3, a, b);
        }

        public float vAt(float a, float b) {
            return interpolate(v0, v1, v2, v3, a, b);
        }

        public int cellsAlongA() {
            return Math.max(1, Math.round(Math.max(Math.abs(u3 - u0), Math.abs(v3 - v0))));
        }

        public int cellsAlongB() {
            return Math.max(1, Math.round(Math.max(Math.abs(u1 - u0), Math.abs(v1 - v0))));
        }

        public int cellsAlongA(double uvSheetWidth, double uvSheetHeight, int imageWidth, int imageHeight) {
            var scale = pixelScale(uvSheetWidth, uvSheetHeight, imageWidth, imageHeight);
            if (scale == null) {
                return cellsAlongA();
            }
            return Math.max(
                1,
                Math.round(Math.max(Math.abs(u3 - u0) * scale.x(), Math.abs(v3 - v0) * scale.y()))
            );
        }

        public int cellsAlongB(double uvSheetWidth, double uvSheetHeight, int imageWidth, int imageHeight) {
            var scale = pixelScale(uvSheetWidth, uvSheetHeight, imageWidth, imageHeight);
            if (scale == null) {
                return cellsAlongB();
            }
            return Math.max(
                1,
                Math.round(Math.max(Math.abs(u1 - u0) * scale.x(), Math.abs(v1 - v0) * scale.y()))
            );
        }

        public @Nullable Pixel pixelAt(float a, float b, int textureWidth, int textureHeight) {
            return pixelAt(a, b, textureWidth, textureHeight, textureWidth, textureHeight);
        }

        public @Nullable Pixel pixelAt(
            float a,
            float b,
            double uvSheetWidth,
            double uvSheetHeight,
            int imageWidth,
            int imageHeight
        ) {
            var scale = pixelScale(uvSheetWidth, uvSheetHeight, imageWidth, imageHeight);
            if (scale == null) {
                return null;
            }
            var u = uAt(a, b);
            var v = vAt(a, b);
            var minU = (int) Math.floor(Math.min(Math.min(u0, u1), Math.min(u2, u3)));
            var maxU = (int) Math.ceil(Math.max(Math.max(u0, u1), Math.max(u2, u3))) - 1;
            var minV = (int) Math.floor(Math.min(Math.min(v0, v1), Math.min(v2, v3)));
            var maxV = (int) Math.ceil(Math.max(Math.max(v0, v1), Math.max(v2, v3))) - 1;
            var minX = (int) Math.floor(minU * scale.x());
            var maxX = (int) Math.ceil((maxU + 1) * scale.x()) - 1;
            var minY = (int) Math.floor(minV * scale.y());
            var maxY = (int) Math.ceil((maxV + 1) * scale.y()) - 1;
            if (maxX < minX || maxY < minY) {
                return null;
            }
            var x = clamp((int) Math.floor(u * scale.x()), minX, maxX);
            var y = clamp((int) Math.floor(v * scale.y()), minY, maxY);
            if (x < 0 || x >= imageWidth || y < 0 || y >= imageHeight) {
                return null;
            }
            return new Pixel(x, y);
        }

        public @Nullable FaceCell faceCellForPixel(Pixel pixel) {
            return faceCellForUvBounds(pixel.x(), pixel.y(), pixel.x() + 1.0f, pixel.y() + 1.0f);
        }

        public @Nullable FaceCell faceCellForPixel(
            Pixel pixel,
            double uvSheetWidth,
            double uvSheetHeight,
            int imageWidth,
            int imageHeight
        ) {
            var scale = pixelScale(uvSheetWidth, uvSheetHeight, imageWidth, imageHeight);
            if (scale == null
                || pixel.x() < 0
                || pixel.x() >= imageWidth
                || pixel.y() < 0
                || pixel.y() >= imageHeight) {
                return null;
            }
            var u0 = (float) (pixel.x() / scale.x());
            var v0 = (float) (pixel.y() / scale.y());
            var u1 = (float) ((pixel.x() + 1.0) / scale.x());
            var v1 = (float) ((pixel.y() + 1.0) / scale.y());
            return faceCellForUvBounds(u0, v0, u1, v1);
        }

        private @Nullable FaceCell faceCellForUvBounds(float u0, float v0, float u1, float v1) {
            var p0 = facePointForUv(u0, v0);
            var p1 = facePointForUv(u1, v0);
            var p2 = facePointForUv(u1, v1);
            var p3 = facePointForUv(u0, v1);
            if (p0 == null || p1 == null || p2 == null || p3 == null) {
                return null;
            }
            var minA = clamp01(Math.min(Math.min(p0.a(), p1.a()), Math.min(p2.a(), p3.a())));
            var maxA = clamp01(Math.max(Math.max(p0.a(), p1.a()), Math.max(p2.a(), p3.a())));
            var minB = clamp01(Math.min(Math.min(p0.b(), p1.b()), Math.min(p2.b(), p3.b())));
            var maxB = clamp01(Math.max(Math.max(p0.b(), p1.b()), Math.max(p2.b(), p3.b())));
            if (maxA - minA < EPSILON || maxB - minB < EPSILON) {
                return null;
            }
            return new FaceCell(minA, minB, maxA, maxB);
        }

        private @Nullable FacePoint facePointForUv(float u, float v) {
            var duDa = u3 - u0;
            var duDb = u1 - u0;
            var dvDa = v3 - v0;
            var dvDb = v1 - v0;
            var det = duDa * dvDb - duDb * dvDa;
            if (Math.abs(det) < EPSILON) {
                return null;
            }
            var relU = u - u0;
            var relV = v - v0;
            var a = (relU * dvDb - duDb * relV) / det;
            var b = (duDa * relV - relU * dvDa) / det;
            return new FacePoint(a, b);
        }

        private static float interpolate(float c0, float c1, float c2, float c3, float a, float b) {
            var ab0 = c0 * (1 - b) + c1 * b;
            var ab1 = c3 * (1 - b) + c2 * b;
            return ab0 * (1 - a) + ab1 * a;
        }
    }

    private static @Nullable PixelScale pixelScale(double uvSheetWidth, double uvSheetHeight, int imageWidth, int imageHeight) {
        if (uvSheetWidth <= EPSILON || uvSheetHeight <= EPSILON || imageWidth <= 0 || imageHeight <= 0) {
            return null;
        }
        return new PixelScale(imageWidth / (float) uvSheetWidth, imageHeight / (float) uvSheetHeight);
    }

    public record Pixel(
        int x,
        int y
    ) {}

    public record FaceCell(
        float a0,
        float b0,
        float a1,
        float b1
    ) {}

    private record FacePoint(
        float a,
        float b
    ) {}

    private record UvPoint(
        float u,
        float v
    ) {}

    private record PixelScale(
        float x,
        float y
    ) {}
}
