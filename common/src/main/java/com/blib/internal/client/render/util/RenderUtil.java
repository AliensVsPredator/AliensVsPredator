package com.blib.internal.client.render.util;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.blib.api.client.model.v1.AzBone;
import com.blib.internal.client.model.GeoCube;
import com.blib.mod.BLib;

public class RenderUtil {

    private static final Matrix4f TRANSLATE_MATRIX_CACHE = new Matrix4f();

    private static final Quaternionf X_QUATERNION_CACHE = new Quaternionf();

    private static final Quaternionf Y_QUATERNION_CACHE = new Quaternionf();

    private static final Quaternionf Z_QUATERNION_CACHE = new Quaternionf();

    public static void translateMatrixToBone(PoseStack poseStack, AzBone bone) {
        poseStack.translate(-bone.getPosX() / 16f, bone.getPosY() / 16f, bone.getPosZ() / 16f);
    }

    public static void rotateMatrixAroundBone(PoseStack poseStack, AzBone bone) {
        float rotX = bone.getRotX();
        float rotY = bone.getRotY();
        float rotZ = bone.getRotZ();

        if (rotZ != 0)
            poseStack.mulPose(Z_QUATERNION_CACHE.rotationXYZ(0f, 0f, rotZ));

        if (rotY != 0)
            poseStack.mulPose(Y_QUATERNION_CACHE.rotationXYZ(0f, rotY, 0f));

        if (rotX != 0)
            poseStack.mulPose(X_QUATERNION_CACHE.rotationXYZ(rotX, 0f, 0f));
    }

    public static void rotateMatrixAroundCube(PoseStack poseStack, GeoCube cube) {
        Vec3 rotation = cube.rotation();

        if (rotation.z() != 0f) {
            Z_QUATERNION_CACHE.identity().rotateZ((float) rotation.z());
            poseStack.mulPose(Z_QUATERNION_CACHE);
        }
        if (rotation.y() != 0f) {
            Y_QUATERNION_CACHE.identity().rotateY((float) rotation.y());
            poseStack.mulPose(Y_QUATERNION_CACHE);
        }
        if (rotation.x() != 0f) {
            X_QUATERNION_CACHE.identity().rotateX((float) rotation.x());
            poseStack.mulPose(X_QUATERNION_CACHE);
        }
    }

