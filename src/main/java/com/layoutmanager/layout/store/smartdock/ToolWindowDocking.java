package com.layoutmanager.layout.store.smartdock;

import java.awt.Rectangle;

import com.layoutmanager.persistence.ToolWindowInfo;

public class ToolWindowDocking {

    private final ToolWindowInfo toolWindowInfo;
    private final Rectangle containingScreen;
    private final int threshold;

    public ToolWindowDocking(
            ToolWindowInfo toolWindowInfo,
            Rectangle containingScreen,
            int threshold) {
        this.toolWindowInfo = toolWindowInfo;
        this.containingScreen = containingScreen;
        this.threshold = threshold;
    }

    public ToolWindowInfo getToolWindowInfo() {
        return this.toolWindowInfo;
    }

    public Rectangle getContainingScreen() {
        return this.containingScreen;
    }

    public Rectangle getBounds() {
        return this.toolWindowInfo.getBounds();
    }

    public Rectangle getLeftDockingBounds() {
        return new Rectangle(
                (int) Math.max(0, this.toolWindowInfo.getBounds().getX() - this.threshold),
                (int) this.toolWindowInfo.getBounds().getY(),
                this.threshold * 2,
                (int) this.toolWindowInfo.getBounds().getHeight());
    }

    public Rectangle getTopDockingBounds() {
        return new Rectangle(
                (int) this.toolWindowInfo.getBounds().getX(),
                (int) Math.max(0, this.toolWindowInfo.getBounds().getY() - this.threshold),
                (int) this.toolWindowInfo.getBounds().getWidth(),
                this.threshold * 2);
    }

    public Rectangle getRightDockingBounds() {
        return new Rectangle(
                (int) (this.toolWindowInfo.getBounds().getMaxX() - this.threshold),
                (int) this.toolWindowInfo.getBounds().getY(),
                (int) Math.min(this.threshold * 2, this.threshold + this.containingScreen.getMaxX() - this.toolWindowInfo.getBounds().getMaxX()),
                (int) this.toolWindowInfo.getBounds().getHeight());
    }

    public Rectangle getBottomDockingBounds() {
        return new Rectangle(
                (int) this.toolWindowInfo.getBounds().getX(),
                (int) this.toolWindowInfo.getBounds().getMaxY() - this.threshold,
                (int) this.toolWindowInfo.getBounds().getWidth(),
                (int) Math.min(this.threshold * 2, this.threshold + this.containingScreen.getMaxY() - this.toolWindowInfo.getBounds().getMaxY()));
    }
}
