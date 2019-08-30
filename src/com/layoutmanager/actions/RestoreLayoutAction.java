package com.layoutmanager.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowManagerImpl;
import com.intellij.openapi.wm.impl.WindowInfoImpl;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.persistence.ToolWindowInfo;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RestoreLayoutAction extends AnAction {

    private final int number;

    public RestoreLayoutAction(int number) {
        super();
        this.number = number;

        Layout layout = LayoutConfig.getInstance().getLayout(number);
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(layout.getName());
        presentation.setIcon(AllIcons.Actions.Export);
        presentation.setEnabledAndVisible(layout.isDefined());
    }

    @Override
    public void update(AnActionEvent e) {
        Layout layout = LayoutConfig.getInstance().getLayout(number);
        e.getPresentation().setEnabledAndVisible(layout.isDefined());
        e.getPresentation().setText(layout.getName());
        super.update(e);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Layout layout = LayoutConfig.getInstance().getLayout(this.number);
        applyLayout(event, layout);

    }

    private void applyLayout(AnActionEvent event, Layout layout) {
        ToolWindowManagerImpl toolWindowManager = getToolWindowManager(event);
        for (ToolWindowInfo toolWindow : layout.getToolWindows()) {
            applyToolWindowLayout(toolWindowManager, toolWindow);
        }
    }

    private void applyToolWindowLayout(ToolWindowManagerImpl toolWindowManager, ToolWindowInfo info) {
        ToolWindow toolWindow = toolWindowManager.getToolWindow(info.getId());

        if (toolWindow != null) {
            toolWindow.hide(null);
            if (info.isVisible()) {
                setBounds(toolWindowManager, info);
                toolWindow.setAnchor(ToolWindowAnchor.fromText(info.getAnchor()), null);
                toolWindow.setType(info.getType(), null);
                toolWindow.show(null);
            }
        }
    }

    private void setBounds(ToolWindowManager toolWindowManager, ToolWindowInfo toolWindow) {
        try {
            Method method = toolWindowManager.getClass().getDeclaredMethod("getRegisteredInfoOrLogError", String.class);
            method.setAccessible(true);
            WindowInfoImpl info = (WindowInfoImpl)method.invoke(toolWindowManager, toolWindow.getId());
            info.setFloatingBounds(toolWindow.getBounds());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private ToolWindowManagerImpl getToolWindowManager(AnActionEvent event) {
        Project project = event.getProject();
        return (ToolWindowManagerImpl) ToolWindowManager.getInstance(project);
    }
}
