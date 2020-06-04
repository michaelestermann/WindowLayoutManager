package com.layoutmanager.layout.store.overwrite;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.wm.ToolWindowManager;

import com.layoutmanager.layout.store.LayoutCreator;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.helpers.BaloonNotificationHelper;
import com.layoutmanager.ui.menu.WindowMenuService;

import org.jetbrains.annotations.NotNull;

// TODO:
//  - Add new layout to all buttons (Restore, delete, apply) -> Do not create a new action
//  - Pass layout as variable (Ctor)
//  - Layout must notify, when name has changed, so all buttons can update their captions
public class OverwriteLayoutAction extends AnAction implements DumbAware {

    private final LayoutCreator layoutCreator;
    public final int number;

    public OverwriteLayoutAction(
            LayoutCreator layoutCreator,
            int number) {
        this.layoutCreator = layoutCreator;
        this.number = number;

        Layout layout = LayoutConfig.getInstance().getLayout(number);
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(layout.getName());
        presentation.setIcon(AllIcons.Actions.Edit);
    }

    @Override
    public void update(AnActionEvent e) {
        Layout layout = LayoutConfig.getInstance().getLayout(this.number);
        e.getPresentation().setText(layout.getName());
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Layout previousLayout = LayoutConfig.getInstance().getLayout(this.number);
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(event.getProject());
        Layout updatedLayout = this.layoutCreator.create(toolWindowManager, previousLayout.getName());

        if (updatedLayout != null) {
            this.storeLayout(updatedLayout);
            this.updateWindowMenuItems();
            this.showNotification(updatedLayout, previousLayout);
        }
    }

    private void storeLayout(Layout layout) {
        //LayoutConfig.getInstance().setLayout(this.number, layout);
    }

    private void updateWindowMenuItems() {
        //WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        //windowMenuService.recreate();
    }

    private void showNotification(Layout updatedLayout, Layout previousLayout) {
        BaloonNotificationHelper.info(
                MessagesHelper.message("StoreLayout.Overwrite.Notification.Title"),
                MessagesHelper.message("StoreLayout.Overwrite.Notification.Content", previousLayout.getName(), updatedLayout.getName()));
    }
}
