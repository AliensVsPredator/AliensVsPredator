package com.avp.core.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class AcidParticle extends TextureSheetParticle {

    private static final float red = 160 / 255.0f;

    private static final float green = 158 / 255.0f;

    private static final float blue = 9 / 255.0f;

    protected final SpriteSet spriteProvider;

    protected boolean reachedGround;

    public AcidParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, SpriteSet spriteProvider) {
        super(clientWorld, d, e, f);
        xd = g;
        yd = h;
        zd = i;
        var colorRed = Mth.nextFloat(random, red - 0.05f, red + 0.05f);
        var colorGreen = Mth.nextFloat(random, green - 0.05f, green + 0.05f);
        var colorBlue = Mth.nextFloat(random, blue - 0.015f, blue + 0.015f);
        this.setColor(colorRed, colorGreen, colorBlue);
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
        } else {
            this.remove();
        }
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
