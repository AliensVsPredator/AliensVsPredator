package org.avp.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MixinMinecraft_Accessor {

    @Accessor("rightClickDelay")
    public void setRightClickDelay(int rightClickDelay);
}
