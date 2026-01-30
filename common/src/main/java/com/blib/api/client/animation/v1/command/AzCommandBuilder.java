package com.blib.api.client.animation.v1.command;

import java.util.ArrayList;
import java.util.List;

import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public abstract class AzCommandBuilder {

    protected final List<AzAction> actions;

    protected AzCommandBuilder() {
        this.actions = new ArrayList<>();
    }

    public AzCommand build() {
        return new AzCommand(actions);
    }
}
