package com.blib.azurelib.common.animation.dispatch.command;

import java.util.ArrayList;
import java.util.List;

import com.blib.azurelib.common.animation.dispatch.command.action.AzAction;

public abstract class AzCommandBuilder {

    protected final List<AzAction> actions;

    protected AzCommandBuilder() {
        this.actions = new ArrayList<>();
    }

    public AzCommand build() {
        return new AzCommand(actions);
    }
}
