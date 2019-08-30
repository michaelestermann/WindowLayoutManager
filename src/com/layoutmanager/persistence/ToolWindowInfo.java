package com.layoutmanager.persistence;

import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowType;

import java.awt.*;

public class ToolWindowInfo {
    private String id;
    private ToolWindowType type;
    private String anchor;
    private Rectangle bounds;
    private boolean isVisible;

    public ToolWindowInfo() {
        this.id = null;
        this.type = null;
        this.anchor = "top";
        this.bounds = null;
        this.isVisible = false;
    }

    public ToolWindowInfo(String id, ToolWindowType type, String anchor, Rectangle bounds, boolean isVisible) {
        this.id = id;
        this.type = type;
        this.anchor = anchor;
        this.bounds = bounds;
        this.isVisible = isVisible;
    }

    public String getId() {
        return this.id;
    }

    public ToolWindowType getType() {
        return type;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(ToolWindowType type) {
        this.type = type;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
