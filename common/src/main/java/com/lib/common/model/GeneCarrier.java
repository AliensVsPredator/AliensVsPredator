package com.lib.common.model;

import com.lib.common.gameplay.entity.manager.GeneManager;
import org.jetbrains.annotations.Nullable;

public interface GeneCarrier {

    GeneManager getOrCreateGeneManager();

    void setGeneManager(@Nullable GeneManager geneManager);
}
