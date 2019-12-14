package com.layoutmanager.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.ToolWindowInfo;
import com.layoutmanager.ui.ToolWindowHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LayoutCreator {

    public static Layout create(Project project, String defaultName) {

        String name = getLayoutName(defaultName);
        return name != null ?
                createLayout(ToolWindowManager.getInstance(project), name) :
                null;
    }

    private static String getLayoutName(String defaultName) {
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

    private static Layout createLayout(ToolWindowManager toolWindowManager, String name) {
        List<ToolWindowInfo> toolWindows = getToolWindows(toolWindowManager);

        return new Layout(name, toolWindows.toArray(ToolWindowInfo[]::new));
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
                    toolWindow.isVisible());
            toolWindows.add(info);
        }
        return toolWindows;
    }
}
