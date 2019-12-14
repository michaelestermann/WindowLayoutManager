package com.layoutmanager.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.menu.WindowMenuService;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.NotificationHelper;
import org.jetbrains.annotations.NotNull;

public class NewLayoutAction extends AnAction {

    public NewLayoutAction() {
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(MessagesHelper.message("StoreLayout.New.Menu"));
        presentation.setIcon(AllIcons.Welcome.CreateNewProject);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Layout updatedLayout = LayoutCreator.create(event.getProject(), "");

        if (updatedLayout != null) {
            this.storeLayout(updatedLayout);
            updateWindowMenuItems();
            showNotification(updatedLayout);
        }
    }

    private void storeLayout(Layout layout) {
        LayoutConfig.getInstance().addLayout(layout);
    }

    private void updateWindowMenuItems() {
        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.recreate();
    }

    private void showNotification(Layout updatedLayout) {
        NotificationHelper.info(
                MessagesHelper.message("StoreLayout.New.Notification.Title"),
                MessagesHelper.message("StoreLayout.New.Notification.Content", updatedLayout.getName()));
    }
}