    public static void scaleMatrixForBone(PoseStack poseStack, AzBone bone) {
        poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static void translateToPivotPoint(PoseStack poseStack, GeoCube cube) {
        Vec3 pivot = cube.pivot();
        poseStack.translate(pivot.x() / 16f, pivot.y() / 16f, pivot.z() / 16f);
    }

    public static void translateToPivotPoint(PoseStack poseStack, AzBone bone) {
        poseStack.translate(bone.getPivotX() / 16f, bone.getPivotY() / 16f, bone.getPivotZ() / 16f);
    }

    public static void translateAwayFromPivotPoint(PoseStack poseStack, GeoCube cube) {
        Vec3 pivot = cube.pivot();

        poseStack.translate(-pivot.x() / 16f, -pivot.y() / 16f, -pivot.z() / 16f);
    }

    public static void translateAwayFromPivotPoint(PoseStack poseStack, AzBone bone) {
        poseStack.translate(-bone.getPivotX() / 16f, -bone.getPivotY() / 16f, -bone.getPivotZ() / 16f);
    }

    public static void translateAndRotateMatrixForBone(PoseStack poseStack, AzBone bone) {
        translateToPivotPoint(poseStack, bone);
        rotateMatrixAroundBone(poseStack, bone);
    }

    public static void prepMatrixForBone(PoseStack poseStack, AzBone bone) {
        translateMatrixToBone(poseStack, bone);
        translateToPivotPoint(poseStack, bone);
        rotateMatrixAroundBone(poseStack, bone);
        scaleMatrixForBone(poseStack, bone);
        translateAwayFromPivotPoint(poseStack, bone);
    }

    public static void applyCubeInflation(PoseStack poseStack, GeoCube cube, float inflate) {
        var size = cube.size();
        var sizeX = (float) size.x() / 16f;
        var sizeY = (float) size.y() / 16f;
        var sizeZ = (float) size.z() / 16f;

        if (sizeX <= 0 && sizeY <= 0 && sizeZ <= 0) {
            return;
        }

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

        for (var quad : cube.quads()) {
            if (quad == null)
                continue;

            for (var vertex : quad.vertices()) {
                var pos = vertex.position();
                minX = Math.min(minX, pos.x());
                maxX = Math.max(maxX, pos.x());
                minY = Math.min(minY, pos.y());
                maxY = Math.max(maxY, pos.y());
                minZ = Math.min(minZ, pos.z());
                maxZ = Math.max(maxZ, pos.z());
            }
        }

        var centerX = (minX + maxX) / 2f;
        var centerY = (minY + maxY) / 2f;
        var centerZ = (minZ + maxZ) / 2f;

        var extentX = (maxX - minX) / 2f;
        var extentY = (maxY - minY) / 2f;
        var extentZ = (maxZ - minZ) / 2f;

        var scaleX = extentX > 0 ? (extentX + inflate) / extentX : 1f;
        var scaleY = extentY > 0 ? (extentY + inflate) / extentY : 1f;
        var scaleZ = extentZ > 0 ? (extentZ + inflate) / extentZ : 1f;

        poseStack.translate(centerX, centerY, centerZ);
        poseStack.scale(scaleX, scaleY, scaleZ);
        poseStack.translate(-centerX, -centerY, -centerZ);
    }

    public static Matrix4f invertAndMultiplyMatrices(Matrix4f baseMatrix, Matrix4f inputMatrix) {
        inputMatrix = new Matrix4f(inputMatrix);

        inputMatrix.invert();
        inputMatrix.mul(baseMatrix);

        return inputMatrix;
    }

    public static void faceRotation(PoseStack poseStack, Entity animatable, float partialTick) {
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
    }

    public static Matrix4f translateMatrix(Matrix4f matrix, Vector3f vector) {
        TRANSLATE_MATRIX_CACHE.m30(vector.x).m31(vector.y).m32(vector.z);
        return matrix.add(TRANSLATE_MATRIX_CACHE);
    }

    @Nullable
    public static IntIntPair getTextureDimensions(ResourceLocation texture) {
        if (texture == null)
            return null;

        AbstractTexture originalTexture = null;
        Minecraft mc = Minecraft.getInstance();

        try {
            originalTexture = mc.submit(() -> mc.getTextureManager().getTexture(texture)).get();
        } catch (Exception e) {
            BLib.LOGGER.warn("Failed to load image for id {}", texture);
            e.printStackTrace();
        }

        if (originalTexture == null)
            return null;

        NativeImage image = null;

        try {
            image = originalTexture instanceof DynamicTexture dynamicTexture
                ? dynamicTexture.getPixels()
                : NativeImage.read(mc.getResourceManager().getResource(texture).get().open());
        } catch (Exception e) {
            BLib.LOGGER.error("Failed to read image for id {}", texture);
            e.printStackTrace();
        }

        return image == null ? null : IntIntImmutablePair.of(image.getWidth(), image.getHeight());
    }

    public static double getCurrentSystemTick() {
        return System.nanoTime() / 1E6 / 50d;
    }

    public static double getCurrentTick() {
        return Blaze3D.getTime() * 20d;
    }

    public static float booleanToFloat(boolean input) {
        return input ? 1f : 0f;
    }

    public static Vec3 arrayToVec(double[] array) {
        return new Vec3(array[0], array[1], array[2]);
    }

    public static void matchModelPartRot(ModelPart from, AzBone to) {
        to.updateRotation(-from.xRot, -from.yRot, from.zRot);
    }

    public static void fixInvertedFlatCube(GeoCube cube, Vector3f normal) {
        if (normal.x() < 0 && (cube.size().y() == 0 || cube.size().z() == 0))
            normal.mul(-1, 1, 1);

        if (normal.y() < 0 && (cube.size().x() == 0 || cube.size().z() == 0))
            normal.mul(1, -1, 1);

        if (normal.z() < 0 && (cube.size().x() == 0 || cube.size().y() == 0))
            normal.mul(1, 1, -1);
    }

    public static float getDirectionAngle(Direction direction) {
        return switch (direction) {
            case SOUTH -> 90f;
            case NORTH -> 270f;
            case EAST -> 180f;
            default -> 0f;
        };
    }
}
