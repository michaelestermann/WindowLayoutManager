package com.layoutmanager.persistence;


import java.util.Arrays;
import java.util.Objects;

public class Layout {
    private String name;
    private int editorTabPlacement;
    private ToolWindowInfo[] toolWindows;

    @SuppressWarnings({"unused", "Used for serialization."})
    public Layout() {
        this.name = "";
        this.toolWindows = new ToolWindowInfo[0];
        this.editorTabPlacement = -1;
    }

    public Layout(String name, ToolWindowInfo[] toolWindows, int editorTabPlacement) {
        this.name = name;
        this.toolWindows = toolWindows;
        this.editorTabPlacement = editorTabPlacement;
    }

    public String getName() {
        return this.name;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setName(String name) {
        this.name = name;
    }

    public int getEditorPlacement() {
        return this.editorTabPlacement;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setEditorPlacement(int editorTabPlacement) {
        this.editorTabPlacement = editorTabPlacement;
    }

    public ToolWindowInfo[] getToolWindows() {
        return this.toolWindows;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setToolWindows(ToolWindowInfo[] toolWindows) {
        this.toolWindows = toolWindows;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
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
        result = 31 * result + Arrays.hashCode(toolWindows);
        return result;
    }
}