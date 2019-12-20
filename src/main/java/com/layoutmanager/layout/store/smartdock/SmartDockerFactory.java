package com.layoutmanager.layout.store.smartdock;

import com.intellij.openapi.wm.ToolWindowManager;
import com.layoutmanager.layout.store.smartdock.dockers.ScreenBorderDocker;
import com.layoutmanager.layout.store.smartdock.dockers.ToolWindowDocker;
import com.layoutmanager.layout.store.smartdock.dockers.ToolWindowToScreenShrinker;

public class SmartDockerFactory {
    public SmartDocker create(ToolWindowManager toolWindowManager) {
        return new SmartDocker(
            toolWindowManager,
                new ToolWindowToScreenShrinker(),
                new ToolWindowDocker(),
                new ScreenBorderDocker());
    }
}
