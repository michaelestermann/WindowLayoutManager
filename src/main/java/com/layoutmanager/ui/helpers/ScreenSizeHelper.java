package com.layoutmanager.ui.helpers;

import com.layoutmanager.persistence.ToolWindowInfo;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

import java.util.Arrays;
import java.util.Comparator;

public class ScreenSizeHelper {
    public static Rectangle getContainingScreenBounds(ToolWindowInfo toolWindow) {
        return Arrays
                .stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
                .map(ScreenSizeHelper::getScreenRectangle)
                .min(Comparator.comparingInt(x -> -getIntersectionSize(x, toolWindow.getBounds())))
                .orElse(new Rectangle());
    }

    private static Rectangle getScreenRectangle(GraphicsDevice device) {
        Rectangle bounds = device.getDefaultConfiguration().getBounds();
        Insets insets = Toolkit
                .getDefaultToolkit()
                .getScreenInsets(device.getDefaultConfiguration());
        
        int x = bounds.x + insets.left;
        int y = bounds.y + insets.top;
        int width = bounds.width - (insets.left + insets.right);
        int height = bounds.height - (insets.top + insets.bottom);
        
        return new Rectangle(x, y, width, height);
    }

    private static int getIntersectionSize(Rectangle screen, Rectangle window) {
        return (int)(Math.max(0, Math.min(screen.getMaxX(), window.getMaxX()) - Math.max(screen.getX(), window.getX())) *
                Math.max(0, Math.min(screen.getMaxY(), window.getMaxY()) - Math.max(screen.getY(), window.getY())));
    }
}
