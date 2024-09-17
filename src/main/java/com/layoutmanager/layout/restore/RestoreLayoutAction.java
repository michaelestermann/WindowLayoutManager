package com.layoutmanager.layout.restore;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.util.Alarm;
import com.intellij.util.concurrency.EdtExecutorService;
import com.layoutmanager.layout.LayoutAction;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.ToolWindowInfo;
import com.layoutmanager.ui.helpers.BalloonNotificationHelper;
import com.layoutmanager.ui.helpers.ToolWindowHelper;
import com.layoutmanager.ui.icons.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestoreLayoutAction
        extends LayoutAction
        implements DumbAware {

    private final Layout layout;

    public RestoreLayoutAction(Layout layout) {
        super();
        this.layout = layout;

        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(layout.getName());
        presentation.setIcon(Icons.Menu.RestoreLayout);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(AnActionEvent actionEvent) {
        actionEvent
                .getPresentation()
                .setText(this.layout.getName());

        super.update(actionEvent);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (this.isProjectLoaded(event)) {
            this.applyLayout(event, this.layout);
            this.showNotification(this.layout);
        }
    }

    public Layout getLayout() {
        return this.layout;
    }

    private void applyLayout(AnActionEvent event, Layout layout) {
        applyEditorTabPlacement(layout);
        ToolWindowManager toolWindowManager = getToolWindowManager(event);

        Map<ToolWindowInfo, ToolWindowEx> toolWindows = getToolWindows(toolWindowManager, layout.getToolWindows());
        hideAllToolWindows(toolWindows);
        applyToolWindowLayout(toolWindows);
    }

    private Map<ToolWindowInfo, ToolWindowEx> getToolWindows(ToolWindowManager toolWindowManager, ToolWindowInfo[] toolWindows) {
        return Stream
                .of(toolWindows)
                .map(x -> new Pair<>(x, (ToolWindowEx) toolWindowManager.getToolWindow(x.getId())))
                .filter(x -> x.second != null)
                .collect(Collectors.toMap(x -> x.first, x -> x.second));
    }

    private void applyEditorTabPlacement(Layout layout) {
        if (layout.getEditorPlacement() >= 0) {
            UISettings uiSettings = UISettings.getInstance();
            uiSettings.setEditorTabPlacement(layout.getEditorPlacement());
            uiSettings.setWideScreenSupport(layout.getWideScreenSupport());
            uiSettings.fireUISettingsChanged();
        }
    }

    private void hideAllToolWindows(Map<ToolWindowInfo, ToolWindowEx> toolWindows) {
        toolWindows.forEach((info, toolWindow) -> {
            if (!info.isVisible()) {
                toolWindow.hide(null);
            }
        });
    }

    private void applyToolWindowLayout(Map<ToolWindowInfo, ToolWindowEx> toolWindows) {
        toolWindows.forEach((info, toolWindow) -> {
            if (info.isVisible()) {
                // !! Workaround !!
                // decorator is not set and throws exception. When calling this method, the content manager lazy variable will be loaded and therefore also the decorator...
                // See: https://github.com/JetBrains/intellij-community/blob/a63286c3b29fe467399fb353c71ed15cd65db8dd/platform/platform-impl/src/com/intellij/openapi/wm/impl/ToolWindowImpl.kt
                toolWindow.getComponent();

                toolWindow.setAnchor(ToolWindowAnchor.fromText(info.getAnchor()), null);
                toolWindow.setType(info.getType(), null);
                toolWindow.setSplitMode(info.isToolWindow(), null);

                ToolWindowHelper.setBounds(toolWindow, info.getBounds());

                toolWindow.show();
            }
        });
    }

    private ToolWindowManager getToolWindowManager(AnActionEvent event) {
        Project project = event.getProject();
        return ToolWindowManager.getInstance(
                Objects.requireNonNull(project));
    }

    private void showNotification(Layout updatedLayout) {
        BalloonNotificationHelper.info(
                MessagesHelper.message("RestoreLayout.Notification.Title"),
                MessagesHelper.message("RestoreLayout.Notification.Content", updatedLayout.getName()));
    }

    private boolean isProjectLoaded(AnActionEvent event) {
        return event.getProject() != null;
    }
}
