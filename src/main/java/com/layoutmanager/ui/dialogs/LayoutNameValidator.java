package com.layoutmanager.ui.dialogs;

public class LayoutNameValidator {
    public Boolean isValid(String name) {
        return name != null && !this.isBlank(name);
    }

    private boolean isBlank(String name) {
        return name.trim().isEmpty();
    }
}
