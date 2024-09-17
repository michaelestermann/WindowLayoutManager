package com.layoutmanager.layout.store.smartdock.dockers;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import com.layoutmanager.layout.store.smartdock.ToolWindowDocking;

public class ToolWindowDocker {
    public void dock(ToolWindowDocking[] floatedOrWindowsToolWindows) {
        this.dockLeft(floatedOrWindowsToolWindows);
        this.dockTop(floatedOrWindowsToolWindows);
        this.dockRight(floatedOrWindowsToolWindows);
        this.dockBottom(floatedOrWindowsToolWindows);
    }

    private void dockLeft(ToolWindowDocking[] floatedOrWindowsToolWindows) {
        this.dockToolWindow(
            floatedOrWindowsToolWindows,
            (x, y) -> (int) (x.getBounds().getX() - y.getBounds().getX()),
            (x, y) -> x.getRightDockingBounds().intersects(y.getLeftDockingBounds()),
            Comparator.comparingInt(x -> (int) x.getBounds().getY()),
            (toolWindow, boundsToDock) -> {
                int x = (int) toolWindow.getBounds().getX();
                int newX = (int) boundsToDock.getMaxX();
                return new Rectangle(
                    newX,
                    (int) toolWindow.getBounds().getY(),
                    (int) (toolWindow.getBounds().getWidth() + (x - newX)),
                    (int) toolWindow.getBounds().getHeight());
            });
    }

    private void dockTop(ToolWindowDocking[] floatedOrWindowsToolWindows) {
        this.dockToolWindow(
            floatedOrWindowsToolWindows,
            (x, y) -> (int) (x.getBounds().getY() - y.getBounds().getY()),
            (x, y) -> x.getBottomDockingBounds().intersects(y.getTopDockingBounds()),
            Comparator.comparingInt(x -> (int) x.getBounds().getX()),
            (toolWindow, boundsToDock) -> {
                int y = (int) toolWindow.getBounds().getY();
                int newY = (int) boundsToDock.getMaxY();
                return new Rectangle(
                    (int) toolWindow.getBounds().getX(),
                    newY,
                    (int) toolWindow.getBounds().getWidth(),
                    (int) (toolWindow.getBounds().getHeight() + (y - newY)));
            });
    }

    private void dockRight(ToolWindowDocking[] floatedOrWindowsToolWindows) {
        this.dockToolWindow(
            floatedOrWindowsToolWindows,
            (x, y) -> (int) (x.getBounds().getMaxX() - y.getBounds().getMaxX()),
            (x, y) -> x.getLeftDockingBounds().intersects(y.getRightDockingBounds()),
            Comparator.comparingInt(x -> (int) x.getBounds().getY()),
            (toolWindow, boundsToDock) -> new Rectangle(
                (int) toolWindow.getBounds().getX(),
                (int) toolWindow.getBounds().getY(),
                (int) (boundsToDock.getX() - toolWindow.getBounds().getX()),
                (int) toolWindow.getBounds().getHeight()));
    }

    private void dockBottom(ToolWindowDocking[] floatedOrWindowsToolWindows) {
        this.dockToolWindow(
            floatedOrWindowsToolWindows,
            (x, y) -> (int) (x.getBounds().getMaxY() - y.getBounds().getMaxY()),
            (x, y) -> x.getTopDockingBounds().intersects(y.getBottomDockingBounds()),
            Comparator.comparingInt(x -> (int) x.getBounds().getY()),
            (toolWindow, boundsToDock) -> new Rectangle(
                (int) toolWindow.getBounds().getX(),
                (int) toolWindow.getBounds().getY(),
                (int) toolWindow.getBounds().getWidth(),
                (int) (boundsToDock.getY() - toolWindow.getBounds().getY())));
    }

    private void dockToolWindow(
            ToolWindowDocking[] floatedOrWindowsToolWindows,
            Comparator<? super ToolWindowDocking> sortForProcessing,
            BiPredicate<ToolWindowDocking, ToolWindowDocking> isIntersecting,
            Comparator<? super ToolWindowDocking> sortForClosestToDock,
            BiFunction<ToolWindowDocking, Rectangle, Rectangle> calculateNewBounds) {

        ToolWindowDocking[] sortedByLeftPosition = Arrays.stream(floatedOrWindowsToolWindows)
                .sorted(sortForProcessing)
                .skip(1)
                .toArray(ToolWindowDocking[]::new);

        for (ToolWindowDocking toolWindowDocking : sortedByLeftPosition) {
            ToolWindowDocking dockingTarget = Arrays.stream(floatedOrWindowsToolWindows)
                .filter(x ->
                    !x.getToolWindowInfo().getId().equals(toolWindowDocking.getToolWindowInfo().getId()) &&
                    isIntersecting.test(x, toolWindowDocking))
                .min(sortForClosestToDock)
                .orElse(null);

            if (dockingTarget != null) {
                Rectangle newBounds = calculateNewBounds.apply(toolWindowDocking, dockingTarget.getBounds());
                toolWindowDocking.getToolWindowInfo().setBounds(newBounds);
            }
        }
    }
}
