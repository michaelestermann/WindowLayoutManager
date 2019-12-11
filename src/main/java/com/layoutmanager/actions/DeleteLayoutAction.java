package com.layoutmanager.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.menu.WindowMenuService;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import org.jetbrains.annotations.NotNull;

public class DeleteLayoutAction extends AnAction {

    private final Layout layout;

    public DeleteLayoutAction(Layout layout) {
        this.layout = layout;
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(layout.getName());
        presentation.setIcon(AllIcons.Actions.GC);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        deleteLayout();
        updateWindowMenuItems();
    }

    private void deleteLayout() {
        LayoutConfig layoutConfig = ServiceManager.getService(LayoutConfig.class);
        layoutConfig.removeLayout(this.layout);
    }

    private void updateWindowMenuItems() {
        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.recreate();
    }
}
