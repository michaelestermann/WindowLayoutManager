package com.layoutmanager.ui.settings.exporting;

import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.ui.helpers.ComponentNotificationHelper;
import com.layoutmanager.ui.settings.ImportExportConstants;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jetbrains.annotations.NotNull;

public class ExportDialog extends JDialog {
    private JTextArea exportTextBox;
    private JPanel contentPanel;
    private JButton exportToFileButton;
    private JButton exportToClipboardButton;
    private JButton closeButton;
    private JLabel layoutNameLabel;

    private final String layoutName;
    private final String content;

    public ExportDialog(String layoutName, String content) {
        this.layoutName = layoutName;
        this.content = content;

        this.setContentPane(this.contentPanel);
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.closeButton);

        this.exportToClipboardButton.addActionListener(actionEvent -> this.exportToClipboard());
        this.exportToFileButton.addActionListener(actionEvent -> this.exportToFile());
        this.closeButton.addActionListener(actionEvent -> this.onClose());

        this.layoutNameLabel.setText(layoutName);
        this.exportTextBox.setText(content);

        this.contentPanel.registerKeyboardAction(
                e -> this.onClose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void showDialogInCenterOf(JDialog parent) {
        this.setTitle(MessagesHelper.message("ExportDialog.Title"));
        this.setSize(this.getPreferredSize());
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    private void exportToClipboard() {
        this.exportTextBox.requestFocus();
        this.exportTextBox.selectAll();
        Toolkit
                .getDefaultToolkit()
                .getSystemClipboard()
                .setContents(
                        new StringSelection(this.exportTextBox.getText()),
                        null);
        ComponentNotificationHelper.info(this.exportToClipboardButton, MessagesHelper.message("ExportDialog.CopiedToClipboard"));
    }

    private void exportToFile() {
        File selectedFile = this.selectFile();
        if (selectedFile != null) {
            Path path = this.getPath(selectedFile);
            this.writeContentToFile(path);
        }
    }

    private File selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(MessagesHelper.message("ExportDialog.SaveFileTitle"));
        fileChooser.setSelectedFile(new File(this.layoutName + ImportExportConstants.FILE_ENDING_WITH_DOT));
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(ImportExportConstants.FILE_TYPE_NAME, ImportExportConstants.FILE_ENDING);
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showSaveDialog(null);
        return result == JFileChooser.APPROVE_OPTION ?
                fileChooser.getSelectedFile() :
                null;
    }

    @NotNull
    private Path getPath(File selectedFile) {
        String fullPath = selectedFile.getAbsolutePath();
        if (!fullPath.toLowerCase().endsWith(ImportExportConstants.FILE_ENDING_WITH_DOT)) {
            fullPath += ImportExportConstants.FILE_ENDING_WITH_DOT;
        }

        return Paths.get(fullPath);
    }

    private void writeContentToFile(Path path) {
        try {
            byte[] encodedContent = this.content.getBytes();
            Files.write(path, encodedContent);
            ComponentNotificationHelper.info(this.exportToFileButton, MessagesHelper.message("ExportDialog.SavedTo", path.getFileName().toString()));
        } catch (IOException e) {
            ComponentNotificationHelper.error(this.exportToFileButton, MessagesHelper.message("ExportDialog.FailedToWriteFile", e.getMessage()));
        }
    }

    private void onClose() {
        JDialog window = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this.contentPanel);
        window.dispose();
    }
}
