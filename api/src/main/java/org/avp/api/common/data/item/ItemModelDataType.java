package org.avp.api.common.data.item;

import net.minecraft.world.item.Item;

public sealed interface ItemModelDataType {

    enum GenType {
        FLAT,
        HANDHELD,
        NONE
    }

    GenType getGenType();

    record Flat(Item item) implements ItemModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.FLAT;
        }
    }

    record HandHeld(Item item) implements ItemModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.HANDHELD;
        }
    }

    record None() implements ItemModelDataType {

        @Override
        public GenType getGenType() {
            return GenType.NONE;
        }
    }
}
