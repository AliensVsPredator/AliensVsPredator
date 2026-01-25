package com.blib.azurelib.common.animation.dispatch.command;

import java.util.ArrayList;
import java.util.List;

import com.blib.azurelib.common.animation.dispatch.command.action.AzAction;

/**
 * AzCommandBuilder is an abstract base class designed for constructing
 * {@link com.blib.azurelib.common.animation.dispatch.command.AzCommand} objects by aggregating a series of
 * {@link AzAction} instances. It allows for flexible configuration of animation commands, enabling derived classes to
 * define and append various animation-related actions.
 */
public abstract class AzCommandBuilder {

    protected final List<AzAction> actions;

    protected AzCommandBuilder() {
        this.actions = new ArrayList<>();
    }

    public com.blib.azurelib.common.animation.dispatch.command.AzCommand build() {
        return new AzCommand(actions);
    }
}
