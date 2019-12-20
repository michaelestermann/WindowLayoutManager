package com.layoutmanager.layout.delete;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.ui.menu.WindowMenuService;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.helpers.NotificationHelper;
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
        showNotification();
    }

    private void deleteLayout() {
        LayoutConfig layoutConfig = ServiceManager.getService(LayoutConfig.class);
        layoutConfig.removeLayout(this.layout);
    }

    private void showNotification() {
        NotificationHelper.info(
                MessagesHelper.message("DeleteLayout.Notification.Title"),
                MessagesHelper.message("DeleteLayout.Notification.Content", this.layout.getName()));
    }

    private void updateWindowMenuItems() {
        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.recreate();
    }
}
