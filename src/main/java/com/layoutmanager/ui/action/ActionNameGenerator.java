package com.layoutmanager.ui.action;

import com.layoutmanager.layout.LayoutAction;
import com.layoutmanager.layout.restore.RestoreLayoutAction;
import com.layoutmanager.persistence.Layout;

import static com.layoutmanager.ui.action.ActionRegistry.ACTION_PREFIX;

public class ActionNameGenerator {
    public static String getActionNameForLayoutAction(LayoutAction layoutAction) {
        String className = layoutAction.getClass().getSimpleName();
        String typeName = className.substring(0, className.length() - "LayoutAction".length());

        return getActionName(layoutAction.getLayout().getId(), typeName);
    }

    public static String getActionNameForLayout(Layout layout) {
        String className = RestoreLayoutAction.class.getSimpleName();
        String typeName = className.substring(0, className.length() - "LayoutAction".length());

        return getActionName(layout.getId(), typeName);
    }

    private static String getActionName(int id, String typeName) {
        return ACTION_PREFIX + typeName + "." + id;
    }
}
