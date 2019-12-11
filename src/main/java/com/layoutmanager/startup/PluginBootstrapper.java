package com.layoutmanager.startup;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.layoutmanager.cleanup.EmptyLayoutRemoverService;
import com.layoutmanager.menu.WindowMenuService;
import org.jetbrains.annotations.NotNull;

public class PluginBootstrapper implements StartupActivity {

    public void runActivity(@NotNull Project project) {
        EmptyLayoutRemoverService emptyLayoutRemoverService = ServiceManager.getService(EmptyLayoutRemoverService.class);
        emptyLayoutRemoverService.execute();

        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.create();
    }
}