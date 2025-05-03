package com.avp.common.entity.gene.behavior;

public interface GeneDecoder<T> {

    T decode(byte value);
}
