package com.projectgalen.app;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectgalen.app.settings.Settings;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.InputStream;

public class JPAFromMySQL extends JDialog {

    public static final String       SETTINGS_CMDLINE_FLAG = "--settings";
    private static      JPAFromMySQL app;

    protected JPanel         contentPane;
    protected JButton        buttonGenerate;
    protected JButton        buttonQuit;
    protected JTextField     mySqlHostnameField;
    protected JTextField     mySqlPortFile;
    protected JTextField     mySqlSchemaField;
    protected JTextField     mySqlUsernameField;
    protected JPasswordField mySqlPasswordField;
    protected JTextField     projectField;
    protected JTextField     authorField;
    protected JTextField     organizationField;
    protected JTextField     basePackageField;
    protected JCheckBox      splitEntitiesIntoBaseCheckBox;
    protected JTextField     baseClassPrefixField;
    protected JTextField     baseClassSuffixField;
    protected JTextField     subClassPackageField;
    protected JTextField     subClassPrefixField;
    protected JTextField     subClassSuffixField;
    protected JTextField     fkPrefixField;
    protected JTextField     fkSuffixField;
    protected JTextField     kfToManyFieldPatternField;
    protected JTextField     fkToOneFieldPatternField;
    protected JCheckBox      saveValuesForLaterCheckBox;
    protected JTree          tablesTree;
    protected JTabbedPane    tabbedPane;
    protected JTextField     savePathField;
    protected JButton        savePathLookupButton;
    protected JTextField     outputPathField;
    protected JButton        outputPathLookupButton;
    protected int            exitCode = 0;

    protected Settings settings = null;

    public JPAFromMySQL() {
        setTitle("JPA From MySQL");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonGenerate);
        buttonGenerate.addActionListener(e -> generateJPA());
        buttonQuit.addActionListener(e -> quit());
    }

    private void generateJPA() {
        try {

        }
        catch(Exception e) {
            exitCode = 1;
            dispose();
        }
    }

    private void loadSettings(String filename) {
        try(InputStream inputStream = new FileInputStream(filename)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            settings = mapper.readValue(inputStream, Settings.class);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to load settings from \"%s\": %s".formatted(filename, e.getMessage()), "Settings Load Error", JOptionPane.WARNING_MESSAGE);
            settings = null;
        }
    }

    private void quit() {
        exitCode = 0;
        dispose();
    }

    public static JPAFromMySQL app() {
        return app;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            app = new JPAFromMySQL();

            for(int i = 0; i < args.length; i++) {
                int j = (i + 1);
                if(SETTINGS_CMDLINE_FLAG.equals(args[i])) {
                    if(j < args.length) app.loadSettings(args[j]);
                    else throw new IllegalArgumentException("Expected filename after \"%s\" but didn't get one.".formatted(SETTINGS_CMDLINE_FLAG));
                }
            }

            if(app.settings == null) {
                app.settings = new Settings(false);
            }

            app.pack();
            app.setResizable(false);
            app.setLocationRelativeTo(null);
            app.setVisible(true);
            System.exit(app.exitCode);
        });
    }
}
