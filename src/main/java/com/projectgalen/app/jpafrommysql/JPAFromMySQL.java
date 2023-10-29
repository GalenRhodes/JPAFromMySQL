package com.projectgalen.app.jpafrommysql;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: JPAFromMySQL.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 25, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ================================================================================================================================

import com.formdev.flatlaf.util.SystemInfo;
import com.projectgalen.app.jpafrommysql.components.DatabaseTreeForm;
import com.projectgalen.app.jpafrommysql.components.LeftHandColumn;
import com.projectgalen.app.jpafrommysql.components.OmittedForm;
import com.projectgalen.app.jpafrommysql.dbinfo.DBServer;
import com.projectgalen.app.jpafrommysql.settings.GenerationInfo;
import com.projectgalen.app.jpafrommysql.settings.Settings;
import com.projectgalen.app.jpafrommysql.settings.app.AppSettings;
import com.projectgalen.lib.ui.Fonts;
import com.projectgalen.lib.ui.UI;
import com.projectgalen.lib.ui.menus.PGJMenu;
import com.projectgalen.lib.ui.menus.PGJMenuBar;
import com.projectgalen.lib.ui.menus.PGJMenuItem;
import com.projectgalen.lib.ui.menus.PGJPopupMenu;
import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.json.JsonTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Comparator;
import java.util.Objects;

import static com.projectgalen.app.jpafrommysql.Utils.getFromFileDialog;
import static java.util.Optional.ofNullable;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
public class JPAFromMySQL extends JFrame {

    public static final PGResourceBundle msgs                    = PGResourceBundle.getPGBundle("com.projectgalen.app.jpafrommysql.messages");
    public static final PGProperties     props                   = PGProperties.getXMLProperties("settings.xml", JPAFromMySQL.class);
    public static final PGProperties     sql                     = PGProperties.getXMLProperties("sql.xml", JPAFromMySQL.class);
    public static final String           DEFAULT_CONFIG_FILENAME = props.getProperty("default.config.filename").replace("/", File.separator);
    public static final String           SETTINGS_CMDLINE_FLAG   = props.getProperty("cmdline.flag.settings");

    private static JPAFromMySQL app;

    protected       JPanel           contentPane;
    protected       JButton          buttonGenerate;
    protected       JButton          saveButton;
    protected       JButton          openButton;
    protected       JButton          newFileButton;
    protected       JTabbedPane      tabbedPane;
    protected       OmittedForm      omittedForm;
    protected       DatabaseTreeForm treeList;
    protected       LeftHandColumn   leftHandComponent;
    protected final JMenuBar         menuBar;
    protected final JPopupMenu       popupMenu;
    protected       DBServer         dbServer   = null;
    protected       boolean          hasChanges = false;

    protected       Settings    settings = new Settings(false);
    protected final AppSettings appSettings;

    public JPAFromMySQL() { this(new String[0]); }

