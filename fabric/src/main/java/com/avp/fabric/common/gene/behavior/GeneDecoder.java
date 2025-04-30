package com.avp.fabric.common.gene.behavior;

public interface GeneDecoder<T> {

    T decode(byte value);
}
