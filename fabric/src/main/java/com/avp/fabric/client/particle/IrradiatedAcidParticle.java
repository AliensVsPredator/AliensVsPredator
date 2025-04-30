package com.avp.fabric.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class IrradiatedAcidParticle extends TextureSheetParticle {

    private static final float red = 78 / 255.0f;

    private static final float green = 101 / 255.0f;

    private static final float blue = 229 / 255.0f;

    private boolean isBlue = true;

    protected final SpriteSet spriteProvider;

    protected boolean reachedGround;

    public IrradiatedAcidParticle(
        ClientLevel clientWorld,
        double d,
        double e,
        double f,
        double g,
        double h,
        double i,
        SpriteSet spriteProvider
    ) {
        super(clientWorld, d, e, f);
        xd = g;
        yd = h;
        zd = i;
        this.gravity = 3.0E-6F;
        this.quadSize *= 0.75f;
        this.lifetime = (int) (10.0 / ((random.nextFloat()) * 0.8 + 0.2));
        this.reachedGround = false;
        this.hasPhysics = true;
        this.spriteProvider = spriteProvider;
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.xo = x;
        this.yo = y;
        this.zo = z;

        if (age++ < lifetime && alpha > 0) {
            this.xd += random.nextFloat() / 5000 * (random.nextBoolean() ? 1 : -1);
            this.zd += random.nextFloat() / 5000 * (random.nextBoolean() ? 1 : -1);
            this.yd -= gravity;

            move(xd, yd, zd);

            if (age >= lifetime && alpha > 0.01F) {
                this.alpha -= 0.015F;
            }
            this.setColor();
        } else {
            this.remove();
        }
    }

    private void setColor() {
        if (isBlue) {
            this.setColor(red, green, blue);
        } else {
            this.setColor(1.0f, 1.0f, 1.0f);
        }
        isBlue = !isBlue;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float tickDelta) {
        return quadSize * Mth.clamp(((age) + tickDelta) / (lifetime) * 32.0f, 0.0f, 1.0f);
    }
}
