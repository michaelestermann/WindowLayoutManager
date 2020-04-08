package com.layoutmanager.ui.settings;

import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;

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
    // TODO: Move to common class
    public static final String FILE_ENDING = "wl";
    public static final String FILE_ENDING_WITH_DOT = "." + FILE_ENDING;

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
            showNotificationOnComponent(this.exportToClipboardButton, "Copied to clipboard!", MessageType.INFO); // TODO: Resources
        });

        exportToFileButton.addActionListener(actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save"); // TODO: Resources
            fileChooser.setSelectedFile(new File(this.layoutName + FILE_ENDING_WITH_DOT));
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Window layouts", FILE_ENDING); // TODO: Window layouts to constant
            fileChooser.setFileFilter(filter);

            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                byte[] encodedContent = this.content.getBytes();
                String fullPath = fileToSave.getAbsolutePath();
                if (!fullPath.toLowerCase().endsWith(FILE_ENDING_WITH_DOT)) {
                    fullPath += FILE_ENDING_WITH_DOT;
                }

                Path path = Paths.get(fullPath);
                try {
                    Files.write(path, encodedContent);
                    showNotificationOnComponent(this.exportToFileButton, "Saved to file " + path.getFileName().toString() +" !", MessageType.INFO); // TODO: Resources
                } catch (IOException e) {
                    showNotificationOnComponent(this.exportToFileButton, "Failed to write export file: " + e.getMessage(), MessageType.ERROR); // TODO: Resources
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
