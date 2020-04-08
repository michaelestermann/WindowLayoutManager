package com.layoutmanager.ui.settings;

import blazing.chain.LZSEncoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.components.ServiceManager;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.persistence.LayoutConfig;
import com.layoutmanager.ui.dialogs.LayoutNameDialog;
import com.layoutmanager.ui.menu.WindowMenuService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class LayoutManagerSettingsPanel {
    private final LayoutConfig layoutConfig;
    private final LayoutNameDialog layoutNameDialog;

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
            LayoutNameDialog layoutNameDialog) {
        this.layoutConfig = layoutConfig;
        this.layoutNameDialog = layoutNameDialog;

        Collections.addAll(this.currentLayouts, layoutConfig.getLayouts());

        DefaultTableModel table = createTableContent();
        this.layoutsTable.setModel(table);
        this.layoutsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        useSmartDockingCheckbox.setSelected(layoutConfig.getSettings().getUseSmartDock());

        this.layoutsTable.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
            deleteButton.setEnabled(this.layoutsTable.getSelectedRow() >= 0);
            renameButton.setEnabled(this.layoutsTable.getSelectedRow() >= 0);
            exportButton.setEnabled(this.layoutsTable.getSelectedRow() >= 0);
        });
        this.deleteButton.addActionListener(e ->
                ((DefaultTableModel)this.layoutsTable.getModel())
                    .removeRow(this.layoutsTable.getSelectedRow()));
        this.renameButton.addActionListener(e -> {
            int selectedRow = this.layoutsTable.getSelectedRow();
            String newName = this.layoutNameDialog.show(this.currentLayouts.get(selectedRow).getName());

            if (newName != null) {
                this.layoutsTable.setValueAt(newName, selectedRow, 0);
            }
        });

        exportButton.addActionListener(actionEvent -> {
            int selectedRow = this.layoutsTable.getSelectedRow();
            Layout selectedLayout = this.currentLayouts.get(selectedRow);

            // TODO: Extract to class
            Gson gson = new GsonBuilder().create();
            String jsonContent = gson.toJson(selectedLayout);
            String lzEncodedContent = LZSEncoding.compressToBase64(jsonContent);

            ExportPage exportPage = new ExportPage(selectedLayout.getName(), lzEncodedContent);

            showDialog(exportPage.getPanel());

        });

        importButton.addActionListener(actionEvent -> {
            ImportDialog importDialog = new ImportDialog();
            JDialog parent = getParentDialog();
            if (importDialog.showDialogInCenterOf(parent) == ImportDialog.OK_RESULT) {
                this.currentLayouts.add(importDialog.getImportedLayout());
                ((DefaultTableModel)layoutsTable.getModel()).fireTableDataChanged();
            }
        });
    }

    private void showDialog(JComponent component) {
        // TODO: Extract to class
        JDialog parent = getParentDialog();
        final JDialog dialog = new JDialog(parent, "Export layout", true); // TODO: Resources
        dialog.getContentPane().add(component);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
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

    @NotNull
    private DefaultTableModel createTableContent() {
        DefaultTableModel model = new DefaultTableModel(new String[] { "Name", "Configured Windows" }, currentLayouts.size()) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                currentLayouts.get(row).setName(aValue.toString());
                this.fireTableChanged(new TableModelEvent(this, row));
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

                switch(column) {
                    case 0:
                        return layout.getName();
                    case 1:
                        return layout.getToolWindows().length;
                }
                return null;
            }
        };

        return model;
    }

    public JPanel getPanel() {
        return this.settingsPanel;
    }
}
