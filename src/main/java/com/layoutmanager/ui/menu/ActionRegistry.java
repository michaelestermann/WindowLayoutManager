package com.layoutmanager.ui.menu;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.layoutmanager.layout.LayoutAction;
import org.jetbrains.annotations.Nullable;

public class ActionRegistry {
    private static final String PLUGIN_ID = "com.layoutmanager";

    public void register(LayoutAction layoutAction) {
        PluginId pluginId = this.getPluginId();
        String actionName = this.getActionNameForLayout(layoutAction);

        ActionManager.getInstance().registerAction(actionName, layoutAction, pluginId);
    }

    public void unregister(LayoutAction layoutAction) {
        String actionName = this.getActionNameForLayout(layoutAction);
        ActionManager.getInstance().unregisterAction(actionName);
    }

    public void rename(LayoutAction layoutAction) {
        ActionManager actionManager = ActionManager.getInstance();
        Keymap activeKeymap = KeymapManager.getInstance().getActiveKeymap();
        String previousActionId = actionManager.getId(layoutAction);
        Shortcut[] shortcuts = activeKeymap.getShortcuts(previousActionId);

        if (shortcuts.length > 0) {
            String newActionName = getActionNameForLayout(layoutAction);
            this.reRegisterAction(actionManager, layoutAction, previousActionId, newActionName);
            activeKeymap.removeAllActionShortcuts(previousActionId);

            for (Shortcut shortcut : shortcuts) {
                activeKeymap.addShortcut(newActionName, shortcut);
            }
        }
    }

    private void reRegisterAction(ActionManager actionManager, LayoutAction layoutAction, String previousActionId, String newActionName) {
        actionManager.unregisterAction(previousActionId);
        actionManager.registerAction(newActionName, layoutAction, getPluginId());
    }

    @Nullable
    private PluginId getPluginId() {
        return PluginId.findId("com.layoutmanager");
    }

    private String getActionNameForLayout(LayoutAction layoutAction) {
        String className = layoutAction.getClass().getName();
        String typeName = className.substring(className.length() - "LayoutAction".length());

        return getActionNameForLayout(layoutAction.getLayout().getName(), typeName);
    }

    private String getActionNameForLayout(String layoutName, String typeName) {
        return "WindowLayoutManager." + typeName + "." + layoutName;
    }

}
