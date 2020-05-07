package com.layoutmanager.ui.settings.importing;

import blazing.chain.LZSEncoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.layoutmanager.localization.MessagesHelper;
import com.layoutmanager.persistence.Layout;
import com.layoutmanager.ui.dialogs.LayoutNameValidator;
import com.layoutmanager.ui.helpers.ComponentNotificationHelper;
import com.layoutmanager.ui.settings.ImportExportConstants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

// TODO:
// Nicer UI
public class ImportDialog extends JDialog {
    public static final int OK_RESULT = 1;
    public static final int ABORT_RESULT = 0;
    private final LayoutNameValidator layoutNameValidator;

    private JPanel contentPane;
    private JButton importButton;
    private JButton abortButton;
    private JButton importFromFileButton;
    private JButton importFromClipboardButton;
    private JLabel layoutConfiguredWindowCountLabel;
    private JTextField layoutNameTextBox;

    private Layout importedLayout;
    private int result;

    public ImportDialog(LayoutNameValidator layoutNameValidator) {
        this.layoutNameValidator = layoutNameValidator;

        this.setContentPane(contentPane);
        this.setModal(true);
        this.setResizable(false);
        this.getRootPane().setDefaultButton(importButton);

        this.importButton.addActionListener(e -> onOK());
        this.abortButton.addActionListener(e -> onCancel());
        this.importFromClipboardButton.addActionListener(actionEvent -> importFromClipboard());
        this.importFromFileButton.addActionListener(actionEvent -> importFromFile());

        // call onCancel() when cross is clicked
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        this.contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public Layout getImportedLayout() {
        return this.importedLayout;
    }

    public int showDialogInCenterOf(JDialog parent) {
        this.setSize(this.getPreferredSize());
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
        return this.result;
    }

    private void importFromFile() {
        File selectedFile = selectFile();
        if (selectedFile != null) {
            importFile(selectedFile);
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
            String lzEncodedContent = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
            importLayout(lzEncodedContent);
        } catch (IOException e) {
            ComponentNotificationHelper.error(importFromFileButton, MessagesHelper.message("ImportDialog.UnexpectedError", e.getMessage()));
        }
    }

    private void importFromClipboard() {
        try {
            String lzEncodedContent = (String) Toolkit
                    .getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);

            importLayout(lzEncodedContent);
        } catch (Exception e) {
            ComponentNotificationHelper.error(importFromClipboardButton,  MessagesHelper.message("ImportDialog.UnexpectedError", e.getMessage()));
        }
    }

    private void importLayout(String lzEncodedContent) {
        // TODO Export to class
        String jsonContent = LZSEncoding.decompressFromBase64(lzEncodedContent);
        Gson gson = new GsonBuilder().create();
        Layout selectedLayout = gson.fromJson(jsonContent, Layout.class);

        selectLayout(selectedLayout);
    }

    private void selectLayout(Layout layout) {
        this.importedLayout = layout;

        this.layoutNameTextBox.setText(layout.getName());
        this.layoutNameTextBox.requestFocus();
        this.layoutConfiguredWindowCountLabel.setText(Integer.toString(layout.getToolWindows().length));

        ComponentNotificationHelper.info(layoutNameTextBox, MessagesHelper.message("ImportDialog.SuccessfullyLoadedLayout", layout.getName()));

        this.importButton.setEnabled(true);
        this.layoutNameTextBox.setEnabled(true);
    }

    private void onOK() {
        if (!this.layoutNameValidator.isValid(this.layoutNameTextBox.getText())) {
            ComponentNotificationHelper.error(importButton, MessagesHelper.message("LayoutNameValidation.InvalidName"));
            return;
        }


        this.result = OK_RESULT;
        dispose();
    }

    private void onCancel() {
        this.result = ABORT_RESULT;
        dispose();
    }
}
