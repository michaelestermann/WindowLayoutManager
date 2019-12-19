package com.layoutmanager.persistence;


public class Layout {
    private String name;
    private int editorTabPlacement;
    private ToolWindowInfo[] toolWindows;

    public Layout() {
        name = "";
        toolWindows = new ToolWindowInfo[0];
        editorTabPlacement = -1;
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
        return editorTabPlacement;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setEditorPlacement(int editorTabPlacement) {
        this.editorTabPlacement = editorTabPlacement;
    }

    public ToolWindowInfo[] getToolWindows() {
        return toolWindows;
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setToolWindows(ToolWindowInfo[] toolWindows) {
        this.toolWindows = toolWindows;
    }
}