package com.layoutmanager.ui.settings;

import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.ui.menu.WindowMenuService;

import java.util.List;

public class WindowMenuChangesApplier {
    public void apply(List<EditLayout> editedLayouts, Layout[] originalLayouts) {
        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);

        for (EditLayout editLayout : editedLayouts) {
            if (editLayout.getOriginalLayout() == null) {
                windowMenuService.addLayout(editLayout.getEditedLayout());
            } else if (editLayout.nameHasChanged()) {
                editLayout.applyNameChange();
                windowMenuService.renameLayout(editLayout.getOriginalLayout());
            }
        }

        for (Layout layout : originalLayouts) {
            if (editedLayouts.stream().noneMatch(x -> x.getOriginalLayout() == layout)) {
                windowMenuService.deleteLayout(layout);
            }
        }
    }
}