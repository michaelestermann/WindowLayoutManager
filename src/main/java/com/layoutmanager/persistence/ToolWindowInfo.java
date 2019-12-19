package com.layoutmanager.persistence;

import com.intellij.openapi.wm.ToolWindowType;

import java.awt.*;

public class ToolWindowInfo {
    private String id;
    private ToolWindowType type;
    private String anchor;
    private Rectangle bounds;
    private boolean isVisible;
    private boolean isToolWindow;

    @SuppressWarnings({"unused", "Used for serialization."})
    public ToolWindowInfo() {
        this.id = null;
        this.type = null;
        this.anchor = "top";
        this.bounds = null;
        this.isVisible = false;
        this.isToolWindow = false;
    }

    public ToolWindowInfo(
            String id,
            ToolWindowType type,
            String anchor,
            Rectangle bounds,
            boolean isVisible,
            boolean isToolWindow) {
        this.id = id;
        this.type = type;
        this.anchor = anchor;
        this.bounds = bounds;
        this.isVisible = isVisible;
        this.isToolWindow = isToolWindow;
    }

    public String getId() {
        return this.id;
    }

    public ToolWindowType getType() {
        return this.type;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public boolean isToolWindow() {
        return this.isToolWindow;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setType(ToolWindowType type) {
        this.type = type;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setIsToolWindow(boolean isSplit) {
        this.isToolWindow = isToolWindow;
    }
}
