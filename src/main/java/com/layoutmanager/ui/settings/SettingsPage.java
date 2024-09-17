package com.layoutmanager.ui.settings;

import javax.swing.JComponent;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.options.Configurable;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.dialogs.LayoutNameDialog;
import com.layoutmanager.ui.dialogs.LayoutNameValidator;

public class SettingsPage implements Configurable {
    private final LayoutManagerSettingsPanel panel;

    public SettingsPage() {
        LayoutNameValidator layoutNameValidator = new LayoutNameValidator();
        LayoutSerializer layoutSerializer = new LayoutSerializer();
        this.panel = new LayoutManagerSettingsPanel(
                LayoutConfig.getInstance(),
                new LayoutNameDialog(layoutNameValidator),
                layoutNameValidator,
                layoutSerializer,
                new LayoutDuplicator(layoutSerializer),
                new WindowMenuChangesApplier());
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
}
