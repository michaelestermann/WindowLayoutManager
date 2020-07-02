package com.layoutmanager.startup;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.cleanup.EmptyLayoutRemoverService;
import com.layoutmanager.ui.menu.WindowMenuService;

public class PluginBootstrapper implements AppLifecycleListener {

    @Override
    public void welcomeScreenDisplayed() {
        EmptyLayoutRemoverService emptyLayoutRemoverService = ServiceManager.getService(EmptyLayoutRemoverService.class);
        emptyLayoutRemoverService.execute();

        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.create();
    }
}