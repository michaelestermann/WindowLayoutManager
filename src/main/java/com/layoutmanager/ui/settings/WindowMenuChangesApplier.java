package com.layoutmanager.ui.settings;

import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.ui.menu.WindowMenuService;

import java.util.List;

public class WindowMenuChangesApplier {
    public void apply(List<EditLayout> editedLayouts, Layout[] originalLayouts) {
        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);

        addOrUpdateLayouts(editedLayouts, windowMenuService);

        removeObsoleteLayouts(editedLayouts, originalLayouts, windowMenuService);
    }

    private void addOrUpdateLayouts(List<EditLayout> editedLayouts, WindowMenuService windowMenuService) {
        for (EditLayout editLayout : editedLayouts) {
            if (isNew(editLayout)) {
                windowMenuService.addLayout(editLayout.getEditedLayout());
            } else if (editLayout.nameHasChanged()) {
                editLayout.applyNameChange();
                windowMenuService.renameLayout(editLayout.getOriginalLayout());
            }
        }
    }

    private boolean isNew(EditLayout editLayout) {
        return editLayout.getOriginalLayout() == null;
    }

    private void removeObsoleteLayouts(List<EditLayout> editedLayouts, Layout[] originalLayouts, WindowMenuService windowMenuService) {
        for (Layout layout : originalLayouts) {
            if (editedLayouts.stream().noneMatch(x -> x.getOriginalLayout().equals(layout))) {
                windowMenuService.deleteLayout(layout);
            }
        }
    }
}