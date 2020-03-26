package com.layoutmanager.ui.menu;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import com.layoutmanager.layout.delete.DeleteLayoutAction;
import com.layoutmanager.layout.restore.RestoreLayoutAction;
import com.layoutmanager.layout.store.LayoutCreator;
import com.layoutmanager.layout.store.create.NewLayoutAction;
import com.layoutmanager.layout.store.overwrite.OverwriteLayoutAction;
import com.layoutmanager.layout.store.smartdock.SmartDockerFactory;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;

public class WindowMenuService {
    private DefaultActionGroup storeLayout;
    private DefaultActionGroup restoreLayout;
    private DefaultActionGroup deleteLayout;

    public void create() {
        if (this.storeLayout != null) {
            return;
        }

        this.createMainActions((DefaultActionGroup) ActionManager.getInstance().getAction("WindowMenu"));
        this.createStoreRestoreActions();
    }

    public void recreate() {
        this.restoreLayout.removeAll();
        this.storeLayout.removeAll();
        this.deleteLayout.removeAll();

        this.createStoreRestoreActions();
    }

    private void createMainActions(DefaultActionGroup windowMenu) {
        this.storeLayout = this.createMainAction(MessagesHelper.message("StoreLayout.Menu"), windowMenu);
        this.restoreLayout = this.createMainAction(MessagesHelper.message("RestoreLayout.Menu"), windowMenu);
        this.deleteLayout = this.createMainAction(MessagesHelper.message("DeleteLayout.Menu"), windowMenu);
    }

    private DefaultActionGroup createMainAction(String name, DefaultActionGroup windowMenu) {
        DefaultActionGroup windowMenuItem = new DefaultActionGroup(name, true);
        windowMenu.add(windowMenuItem);

        return windowMenuItem;
    }

    private void createStoreRestoreActions() {
        LayoutConfig config = LayoutConfig.getInstance();
        this.addStoreLayoutActions(config);
        this.addRestoreLayoutActions(config);
        this.addDeleteLayoutActions(config);
    }

    private void addStoreLayoutActions(LayoutConfig config) {
        LayoutCreator layoutCreator = new LayoutCreator(new SmartDockerFactory());

        for (int index = 0; index < config.getLayoutCount(); index++) {
            this.storeLayout.add(new OverwriteLayoutAction(layoutCreator, index));
        }

        if (config.getLayoutCount() > 0) {
            this.storeLayout.addSeparator();
        }

        this.storeLayout.add(new NewLayoutAction(layoutCreator));
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
