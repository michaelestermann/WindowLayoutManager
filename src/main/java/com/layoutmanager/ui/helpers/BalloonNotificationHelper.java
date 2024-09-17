package com.layoutmanager.ui.helpers;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BalloonNotificationHelper {
    private static final int NOTIFICATION_DISPLAY_TIME_IN_SECONDS = 6;

    public static void info(String title, String content) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("WindowLayoutManager")
                .createNotification(title, content, NotificationType.INFORMATION);
        notify(notification);
    }

    public static void warning(String title, String content) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("WindowLayoutManager")
                .createNotification(title, content, NotificationType.WARNING);
        notify(notification);
    }

    private static void notify(Notification notification) {
        Project currentProject = getCurrentProject();
        notification.notify(currentProject);
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
        Project[] openedProjects = ProjectManager.getInstance().getOpenProjects();
        return Arrays.stream(openedProjects)
                .findFirst()
                .orElse(null);
    }
}
