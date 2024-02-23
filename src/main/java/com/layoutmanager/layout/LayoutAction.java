package com.layoutmanager.layout;

import com.intellij.openapi.actionSystem.AnAction;
import com.layoutmanager.persistence.Layout;

public abstract class LayoutAction extends AnAction {
    public abstract Layout getLayout();
}
