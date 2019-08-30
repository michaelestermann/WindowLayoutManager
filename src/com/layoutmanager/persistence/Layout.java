package com.layoutmanager.persistence;


public class Layout {
    private String name;
    private ToolWindowInfo[] toolWindows;

    public Layout() {
        name = "";
        toolWindows = new ToolWindowInfo[0];
    }

    public Layout(String name, ToolWindowInfo[] toolWindows) {
        this.name = name;
        this.toolWindows = toolWindows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ToolWindowInfo[] getToolWindows() {
        return toolWindows;
    }

    public void setToolWindows(ToolWindowInfo[] toolWindows) {
        this.toolWindows = toolWindows;
    }

    public boolean isDefined() {
        return this.toolWindows.length > 0;
    }
}
