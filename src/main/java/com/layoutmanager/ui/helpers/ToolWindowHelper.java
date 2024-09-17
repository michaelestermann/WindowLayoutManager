package com.layoutmanager.ui.helpers;

import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.openapi.wm.impl.FloatingDecorator;
import com.intellij.openapi.wm.impl.InternalDecorator;
import com.intellij.util.ui.UIUtil;

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ToolWindowHelper {
    public static Rectangle getBounds(ToolWindowEx toolWindow) {
        if (toolWindow.isVisible()) {
            if (toolWindow.getType() == ToolWindowType.FLOATING) {
                FloatingDecorator floatingDecorator = getFloatingDecorator(toolWindow);
                return floatingDecorator != null ?
                        floatingDecorator.getBounds() :
                        new Rectangle(100, 100);
            } else if (toolWindow.getType() == ToolWindowType.WINDOWED) {
                Window window = getWindow(toolWindow);
                return window != null ?
                        window.getBounds() :
                        new Rectangle(100, 100);
            } else {
                InternalDecorator decorator = toolWindow.getDecorator();
                return decorator.getBounds();
            }
        }

        return new Rectangle(0, 0, 0, 0);
    }

    public static void setBounds(ToolWindowEx toolWindow, Rectangle bounds) {
        if (toolWindow.getType() == ToolWindowType.FLOATING) {
            FloatingDecorator floatingDecorator = getFloatingDecorator(toolWindow);
            if (floatingDecorator != null) {
                floatingDecorator.setBounds(bounds);
            }
            else {
                delayedFloatingDecoratorSetBounds(toolWindow, bounds);
            }
        } else if (toolWindow.getType() == ToolWindowType.WINDOWED) {
            Window window = getWindow(toolWindow);
            if (window != null) {
                window.setBounds(bounds);
            }
        } else {
            Rectangle currentBounds = toolWindow.getDecorator().getBounds();
            if (toolWindow.getAnchor() == ToolWindowAnchor.TOP || toolWindow.getAnchor() == ToolWindowAnchor.BOTTOM) {
                toolWindow.stretchHeight(bounds.height - currentBounds.height);
            } else {
                toolWindow.stretchWidth(bounds.width - currentBounds.width);
            }
        }
    }

    private static void delayedFloatingDecoratorSetBounds(ToolWindowEx toolWindow, Rectangle bounds) {
        InternalDecorator decorator = toolWindow.getDecorator();
        decorator.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
                    FloatingDecorator newFloatingDecorator = getFloatingDecorator(toolWindow);
                    if (newFloatingDecorator != null) {
                        newFloatingDecorator.setBounds(bounds);
                        decorator.removeHierarchyListener(this);
                    }
                }
            }
        });
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
