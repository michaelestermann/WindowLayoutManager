package com.layoutmanager.persistence;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@State(
        name = "Layout",
        storages = {
                @Storage("layout.xml")
        }
)
public class LayoutConfig implements PersistentStateComponent<LayoutConfig> {
    private List<Layout> layouts = new ArrayList<>();

    @Nullable
    @Override
    public LayoutConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LayoutConfig layoutConfig) {
        XmlSerializerUtil.copyBean(layoutConfig, this);
    }

    public Layout getLayout(int number) {
        return this.layouts.get(number);
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public Layout[] getLayouts() {
        return this.layouts.toArray(Layout[]::new);
    }

    public int getLayoutCount() {
        return this.layouts.size();
    }

    public void setLayout(int number, Layout layout) {
        this.layouts.set(number, layout);
    }

    public void addLayout(Layout layout) {
        this.layouts.add(layout);
    }

    public void removeLayout(Layout layout) {
        this.layouts.remove(layout);
    }

    @Nullable
    public static LayoutConfig getInstance() {
        return ServiceManager.getService(LayoutConfig.class);
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setLayouts(Layout[] layouts) {
        this.layouts = new ArrayList<>(Arrays.asList(layouts));
    }
}

