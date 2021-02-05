package com.layoutmanager.ui.helpers;

import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.openapi.wm.impl.FloatingDecorator;
import com.intellij.openapi.wm.impl.InternalDecorator;
import com.intellij.util.ui.UIUtil;

import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jetbrains.annotations.Nullable;

public class ToolWindowHelper {
    public static Rectangle getBounds(ToolWindowEx toolWindow) {
        if (toolWindow.isVisible()) {
            if (toolWindow.getType() == ToolWindowType.FLOATING) {
                FloatingDecorator floatingDecorator = getFloatingDecorator(toolWindow);
                return floatingDecorator.getBounds();
            } else if (toolWindow.getType() == ToolWindowType.WINDOWED) {
                Window window = getWindow(toolWindow);
                return window.getBounds();
            } else {
                InternalDecorator decorator = toolWindow.getDecorator();
                return decorator.getBounds();
            }
        }

        return new Rectangle(0, 0, 0, 0);
    }

    public static void setBounds(ToolWindowEx toolWindow, Rectangle bounds) {
        // TODO: To fix the issue with to small screen on multi dpi screens -> Move window when not on screen, then resize it
        if (toolWindow.isVisible()) {
            if (toolWindow.getType() == ToolWindowType.FLOATING) {
                FloatingDecorator floatingDecorator = getFloatingDecorator(toolWindow);
                floatingDecorator.setBounds(bounds);
            } else if (toolWindow.getType() == ToolWindowType.WINDOWED) {
                Window window = getWindow(toolWindow);
                window.setBounds(bounds);
            } else {
                Rectangle currentBounds = toolWindow.getDecorator().getBounds();
                if (toolWindow.getAnchor() == ToolWindowAnchor.TOP || toolWindow.getAnchor() == ToolWindowAnchor.BOTTOM) {
                    toolWindow.stretchHeight(bounds.height - currentBounds.height);
                } else {
                    toolWindow.stretchWidth(bounds.width - currentBounds.width);
                }
            }
        }
    }

    @Nullable
    private static FloatingDecorator getFloatingDecorator(ToolWindowEx toolWindow) {
        InternalDecorator decorator = toolWindow.getDecorator();
        return (FloatingDecorator) SwingUtilities.getAncestorOfClass(FloatingDecorator.class, decorator);
    }

    @Nullable
    private static Window getWindow(ToolWindowEx toolWindow) {
        JComponent component = toolWindow.getComponent();
        return UIUtil.getWindow(component);
    }
}
