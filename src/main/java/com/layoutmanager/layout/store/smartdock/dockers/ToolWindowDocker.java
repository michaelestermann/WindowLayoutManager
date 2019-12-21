package com.layoutmanager.layout.store.smartdock.dockers;

import com.layoutmanager.layout.store.smartdock.ToolWindowDocking;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;

public class ToolWindowDocker {
    public void dock(ToolWindowDocking[] floatedOrWindowsToolWindows) {
        this.dockLeft(floatedOrWindowsToolWindows);
        this.dockTop(floatedOrWindowsToolWindows);
        // TODO: Add bottom and left in a more generic way...
        // Idea: Introduce class DockingBounds and use this in the ToolWindowDocking
    }

    private void dockLeft(ToolWindowDocking[] floatedOrWindowsToolWindows) {
        ToolWindowDocking[] sortedByLeftPosition = Arrays.stream(floatedOrWindowsToolWindows)
                .sorted((x, y) -> (int) (x.getBounds().getX() - y.getBounds().getX()))
                .skip(1)
                .toArray(ToolWindowDocking[]::new);

        for (ToolWindowDocking toolWindowDocking : sortedByLeftPosition) {
            ToolWindowDocking[] intersectionToolWindows = Arrays.stream(floatedOrWindowsToolWindows)
                    .filter(x ->
                            !x.getToolWindowInfo().getId().equals(toolWindowDocking.getToolWindowInfo().getId()) &&
                            x.getRightDockingBounds().intersects(toolWindowDocking.getLeftDockingBounds()))
                    .sorted(Comparator.comparingInt(x -> (int) x.getBounds().getY()))
                    .toArray(ToolWindowDocking[]::new);

            if (intersectionToolWindows.length > 0) {
                int x = (int)toolWindowDocking.getBounds().getX();
                int newX = (int)intersectionToolWindows[0].getBounds().getMaxX();
                Rectangle newBounds = new Rectangle(
                        newX,
                        (int) toolWindowDocking.getBounds().getY(),
                        (int)(toolWindowDocking.getBounds().getWidth() + (x - newX)),
                        (int) toolWindowDocking.getBounds().getHeight());
                toolWindowDocking.getToolWindowInfo().setBounds(newBounds);
            }
        }
    }

    private void dockTop(ToolWindowDocking[] floatedOrWindowsToolWindows) {
        ToolWindowDocking[] sortedByTopPosition = Arrays.stream(floatedOrWindowsToolWindows)
                .sorted((x, y) -> (int) (x.getBounds().getY() - y.getBounds().getY()))
                .skip(1)
                .toArray(ToolWindowDocking[]::new);

        for (ToolWindowDocking toolWindowDocking : sortedByTopPosition) {
            ToolWindowDocking[] intersectionToolWindows = Arrays.stream(floatedOrWindowsToolWindows)
                    .filter(x ->
                            !x.getToolWindowInfo().getId().equals(toolWindowDocking.getToolWindowInfo().getId()) &&
                            x.getBottomDockingBounds().intersects(toolWindowDocking.getTopDockingBounds()))
                    .sorted(Comparator.comparingInt(x -> (int) x.getBounds().getX()))
                    .toArray(ToolWindowDocking[]::new);

            if (intersectionToolWindows.length > 0) {
                int y = (int)toolWindowDocking.getBounds().getY();
                int newY = (int)intersectionToolWindows[0].getBounds().getMaxY();
                Rectangle newBounds = new Rectangle(
                        (int) toolWindowDocking.getBounds().getX(),
                        newY,
                        (int) toolWindowDocking.getBounds().getWidth(),
                        (int)(toolWindowDocking.getBounds().getHeight() + (y - newY)));
                toolWindowDocking.getToolWindowInfo().setBounds(newBounds);
            }
        }
    }
}
