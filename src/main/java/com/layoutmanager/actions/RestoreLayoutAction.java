package com.layoutmanager.actions;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.ToolWindowInfo;
import com.layoutmanager.ui.NotificationHelper;
import com.layoutmanager.ui.ToolWindowHelper;
import org.jetbrains.annotations.NotNull;

public class RestoreLayoutAction extends AnAction {

    private final Layout layout;

    public RestoreLayoutAction(Layout layout) {
        super();
        this.layout = layout;

        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(layout.getName());
        presentation.setIcon(AllIcons.Actions.GroupByModule);
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setText(this.layout.getName());
        super.update(e);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        applyLayout(event, this.layout);
        showNotification(this.layout);
    }

    private void applyLayout(AnActionEvent event, Layout layout) {
        applyEditorTabPlacement(layout);
        ToolWindowManager toolWindowManager = getToolWindowManager(event);
        for (ToolWindowInfo toolWindow : layout.getToolWindows()) {
            applyToolWindowLayout(toolWindowManager, toolWindow);
        }
    }

    private void applyEditorTabPlacement(Layout layout) {

        if (layout.getEditorPlacement() >= 0) {
            UISettings uiSettings = UISettings.getInstance();
            uiSettings.setEditorTabPlacement(layout.getEditorPlacement());
            uiSettings.fireUISettingsChanged();
        }
    }

    private void applyToolWindowLayout(ToolWindowManager toolWindowManager, ToolWindowInfo info) {
        ToolWindowImpl toolWindow = (ToolWindowImpl)toolWindowManager.getToolWindow(info.getId());

        if (toolWindow != null) {
            toolWindow.hide(null);

            toolWindow.setAnchor(ToolWindowAnchor.fromText(info.getAnchor()), null);
            toolWindow.setType(info.getType(), null);
            toolWindow.setSplitMode(info.isToolWindow(), null);
            ToolWindowHelper.setBounds(toolWindow, info.getBounds());

            if (info.isVisible()) {
                toolWindow.show(null);
            }
        }
    }

    private ToolWindowManager getToolWindowManager(AnActionEvent event) {
        Project project = event.getProject();
        return ToolWindowManager.getInstance(project);
    }
    private void showNotification(Layout updatedLayout) {
        NotificationHelper.info(
                MessagesHelper.message("RestoreLayout.Notification.Title"),
                MessagesHelper.message("RestoreLayout.Notification.Content", updatedLayout.getName()));
    }
}
