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
import com.projectgalen.app.jpafrommysql.components.OmittedForm;
import com.projectgalen.app.jpafrommysql.dbinfo.DBForeignKey;
import com.projectgalen.app.jpafrommysql.dbinfo.DBTable;
import com.projectgalen.app.jpafrommysql.settings.GenerationInfo;
import com.projectgalen.app.jpafrommysql.settings.ServerInfo;
import com.projectgalen.app.jpafrommysql.settings.Settings;
import com.projectgalen.app.jpafrommysql.tree.*;
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
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

import static com.projectgalen.app.jpafrommysql.Utils.abbreviatePath;
import static com.projectgalen.app.jpafrommysql.Utils.getFromFileDialog;
import static java.util.Optional.ofNullable;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
public class JPAFromMySQL extends JFrame {

    public static final String           SETTINGS_CMDLINE_FLAG = "--settings";
    public static final PGProperties     props                 = PGProperties.getXMLProperties("settings.xml", JPAFromMySQL.class);
    public static final PGProperties     sql                   = PGProperties.getXMLProperties("sql.xml", JPAFromMySQL.class);
    public static final PGResourceBundle msgs                  = PGResourceBundle.getPGBundle("com.projectgalen.app.jpafrommysql.messages");
    public static final StringTreeNode   DEFAULT_TOP_NODE      = new StringTreeNode("<-N/A->", DatabaseTreeNode.unknownIcon, false);

    private static JPAFromMySQL app;

    protected          JPanel               contentPane;
    protected          JButton              buttonGenerate;
    protected          JButton              saveButton;
    protected          JButton              savePathLookupButton;
    protected          JButton              outputPathLookupButton;
    protected          JButton              reloadInfoButton;
    protected          JCheckBox            splitEntitiesIntoBaseCheckBox;
    protected          JCheckBox            saveValuesForLaterCheckBox;
    protected final    JMenuBar             menuBar;
    protected          JPasswordField       mySqlPasswordField;
    protected          JTextField           mySqlHostnameField;
    protected          JTextField           mySqlPortField;
    protected          JTextField           mySqlSchemaField;
    protected          JTextField           mySqlUsernameField;
    protected          JTextField           projectField;
    protected          JTextField           authorField;
    protected          JTextField           organizationField;
    protected          JTextField           basePackageField;
    protected          JTextField           baseClassPrefixField;
    protected          JTextField           baseClassSuffixField;
    protected          JTextField           subClassPackageField;
    protected          JTextField           subClassPrefixField;
    protected          JTextField           subClassSuffixField;
    protected          JTextField           fkPrefixField;
    protected          JTextField           fkSuffixField;
    protected          JTextField           fkToManyFieldPatternField;
    protected          JTextField           fkToOneFieldPatternField;
    protected          JTextField           savePathField;
    protected          JTextField           outputPathField;
    protected          JTree                tablesTree;
    protected          JButton              openButton;
    protected          JTabbedPane          tabbedPane;
    protected          JButton              newFileButton;
    protected          JLabel               subClassPackageLabel;
    protected          JLabel               subClassPrefixLabel;
    protected          JLabel               subClassSuffixLabel;
    protected          JLabel               basePackageLabel;
    protected          JLabel               baseClassPrefixLabel;
    protected          JLabel               baseClassSuffixLabel;
    protected          OmittedForm          omittedForm;
    protected          JPopupMenu           popupMenu;
    protected @NotNull Map<String, DBTable> tableList  = Collections.emptyMap();
    protected          StringTreeNode       topNode    = DEFAULT_TOP_NODE;
    protected          boolean              hasChanges = false;

    protected Settings settings = new Settings(false);

    public JPAFromMySQL() { this(new String[0]); }

