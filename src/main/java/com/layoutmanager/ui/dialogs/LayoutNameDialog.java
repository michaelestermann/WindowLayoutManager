package com.layoutmanager.ui.dialogs;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.Messages;
import com.layoutmanager.localization.MessagesHelper;

public class LayoutNameDialog {

    public String show(String defaultName) {
            String name;
            do {
                name = Messages.showInputDialog(
                        MessagesHelper.message("StoreLayout.Dialog.Title"),
                        MessagesHelper.message("StoreLayout.Dialog.Content"),
                        AllIcons.Actions.Edit,
                        defaultName,
                        null);
            } while (name != null && name.isEmpty());

            return name;
    }
}
