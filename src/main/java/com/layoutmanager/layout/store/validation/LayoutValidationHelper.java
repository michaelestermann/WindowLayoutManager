package com.layoutmanager.layout.store.validation;

import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.openapi.wm.WindowManager;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.ToolWindowInfo;

import java.awt.*;
import java.util.stream.Stream;

public class LayoutValidationHelper {
    public static ToolWindowInfo[] retrieveToolWindowsOutsideOfScreen(Layout layout){
        return Stream
                .of(layout.getToolWindows())
                .filter(x -> x.isVisible() && isWindowType(x) && !isValid(x))
                .toArray(ToolWindowInfo[]::new);
    }

    private static boolean isWindowType(ToolWindowInfo toolWindowInfo) {
        return toolWindowInfo.getType() == ToolWindowType.FLOATING || toolWindowInfo.getType() == ToolWindowType.WINDOWED;
    }

    private static boolean isValid(ToolWindowInfo toolWindowInfo) {
        Rectangle bounds = toolWindowInfo.getBounds();
        return WindowManager.getInstance().isInsideScreenBounds(bounds.x, bounds.y, bounds.width);
    }
}
