package com.layoutmanager.ui.menu;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.layoutmanager.layout.LayoutAction;
import com.layoutmanager.layout.delete.DeleteLayoutAction;
import com.layoutmanager.layout.restore.RestoreLayoutAction;
import com.layoutmanager.layout.store.LayoutCreator;
import com.layoutmanager.layout.store.create.NewLayoutAction;
import com.layoutmanager.layout.store.overwrite.OverwriteLayoutAction;
import com.layoutmanager.layout.store.smartdock.SmartDockerFactory;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.action.ActionRegistry;
import com.layoutmanager.ui.dialogs.LayoutNameDialog;
import com.layoutmanager.ui.dialogs.LayoutNameValidator;
import java.util.Arrays;

public class WindowMenuService {
    private final ActionRegistry actionRegistry;

    private DefaultActionGroup storeLayout;
    private DefaultActionGroup restoreLayout;
    private DefaultActionGroup deleteLayout;

    public WindowMenuService() {
        this.actionRegistry = new ActionRegistry();
    }

    public void create() {
        DefaultActionGroup windowMenu = (DefaultActionGroup) ActionManager
                .getInstance()
                .getAction("WindowMenu");
        this.createMainActions(windowMenu);
        this.createStoreRestoreActions();
    }

    public void addLayout(Layout layout) {
        LayoutCreator layoutCreator = this.createLayoutCreator();

        this.addStoreLayoutActionBeforeSeparator(layout, layoutCreator);
        this.addRestoreLayoutAction(layout);
        this.addDeleteLayout(layout);
    }

    public void deleteLayout(Layout layout) {
        this.deleteActionInGroup(layout, this.storeLayout, false);
        this.deleteActionInGroup(layout, this.restoreLayout, true);
        this.deleteActionInGroup(layout, this.deleteLayout, false);
    }

    public void renameLayout(Layout layout) {
        LayoutAction layoutAction = this.getActionForLayout(this.restoreLayout, layout);
        this.actionRegistry.rename(layoutAction);
    }

    private void deleteActionInGroup(Layout layout, DefaultActionGroup actionGroup, boolean unregister) {
        LayoutAction layoutAction = this.getActionForLayout(actionGroup, layout);

        if (unregister) {
            this.actionRegistry.unregister(layoutAction);
        }

        actionGroup.remove(layoutAction);
    }

    private LayoutAction getActionForLayout(DefaultActionGroup group, Layout layout) {
        return Arrays
                .stream(group.getChildActionsOrStubs())
                .filter(x ->
                        x instanceof LayoutAction &&
                                ((LayoutAction)x).getLayout() == layout)
                .map(x -> (LayoutAction)x)
                .findFirst()
                .orElse(null);
    }

    private LayoutCreator createLayoutCreator() {
        LayoutConfig config = LayoutConfig.getInstance();
        return new LayoutCreator(
                config.getSettings(),
                new SmartDockerFactory(),
                new LayoutNameDialog(new LayoutNameValidator()));
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
        LayoutCreator layoutCreator = this.createLayoutCreator();

        for (Layout layout : config.getLayouts()) {
            this.addOverwriteLayoutActionAtTheEnd(layout, layoutCreator);
        }

        if (config.getLayoutCount() > 0) {
            this.storeLayout.addSeparator();
        }

        int nextAvailableId = config.getNextAvailableId();

        NewLayoutAction newLayoutAction = new NewLayoutAction(layoutCreator, nextAvailableId);
        this.storeLayout.add(newLayoutAction);
        this.actionRegistry.register(newLayoutAction, "NewLayout");
    }

    private void addOverwriteLayoutActionAtTheEnd(Layout layout, LayoutCreator layoutCreator) {
        this.storeLayout.add(new OverwriteLayoutAction(layoutCreator, layout));
    }

    private void addStoreLayoutActionBeforeSeparator(Layout layout, LayoutCreator layoutCreator) {
        AnAction[] actions = this.storeLayout.getChildActionsOrStubs();
        AnAction newLayoutAction = actions[actions.length - 1];

        this.removeSeparator(actions);
        this.storeLayout.remove(newLayoutAction);

        this.addOverwriteLayoutActionAtTheEnd(layout, layoutCreator);

        this.storeLayout.addSeparator();
        this.storeLayout.add(newLayoutAction);
    }

    private void removeSeparator(AnAction[] actions) {
        boolean hasSeparator = actions.length > 1;
        if (hasSeparator) {
            AnAction separator = actions[actions.length - 2];
            this.storeLayout.remove(separator);
        }
    }

    private void addRestoreLayoutActions(LayoutConfig config) {
        for (Layout layout : config.getLayouts()) {
            this.addRestoreLayoutAction(layout);
        }
    }

    private void addRestoreLayoutAction(Layout layout) {
        RestoreLayoutAction restoreLayoutAction = new RestoreLayoutAction(layout);
        this.restoreLayout.add(restoreLayoutAction);
        this.actionRegistry.register(restoreLayoutAction);
    }

    private void addDeleteLayoutActions(LayoutConfig config) {
        for (Layout layout : config.getLayouts()) {
            this.addDeleteLayout(layout);
        }
    }

    private void addDeleteLayout(Layout layout) {
        this.deleteLayout.add(new DeleteLayoutAction(layout));
    }
}
