package com.layoutmanager.ui;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import java.util.Arrays;

public class NotificationHelper {
    private static final NotificationGroup GROUP =
            new NotificationGroup("demo.notifications.balloon", NotificationDisplayType.BALLOON, true);

    public static void info(String title, String content) {
        Notification notification = GROUP.createNotification(title, null, content, NotificationType.INFORMATION);

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
