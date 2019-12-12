package com.layoutmanager.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.WindowInfoImpl;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.ToolWindowInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LayoutCreator {

    public static Layout create(Project project) {

        String name = getLayoutName();
        return name != null ?
                createLayout(ToolWindowManager.getInstance(project), name) :
                null;
    }

    private static String getLayoutName() {
        String name;
        do {
            name = Messages.showInputDialog(
                    MessagesHelper.message("StoreLayout.Dialog.Title"),
                    MessagesHelper.message("StoreLayout.Dialog.Content"),
                    AllIcons.Actions.Edit);
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
            ToolWindow toolWindow = toolWindowManager.getToolWindow(id);

            ToolWindowInfo info = new ToolWindowInfo(
                    id,
                    toolWindow.getType(),
                    toolWindow.getAnchor().toString(),
                    getBounds(toolWindowManager, id),
                    toolWindow.isVisible());
            toolWindows.add(info);
        }
        return toolWindows;
    }

    private static Rectangle getBounds(ToolWindowManager toolWindowIds, String toolWindowId) {
        try {
            Method method = toolWindowIds.getClass().getDeclaredMethod("getRegisteredInfoOrLogError", String.class);
            method.setAccessible(true);
            WindowInfoImpl info = (WindowInfoImpl)method.invoke(toolWindowIds, toolWindowId);
            return info.getFloatingBounds();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return new Rectangle(0, 0, 100, 100);
        }
    }
}
