package com.layoutmanager.layout.store.create;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.wm.ToolWindowManager;

import com.layoutmanager.layout.store.LayoutCreator;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.helpers.NotificationHelper;
import com.layoutmanager.ui.menu.WindowMenuService;

import org.jetbrains.annotations.NotNull;

public class NewLayoutAction extends AnAction {

    private final LayoutCreator layoutCreator;

    public NewLayoutAction(LayoutCreator layoutCreator) {
        this.layoutCreator = layoutCreator;
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(MessagesHelper.message("StoreLayout.New.Menu"));
        presentation.setIcon(AllIcons.Welcome.CreateNewProject);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(event.getProject());
        Layout updatedLayout = this.layoutCreator.create(toolWindowManager, "");

        if (updatedLayout != null) {
            this.storeLayout(updatedLayout);
            this.updateWindowMenuItems();
            this.showNotification(updatedLayout);
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
