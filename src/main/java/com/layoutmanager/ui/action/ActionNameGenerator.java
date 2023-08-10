package com.layoutmanager.ui.action;

import com.layoutmanager.layout.LayoutAction;
import com.layoutmanager.layout.restore.RestoreLayoutAction;
import com.layoutmanager.persistence.Layout;

import static com.layoutmanager.ui.action.ActionRegistry.ACTION_PREFIX;

public class ActionNameGenerator {
    public static String getActionNameForLayoutAction(LayoutAction layoutAction) {
        String className = layoutAction.getClass().getSimpleName();
        String typeName = className.substring(0, className.length() - "LayoutAction".length());

        return getActionNameForLayoutAction(layoutAction.getLayout().getName(), typeName);
    }

    public static String getActionNameForLayout(Layout layout) {
        String className = RestoreLayoutAction.class.getSimpleName();
        String typeName = className.substring(0, className.length() - "LayoutAction".length());

        return getActionNameForLayoutAction(layout.getName(), typeName);
    }

    private static String getActionNameForLayoutAction(String layoutName, String typeName) {
        return ACTION_PREFIX + typeName + "." + layoutName;
    }
}
