package com.layoutmanager.persistence;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "Layout",
        storages = {
                @Storage("layout.xml")
        }
)
public class LayoutConfig implements PersistentStateComponent<LayoutConfig> {
    private Layout[] layouts = new Layout[] {
            new Layout("<Empty>", new ToolWindowInfo[0]),
            new Layout("<Empty>", new ToolWindowInfo[0]),
            new Layout("<Empty>", new ToolWindowInfo[0]),
            new Layout("<Empty>", new ToolWindowInfo[0]),
    };

    @Nullable
    @Override
    public LayoutConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LayoutConfig layoutConfig) {
        XmlSerializerUtil.copyBean(layoutConfig, this);
    }

    public boolean hasLayout(int number) {
        return number >= 0 && number < this.layouts.length;
    }

    public Layout getLayout(int number) {
        return this.layouts[number];
    }

    public int supportedLayouts() {
        return this.layouts.length;
    }

    public void setLayout(int number, Layout layout) {
        this.layouts[number] = layout;
    }

    @Nullable
    public static LayoutConfig getInstance() {
        return ServiceManager.getService(LayoutConfig.class);
    }

    public Layout[] getLayouts() {
        return layouts;
    }

    public void setLayouts(Layout[] layouts) {
        this.layouts = layouts;
    }
}

