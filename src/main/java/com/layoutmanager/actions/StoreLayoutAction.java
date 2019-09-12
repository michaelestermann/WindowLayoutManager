package com.layoutmanager.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowManagerImpl;
import com.intellij.openapi.wm.impl.WindowInfoImpl;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.persistence.ToolWindowInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StoreLayoutAction extends AnAction {

    public int number;

    public StoreLayoutAction(int number) {
        this.number = number;

        Layout layout = LayoutConfig.getInstance().getLayout(number);
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(layout.getName());
        presentation.setIcon(AllIcons.Actions.Upload);
    }

    @Override
    public void update(AnActionEvent e) {
        Layout layout = LayoutConfig.getInstance().getLayout(number);
        e.getPresentation().setText(layout.getName());
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ToolWindowManagerImpl toolWindowManager = getToolWindowManager(event);
        String name = this.getLayoutName();
        if (name != null) {
            Layout layout = this.createLayout(toolWindowManager, name);
            this.storeLayout(layout);
        }
    }

    private String getLayoutName() {
        String name;
        do {
            name = Messages.showInputDialog("Enter the name for the layout.", "Layout name", AllIcons.Actions.Edit);
        } while (name != null && name.isEmpty());
        return name;
    }

    private ToolWindowManagerImpl getToolWindowManager(AnActionEvent event) {
        Project project = event.getProject();
        return (ToolWindowManagerImpl) ToolWindowManager.getInstance(project);
    }

    private Layout createLayout(ToolWindowManagerImpl toolWindowManager, String name) {
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

        return new Layout(name, toolWindows.toArray(new ToolWindowInfo[0]));
    }

    private void storeLayout(Layout layout) {
        LayoutConfig.getInstance().setLayout(this.number, layout);
    }

    private Rectangle getBounds(ToolWindowManagerImpl toolWindowIds, String toolWindowId) {
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
