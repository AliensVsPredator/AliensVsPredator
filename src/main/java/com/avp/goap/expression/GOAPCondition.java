package com.avp.goap.expression;

import com.avp.goap.TypedIdentifier;

public record GOAPCondition<T>(
    TypedIdentifier<? super T> identifier,
    GOAPExpression<? super T> expression
) {}
