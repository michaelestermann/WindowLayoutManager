package com.layoutmanager.cleanup;

import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;

public class EmptyLayoutRemoverService {
    public void execute() {
        LayoutConfig layoutConfig = ServiceManager.getService(LayoutConfig.class);

        for (Layout layout : layoutConfig.getLayouts()) {
            if (isEmpty(layout)) {
                layoutConfig.removeLayout(layout);
            }
        }
    }

    private boolean isEmpty(Layout layout) {
        return layout.getToolWindows().length == 0;
    }
}
