package com.layoutmanager.layout.restore;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.ToolWindowInfo;
import com.layoutmanager.ui.helpers.BaloonNotificationHelper;
import com.layoutmanager.ui.helpers.ToolWindowHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestoreLayoutAction extends AnAction implements DumbAware {

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
        this.applyLayout(event, this.layout);
        this.showNotification(this.layout);
    }

    private void applyLayout(AnActionEvent event, Layout layout) {
        applyEditorTabPlacement(layout);
        ToolWindowManager toolWindowManager = getToolWindowManager(event);

        Map<ToolWindowInfo, ToolWindowImpl> toolWindows = getToolWindows(toolWindowManager, layout.getToolWindows());
        hideAllToolWindows(toolWindows);
        applyToolWindowLayout(toolWindows);
    }

    private Map<ToolWindowInfo, ToolWindowImpl> getToolWindows(ToolWindowManager toolWindowManager, ToolWindowInfo[] toolWindows) {
        return Stream.of(toolWindows)
                .map(x -> new Pair<ToolWindowInfo, ToolWindowImpl>(x, (ToolWindowImpl)toolWindowManager.getToolWindow(x.getId())))
                .filter(x -> x.second != null)
                .collect(Collectors.toMap(x -> x.first, x -> x.second));
    }

    private void applyEditorTabPlacement(Layout layout) {

        if (layout.getEditorPlacement() >= 0) {
            UISettings uiSettings = UISettings.getInstance();
            uiSettings.setEditorTabPlacement(layout.getEditorPlacement());
            uiSettings.fireUISettingsChanged();
        }
    }

    private void hideAllToolWindows(Map<ToolWindowInfo, ToolWindowImpl> toolWindows) {
        toolWindows.forEach((info, toolWindow) -> {
            toolWindow.hide(null);
        });
    }

    private void applyToolWindowLayout(Map<ToolWindowInfo, ToolWindowImpl> toolWindows) {

        toolWindows.forEach((info, toolWindow) -> {
            // !! Workaround !!
            // decorator is not set and throws exception. When calling this method, the content manager lazy variable will be loaded and therefor also the decorator...
            // See: https://github.com/JetBrains/intellij-community/blob/a63286c3b29fe467399fb353c71ed15cd65db8dd/platform/platform-impl/src/com/intellij/openapi/wm/impl/ToolWindowImpl.kt
            toolWindow.getComponent();

            toolWindow.setAnchor(ToolWindowAnchor.fromText(info.getAnchor()), null);
            toolWindow.setType(info.getType(), null);
            toolWindow.setSplitMode(info.isToolWindow(), null);

            if (info.isVisible()) {
                toolWindow.show(null);
            }

            ToolWindowHelper.setBounds(toolWindow, info.getBounds());
        });
    }

    private ToolWindowManager getToolWindowManager(AnActionEvent event) {
        Project project = event.getProject();
        return ToolWindowManager.getInstance(project);
    }

    private void showNotification(Layout updatedLayout) {
        BaloonNotificationHelper.info(
                MessagesHelper.message("RestoreLayout.Notification.Title"),
                MessagesHelper.message("RestoreLayout.Notification.Content", updatedLayout.getName()));
    }
}
