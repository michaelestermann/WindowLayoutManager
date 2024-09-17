package com.layoutmanager.layout.store;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.layoutmanager.layout.store.smartdock.SmartDocker;
import com.layoutmanager.layout.store.smartdock.SmartDockerFactory;
import com.layoutmanager.layout.store.validation.LayoutValidationHelper;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutSettings;
import com.layoutmanager.persistence.ToolWindowInfo;
import com.layoutmanager.ui.dialogs.LayoutNameDialog;
import com.layoutmanager.ui.helpers.BalloonNotificationHelper;
import com.layoutmanager.ui.helpers.ToolWindowHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class LayoutCreator {
    private final LayoutSettings layoutSettings;
    private final SmartDockerFactory smartDockerFactory;
    private final LayoutNameDialog layoutNameDialog;

    public LayoutCreator(
            LayoutSettings layoutSettings,
            SmartDockerFactory smartDockerFactory,
            LayoutNameDialog layoutNameDialog) {
        this.layoutSettings = layoutSettings;
        this.smartDockerFactory = smartDockerFactory;
        this.layoutNameDialog = layoutNameDialog;
    }

    public Layout create(
            ToolWindowManager toolWindowManager,
            int id,
            String defaultName) {

        String name = this.layoutNameDialog.show(defaultName);
        return name != null ?
                this.createLayout(toolWindowManager, id, name) :
                null;
    }

    private Layout createLayout(ToolWindowManager toolWindowManager, int id, String name) {
        List<ToolWindowInfo> toolWindows = getToolWindows(toolWindowManager);
        Layout layout = new Layout(
                id,
                name,
                toolWindows.toArray(ToolWindowInfo[]::new),
                getEditorPlacement(),
                getWideScreenSupport());

        if (this.layoutSettings.getUseSmartDock()) {
            this.dock(toolWindowManager, layout);
        }

        validateLayout(layout);

        return layout;
    }

    @NotNull
    private static List<ToolWindowInfo> getToolWindows(ToolWindowManager toolWindowManager) {
        String[] toolWindowIds = toolWindowManager.getToolWindowIds();
        List<ToolWindowInfo> toolWindows = new ArrayList<>();
        for (String id : toolWindowIds) {
            ToolWindowEx toolWindow = (ToolWindowEx)toolWindowManager.getToolWindow(id);

            ToolWindowInfo info = new ToolWindowInfo(
                    id,
                    Objects.requireNonNull(toolWindow).getType(),
                    toolWindow.getAnchor().toString(),
                    ToolWindowHelper.getBounds(toolWindow),
                    toolWindow.isVisible(),
                    toolWindow.isSplitMode());
            toolWindows.add(info);
        }

        return toolWindows;
    }

    private void dock(ToolWindowManager toolWindowManager, Layout layout) {
        SmartDocker smartDocker = this.smartDockerFactory.create(toolWindowManager);
        smartDocker.dock(layout);
    }

    private static void validateLayout(Layout layout) {
        ToolWindowInfo[] invalidToolWindows = LayoutValidationHelper.retrieveToolWindowsOutsideOfScreen(layout);
        if (invalidToolWindows.length != 0) {
            String invalidToolWindowNames = String.join(
                    ", ",
                    Stream.of(invalidToolWindows)
                            .map(ToolWindowInfo::getId)
                            .toArray(String[]::new));

            BalloonNotificationHelper.warning(
                    MessagesHelper.message("StoreLayout.Validation.ToolWindowOutOfScreen.Title"),
                    MessagesHelper.message("StoreLayout.Validation.ToolWindowOutOfScreen.Content", invalidToolWindowNames));
        }
    }

    private static int getEditorPlacement() {
        return UISettings
                .getInstance()
                .getEditorTabPlacement();
    }

    private static boolean getWideScreenSupport() {
        return UISettings
                .getInstance()
                .getWideScreenSupport();
    }
}
