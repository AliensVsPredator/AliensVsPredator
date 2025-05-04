package com.avp.common.entity.living.gene.behavior;

public interface GeneDecoder<T> {

    T decode(byte value);
}
