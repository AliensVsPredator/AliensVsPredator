package com.avp.core.common.gene.behavior;

public interface GeneDecoder<T> {

    T decode(byte value);
}
