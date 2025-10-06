package com.human.common.gameplay.entity.living.human.marine.ai.utility.general;

public interface Strategy<M, C, R> {

    boolean matches(M matchable);

    double score(M matchable, C context);

    R execute(C context);
}
