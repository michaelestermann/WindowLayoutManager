package com.layoutmanager.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "Layout",
        storages = {
                @Storage("layout.xml")
        }
)
public class LayoutConfig implements PersistentStateComponent<LayoutConfig> {
    private List<Layout> layouts = new ArrayList<>();
    private LayoutSettings settings = new LayoutSettings();

    @Nullable
    @Override
    public LayoutConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LayoutConfig layoutConfig) {
        XmlSerializerUtil.copyBean(layoutConfig, this);
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public Layout getLayout(int number) {
        return this.layouts.get(number);
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public Layout[] getLayouts() {
        return this.layouts.toArray(Layout[]::new);
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setLayouts(Layout[] layouts) {
        this.layouts = new ArrayList<>(Arrays.asList(layouts));
    }

    public LayoutSettings getSettings() {
        return this.settings;
    }

    public void setSettings(LayoutSettings settings) {
        this.settings = settings;
    }

    public int getLayoutCount() {
        return this.layouts.size();
    }

    @SuppressWarnings({"unused", "Used for serialization."})
    public void setLayout(int number, Layout layout) {
        this.layouts.set(number, layout);
    }

    public void addLayout(Layout layout) {
        this.layouts.add(layout);
    }

    public void removeLayout(Layout layout) {
        this.layouts.remove(layout);
    }

    public int getNextAvailableId() {
        for(int id = 0; id < Integer.MAX_VALUE; id++) {
            int finalId = id;
            if (this.layouts.stream().noneMatch(x -> x.getId() == finalId)) {
                return id;
            }
        }

        return Integer.MAX_VALUE;
    }

    public static LayoutConfig getInstance() {
        return ApplicationManager
                .getApplication()
                .getService(LayoutConfig.class);
    }
}