package com.layoutmanager.menu;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.layoutmanager.actions.DeleteLayoutAction;
import com.layoutmanager.actions.NewLayoutAction;
import com.layoutmanager.actions.OverwriteLayoutAction;
import com.layoutmanager.actions.RestoreLayoutAction;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;

public class WindowMenuService {
    private DefaultActionGroup storeLayout;
    private DefaultActionGroup restoreLayout;
    private DefaultActionGroup deleteLayout;

    public void create() {
        if (storeLayout != null) {
            throw new IllegalStateException("Menu items already created");
        }

        createMainActions( (DefaultActionGroup) ActionManager.getInstance().getAction("WindowMenu"));
        createStoreRestoreActions();
    }

    public void recreate() {
        this.restoreLayout.removeAll();
        this.storeLayout.removeAll();
        this.deleteLayout.removeAll();

        createStoreRestoreActions();
    }

    private void createMainActions(DefaultActionGroup windowMenu) {
        this.storeLayout = createMainAction("Store Layout", windowMenu);
        this.restoreLayout = createMainAction("Restore Layout", windowMenu);
        this.deleteLayout = createMainAction("Delete Layout", windowMenu);
    }

    private DefaultActionGroup createMainAction(String name, DefaultActionGroup windowMenu){
        DefaultActionGroup windowMenuItem = new DefaultActionGroup(name, true);
        windowMenu.add(windowMenuItem);

        return windowMenuItem;
    }

    private void createStoreRestoreActions() {
        LayoutConfig config = LayoutConfig.getInstance();
        addStoreLayoutActions(config);
        addRestoreLayoutActions(config);
        addDeleteLayoutActions(config);
    }

    private void addStoreLayoutActions(LayoutConfig config) {
        for (int index = 0; index < config.getLayoutCount(); index++) {
            this.storeLayout.add(new OverwriteLayoutAction(index));
        }

        if (config.getLayoutCount() > 0) {
            this.storeLayout.addSeparator();
        }

        this.storeLayout.add(new NewLayoutAction());
    }

    private void addRestoreLayoutActions(LayoutConfig config) {
        for (Layout layout : config.getLayouts()) {
            this.restoreLayout.add(new RestoreLayoutAction(layout));
        }
    }

    private void addDeleteLayoutActions(LayoutConfig config) {
        for (Layout layout : config.getLayouts()) {
            this.deleteLayout.add(new DeleteLayoutAction(layout));
        }
    }
}
