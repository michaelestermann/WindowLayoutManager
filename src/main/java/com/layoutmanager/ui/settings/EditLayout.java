package com.layoutmanager.ui.settings;

import org.jetbrains.annotations.NotNull;

import com.layoutmanager.persistence.Layout;

public record EditLayout(Layout originalLayout, Layout editedLayout) {
    public EditLayout(
            Layout originalLayout,
            @NotNull Layout editedLayout) {
        this.originalLayout = originalLayout;
        this.editedLayout = editedLayout;
    }

    public boolean nameHasChanged() {
        return this.originalLayout() == null ||
                !this.originalLayout.getName().equals(this.editedLayout.getName());
    }

    public void applyNameChange() {
        this.originalLayout.setName(this.editedLayout.getName());
    }
}
