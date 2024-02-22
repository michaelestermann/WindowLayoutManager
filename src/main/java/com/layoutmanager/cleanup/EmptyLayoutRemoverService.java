package com.layoutmanager.cleanup;

import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;

public class EmptyLayoutRemoverService {
    public void execute() {
        LayoutConfig layoutConfig = LayoutConfig.getInstance();

        for (Layout layout : layoutConfig.getLayouts()) {
            if (this.isEmpty(layout)) {
                layoutConfig.removeLayout(layout);
            }
        }
    }

    private boolean isEmpty(Layout layout) {
        return layout.getToolWindows().length == 0;
    }
}
