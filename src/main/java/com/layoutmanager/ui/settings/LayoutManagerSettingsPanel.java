package com.layoutmanager.ui.settings;

import static com.intellij.openapi.keymap.impl.ui.KeymapPanel.addKeyboardShortcut;
import static javax.swing.JComponent.WHEN_FOCUSED;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeyMapBundle;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.keymap.impl.ActionShortcutRestrictions;
import com.intellij.openapi.keymap.impl.ShortcutRestrictions;
import com.intellij.openapi.keymap.impl.SystemShortcuts;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.text.StringUtil;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.action.ActionNameGenerator;
import com.layoutmanager.ui.dialogs.LayoutNameDialog;
import com.layoutmanager.ui.dialogs.LayoutNameValidator;
import com.layoutmanager.ui.helpers.ComponentNotificationHelper;
import com.layoutmanager.ui.settings.exporting.ExportDialog;
import com.layoutmanager.ui.settings.importing.ImportDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LayoutManagerSettingsPanel {
    private final LayoutConfig layoutConfig;
    private final LayoutNameDialog layoutNameDialog;
    private final LayoutNameValidator layoutNameValidator;
    private final LayoutSerializer layoutSerializer;
    private final LayoutDuplicator layoutDuplicator;
    private final WindowMenuChangesApplier windowMenuChangesApplier;
    private final ArrayList<EditLayout> editLayouts = new ArrayList<>();

    private JCheckBox useSmartDockingCheckbox;
    private JButton deleteButton;
    private JButton renameButton;
    private JButton exportButton;
    private JButton importButton;
    private JPanel settingsPanel;
    private JTable layoutsTable;

    public LayoutManagerSettingsPanel(
            LayoutConfig layoutConfig,
            LayoutNameDialog layoutNameDialog,
            LayoutNameValidator layoutNameValidator,
            LayoutSerializer layoutSerializer,
            LayoutDuplicator layoutDuplicator,
            WindowMenuChangesApplier windowMenuChangesApplier) {
        this.layoutConfig = layoutConfig;
        this.layoutNameDialog = layoutNameDialog;
        this.layoutNameValidator = layoutNameValidator;
        this.layoutSerializer = layoutSerializer;
        this.layoutDuplicator = layoutDuplicator;
        this.windowMenuChangesApplier = windowMenuChangesApplier;

        this.loadSettings(layoutConfig);

        this.layoutsTable
                .getSelectionModel()
                .addListSelectionListener(listSelectionEvent -> this.selectedLayoutChanged());
        this.deleteButton.addActionListener(e -> this.deleteLayout());
        this.renameButton.addActionListener(e -> this.renameLayout());
        this.exportButton.addActionListener(actionEvent -> this.exportLayout());
        this.importButton.addActionListener(actionEvent -> this.importLayout());
    }

    private void loadSettings(LayoutConfig layoutConfig) {
        Collections.addAll(
                this.editLayouts,
                Arrays
                        .stream(layoutConfig.getLayouts())
                        .map(x -> new EditLayout(x, this.layoutDuplicator.duplicate(x)))
                        .toArray(EditLayout[]::new));

        DefaultTableModel table = this.createTableContent();
        this.setKeyBindings(table);
        this.layoutsTable.setModel(table);
        this.layoutsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.layoutsTable.addMouseListener(new DoubleClickOpenShortcutListener());

        this.useSmartDockingCheckbox.setSelected(layoutConfig.getSettings().getUseSmartDock());
    }

    private void setKeyBindings(DefaultTableModel tableModel) {
        InputMap inputMap = this.layoutsTable.getInputMap(WHEN_FOCUSED);
        ActionMap actionMap = this.layoutsTable.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                int selectedRow = LayoutManagerSettingsPanel.this.layoutsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    tableModel.removeRow(selectedRow);
                }
            }
        });
    }

    private void selectedLayoutChanged() {
        this.deleteButton.setEnabled(this.layoutsTable.getSelectedRow() >= 0);
        this.renameButton.setEnabled(this.layoutsTable.getSelectedRow() >= 0);
        this.exportButton.setEnabled(this.layoutsTable.getSelectedRow() >= 0);
    }

    private void deleteLayout() {
        DefaultTableModel table = (DefaultTableModel) this.layoutsTable.getModel();
        table.removeRow(this.layoutsTable.getSelectedRow());
    }

    private void renameLayout() {
        int selectedRow = this.layoutsTable.getSelectedRow();
        String newName = this.layoutNameDialog.show(
                this.editLayouts
                        .get(selectedRow)
                        .editedLayout()
                        .getName());

        if (newName != null) {
            this.layoutsTable.setValueAt(newName, selectedRow, 0);
        }
    }

    private void exportLayout() {
        int selectedRow = this.layoutsTable.getSelectedRow();
        EditLayout selectedLayout = this.editLayouts.get(selectedRow);

        String encodedContent = this.layoutSerializer.serialize(selectedLayout.editedLayout());
        this.showExportDialog(selectedLayout.editedLayout(), encodedContent);
    }

    private void showExportDialog(Layout selectedLayout, String encodedContent) {
        ExportDialog exportDialog = new ExportDialog(selectedLayout.getName(), encodedContent);
        JDialog parent = this.getParentDialog();
        exportDialog.showDialogInCenterOf(parent);
    }

    private void importLayout() {
        ImportDialog importDialog = new ImportDialog(this.layoutNameValidator, this.layoutSerializer);
        JDialog parent = this.getParentDialog();
        if (importDialog.showDialogInCenterOf(parent) == ImportDialog.OK_RESULT) {
            this.editLayouts.add(new EditLayout(null, importDialog.getImportedLayout()));
            ((DefaultTableModel) this.layoutsTable.getModel()).fireTableDataChanged();
        }
    }

    @Nullable
    private JDialog getParentDialog() {
        return (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this.settingsPanel);
    }

    public boolean hasChanged() {
        return this.useSmartDockingCheckbox.isSelected() != this.layoutConfig.getSettings().getUseSmartDock() ||
                this.layoutsHasBeenAddedOrRemoved() ||
                this.layoutsHasBeenRenamed();
    }

    private boolean layoutsHasBeenAddedOrRemoved() {
        return !Arrays.equals(
                this.layoutConfig.getLayouts(),
                this.editLayouts
                        .stream()
                        .map(EditLayout::originalLayout)
                        .toArray(Layout[]::new));
    }

    private boolean layoutsHasBeenRenamed() {
        return this.editLayouts
                .stream()
                .anyMatch(x -> !x.originalLayout().getName().equals(x.editedLayout().getName()));
    }

    private void keymapChanged() {
        this.layoutsTable.repaint();
    }

    public void apply() {
        this.windowMenuChangesApplier.apply(this.editLayouts, this.layoutConfig.getLayouts());

        this.layoutConfig.getSettings().setUseSmartDock(this.useSmartDockingCheckbox.isSelected());
        this.layoutConfig.setLayouts(this.editLayouts
                .stream()
                .map(x ->
                        x.originalLayout() == null ?
                                x.editedLayout() :
                                x.originalLayout())
                .toArray(Layout[]::new));
    }

    public JPanel getPanel() {
        return this.settingsPanel;
    }

    @NotNull
    private DefaultTableModel createTableContent() {
        return new DefaultTableModel(
                new String[]{
                        MessagesHelper.message("SettingsPage.NameColumn"),
                        MessagesHelper.message("SettingsPage.ConfiguredWindowsColumn"),
                        MessagesHelper.message("SettingsPage.ShortcutColumn")
                },
                this.editLayouts.size()) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public void setValueAt(Object value, int row, int column) {
                String newLayoutName = value.toString();
                if (LayoutManagerSettingsPanel.this.layoutNameValidator.isValid(newLayoutName)) {
                    LayoutManagerSettingsPanel.this.editLayouts
                            .get(row)
                            .editedLayout()
                            .setName(value.toString());

                    this.fireTableChanged(new TableModelEvent(this, row));
                } else {
                    ComponentNotificationHelper.error(
                            LayoutManagerSettingsPanel.this.layoutsTable,
                            MessagesHelper.message("LayoutNameValidation.InvalidName"));
                }
            }

            @Override
            public int getRowCount() {
                return LayoutManagerSettingsPanel.this.editLayouts.size();
            }

            @Override
            public void removeRow(int row) {
                LayoutManagerSettingsPanel.this.editLayouts.remove(row);
                this.fireTableRowsDeleted(row, row);
            }

            @Override
            public Object getValueAt(int row, int column) {
                Layout layout = LayoutManagerSettingsPanel.this.editLayouts.get(row).editedLayout();

                return switch (column) {
                    case 0 -> layout.getName();
                    case 1 -> layout.getToolWindows().length;
                    case 2 -> this.getKeyMap(layout);
                    default -> null;
                };
            }

            private String getKeyMap(Layout layout) {
                Keymap keymap = KeymapManager.getInstance().getActiveKeymap();
                String actionName = ActionNameGenerator.getActionNameForLayout(layout);

                Shortcut[] shortcuts = keymap.getShortcuts(actionName);
                return Arrays.stream(shortcuts)
                        .map(KeymapUtil::getShortcutText)
                        .collect(Collectors.joining(", "));
            }
        };
    }

    private @NotNull DefaultActionGroup createEditActionGroup(@NotNull String actionId, Keymap selectedKeymap) {
        DefaultActionGroup group = new DefaultActionGroup();
        final ShortcutRestrictions restrictions = ActionShortcutRestrictions.getInstance().getForActionId(actionId);
        if (restrictions.allowKeyboardShortcut) {
            group.add(new AddKeyboardShortcutAction(actionId, restrictions, selectedKeymap));
        }

        group.addSeparator();

        Shortcut[] shortcuts = selectedKeymap.getShortcuts(actionId);
        for (Shortcut shortcut : shortcuts) {
            group.add(new RemoveShortcutAction(shortcut, selectedKeymap, actionId));
        }

        if (shortcuts.length > 2) {
            group.add(new Separator());
            group.add(new RemoveAllShortcuts(selectedKeymap, actionId));
        }

        return group;
    }

    private class DoubleClickOpenShortcutListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 || SwingUtilities.isRightMouseButton(e)) {
                JTable table = (JTable) e.getSource();
                int column = table.getSelectedColumn();
                if (column == 2) {
                    EditLayout layout = LayoutManagerSettingsPanel.this.editLayouts.get(table.getSelectedRow());
                    String actionId = ActionNameGenerator.getActionNameForLayout(layout.editedLayout());

                    DefaultActionGroup group = LayoutManagerSettingsPanel.this.createEditActionGroup(
                            actionId,
                            KeymapManager.getInstance().getActiveKeymap());
                    ActionManager.getInstance()
                            .createActionPopupMenu("popup@Keymap.ActionsTree.Menu", group)
                            .getComponent()
                            .show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

    private final class AddKeyboardShortcutAction extends DumbAwareAction {
        private final @NotNull String actionId;
        private final ShortcutRestrictions restrictions;
        private final Keymap keymap;

        private AddKeyboardShortcutAction(@NotNull String actionId, ShortcutRestrictions restrictions, Keymap keymap) {
            super(IdeBundle.messagePointer("action.Anonymous.text.add.keyboard.shortcut"));
            this.actionId = actionId;
            this.restrictions = restrictions;
            this.keymap = keymap;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            addKeyboardShortcut(
                    this.actionId,
                    this.restrictions,
                    this.keymap,
                    LayoutManagerSettingsPanel.this.layoutsTable,
                    null,
                    SystemShortcuts.getInstance());
            LayoutManagerSettingsPanel.this.keymapChanged();
        }
    }

    private final class RemoveShortcutAction extends DumbAwareAction {
        private final Shortcut shortcut;
        private final Keymap keymap;
        private final @NotNull String actionId;

        private RemoveShortcutAction(Shortcut shortcut, Keymap keymap, @NotNull String actionId) {
            super(IdeBundle.message("action.text.remove.0", KeymapUtil.getShortcutText(shortcut)));
            this.shortcut = shortcut;
            this.keymap = keymap;
            this.actionId = actionId;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            this.keymap.removeShortcut(this.actionId, this.shortcut);
            if (StringUtil.startsWithChar(this.actionId, '$')) {
                this.keymap.removeShortcut(KeyMapBundle.message("editor.shortcut", this.actionId.substring(1)), this.shortcut);
            }
            LayoutManagerSettingsPanel.this.keymapChanged();
        }
    }

    private final class RemoveAllShortcuts extends DumbAwareAction {
        private final Keymap keymap;
        private final String actionId;

        private RemoveAllShortcuts(Keymap selectedKeymap, @NotNull String actionId) {
            super(IdeBundle.messagePointer("action.text.remove.all.shortcuts"));
            this.keymap = selectedKeymap;
            this.actionId = actionId;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent event) {
            this.keymap.removeAllActionShortcuts(this.actionId);
            LayoutManagerSettingsPanel.this.keymapChanged();
        }
    }
}