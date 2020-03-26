package com.layoutmanager.ui.settings;

import com.intellij.openapi.options.Configurable;

import javax.swing.JComponent;

import com.layoutmanager.persistence.LayoutConfig;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class SettingsPage implements Configurable {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Hello World";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return new LayoutManagerSettingsPanel(LayoutConfig.getInstance().getLayouts())
                .getPanel();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {

    }

    @Override
    public void reset() {

    }
}
