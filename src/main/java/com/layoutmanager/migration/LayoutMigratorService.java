package com.layoutmanager.migration;

import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;

import java.util.Arrays;

public class LayoutMigratorService {

    public void migrate() {
        LayoutConfig layoutConfig = LayoutConfig.getInstance();
        this.addMissingIds(layoutConfig);
    }

    private void addMissingIds(LayoutConfig layoutConfig) {
        for (int index = 1; index < layoutConfig.getLayoutCount(); index++) {

            Layout layout = layoutConfig.getLayout(index);
            if (this.isTaken(layoutConfig, layout.getId())) {
                layout.setId(layoutConfig.getNextAvailableId());
            }
        }
    }

    private boolean isTaken(LayoutConfig config, int id) {
        return Arrays
                .stream(config.getLayouts())
                .filter(x -> x.getId() == id)
                .count() > 1;
    }
}
