package com.layoutmanager.layout.store.create;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.layoutmanager.layout.store.LayoutCreator;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.helpers.BalloonNotificationHelper;
import com.layoutmanager.ui.icons.Icons;
import com.layoutmanager.ui.menu.WindowMenuService;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NewLayoutAction
        extends AnAction
        implements DumbAware {

    private final LayoutCreator layoutCreator;
    private final int id;

    public NewLayoutAction(LayoutCreator layoutCreator, int id) {
        this.layoutCreator = layoutCreator;
        this.id = id;
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(MessagesHelper.message("StoreLayout.New.Menu"));
        presentation.setIcon(Icons.Menu.CreateNewLayout);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (this.isProjectLoaded(event)) {
            ToolWindowManager toolWindowManager = this.getToolWindowManager(event);
            Layout newLayout = this.layoutCreator.create(
                    toolWindowManager,
                    this.id,
                    "");

            if (newLayout != null) {
                this.storeLayout(newLayout);
                this.updateWindowMenuItems(newLayout);
                this.showNotification(newLayout);
            }
        }
    }

    private void storeLayout(Layout layout) {
        LayoutConfig
                .getInstance()
                .addLayout(layout);
    }

    private void updateWindowMenuItems(Layout newLayout) {
        WindowMenuService windowMenuService = ApplicationManager
                .getApplication()
                .getService(WindowMenuService.class);
        windowMenuService.addLayout(newLayout);
    }

    private void showNotification(Layout newLayout) {
        BalloonNotificationHelper.info(
                MessagesHelper.message("StoreLayout.New.Notification.Title"),
                MessagesHelper.message("StoreLayout.New.Notification.Content", newLayout.getName()));
    }

    private ToolWindowManager getToolWindowManager(AnActionEvent event) {
        Project project = event.getProject();
        return ToolWindowManager.getInstance(
                Objects.requireNonNull(project));
    }

    private boolean isProjectLoaded(AnActionEvent event) {
        return event.getProject() != null;
    }
}
