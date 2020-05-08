package com.layoutmanager.ui.helpers;

import com.layoutmanager.persistence.ToolWindowInfo;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import java.util.Arrays;
import java.util.Comparator;

import sun.java2d.SunGraphicsEnvironment;

public class ScreenSizeHelper {
    public static Rectangle getContainingScreenBounds(ToolWindowInfo toolWindow) {
        return Arrays
                .stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
                .map(ScreenSizeHelper::getScreenRectangle)
                .min(Comparator.comparingInt(x -> -getIntersectionSize(x, toolWindow.getBounds())))
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
