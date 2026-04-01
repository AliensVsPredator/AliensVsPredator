package com.blib.internal.common.faction;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.util.v1.Dirty;

@ApiStatus.Internal
public class BLibFactionData implements Dirty {

    private static final String KEY_NAME = "name";

    private static final String KEY_COLOR = "color";

    private static final String KEY_MOD_DATA = "mod_data";

    private String name;

    private int color;

    private @Nullable FactionData modData;

    private boolean dirty;

    public BLibFactionData(String name, int color, @Nullable FactionData modData) {
        this.name = name;
        this.color = color;
        this.modData = modData;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        markDirty();
    }

    public int color() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        markDirty();
    }

    public @Nullable FactionData modData() {
        return modData;
    }

    public void setModData(FactionData modData) {
        this.modData = modData;
    }

    public void save(CompoundTag tag) {
        tag.putString(KEY_NAME, name);
        tag.putInt(KEY_COLOR, color);

        if (modData != null) {
            var modDataTag = new CompoundTag();
            modData.save(modDataTag);
            tag.put(KEY_MOD_DATA, modDataTag);
        }
    }

    public void load(CompoundTag tag) {
        name = tag.getString(KEY_NAME);
        color = tag.getInt(KEY_COLOR);

        if (modData != null && tag.contains(KEY_MOD_DATA)) {
            modData.load(tag.getCompound(KEY_MOD_DATA));
        }
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty || (modData != null && modData.isDirty());
    }

    @Override
    public void clearDirty() {
        dirty = false;

        if (modData != null) {
            modData.clearDirty();
        }
    }

    public static int randomColor() {
        var hue = (float) Math.random();

        return Color.HSBtoRGB(hue, 0.7f, 0.9f) & 0xFFFFFF;
    }
}
