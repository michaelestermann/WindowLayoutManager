package com.layoutmanager.startup;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.cleanup.EmptyLayoutRemoverService;
import com.layoutmanager.ui.menu.WindowMenuService;

public class PluginBootstrapper implements ProjectComponent
{
    @Override
    public void projectOpened() {
        EmptyLayoutRemoverService emptyLayoutRemoverService = ServiceManager.getService(EmptyLayoutRemoverService.class);
        emptyLayoutRemoverService.execute();

        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.create();
    }
}

