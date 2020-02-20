package com.layoutmanager.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.openapi.wm.impl.FloatingDecorator;
import com.intellij.openapi.wm.impl.InternalDecorator;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ToolWindowHelper {

    private static final Logger log = Logger.getInstance(ToolWindowHelper.class);

    public static Rectangle getBounds(ToolWindowImpl toolWindow) {
        try {
            if (toolWindow.isVisible()) {
                if (toolWindow.getType() == ToolWindowType.FLOATING) {
                    FloatingDecorator floatingDecorator = getFloatingDecorator(toolWindow);
                    return floatingDecorator.getBounds();
                } else if (toolWindow.getType() == ToolWindowType.WINDOWED) {
                    Window window = getWindow(toolWindow);
                    return window.getBounds();
                }
            }
        }
        catch(Exception e) {
            log.error("Could not fetch bounds of window " + toolWindow.getId() + "\n" + e.getStackTrace());
        }

        return new Rectangle(0, 0, 0, 0);
    }

    public static void setBounds(ToolWindowImpl toolWindow, Rectangle bounds) {
        try {
            if (toolWindow.isVisible()) {
                if (toolWindow.getType() == ToolWindowType.FLOATING) {
                    FloatingDecorator floatingDecorator = getFloatingDecorator(toolWindow);
                    floatingDecorator.setBounds(bounds);
                } else if (toolWindow.getType() == ToolWindowType.WINDOWED) {
                    Window window = getWindow(toolWindow);
                    window.setBounds(bounds);
                }
            }
        }
        catch(Exception e) {
            log.error("Could not set bounds of window " + toolWindow.getId() + "\n" + e.getStackTrace());
        }
    }

    @Nullable
    private static FloatingDecorator getFloatingDecorator(ToolWindowImpl toolWindow) {
        InternalDecorator decorator = toolWindow.getDecorator();
        return (FloatingDecorator) SwingUtilities.getAncestorOfClass(FloatingDecorator.class, decorator);
    }

    @Nullable
    private static Window getWindow(ToolWindowImpl toolWindow) {
        JComponent component = toolWindow.getComponent();
        return UIUtil.getWindow(component);
    }
}
