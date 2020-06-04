package com.layoutmanager.layout.store.create;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.wm.ToolWindowManager;

import com.layoutmanager.layout.store.LayoutCreator;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.helpers.BaloonNotificationHelper;
import com.layoutmanager.ui.menu.WindowMenuService;

import org.jetbrains.annotations.NotNull;

public class NewLayoutAction extends AnAction implements DumbAware {

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
        Layout newLayout = this.layoutCreator.create(toolWindowManager, "");

        if (newLayout != null) {
            this.storeLayout(newLayout);
            this.updateWindowMenuItems();
            this.showNotification(newLayout);
        }
    }

    private void storeLayout(Layout layout) {
        LayoutConfig.getInstance().addLayout(layout);
    }

    private void updateWindowMenuItems() {
        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.recreate();
    }

    private void showNotification(Layout newLayout) {
        BaloonNotificationHelper.info(
                MessagesHelper.message("StoreLayout.New.Notification.Title"),
                MessagesHelper.message("StoreLayout.New.Notification.Content", newLayout.getName()));
    }
}
