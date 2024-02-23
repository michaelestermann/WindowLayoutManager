package com.layoutmanager.ui.settings;

import com.layoutmanager.persistence.Layout;

public class LayoutDuplicator {
    private final LayoutSerializer serializer;

    public LayoutDuplicator(LayoutSerializer serializer) {
        this.serializer = serializer;
    }

    public Layout duplicate(Layout origin) {
        return this.serializer.deserialize(this.serializer.serialize(origin));
    }
}
