package com.avp.common.gene.behavior;

public interface GeneDecoder<T> {

    T decode(byte value);
}
