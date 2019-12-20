package com.layoutmanager.layout.store;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.layoutmanager.layout.store.smartdock.SmartDocker;
import com.layoutmanager.layout.store.smartdock.SmartDockerFactory;
import com.layoutmanager.layout.store.validation.LayoutValidationHelper;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.ToolWindowInfo;
import com.layoutmanager.ui.helpers.NotificationHelper;
import com.layoutmanager.ui.helpers.ToolWindowHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LayoutCreator {

    private final SmartDockerFactory smartDockerFactory;

    public LayoutCreator(SmartDockerFactory smartDockerFactory) {
        this.smartDockerFactory = smartDockerFactory;
    }

    public Layout create(ToolWindowManager toolWindowManager, String defaultName) {

        String name = getLayoutName(defaultName);
        return name != null ?
                createLayout(toolWindowManager, name) :
                null;
    }

    private String getLayoutName(String defaultName) {
        String name;
        do {
            name = Messages.showInputDialog(
                    MessagesHelper.message("StoreLayout.Dialog.Title"),
                    MessagesHelper.message("StoreLayout.Dialog.Content"),
                    AllIcons.Actions.Edit,
                    defaultName,
                    null);
        } while (name != null && name.isEmpty());

        return name;
    }

    private Layout createLayout(ToolWindowManager toolWindowManager, String name) {
        List<ToolWindowInfo> toolWindows = getToolWindows(toolWindowManager);
        Layout layout = new Layout(
                name,
                toolWindows.toArray(ToolWindowInfo[]::new),
                getEditorPlacement());

        dock(toolWindowManager, layout);
        validateLayout(layout);

        return layout;
    }

    @NotNull
    private static List<ToolWindowInfo> getToolWindows(ToolWindowManager toolWindowManager) {
        String[] toolWindowIds = toolWindowManager.getToolWindowIds();
        List<ToolWindowInfo> toolWindows = new ArrayList<>();
        for (String id : toolWindowIds) {
            ToolWindowImpl toolWindow = (ToolWindowImpl)toolWindowManager.getToolWindow(id);

            ToolWindowInfo info = new ToolWindowInfo(
                    id,
                    toolWindow.getType(),
                    toolWindow.getAnchor().toString(),
                    ToolWindowHelper.getBounds(toolWindow),
                    toolWindow.isVisible(),
                    toolWindow.isSplitMode());
            toolWindows.add(info);
        }

        return toolWindows;
    }

    private void dock(ToolWindowManager toolWindowManager, Layout layout) {
        SmartDocker smartDocker = smartDockerFactory.create(toolWindowManager);
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

            NotificationHelper.warning(
                    MessagesHelper.message("StoreLayout.Validation.ToolWindowOutOfScreen.Title"),
                    MessagesHelper.message("StoreLayout.Validation.ToolWindowOutOfScreen.Content", invalidToolWindowNames));
        }
    }

    private static int getEditorPlacement() {
        return UISettings.getInstance().getEditorTabPlacement();
    }
}
