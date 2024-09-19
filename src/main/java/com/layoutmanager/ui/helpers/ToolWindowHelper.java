package com.layoutmanager.ui.helpers;

import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.Nullable;

public class ToolWindowHelper {
    public static Rectangle getBounds(ToolWindowEx toolWindow) {
        if (toolWindow.isVisible()) {
            if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.WINDOWED) {
                Window window = getWindow(toolWindow);
                return window != null ? window.getBounds() : new Rectangle(100, 100);
            } else {
                return toolWindow
                        .getComponent()
                        .getBounds();
            }
        }
        return new Rectangle(0, 0, 0, 0);
    }

    public static void setBounds(ToolWindowEx toolWindow, Rectangle bounds) {
        if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.WINDOWED) {
            setWindowBounds(toolWindow, bounds);
        } else {
            Rectangle currentBounds = toolWindow
                    .getComponent()
                    .getBounds();
            if (toolWindow.getAnchor() == ToolWindowAnchor.TOP || toolWindow.getAnchor() == ToolWindowAnchor.BOTTOM) {
                toolWindow.stretchHeight(bounds.height - currentBounds.height);
            } else {
                toolWindow.stretchWidth(bounds.width - currentBounds.width);
            }
        }
    }

    private static void setWindowBounds(ToolWindowEx toolWindow, Rectangle bounds) {
        SwingUtilities.invokeLater(() -> {
            Window window = getWindow(toolWindow);
            if (window != null) {
                window.setBounds(bounds);
            } else {
                toolWindow
                        .getComponent()
                        .addHierarchyListener(new HierarchyListener() {

                    @Override
                    public void hierarchyChanged(HierarchyEvent e) {
                        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                            Window window = getWindow(toolWindow);
                            if (window != null) {
                                window.setBounds(bounds);
                                toolWindow
                                        .getComponent()
                                        .removeHierarchyListener(this);
                            }
                        }
                    }
                });
            }
        });
    }

    @Nullable
    private static Window getWindow(ToolWindowEx toolWindow) {
        return SwingUtilities.getWindowAncestor(toolWindow.getComponent());
    }
}
