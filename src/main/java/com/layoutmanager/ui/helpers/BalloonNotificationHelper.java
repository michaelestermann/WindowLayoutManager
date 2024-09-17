package com.layoutmanager.ui.helpers;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

public class BalloonNotificationHelper {
    private static final int NOTIFICATION_DISPLAY_TIME_IN_SECONDS = 6;
    private static final NotificationGroup LAYOUT_MANAGER_GROUP =
            new NotificationGroup("WindowLayoutManager", NotificationDisplayType.BALLOON, true);

    public static void info(String title, String content) {
        Notification notification = LAYOUT_MANAGER_GROUP.createNotification(
                title,
                null,
                content,
                NotificationType.INFORMATION);
        notify(notification);
    }

    public static void warning(String title, String content) {
        Notification notification = LAYOUT_MANAGER_GROUP.createNotification(
                title,
                null,
                content,
                NotificationType.WARNING);
        notify(notification);
    }

    private static void notify(Notification notification) {
        Project currentProject = getCurrentProject();
        Notifications.Bus.notify(notification, currentProject);

        hideAfterTimeSpan(notification);
    }

    private static void hideAfterTimeSpan(Notification notification) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(
                notification::expire,
                NOTIFICATION_DISPLAY_TIME_IN_SECONDS,
                TimeUnit.SECONDS);
    }

    private static Project getCurrentProject() {
        Project[] openedProjects = ProjectManager
                .getInstance()
                .getOpenProjects();
        return Arrays.stream(openedProjects)
                .findFirst()
                .orElse(null);
    }
}
