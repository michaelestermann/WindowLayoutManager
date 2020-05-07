package com.layoutmanager.ui.settings;

import com.intellij.openapi.options.Configurable;

import javax.swing.JComponent;

import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.dialogs.LayoutNameDialog;
import com.layoutmanager.ui.dialogs.LayoutNameValidator;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class SettingsPage implements Configurable {
    private final LayoutManagerSettingsPanel panel;

    public SettingsPage() {
        LayoutNameValidator layoutNameValidator = new LayoutNameValidator();
        this.panel = new LayoutManagerSettingsPanel(
                LayoutConfig.getInstance(),
                new LayoutNameDialog(layoutNameValidator),
                layoutNameValidator,
                new LayoutSerializer());
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return MessagesHelper.message("SettingsPage.Title");
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return this.panel.getPanel();
    }

    @Override
    public boolean isModified() {
        return this.panel.hasChanged();
    }

    @Override
    public void apply() {
        this.panel.apply();
    }

    @Override
    public void reset() {
    }
}
