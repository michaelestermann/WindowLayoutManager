package com.layoutmanager.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.menu.WindowMenuService;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import org.jetbrains.annotations.NotNull;

public class NewLayoutAction extends AnAction {

    public NewLayoutAction() {
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(MessagesHelper.message("MenuStoreNewLayout"));
        presentation.setIcon(AllIcons.Welcome.CreateNewProject);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Layout updatedLayout = LayoutCreator.create(event.getProject());

        if (updatedLayout != null) {
            this.storeLayout(updatedLayout);
            updateWindowMenuItems();
        }
    }

    private void updateWindowMenuItems() {
        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.recreate();
    }

    private void storeLayout(Layout layout) {
        LayoutConfig.getInstance().addLayout(layout);
    }
}
