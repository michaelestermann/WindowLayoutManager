package com.layoutmanager.ui.helpers;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import java.util.Arrays;
import java.util.UUID;

public class NotificationHelper {
    private static final NotificationGroup DEFAULT_GROUP =
            new NotificationGroup("demo.notifications.balloon", NotificationDisplayType.STICKY_BALLOON, true);

    public static void info(String title, String content) {
        Notification notification = DEFAULT_GROUP.createNotification(title, null, content, NotificationType.INFORMATION);
        notify(notification);
    }

    public static void warning(String title, String content) {
        String groupId = UUID.nameUUIDFromBytes(title.getBytes()).toString();
        NotificationGroup groupByNotificationTitle = new NotificationGroup(groupId, NotificationDisplayType.STICKY_BALLOON, true);
        Notification notification = groupByNotificationTitle.createNotification(title, null, content, NotificationType.WARNING);
        notify(notification);
    }

    private static void notify(Notification notification) {
        Project currentProject = getCurrentProject();
        Notifications.Bus.notify(notification, currentProject);
    }

    private static Project getCurrentProject() {
        Project[] openedProjects = ProjectManager.getInstance().getOpenProjects();
        return Arrays.stream(openedProjects)
                .findFirst()
                .orElse(null);
    }
}
