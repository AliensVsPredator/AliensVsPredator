package com.avp.common.goap.condition;

import java.util.ArrayList;

public class GOAPMutableConditionContainer extends GOAPConditionContainer {

    public GOAPMutableConditionContainer() {
        super(new ArrayList<>());
    }

    public void addCondition(GOAPCondition<?> condition) {
        conditions.add(condition);
    }
}
