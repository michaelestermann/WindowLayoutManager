package com.layoutmanager.layout.store.overwrite;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.layoutmanager.layout.LayoutAction;
import com.layoutmanager.layout.store.LayoutCreator;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.ui.helpers.BalloonNotificationHelper;
import com.layoutmanager.ui.icons.Icons;
import com.layoutmanager.ui.menu.WindowMenuService;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class OverwriteLayoutAction
        extends LayoutAction
        implements DumbAware {

    private final LayoutCreator layoutCreator;
    public final Layout layout;

    public OverwriteLayoutAction(
            LayoutCreator layoutCreator,
            Layout layout) {
        this.layoutCreator = layoutCreator;
        this.layout = layout;

        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(this.layout.getName());
        presentation.setIcon(Icons.Menu.OverwriteLayout);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setText(this.layout.getName());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (this.isProjectLoaded(event)) {
            ToolWindowManager toolWindowManager = this.getToolWindowManager(event);
            String previousName = this.layout.getName();
            Layout updatedLayout = this.layoutCreator.create(
                    toolWindowManager,
                    this.layout.getId(),
                    this.layout.getName());

            if (updatedLayout != null) {
                this.updateLayout(updatedLayout);
                this.updateWindowMenu(previousName);
                this.showNotification(this.layout.getName(), previousName);
            }
        }
    }

    public Layout getLayout() {
        return this.layout;
    }


    private void updateLayout(Layout updatedLayout) {
        this.layout.setEditorPlacement(updatedLayout.getEditorPlacement());
        this.layout.setWideScreenSupport(updatedLayout.getWideScreenSupport());
        this.layout.setName(updatedLayout.getName());
        this.layout.setToolWindows(updatedLayout.getToolWindows());
    }

    private void updateWindowMenu(String previousName) {
        if (!previousName.equals(this.layout.getName())) {
            WindowMenuService windowMenuService = ApplicationManager
                    .getApplication()
                    .getService(WindowMenuService.class);
            windowMenuService.renameLayout(this.layout);
        }
    }

    private void showNotification(String currentLayoutName, String previousLayoutName) {
        BalloonNotificationHelper.info(
                MessagesHelper.message("StoreLayout.Overwrite.Notification.Title"),
                MessagesHelper.message("StoreLayout.Overwrite.Notification.Content", previousLayoutName, currentLayoutName));
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
