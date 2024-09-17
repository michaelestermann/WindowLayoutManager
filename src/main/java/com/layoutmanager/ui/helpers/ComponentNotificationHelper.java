package com.layoutmanager.ui.helpers;

import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import javax.swing.JComponent;

public class ComponentNotificationHelper {

    public static void info(JComponent component, String message) {
        show(component, message, MessageType.INFO);
    }

    public static void error(JComponent component, String message) {
        show(component, message, MessageType.ERROR);
    }

    private static void show(JComponent component, String message, MessageType type) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(message, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(
                        RelativePoint.getCenterOf(component),
                        Balloon.Position.above);
    }
}
