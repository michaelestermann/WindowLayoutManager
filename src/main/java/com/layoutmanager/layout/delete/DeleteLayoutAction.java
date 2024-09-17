package com.layoutmanager.layout.delete;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.layoutmanager.layout.LayoutAction;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.helpers.BalloonNotificationHelper;
import com.layoutmanager.ui.icons.Icons;
import com.layoutmanager.ui.menu.WindowMenuService;


public class DeleteLayoutAction
        extends LayoutAction
        implements DumbAware {

    private final Layout layout;

    public DeleteLayoutAction(Layout layout) {
        this.layout = layout;
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(layout.getName());
        presentation.setIcon(Icons.Menu.DeleteLayout);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        this.deleteLayout();
        this.updateWindowMenuItems();
        this.showNotification();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setText(this.layout.getName());
    }

    public Layout getLayout() {
        return this.layout;
    }

    private void deleteLayout() {
        LayoutConfig layoutConfig = LayoutConfig.getInstance();
        layoutConfig.removeLayout(this.layout);
    }

    private void showNotification() {
        BalloonNotificationHelper.info(
                MessagesHelper.message("DeleteLayout.Notification.Title"),
                MessagesHelper.message("DeleteLayout.Notification.Content", this.layout.getName()));
    }

    private void updateWindowMenuItems() {
        WindowMenuService windowMenuService = ApplicationManager
                .getApplication()
                .getService(WindowMenuService.class);
        windowMenuService.deleteLayout(this.layout);
    }
}
