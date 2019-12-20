package com.layoutmanager.layout.store.smartdock;

import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.layoutmanager.persistence.ToolWindowInfo;

import java.awt.*;

public class ToolWindowDocking {

    private final ToolWindowInfo toolWindowInfo;
    private final ToolWindowImpl toolWindow;
    private final Rectangle containingScreen;
    private final int threshold;

    public ToolWindowDocking(
            ToolWindowInfo toolWindowInfo,
            ToolWindowImpl toolWindow,
            Rectangle containingScreen,
            int threshold) {
        this.toolWindowInfo = toolWindowInfo;
        this.toolWindow = toolWindow;
        this.containingScreen = containingScreen;
        this.threshold = threshold;
    }

    public ToolWindowInfo getToolWindowInfo() {
        return this.toolWindowInfo;
    }

    public ToolWindowImpl getToolWindow() {
        return toolWindow;
    }

    public Rectangle getContainingScreen() {
        return containingScreen;
    }

    public Rectangle getBounds() {
        return toolWindowInfo.getBounds();
    }

    public Rectangle getLeftDockingBounds() {
        return new Rectangle(
                (int) Math.max(0, toolWindowInfo.getBounds().getX() - threshold),
                (int) toolWindowInfo.getBounds().getY(),
                threshold * 2,
                (int) toolWindowInfo.getBounds().getHeight());
    }

    public Rectangle getTopDockingBounds() {
        return new Rectangle(
                (int) toolWindowInfo.getBounds().getX(),
                (int) Math.max(0, toolWindowInfo.getBounds().getY() - threshold),
                (int) toolWindowInfo.getBounds().getWidth(),
                threshold * 2);
    }

    public Rectangle getRightDockingBounds() {
        return new Rectangle(
                (int) (toolWindowInfo.getBounds().getMaxX() - threshold),
                (int) toolWindowInfo.getBounds().getY(),
                (int) Math.min(threshold * 2, threshold + containingScreen.getMaxX() - toolWindowInfo.getBounds().getMaxX()),
                (int) toolWindowInfo.getBounds().getHeight());
    }

    public Rectangle getBottomDockingBounds() {
        return new Rectangle(
                (int) toolWindowInfo.getBounds().getX(),
                (int) toolWindowInfo.getBounds().getMaxY() - threshold,
                (int) toolWindowInfo.getBounds().getWidth(),
                (int) Math.min(threshold * 2, threshold + containingScreen.getMaxY() - toolWindowInfo.getBounds().getMaxY()));
    }
}