    public JPAFromMySQL(String @NotNull ... args) {
        super(msgs.getString("title.main"));
        app = this;
        setContentPane(contentPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        processCmdLine(args);

        buttonGenerate.addActionListener(e -> generateJPA());
        openButton.addActionListener(e -> openSettings());
        saveButton.addActionListener(e -> saveSettings());
        newFileButton.addActionListener(e -> createNewSettings());
        reloadInfoButton.addActionListener(e -> reloadDatabaseInfo(false, true));
        savePathLookupButton.addActionListener(e -> savePathField.setText(getFromFileDialog(this, msgs.getString("title.save_path_file_dialog"), savePathField.getText(), true)));
        outputPathLookupButton.addActionListener(e -> outputPathField.setText(getFromFileDialog(this, msgs.getString("title.output_path_file_dialog"), outputPathField.getText(), false)));
        tablesTree.setModel(new DefaultTreeModel(topNode));
        tablesTree.setCellRenderer(new DatabaseTreeCellRenderer());
        mySqlPortField.addFocusListener(new PortFocusListener());

        addChangeListeners();

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

        tablesTree.addMouseListener(new MouseAdapter() {
            public @Override void mousePressed(@NotNull MouseEvent e) { popupHandler(e); }

            public @Override void mouseReleased(@NotNull MouseEvent e) { popupHandler(e); }

            private void popupHandler(@NotNull MouseEvent e) {
                if(e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        Fonts.adjustFontSizes(this, 2.0f);
        pack();
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
        setMinimumSize(getSize());
    }

    public void updateGeneratedNamesBasedOnSettings() {
        GenerationInfo genInfo = settings.getGenerationInfo();

        tableList.values().forEach(table -> {
            table.setOmitted(genInfo.getOmitted().stream().anyMatch(o -> o.has(table)));
        });
    }

    private void addChangeListeners() {
        mySqlHostnameField.addFocusListener(new TextFieldFocusListener(() -> settings.getServerInfo().getHostName()));
        mySqlPortField.addFocusListener(new TextFieldFocusListener(() -> String.valueOf(settings.getServerInfo().getPortNumber())));
        mySqlSchemaField.addFocusListener(new TextFieldFocusListener(() -> settings.getServerInfo().getSchemaName()));
        mySqlUsernameField.addFocusListener(new TextFieldFocusListener(() -> settings.getServerInfo().getUsername()));
        mySqlPasswordField.addFocusListener(new TextFieldFocusListener(() -> settings.getServerInfo().getPassword()));
        projectField.addFocusListener(new TextFieldFocusListener(() -> settings.getProjectInfo().getProjectName()));
        authorField.addFocusListener(new TextFieldFocusListener(() -> settings.getProjectInfo().getAuthor()));
        organizationField.addFocusListener(new TextFieldFocusListener(() -> settings.getProjectInfo().getOrganization()));
        basePackageField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getBasePackage()));
        baseClassPrefixField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getBaseClassPrefix()));
        baseClassSuffixField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getBaseClassSuffix()));
        subClassPackageField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getSubclassPackage()));
        subClassPrefixField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getSubclassPrefix()));
        subClassSuffixField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getSubclassSuffix()));
        fkPrefixField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getFkPrefix()));
        fkSuffixField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getFkSuffix()));
        fkToManyFieldPatternField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getFkToManyPattern()));
        fkToOneFieldPatternField.addFocusListener(new TextFieldFocusListener(() -> settings.getGenerationInfo().getFkToOnePattern()));

        outputPathField.addFocusListener(new TextFieldFocusListener(() -> abbreviatePath(settings.getGenerationInfo().getOutputPath())));
        savePathField.addFocusListener(new TextFieldFocusListener(() -> abbreviatePath(settings.getSettingsSavePath())));

        splitEntitiesIntoBaseCheckBox.addFocusListener(new CheckBoxFocusListener(() -> settings.getGenerationInfo().isSplitEntities()));
        saveValuesForLaterCheckBox.addFocusListener(new CheckBoxFocusListener(() -> settings.isSaveSettings()));
    }

    private void clearHasChanges() {
        hasChanges = false;
        saveButton.setEnabled(false);
    }

    private void copyFieldsToSettings() {
        settings.getServerInfo().setPortNumber(Integer.parseInt(mySqlPortField.getText()));

        char[] pw = mySqlPasswordField.getPassword();
        settings.getServerInfo().setPassword(pw != null ? String.valueOf(pw) : "");
        Utils.clearPassword(pw);

        settings.getServerInfo().setHostName(mySqlHostnameField.getText());
        settings.getServerInfo().setSchemaName(mySqlSchemaField.getText());
        settings.getServerInfo().setUsername(mySqlUsernameField.getText());

        settings.getProjectInfo().setProjectName(projectField.getText());
        settings.getProjectInfo().setAuthor(authorField.getText());
        settings.getProjectInfo().setOrganization(organizationField.getText());

        settings.getGenerationInfo().setOutputPath(Utils.unabbreviatePath(outputPathField.getText()));
        settings.getGenerationInfo().setSplitEntities(splitEntitiesIntoBaseCheckBox.isSelected());
        settings.getGenerationInfo().setBasePackage(basePackageField.getText());
        settings.getGenerationInfo().setBaseClassPrefix(baseClassPrefixField.getText());
        settings.getGenerationInfo().setBaseClassSuffix(baseClassSuffixField.getText());
        settings.getGenerationInfo().setSubclassPackage(subClassPackageField.getText());
        settings.getGenerationInfo().setSubclassPrefix(subClassPrefixField.getText());
        settings.getGenerationInfo().setSubclassSuffix(subClassSuffixField.getText());
        settings.getGenerationInfo().setFkPrefix(fkPrefixField.getText());
        settings.getGenerationInfo().setFkSuffix(fkSuffixField.getText());
        settings.getGenerationInfo().setFkToOnePattern(fkToOneFieldPatternField.getText());
        settings.getGenerationInfo().setFkToManyPattern(fkToManyFieldPatternField.getText());

        settings.setSaveSettings(saveValuesForLaterCheckBox.isSelected());
        settings.setSettingsSavePath(Utils.unabbreviatePath(savePathField.getText()));

        if(saveValuesForLaterCheckBox.isSelected()) saveSettings();
    }

    private void copySettingsToFields(@NotNull String filename) {
        mySqlHostnameField.setText(settings.getServerInfo().getHostName());
        mySqlPortField.setText(String.valueOf(settings.getServerInfo().getPortNumber()));
        mySqlSchemaField.setText(settings.getServerInfo().getSchemaName());
        mySqlUsernameField.setText(settings.getServerInfo().getUsername());
        mySqlPasswordField.setText(settings.getServerInfo().getPassword());

        projectField.setText(settings.getProjectInfo().getProjectName());
        authorField.setText(settings.getProjectInfo().getAuthor());
        organizationField.setText(settings.getProjectInfo().getOrganization());

        outputPathField.setText(abbreviatePath(settings.getGenerationInfo().getOutputPath()));
        splitEntitiesIntoBaseCheckBox.setSelected(settings.getGenerationInfo().isSplitEntities());
        basePackageField.setText(settings.getGenerationInfo().getBasePackage());
        baseClassPrefixField.setText(settings.getGenerationInfo().getBaseClassPrefix());
        baseClassSuffixField.setText(settings.getGenerationInfo().getBaseClassSuffix());
        subClassPackageField.setText(settings.getGenerationInfo().getSubclassPackage());
        subClassPrefixField.setText(settings.getGenerationInfo().getSubclassPrefix());
        subClassSuffixField.setText(settings.getGenerationInfo().getSubclassSuffix());
        fkPrefixField.setText(settings.getGenerationInfo().getFkPrefix());
        fkSuffixField.setText(settings.getGenerationInfo().getFkSuffix());
        fkToOneFieldPatternField.setText(settings.getGenerationInfo().getFkToOnePattern());
        fkToManyFieldPatternField.setText(settings.getGenerationInfo().getFkToManyPattern());

        saveValuesForLaterCheckBox.setSelected(settings.isSaveSettings());
        savePathField.setText(abbreviatePath(filename));
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
            copyFieldsToSettings();
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
        ServerInfo si         = settings.getServerInfo();
        String     schemaName = si.getSchemaName();

        topNode = new StringTreeNode("%s@%s:%d/%s".formatted(si.getUsername(), si.getHostName(), si.getPortNumber(), schemaName), DatabaseTreeNode.rootIcon);
        StringTreeNode schemaNode = new StringTreeNode(schemaName, DatabaseTreeNode.schemaIcon);
        topNode.add(schemaNode);

        tableList.values().forEach(table -> {
            TableTreeNode tableNode = new TableTreeNode(table);
            schemaNode.add(tableNode);

            StringTreeNode columnListNode = new StringTreeNode("Columns", DatabaseTreeNode.folderIcon);
            StringTreeNode indexListNode  = new StringTreeNode("Indexes", DatabaseTreeNode.folderIcon);
            StringTreeNode fkListNode     = new StringTreeNode("Foreign Keys", DatabaseTreeNode.folderIcon);

            tableNode.add(columnListNode);
            tableNode.add(indexListNode);
            tableNode.add(fkListNode);

            table.getColumns().forEach(c -> columnListNode.add(new ColumnTreeNode(c)));
            table.getIndexes().forEach(index -> indexListNode.add(new IndexTreeNode(index)));
            table.getReferencedForeignKeys().forEach(fk -> fkListNode.add(new ForeignKeyTreeNode(fk)));
        });

        tablesTree.setModel(new DatabaseTreeModel(topNode));
        tablesTree.setCellRenderer(new DatabaseTreeCellRenderer());
        revalidate();
        pack();
        SwingUtilities.invokeLater(() -> tablesTree.expandRow(1));
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
        if(copyFieldsToSettings) copyFieldsToSettings();
        ServerInfo si         = settings.getServerInfo();
        String     schemaName = si.getSchemaName();

        tableList = Objects.requireNonNullElseGet(si.getFromDatabase(suppressErrorDialog, conn -> {
            Map<String, DBTable> list = si.getFromPrepStmt(conn, sql.getProperty("fetch.tables"), suppressErrorDialog, stmt -> {
                Map<String, DBTable> tables = new TreeMap<>();
                stmt.setString(1, schemaName);
                si.forEachRow(stmt, rs -> {
                    DBTable table = new DBTable(rs);
                    tables.put(table.getTableName(), table);
                });
                return tables;
            });

            if((list == null) || list.isEmpty()) return null;

            si.doWithPrepStmt(conn, sql.getProperty("fetch.columns"), suppressErrorDialog, stmt -> {
                stmt.setString(1, schemaName);
                for(DBTable table : list.values()) {
                    stmt.setString(2, table.getTableName());
                    table.addColumns(stmt.executeQuery());
                }
            });

            for(DBTable table : list.values()) {
                si.doWithPrepStmt(conn, sql.format("fetch.indexes", schemaName, table.getTableName()), suppressErrorDialog, stmt -> si.forEachRow(stmt, table::addIndexes));
            }

            si.doWithPrepStmt(conn, sql.getProperty("fetch.foreign_keys"), suppressErrorDialog, stmt -> {
                stmt.setString(1, schemaName);
                si.forEachRow(stmt, rs -> new DBForeignKey(list, rs));
            });

            return list;
        }), Collections::emptyMap);

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
        this.settings = settings;
        if(this.settings != null) {
            copySettingsToFields(Objects.requireNonNullElse(filename, "Config.json"));
            reloadDatabaseInfo(suppressErrorDialog, false);
        }
        else {
            this.settings = new Settings(false);
            copySettingsToFields(Objects.requireNonNullElse(filename, "Config.json"));
            tablesTree.setModel(new DefaultTreeModel(topNode = DEFAULT_TOP_NODE, false));
            revalidate();
            pack();
        }
    }

    public static JPAFromMySQL app() {
        return app;
    }

    public static Settings getSettings() {
        return app.settings;
    }

    public static void main(String[] args) {
        loadMySQLDriver();
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

    private final class CheckBoxFocusListener extends FocusAdapter {
        private final Supplier<Boolean> supplier;

        public CheckBoxFocusListener(@NotNull Supplier<Boolean> supplier) {
            super();
            this.supplier = supplier;
        }

        public @Override void focusLost(@NotNull FocusEvent e) {
            if((e.getSource() instanceof JCheckBox checkBox) && (checkBox.isSelected() != ofNullable(supplier.get()).orElse(false))) setHasChanges();
        }
    }

    private final class TextFieldFocusListener extends FocusAdapter {
        private final Supplier<String> supplier;

        public TextFieldFocusListener(@NotNull Supplier<String> supplier) {
            super();
            this.supplier = supplier;
        }

        public @Override void focusLost(@NotNull FocusEvent e) {
            if(e.getSource() instanceof JPasswordField field) {
                if(!String.valueOf(field.getPassword()).equals(supplier.get())) setHasChanges();
            }
            else if(e.getSource() instanceof JTextField field) {
                if(!field.getText().equals(supplier.get())) setHasChanges();
            }
        }
    }

    private static final class PortFocusListener extends FocusAdapter {
        public PortFocusListener() { }

        public @Override void focusLost(@NotNull FocusEvent e) {
            if(e.getComponent() instanceof JTextField field) {
                try {
                    String input = field.getText();
                    if(input.isEmpty()) {
                        field.setText(String.valueOf(ServerInfo.DEFAULT_MYSQL_PORT));
                    }
                    else {
                        int p = Integer.parseInt(input);
                        if(p <= 0 || p > 0xffff) field.setText(String.valueOf(ServerInfo.DEFAULT_MYSQL_PORT));
                    }
                }
                catch(Exception ex) {
                    field.setText(String.valueOf(ServerInfo.DEFAULT_MYSQL_PORT));
                }
            }
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
