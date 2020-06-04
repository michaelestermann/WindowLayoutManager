package com.layoutmanager.layout.delete;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;

import com.intellij.openapi.project.DumbAware;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.helpers.BaloonNotificationHelper;
import com.layoutmanager.ui.menu.WindowMenuService;

import org.jetbrains.annotations.NotNull;

public class DeleteLayoutAction extends AnAction implements DumbAware {

    private final Layout layout;

    public DeleteLayoutAction(Layout layout) {
        this.layout = layout;
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(layout.getName());
        presentation.setIcon(AllIcons.Actions.GC);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        this.deleteLayout();
        this.updateWindowMenuItems();
        this.showNotification();
    }

    private void deleteLayout() {
        LayoutConfig layoutConfig = ServiceManager.getService(LayoutConfig.class);
        layoutConfig.removeLayout(this.layout);
    }

    private void showNotification() {
        BaloonNotificationHelper.info(
                MessagesHelper.message("DeleteLayout.Notification.Title"),
                MessagesHelper.message("DeleteLayout.Notification.Content", this.layout.getName()));
    }

    private void updateWindowMenuItems() {
        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.recreate();
    }
}