    public JPAFromMySQL(String @NotNull ... args) {
        super(msgs.getString("title.main"));
        app = this;
        setContentPane(contentPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        appSettings = AppSettings.load();

        processCmdLine(args);

        buttonGenerate.addActionListener(e -> generateJPA());
        openButton.addActionListener(e -> openSettings());
        saveButton.addActionListener(e -> saveSettings());
        newFileButton.addActionListener(e -> createNewSettings());
        treeList.addActionListener(e -> reloadDatabaseInfo(false, true));
        treeList.addMouseListener(new MouseAdapter() {
            public @Override void mousePressed(@NotNull MouseEvent e) { popupHandler(e); }

            public @Override void mouseReleased(@NotNull MouseEvent e) { popupHandler(e); }

            private void popupHandler(@NotNull MouseEvent e) {
                if(e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        leftHandComponent.addChangeListeners(settings, this::setHasChanges);

        setJMenuBar(menuBar = new PGJMenuBar(new PGJMenu("File",
                                                         new PGJMenuItem("New...", UI.getIcon("icons/folder_add.png", JPAFromMySQL.class), e -> createNewSettings()),
                                                         new PGJMenuItem("Open...", UI.getIcon("icons/folder.png", JPAFromMySQL.class), e -> openSettings()),
                                                         new PGJMenuItem("Save", UI.getIcon("icons/disk.png", JPAFromMySQL.class), e -> saveSettings()),
                                                         new PGJMenuItem("Generate...", UI.getIcon("", JPAFromMySQL.class), e -> generateJPA()),
                                                         new PGJMenuItem("Quit", UI.getIcon("", JPAFromMySQL.class), e -> exitApplication()))));

        popupMenu = new PGJPopupMenu(new PGJMenuItem("Rename...", e -> { }),
                                     new PGJMenuItem("Omit", e -> { }),
                                     new PGJMenuItem("Convert...", e -> { }),
                                     new PGJMenuItem("Assign Default Value...", e -> { }));

        Fonts.adjustFontSizes(this, 2.0f);
        pack();
        setResizable(true);
        setLocationRelativeTo(null);

        Rectangle windowBounds = getBounds();

        setMinimumSize(windowBounds.getSize());

        if((appSettings.getWindowHeight() == 0) || (appSettings.getWindowWidth() == 0)) {
            appSettings.setWindowBounds(windowBounds);
            appSettings.save();
        }
        else {
            setLocation(appSettings.getWindowLocation());
            setSize(Math.max(windowBounds.width, appSettings.getWindowWidth()), Math.max(windowBounds.height, appSettings.getWindowHeight()));
            appSettings.setWindowBounds(getBounds());
            appSettings.save();
        }

        setVisible(true);
        addComponentListener(new ComponentAdapter() {
            public @Override void componentMoved(@NotNull ComponentEvent e) {
                appSettings.setWindowBounds(getBounds());
                appSettings.save();
            }

            public @Override void componentResized(@NotNull ComponentEvent e) {
                appSettings.setWindowBounds(getBounds());
                appSettings.save();
            }
        });
    }

    public void updateGeneratedNamesBasedOnSettings() {
        GenerationInfo genInfo = settings.getGenerationInfo();
        dbServer.getSchemas().values().forEach(schema -> schema.getTables().values().forEach(table -> table.setOmitted(genInfo.getOmitted().stream().anyMatch(o -> o.has(table)))));
    }

    private void clearHasChanges() {
        hasChanges = false;
        saveButton.setEnabled(false);
    }

    private void createNewSettings() {
        // TODO: Create New Settings...
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void exitApplication() {
        setVisible(false);
        System.exit(0);
    }

    private void generateJPA() {
        try {
            leftHandComponent.copyFieldsToSettings(settings);
            // TODO: Generate JPA POJOs...
        }
        catch(Exception e) {
            System.exit(1);
        }
    }

    private void loadSettings(String filename) {
        try {
            setSettings(Utils.loadSettings(filename), filename, false);
            clearHasChanges();
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, msgs.format("msg.err.settings_load_failure", filename, e.getMessage()), msgs.getString("title.err.settings_load_failure"), JOptionPane.WARNING_MESSAGE);
            setSettings(null, null, true);
            clearHasChanges();
        }
    }

    private void openSettings() {
        ofNullable(getFromFileDialog(this, msgs.getString("menu.file.open"), null, true, true)).ifPresent(this::loadSettings);
    }

    private void populateTree() {
        treeList.setData(dbServer);
    }

    private void processCmdLine(String @NotNull [] args) {
        for(int i = 0; i < args.length; i++) {
            int j = (i + 1);
            if(SETTINGS_CMDLINE_FLAG.equals(args[i])) {
                if(j < args.length) loadSettings(args[j]);
                else throw new IllegalArgumentException(msgs.format("msg.err.missing_filename", SETTINGS_CMDLINE_FLAG));
            }
        }
    }

    private void reloadDatabaseInfo(boolean suppressErrorDialog, boolean copyFieldsToSettings) {
        try {
            if(copyFieldsToSettings) leftHandComponent.copyFieldsToSettings(settings);
            (dbServer = new DBServer(settings.getServerInfo())).loadSchema(settings.getServerInfo().getSchemaName());
        }
        catch(JPASQLException e) {
            if(suppressErrorDialog) e.printStackTrace(System.err);
            else JOptionPane.showMessageDialog(this, e, "Database Error", JOptionPane.ERROR_MESSAGE);
            dbServer = null;
        }
        populateTree();
    }

    private void saveSettings() {
        if(hasChanges) {
            String filename = settings.getSettingsSavePath();
            try {
                JsonTools.writeJsonFile(filename, settings);
                clearHasChanges();
            }
            catch(Exception e) {
                JOptionPane.showMessageDialog(this, msgs.format("msg.err.settings_save_failure", filename, e), msgs.getString("title.err.settings_save_failuer"), JOptionPane.ERROR_MESSAGE);
                setHasChanges();
            }
        }
    }

    private void setHasChanges() {
        saveButton.setEnabled(hasChanges = true);
    }

    private void setSettings(@Nullable Settings settings, @Nullable String filename, boolean suppressErrorDialog) {
        if(settings != null) {
            leftHandComponent.copySettingsToFields((this.settings = settings), Objects.requireNonNullElse(filename, DEFAULT_CONFIG_FILENAME));
            reloadDatabaseInfo(suppressErrorDialog, false);
        }
        else {
            leftHandComponent.copySettingsToFields((this.settings = new Settings(false)), Objects.requireNonNullElse(filename, DEFAULT_CONFIG_FILENAME));
        }
        leftHandComponent.addChangeListeners(this.settings, this::setHasChanges);
    }

    public static JPAFromMySQL app() {
        return app;
    }

    public static Settings getSettings() {
        return app.settings;
    }

    public static void main(String[] args) {
        loadMySQLDriver();
        System.getProperties().entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().toString())).forEach(e -> System.out.printf("%50s : \"%s\"\n", e.getKey(), e.getValue()));
        SwingUtilities.invokeLater(() -> new JPAFromMySQL(args));
    }

    private static void loadMySQLDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, msgs.format("msg.err.sql_driver_load_failed", e), msgs.getString("title.err.driver_error"), JOptionPane.WARNING_MESSAGE);
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    static {
        if(SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "");
            System.setProperty("sun.java2d.metal", "true");
            System.setProperty("sun.java2d.opengl", "false");
        }
        else if(SystemInfo.isWindows_10_orLater) {
            System.setProperty("sun.java2d.metal", "false");
            System.setProperty("sun.java2d.opengl", "false");
            System.setProperty("sun.java2d.noddraw", "false");
            System.setProperty("sun.java2d.ddforcevram", "false");
            System.setProperty("sun.java2d.ddblit", "false");
            System.setProperty("sun.java2d.d3d", "true");
        }
        else {
            System.setProperty("sun.java2d.metal", "false");
            System.setProperty("sun.java2d.opengl", "true");
        }
        try { UI.setFlatLaf(); } catch(UnsupportedLookAndFeelException e) { throw new RuntimeException(e); }
    }
}
