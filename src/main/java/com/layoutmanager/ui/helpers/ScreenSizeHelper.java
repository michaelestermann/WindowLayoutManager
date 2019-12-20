package com.layoutmanager.ui.helpers;

import com.layoutmanager.persistence.ToolWindowInfo;
import sun.java2d.SunGraphicsEnvironment;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

public class ScreenSizeHelper {
    public static Rectangle getContainingScreenBounds(ToolWindowInfo toolWindow) {
        return Arrays
                .stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
                .map(x -> getScreenRectangle(x))
                .sorted(Comparator.comparingInt(x -> 0 - getIntersectionSize(x, toolWindow.getBounds())))
                .findFirst()
                .orElse(new Rectangle());
    }



    private static Rectangle getScreenRectangle(GraphicsDevice device) {
        Rectangle defaultBounds = SunGraphicsEnvironment.getUsableBounds(device);
        return new Rectangle(
                (int)defaultBounds.getX(),
                (int)defaultBounds.getY(),
                (int)defaultBounds.getWidth(),
                (int)defaultBounds.getHeight());
    }

    private static int getIntersectionSize(Rectangle screen, Rectangle window) {
        return (int)(Math.max(0, Math.min(screen.getMaxX(), window.getMaxX()) - Math.max(screen.getX(), window.getX())) *
                Math.max(0, Math.min(screen.getMaxY(), window.getMaxY()) - Math.max(screen.getY(), window.getY())));
    }
}
