package com.layoutmanager.layout.store.smartdock.dockers;

import java.awt.Rectangle;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.layoutmanager.layout.store.smartdock.ToolWindowDocking;

public class ScreenBorderDocker {
    public void dock(ToolWindowDocking[] floatedOrWindowsToolWindows, int threshold) {
        this.pinLeft(floatedOrWindowsToolWindows, threshold);
        this.pinTop(floatedOrWindowsToolWindows, threshold);
        this.pinRight(floatedOrWindowsToolWindows, threshold);
        this.pinBottom(floatedOrWindowsToolWindows, threshold);
    }

    private void pinLeft(ToolWindowDocking[] floatedOrWindowsToolWindows, int threshold) {
        this.pinToScreenBorder(
            floatedOrWindowsToolWindows,
            threshold,
            toolWindowDocking -> (int)(toolWindowDocking.getBounds().getX() - toolWindowDocking.getContainingScreen().getX()),
            (toolWindowDocking, difference) -> new Rectangle(
                (int)toolWindowDocking.getContainingScreen().getX(),
                (int)toolWindowDocking.getBounds().getY(),
                (int)(toolWindowDocking.getBounds().getWidth() + difference),
                (int)toolWindowDocking.getBounds().getHeight()));
    }

    private void pinTop(ToolWindowDocking[] floatedOrWindowsToolWindows, int threshold) {
        this.pinToScreenBorder(
            floatedOrWindowsToolWindows,
            threshold,
            x -> (int)(x.getBounds().getY() - x.getContainingScreen().getY()),
            (toolWindowDocking, difference) -> new Rectangle(
                (int)toolWindowDocking.getBounds().getX(),
                (int)toolWindowDocking.getContainingScreen().getY(),
                (int)toolWindowDocking.getBounds().getWidth(),
                (int)(toolWindowDocking.getBounds().getHeight() + 5)));
    }

    private void pinRight(ToolWindowDocking[] floatedOrWindowsToolWindows, int threshold) {
        this.pinToScreenBorder(
            floatedOrWindowsToolWindows,
            threshold,
            toolWindowDocking -> (int)(toolWindowDocking.getContainingScreen().getMaxX() - toolWindowDocking.getBounds().getMaxX()),
            (toolWindowDocking, difference) ->  new Rectangle(
                (int)toolWindowDocking.getBounds().getX(),
                (int)toolWindowDocking.getBounds().getY(),
                (int)(toolWindowDocking.getBounds().getWidth() + difference),
                (int)toolWindowDocking.getBounds().getHeight()));
    }

    private void pinBottom(ToolWindowDocking[] floatedOrWindowsToolWindows, int threshold) {
        this.pinToScreenBorder(
            floatedOrWindowsToolWindows,
            threshold,
            toolWindowDocking -> (int)(toolWindowDocking.getBounds().getY() - toolWindowDocking.getContainingScreen().getY()),
            (toolWindowDocking, difference) ->  new Rectangle(
                (int)toolWindowDocking.getBounds().getX(),
                (int)toolWindowDocking.getBounds().getY(),
                (int)toolWindowDocking.getBounds().getWidth(),
                (int)(toolWindowDocking.getBounds().getHeight() + difference)));
    }

    private void pinToScreenBorder(
            ToolWindowDocking[] toolWindowDockings,
            int threshold,
            Function<ToolWindowDocking, Integer> getDifference,
            BiFunction<ToolWindowDocking, Integer, Rectangle> calculateBounds) {

        for (ToolWindowDocking toolWindowDocking : toolWindowDockings) {
            Integer difference = getDifference.apply(toolWindowDocking);
            if (difference != 0 && difference < threshold) {
                toolWindowDocking.getToolWindowInfo().setBounds(calculateBounds.apply(toolWindowDocking, difference));
            }
        }
    }
}
