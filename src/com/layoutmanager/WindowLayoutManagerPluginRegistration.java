package com.layoutmanager;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ApplicationComponent;
import com.layoutmanager.actions.RestoreLayoutAction;
import com.layoutmanager.actions.StoreLayoutAction;
import com.layoutmanager.persistence.LayoutConfig;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.stream.IntStream;

public class WindowLayoutManagerPluginRegistration implements ApplicationComponent {
    @NotNull public String getComponentName() {
        return "Window Layout Manager";
    }

    public void initComponent() {
        DefaultActionGroup windowMenu = (DefaultActionGroup) ActionManager.getInstance().getAction("WindowMenu");

        LayoutConfig config = LayoutConfig.getInstance();

        addActions(windowMenu, "Store Layout", Arrays
                .stream(IntStream.range(0, config.supportedLayouts()).toArray())
                .mapToObj(e -> new StoreLayoutAction(e))
                .toArray(StoreLayoutAction[]::new));
        addActions(windowMenu, "Restore Layout", Arrays
                .stream(IntStream.range(0, config.supportedLayouts()).toArray())
                .mapToObj(e -> new RestoreLayoutAction(e))
                .toArray(RestoreLayoutAction[]::new));
    }

    public void disposeComponent() {

    }

    private void addActions(DefaultActionGroup windowMenu, String name, AnAction... actions) {
        DefaultActionGroup storeGroup = new DefaultActionGroup(name, Arrays.asList(actions));
        storeGroup.setPopup(true);
        windowMenu.add(storeGroup);
    }
}
