package com.layoutmanager.startup;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.application.ApplicationManager;
import com.layoutmanager.cleanup.EmptyLayoutRemoverService;
import com.layoutmanager.migration.LayoutMigratorService;
import com.layoutmanager.ui.menu.WindowMenuService;

public class PluginBootstrapper implements AppLifecycleListener {

    @Override
    public void welcomeScreenDisplayed() {
        EmptyLayoutRemoverService emptyLayoutRemoverService = ApplicationManager
                .getApplication()
                .getService(EmptyLayoutRemoverService.class);
        emptyLayoutRemoverService.execute();

        LayoutMigratorService layoutMigratorService = ApplicationManager
                .getApplication()
                .getService(LayoutMigratorService.class);
        layoutMigratorService.migrate();

        WindowMenuService windowMenuService = ApplicationManager
                .getApplication()
                .getService(WindowMenuService.class);
        windowMenuService.create();
    }
}