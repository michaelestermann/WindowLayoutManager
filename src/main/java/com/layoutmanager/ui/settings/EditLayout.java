package com.layoutmanager.ui.settings;

import com.layoutmanager.persistence.Layout;
import org.jetbrains.annotations.NotNull;

public class EditLayout {
    private final Layout originalLayout;
    private final Layout editedLayout;

    public EditLayout(
            Layout originalLayout,
            @NotNull Layout editedLayout) {
        this.originalLayout = originalLayout;
        this.editedLayout = editedLayout;
    }

    public Layout getOriginalLayout() {
        return this.originalLayout;
    }

    public Layout getEditedLayout() {
        return this.editedLayout;
    }

    public boolean nameHasChanged() {
        return this.getOriginalLayout() == null ||
               !this.originalLayout.getName().equals(this.editedLayout.getName());
    }

    public void applyNameChange() {
        this.originalLayout.setName(this.editedLayout.getName());
    }
}
