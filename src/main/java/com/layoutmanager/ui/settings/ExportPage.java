package com.layoutmanager.ui.settings;

import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.layoutmanager.localization.MessagesHelper;

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
            showNotificationOnComponent(this.exportToClipboardButton, MessagesHelper.message("ExportPage.CopiedToClipboard"), MessageType.INFO);
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
                    showNotificationOnComponent(this.exportToFileButton, MessagesHelper.message("ExportPage.SavedTo", path.getFileName().toString()), MessageType.INFO);
                } catch (IOException e) {
                    showNotificationOnComponent(this.exportToFileButton, MessagesHelper.message("ExportPage.FailedToWriteFile", e.getMessage()), MessageType.ERROR);
                }
            }
        });

        this.layoutNameLabel.setText(layoutName);
        this.exportTextBox.setText(content);
    }

    // TODO: To library
    private void showNotificationOnComponent(JComponent component, String message, MessageType type) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(message, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(
                        RelativePoint.getCenterOf(component),
                        Balloon.Position.above);
    }

    public JPanel getPanel() {
        return this.importPanel;
    }
}
