package com.layoutmanager.persistence;


import java.util.Arrays;
import java.util.Objects;

public class Layout {
    private int id;
    private String name;
    private ToolWindowInfo[] toolWindows;
    private int editorTabPlacement;
    private boolean wideScreenSupport;

    @SuppressWarnings({"unused", "Used for serialization."})
    public Layout() {
        this.id = 0;
        this.name = "";
        this.toolWindows = new ToolWindowInfo[0];
        this.editorTabPlacement = -1;
    }

    public Layout(
            int id,
            String name,
            ToolWindowInfo[] toolWindows,
            int editorTabPlacement,
            boolean wideScreenSupport) {
        this.id = id;
        this.name = name;
        this.toolWindows = toolWindows;
        this.editorTabPlacement = editorTabPlacement;
        this.wideScreenSupport = wideScreenSupport;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEditorPlacement() {
        return this.editorTabPlacement;
    }

    public void setEditorPlacement(int editorTabPlacement) {
        this.editorTabPlacement = editorTabPlacement;
    }

    public boolean getWideScreenSupport() {
        return this.wideScreenSupport;
    }

    public void setWideScreenSupport(boolean wideScreenSupport) {
        this.wideScreenSupport = wideScreenSupport;
    }

    public ToolWindowInfo[] getToolWindows() {
        return this.toolWindows;
    }

    public void setToolWindows(ToolWindowInfo[] toolWindows) {
        this.toolWindows = toolWindows;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }

        Layout that = (Layout) other;
        return this.editorTabPlacement == that.editorTabPlacement &&
                this.name.equals(that.name) &&
                Arrays.equals(this.toolWindows, that.toolWindows);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(this.name, this.editorTabPlacement);
        result = 31 * result + Arrays.hashCode(this.toolWindows);
        return result;
    }
}