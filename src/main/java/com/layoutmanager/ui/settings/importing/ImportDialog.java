package com.layoutmanager.ui.settings.importing;

import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.ui.dialogs.LayoutNameValidator;
import com.layoutmanager.ui.helpers.ComponentNotificationHelper;
import com.layoutmanager.ui.settings.ImportExportConstants;
import com.layoutmanager.ui.settings.LayoutSerializer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImportDialog extends JDialog {
    public static final int OK_RESULT = 1;
    public static final int ABORT_RESULT = 0;
    private final LayoutNameValidator layoutNameValidator;
    private final LayoutSerializer layoutSerializer;

    private JPanel contentPanel;
    private JButton importButton;
    private JButton abortButton;
    private JButton importFromFileButton;
    private JButton importFromClipboardButton;
    private JLabel layoutConfiguredWindowCountLabel;
    private JTextField layoutNameTextBox;

    private Layout importedLayout;
    private int result;

    public ImportDialog(
            LayoutNameValidator layoutNameValidator,
            LayoutSerializer layoutSerializer) {
        this.layoutNameValidator = layoutNameValidator;
        this.layoutSerializer = layoutSerializer;

        this.setContentPane(this.contentPanel);
        this.setModal(true);
        this.setResizable(false);
        this.getRootPane().setDefaultButton(this.importButton);

        this.importButton.addActionListener(e -> this.onOK());
        this.abortButton.addActionListener(e -> this.onCancel());
        this.importFromClipboardButton.addActionListener(actionEvent -> this.importFromClipboard());
        this.importFromFileButton.addActionListener(actionEvent -> this.importFromFile());

        // call onCancel() when cross is clicked
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ImportDialog.this.onCancel();
            }
        });

        this.contentPanel.registerKeyboardAction(e -> this.onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public Layout getImportedLayout() {
        return this.importedLayout;
    }

    public int showDialogInCenterOf(JDialog parent) {
        this.setTitle(MessagesHelper.message("ImportDialog.Title"));
        this.setSize(this.getPreferredSize());
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
        return this.result;
    }

    private void importFromFile() {
        File selectedFile = this.selectFile();
        if (selectedFile != null) {
            this.importFile(selectedFile);
        }
    }

    private File selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(MessagesHelper.message("ImportDialog.SelectFileTitle"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(ImportExportConstants.FILE_TYPE_NAME, ImportExportConstants.FILE_ENDING);
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);
        return result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile().exists() ?
                fileChooser.getSelectedFile() : null;
    }

    private void importFile(File file) {
        try {
            String encodedContent = Files.readString(Paths.get(file.getPath()));
            this.importLayout(encodedContent);
        } catch (IOException e) {
            ComponentNotificationHelper.error(this.importFromFileButton, MessagesHelper.message("ImportDialog.IOException", file.getName(), e.getMessage()));
            this.deselectLayout();
        } catch (Exception e) {
            ComponentNotificationHelper.error(this.importFromFileButton,  MessagesHelper.message("ImportDialog.UnknownFormat"));
            this.deselectLayout();
        }
    }

    private void importFromClipboard() {
        try {
            String lzEncodedContent = (String) Toolkit
                    .getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);

            this.importLayout(lzEncodedContent);
        } catch (Exception e) {
            ComponentNotificationHelper.error(this.importFromClipboardButton,  MessagesHelper.message("ImportDialog.UnknownFormat"));
            this.deselectLayout();
        }
    }

    private void importLayout(String encodedContent) {
        Layout selectedLayout = this.layoutSerializer.deserialize(encodedContent);
        this.selectLayout(selectedLayout);
    }

    private void selectLayout(Layout layout) {
        this.importedLayout = layout;

        this.layoutNameTextBox.setText(layout.getName());
        this.layoutNameTextBox.requestFocus();
        this.layoutConfiguredWindowCountLabel.setText(Integer.toString(layout.getToolWindows().length));

        ComponentNotificationHelper.info(this.layoutNameTextBox, MessagesHelper.message("ImportDialog.SuccessfullyLoadedLayout", layout.getName()));

        this.importButton.setEnabled(true);
        this.layoutNameTextBox.setEnabled(true);
    }

    private void deselectLayout() {
        this.importedLayout = null;
        this.importButton.setEnabled(false);
        this.layoutNameTextBox.setText("");
        this.layoutNameTextBox.setEnabled(false);
        this.layoutConfiguredWindowCountLabel.setText("-");
    }

    private void onOK() {
        if (!this.layoutNameValidator.isValid(this.layoutNameTextBox.getText())) {
            ComponentNotificationHelper.error(this.importButton, MessagesHelper.message("LayoutNameValidation.InvalidName"));
            return;
        }
        this.importedLayout.setName(this.layoutNameTextBox.getText());

        this.result = OK_RESULT;
        this.dispose();
    }

    private void onCancel() {
        this.result = ABORT_RESULT;
        this.dispose();
    }
}
