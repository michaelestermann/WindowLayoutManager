package com.layoutmanager.ui.settings;

import java.util.List;

import com.intellij.openapi.application.ApplicationManager;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.ui.menu.WindowMenuService;

public class WindowMenuChangesApplier {
    public void apply(List<EditLayout> editedLayouts, Layout[] originalLayouts) {
        WindowMenuService windowMenuService = ApplicationManager
                .getApplication()
                .getService(WindowMenuService.class);

        this.addOrUpdateLayouts(editedLayouts, windowMenuService);

        this.removeObsoleteLayouts(editedLayouts, originalLayouts, windowMenuService);
    }

    private void addOrUpdateLayouts(List<EditLayout> editedLayouts, WindowMenuService windowMenuService) {
        for (EditLayout editLayout : editedLayouts) {
            if (this.isNew(editLayout)) {
                windowMenuService.addLayout(editLayout.editedLayout());
            } else if (editLayout.nameHasChanged()) {
                editLayout.applyNameChange();
                windowMenuService.renameLayout(editLayout.originalLayout());
            }
        }
    }

    private boolean isNew(EditLayout editLayout) {
        return editLayout.originalLayout() == null;
    }

    private void removeObsoleteLayouts(List<EditLayout> editedLayouts, Layout[] originalLayouts, WindowMenuService windowMenuService) {
        for (Layout layout : originalLayouts) {
            if (editedLayouts.stream().noneMatch(x -> x.originalLayout().equals(layout))) {
                windowMenuService.deleteLayout(layout);
            }
        }
    }
}