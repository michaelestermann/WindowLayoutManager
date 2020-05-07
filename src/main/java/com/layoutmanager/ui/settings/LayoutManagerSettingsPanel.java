package com.layoutmanager.ui.settings;

import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.dialogs.LayoutNameDialog;
import com.layoutmanager.ui.dialogs.LayoutNameValidator;
import com.layoutmanager.ui.helpers.ComponentNotificationHelper;
import com.layoutmanager.ui.menu.WindowMenuService;
import com.layoutmanager.ui.settings.exporting.ExportDialog;
import com.layoutmanager.ui.settings.importing.ImportDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static javax.swing.JComponent.WHEN_FOCUSED;

public class LayoutManagerSettingsPanel {
    private final LayoutConfig layoutConfig;
    private final LayoutNameDialog layoutNameDialog;
    private final LayoutNameValidator layoutNameValidator;
    private final LayoutSerializer layoutSerializer;

    private ArrayList<Layout> currentLayouts = new ArrayList<>();
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
            LayoutSerializer layoutSerializer) {
        this.layoutConfig = layoutConfig;
        this.layoutNameDialog = layoutNameDialog;
        this.layoutNameValidator = layoutNameValidator;
        this.layoutSerializer = layoutSerializer;

        this.loadSettings(layoutConfig);

        this.layoutsTable.getSelectionModel().addListSelectionListener(listSelectionEvent -> this.selectedLayoutChanged());
        this.deleteButton.addActionListener(e -> this.deleteLayout());
        this.renameButton.addActionListener(e -> this.renameLayout());
        this.exportButton.addActionListener(actionEvent -> this.exportLayout());
        this.importButton.addActionListener(actionEvent -> this.importLayout());
    }

    private void loadSettings(LayoutConfig layoutConfig) {
        Collections.addAll(this.currentLayouts, layoutConfig.getLayouts());

        DefaultTableModel table = this.createTableContent();
        this.setKeyBindings(table);
        this.layoutsTable.setModel(table);
        this.layoutsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.useSmartDockingCheckbox.setSelected(layoutConfig.getSettings().getUseSmartDock());
    }

    private void setKeyBindings(DefaultTableModel tableModel){
        InputMap inputMap = layoutsTable.getInputMap(WHEN_FOCUSED);
        ActionMap actionMap = layoutsTable.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                int selectedRow = layoutsTable.getSelectedRow();
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
        DefaultTableModel table = (DefaultTableModel)this.layoutsTable.getModel();
        table.removeRow(this.layoutsTable.getSelectedRow());
    }

    private void renameLayout() {
        int selectedRow = this.layoutsTable.getSelectedRow();
        String newName = this.layoutNameDialog.show(this.currentLayouts.get(selectedRow).getName());

        if (newName != null) {
            this.layoutsTable.setValueAt(newName, selectedRow, 0);
        }
    }

    private void exportLayout() {
        int selectedRow = this.layoutsTable.getSelectedRow();
        Layout selectedLayout = this.currentLayouts.get(selectedRow);

        String encodedContent = this.layoutSerializer.serialize(selectedLayout);
        showExportDialog(selectedLayout, encodedContent);
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
            this.currentLayouts.add(importDialog.getImportedLayout());
            ((DefaultTableModel) layoutsTable.getModel()).fireTableDataChanged();
        }
    }

    @Nullable
    private JDialog getParentDialog() {
        return (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this.settingsPanel);
    }

    public boolean hasChanged() {
        return this.useSmartDockingCheckbox.isSelected() != this.layoutConfig.getSettings().getUseSmartDock() ||
                !Arrays.equals(
                        this.layoutConfig.getLayouts(),
                        this.currentLayouts
                                .stream()
                                .toArray(Layout[]::new));
    }

    public void apply() {
        this.layoutConfig.getSettings().setUseSmartDock(this.useSmartDockingCheckbox.isSelected());
        this.layoutConfig.setLayouts(this.currentLayouts
                .stream()
                .toArray(Layout[]::new));

        WindowMenuService windowMenuService = ServiceManager.getService(WindowMenuService.class);
        windowMenuService.recreate();
    }

    public JPanel getPanel() {
        return this.settingsPanel;
    }

    @NotNull
    private DefaultTableModel createTableContent() {
        return new DefaultTableModel(
                new String[]{
                        MessagesHelper.message("SettingsPage.NameColumn"),
                        MessagesHelper.message("SettingsPage.ConfiguredWindowsColumn")
                },
                currentLayouts.size()) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                String newLayoutName = aValue.toString();
                if (layoutNameValidator.isValid(newLayoutName)) {
                    currentLayouts.get(row).setName(aValue.toString());
                    this.fireTableChanged(new TableModelEvent(this, row));
                } else {
                    ComponentNotificationHelper.error(layoutsTable, MessagesHelper.message("LayoutNameValidation.InvalidName"));
                }
            }

            @Override
            public int getRowCount() {
                return currentLayouts.size();
            }

            @Override
            public void removeRow(int row) {
                currentLayouts.remove(row);
                this.fireTableRowsDeleted(row, row);
            }

            @Override
            public Object getValueAt(int row, int column) {
                Layout layout = currentLayouts.get(row);

                switch (column) {
                    case 0:
                        return layout.getName();
                    case 1:
                        return layout.getToolWindows().length;
                }
                return null;
            }
        };
    }
}
