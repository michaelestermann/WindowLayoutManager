package com.layoutmanager.layout.store.smartdock;

import java.util.Arrays;

import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import com.layoutmanager.layout.store.smartdock.dockers.ScreenBorderDocker;
import com.layoutmanager.layout.store.smartdock.dockers.ToolWindowDocker;
import com.layoutmanager.layout.store.smartdock.dockers.ToolWindowToScreenShrinker;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.ToolWindowInfo;
import com.layoutmanager.ui.helpers.ScreenSizeHelper;

public class SmartDocker {
    private static final int THRESHOLD = 20;

    private final ToolWindowManager toolWindowManager;
    private final ToolWindowToScreenShrinker shrinker;
    private final ToolWindowDocker toolWindowDocker;
    private final ScreenBorderDocker screenBorderDocker;

    public SmartDocker(
            ToolWindowManager toolWindowManager,
            ToolWindowToScreenShrinker shrinker,
            ToolWindowDocker toolWindowDocker,
            ScreenBorderDocker screenBorderDocker) {
        this.toolWindowManager = toolWindowManager;
        this.shrinker = shrinker;
        this.toolWindowDocker = toolWindowDocker;
        this.screenBorderDocker = screenBorderDocker;
    }

    public void dock(Layout layout) {
        ToolWindowDocking[] floatedOrWindowsToolWindows = this.getFloatedOrWindowsToolWindows(layout);

        this.shrinker.shrink(floatedOrWindowsToolWindows);
        this.toolWindowDocker.dock(floatedOrWindowsToolWindows);
        this.screenBorderDocker.dock(floatedOrWindowsToolWindows, THRESHOLD);
    }


    private ToolWindowDocking[] getFloatedOrWindowsToolWindows(Layout layout) {
        return Arrays.stream(layout.getToolWindows())
                .filter(this::isFloatingOrWindowedToolWindow)
                .map(x -> new ToolWindowDocking(
                        x,
                        ScreenSizeHelper.getContainingScreenBounds(x),
                        THRESHOLD))
                .toArray(ToolWindowDocking[]::new);
    }

    private boolean isFloatingOrWindowedToolWindow(ToolWindowInfo toolWindow) {
        return this.toolWindowManager.getToolWindow(toolWindow.getId()) != null &&
                toolWindow.getType() == ToolWindowType.FLOATING ||
                toolWindow.getType() == ToolWindowType.WINDOWED;
    }
}
