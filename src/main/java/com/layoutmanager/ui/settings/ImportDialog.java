package com.layoutmanager.ui.settings;

import blazing.chain.LZSEncoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.layoutmanager.persistence.Layout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

// TODO:
// Name as textbox -> possible to edit
// Nicer UI
public class ImportDialog extends JDialog {
    public static final int OK_RESULT = 1;
    public static final int ABORT_RESULT = 0;

    private JPanel contentPane;
    private JButton importButton;
    private JButton abortButton;
    private JButton fileButton;
    private JButton importFromClipboard;
    private JLabel layoutNameLabel;
    private JLabel layoutConfiguredWindowCountLabel;

    private Layout importedLayout;
    private int result;

    public ImportDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(importButton);

        importButton.addActionListener(e -> onOK());

        abortButton.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        importFromClipboard.addActionListener(actionEvent -> {
            try {
                String lzEncodedContent = (String) Toolkit
                        .getDefaultToolkit()
                        .getSystemClipboard()
                        .getData(DataFlavor.stringFlavor);

                importLayout(lzEncodedContent);
            } catch (Exception e) {
                // TODO: Show
            }
        });

        fileButton.addActionListener(actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select layout file"); // TODO: Resources
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Window layouts", ExportPage.FILE_ENDING); // TODO: Window layouts is also a constant, which must be shared...
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION &&
                    fileChooser.getSelectedFile().exists()) {

                try {
                    String lzEncodedContent = new String(Files.readAllBytes(Paths.get(fileChooser.getSelectedFile().getPath())), StandardCharsets.UTF_8);
                    importLayout(lzEncodedContent);
                } catch (IOException e) {
                    // TODO: handle
                    e.printStackTrace();
                }
            }
        });
    }

    public int showDialogInCenterOf(JDialog parent) {
        this.setSize(this.getPreferredSize());
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
        return result;
    }

    private void onOK() {
        this.result = OK_RESULT;
        dispose();
    }

    private void onCancel() {
        this.result = ABORT_RESULT;
        dispose();
    }

    private void importLayout(String lzEncodedContent) {
        String jsonContent = LZSEncoding.decompressFromBase64(lzEncodedContent);
        Gson gson = new GsonBuilder().create();
        Layout selectedLayout = gson.fromJson(jsonContent, Layout.class);

        selectLayout(selectedLayout);

        // TODO: Show notification
    }

    private void selectLayout(Layout layout) {
        this.importedLayout = layout;

        // TODO: Extract to method
        this.layoutNameLabel.setText(this.importedLayout.getName());
        this.layoutConfiguredWindowCountLabel.setText(Integer.toString(this.importedLayout.getToolWindows().length));

        this.importButton.setEnabled(true);
    }

    public Layout getImportedLayout() {
        return this.importedLayout;
    }
}
