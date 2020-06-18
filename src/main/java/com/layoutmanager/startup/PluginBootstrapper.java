package com.layoutmanager.startup;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.cleanup.EmptyLayoutRemoverService;
import com.layoutmanager.ui.menu.WindowMenuService;

// TODO: Not use this deprecated way, see api documentation.
public class PluginBootstrapper implements ApplicationComponent {

    @Override
    public void initComponent() {
        EmptyLayoutRemoverService emptyLayoutRemoverService = ServiceManager.getService(EmptyLayoutRemoverService.class);
        emptyLayoutRemoverService.execute();

        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.create();

    }
}

