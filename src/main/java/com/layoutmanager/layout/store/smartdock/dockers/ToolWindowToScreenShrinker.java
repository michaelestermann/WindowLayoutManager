package com.layoutmanager.layout.store.smartdock.dockers;

import java.awt.Rectangle;

import com.layoutmanager.layout.store.smartdock.ToolWindowDocking;

public class ToolWindowToScreenShrinker {
    public void shrink(ToolWindowDocking[] floatedOrWindowsToolWindowDockings) {
        for (ToolWindowDocking toolWindowDocking : floatedOrWindowsToolWindowDockings) {
            Rectangle containingScreen = toolWindowDocking.getContainingScreen();
            Rectangle toolWindowBounds = toolWindowDocking.getBounds();

            if (!containingScreen.contains(toolWindowBounds)) {
                double x = Math.max(toolWindowBounds.getX(), containingScreen.getX());
                double y = Math.max(toolWindowBounds.getY(), containingScreen.getY());
                Rectangle newToolWindowBounds = new Rectangle(
                        (int)x,
                        (int)y,
                        (int)Math.min(toolWindowBounds.getWidth(), containingScreen.getMaxX() - x),
                        (int)Math.min(toolWindowBounds.getHeight(), containingScreen.getMaxY() - y));

                toolWindowDocking.getToolWindowInfo().setBounds(newToolWindowBounds);
            }
        }
    }
}
