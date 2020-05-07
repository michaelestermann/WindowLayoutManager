package com.layoutmanager.ui.settings.exporting;

import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.ui.helpers.ComponentNotificationHelper;
import com.layoutmanager.ui.settings.ImportExportConstants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// TODO: Change to dialog and cleanup
public class ExportPage {
    private JTextArea exportTextBox;
    private JPanel importPanel;
    private JButton exportToFileButton;
    private JButton exportToClipboardButton;
    private JButton closeButton;
    private JLabel layoutNameLabel;

    private String layoutName;
    private String content;

    // TODO: Move to own methods
    public ExportPage(String layoutName, String content) {
        this.layoutName = layoutName;
        this.content = content;

        closeButton.addActionListener(actionEvent -> {
            JDialog window = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this.importPanel);
            window.dispose();
        });

        exportToClipboardButton.addActionListener(actionEvent -> {
            this.exportTextBox.requestFocus();
            this.exportTextBox.selectAll();
            Toolkit
                .getDefaultToolkit()
                .getSystemClipboard()
                .setContents(
                    new StringSelection(this.exportTextBox.getText()),
                    null);
            ComponentNotificationHelper.info(this.exportToClipboardButton, MessagesHelper.message("ExportPage.CopiedToClipboard"));
        });

        exportToFileButton.addActionListener(actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(MessagesHelper.message("ExportPage.SaveFileTitle"));
            fileChooser.setSelectedFile(new File(this.layoutName + ImportExportConstants.FILE_ENDING_WITH_DOT));
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(ImportExportConstants.FILE_TYPE_NAME, ImportExportConstants.FILE_ENDING);
            fileChooser.setFileFilter(filter);

            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                byte[] encodedContent = this.content.getBytes();
                String fullPath = fileToSave.getAbsolutePath();
                if (!fullPath.toLowerCase().endsWith(ImportExportConstants.FILE_ENDING_WITH_DOT)) {
                    fullPath += ImportExportConstants.FILE_ENDING_WITH_DOT;
                }

                Path path = Paths.get(fullPath);
                try {
                    Files.write(path, encodedContent);
                    ComponentNotificationHelper.info(this.exportToFileButton, MessagesHelper.message("ExportPage.SavedTo", path.getFileName().toString()));
                } catch (IOException e) {
                    ComponentNotificationHelper.error(this.exportToFileButton, MessagesHelper.message("ExportPage.FailedToWriteFile", e.getMessage()));
                }
            }
        });

        this.layoutNameLabel.setText(layoutName);
        this.exportTextBox.setText(content);
    }

    public JPanel getPanel() {
        return this.importPanel;
    }
}
