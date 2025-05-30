package com.lib.common.gameplay.gene.decoder;

public interface GeneDecoder<T> {

    T decode(byte value);
}
