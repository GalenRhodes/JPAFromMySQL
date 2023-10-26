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
import com.projectgalen.app.jpafrommysql.dbinfo.Column;
import com.projectgalen.app.jpafrommysql.dbinfo.ForeignKey;
import com.projectgalen.app.jpafrommysql.dbinfo.Index;
import com.projectgalen.app.jpafrommysql.dbinfo.Table;
import com.projectgalen.app.jpafrommysql.settings.ServerInfo;
import com.projectgalen.app.jpafrommysql.settings.Settings;
import com.projectgalen.lib.ui.Fonts;
import com.projectgalen.lib.ui.UI;
import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.json.JsonTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

import static com.projectgalen.app.jpafrommysql.Utils.abbreviatePath;
import static java.util.Optional.ofNullable;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
public class JPAFromMySQL extends JFrame {

    public static final String                 SETTINGS_CMDLINE_FLAG = "--settings";
    public static final PGProperties           props                 = PGProperties.getXMLProperties("settings.xml", JPAFromMySQL.class);
    public static final PGProperties           sql                   = PGProperties.getXMLProperties("sql.xml", JPAFromMySQL.class);
    public static final PGResourceBundle       msgs                  = PGResourceBundle.getPGBundle("com.projectgalen.app.jpafrommysql.messages");
    public static final DefaultMutableTreeNode DEFAULT_TOP_NODE      = new DefaultMutableTreeNode("<-N/A->", false);

    private static JPAFromMySQL app;

    protected          JPanel                 contentPane;
    protected          JButton                buttonGenerate;
    protected          JButton                saveButton;
    protected          JButton                savePathLookupButton;
    protected          JButton                outputPathLookupButton;
    protected          JButton                reloadInfoButton;
    protected          JCheckBox              splitEntitiesIntoBaseCheckBox;
    protected          JCheckBox              saveValuesForLaterCheckBox;
    protected final    JMenuBar               menuBar;
    protected          JPasswordField         mySqlPasswordField;
    protected          JTabbedPane            tabbedPane;
    protected          JTextField             mySqlHostnameField;
    protected          JTextField             mySqlPortField;
    protected          JTextField             mySqlSchemaField;
    protected          JTextField             mySqlUsernameField;
    protected          JTextField             projectField;
    protected          JTextField             authorField;
    protected          JTextField             organizationField;
    protected          JTextField             basePackageField;
    protected          JTextField             baseClassPrefixField;
    protected          JTextField             baseClassSuffixField;
    protected          JTextField             subClassPackageField;
    protected          JTextField             subClassPrefixField;
    protected          JTextField             subClassSuffixField;
    protected          JTextField             fkPrefixField;
    protected          JTextField             fkSuffixField;
    protected          JTextField             fkToManyFieldPatternField;
    protected          JTextField             fkToOneFieldPatternField;
    protected          JTextField             savePathField;
    protected          JTextField             outputPathField;
    protected          JTree                  tablesTree;
    protected @NotNull Map<String, Table>     tableList  = Collections.emptyMap();
    protected          DefaultMutableTreeNode topNode    = DEFAULT_TOP_NODE;
    protected          boolean                hasChanges = false;

    protected Settings settings = new Settings(false);

    public JPAFromMySQL() { this(new String[0]); }

    public JPAFromMySQL(String @NotNull ... args) {
        super(msgs.getString("title.main"));
        app = this;
        setContentPane(contentPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        processCmdLine(args);

        buttonGenerate.addActionListener(e -> generateJPA());
        saveButton.addActionListener(e -> saveSettings());
        reloadInfoButton.addActionListener(e -> reloadDatabaseInfo(false, true));
        savePathLookupButton.addActionListener(e -> savePathField.setText(Utils.getFromFileDialog(this, msgs.getString("title.save_path_file_dialog"), savePathField.getText(), true)));
        outputPathLookupButton.addActionListener(e -> outputPathField.setText(Utils.getFromFileDialog(this, msgs.getString("title.output_path_file_dialog"), outputPathField.getText(), false)));
        tablesTree.setModel(new DefaultTreeModel(topNode));
        tablesTree.setCellRenderer(new MyTreeCellRenderer());
        mySqlPortField.addFocusListener(new PortFocusListener());

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

        JMenu     fileMenu         = new JMenu("File");
        JMenuItem openFileMenuItem = new JMenuItem("Open...", UI.getIcon("icons/folder.png", JPAFromMySQL.class));
        JMenuItem saveFileMenuItem = new JMenuItem("Save", UI.getIcon("icons/disk.png", JPAFromMySQL.class));

        openFileMenuItem.addActionListener(e -> openSettings());
        saveFileMenuItem.addActionListener(e -> saveSettings());
        fileMenu.add(openFileMenuItem);
        fileMenu.add(saveFileMenuItem);

        setJMenuBar(menuBar = new JMenuBar());
        menuBar.add(fileMenu);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        SwingUtilities.invokeLater(() -> {
            Fonts.adjustFontSizes(this, -2.0f);
            pack();
            setResizable(false);
            setLocationRelativeTo(null);
        });
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

    private void generateJPA() {
        try {
            copyFieldsToSettings();
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
        loadSettings(Utils.getFromFileDialog(this, "Open...", null, true, true));
    }

    private void populateTree() {
        ServerInfo si         = settings.getServerInfo();
        String     schemaName = si.getSchemaName();

        topNode = new DefaultMutableTreeNode("%s@%s:%d/%s".formatted(si.getUsername(), si.getHostName(), si.getPortNumber(), schemaName), true);
        DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode(schemaName, true);
        topNode.add(schemaNode);

        tableList.values().forEach(table -> {
            DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table, true);
            schemaNode.add(tableNode);

            DefaultMutableTreeNode columnListNode = new DefaultMutableTreeNode("Columns", true);
            DefaultMutableTreeNode indexListNode  = new DefaultMutableTreeNode("Indexes", true);
            DefaultMutableTreeNode fkListNode     = new DefaultMutableTreeNode("Foreign Keys", true);

            tableNode.add(columnListNode);
            tableNode.add(indexListNode);
            tableNode.add(fkListNode);

            table.getColumns().forEach(c -> columnListNode.add(new DefaultMutableTreeNode(c, false)));
            table.getIndexes().forEach(index -> indexListNode.add(new DefaultMutableTreeNode(index, false)));
            table.getReferencedForeignKeys().forEach(fk -> fkListNode.add(new DefaultMutableTreeNode(fk, false)));
        });

        tablesTree.setModel(new DefaultTreeModel(topNode, true));
        tablesTree.setCellRenderer(new MyTreeCellRenderer());
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
            Map<String, Table> list = si.getFromPrepStmt(conn, sql.getProperty("fetch.tables"), suppressErrorDialog, stmt -> {
                Map<String, Table> tables = new TreeMap<>();
                stmt.setString(1, schemaName);
                si.forEachRow(stmt, rs -> {
                    Table table = new Table(rs);
                    tables.put(table.getTableName(), table);
                });
                return tables;
            });

            if((list == null) || list.isEmpty()) return null;

            si.doWithPrepStmt(conn, sql.getProperty("fetch.columns"), suppressErrorDialog, stmt -> {
                stmt.setString(1, schemaName);
                for(Table table : list.values()) {
                    stmt.setString(2, table.getTableName());
                    table.addColumns(stmt.executeQuery());
                }
            });

            for(Table table : list.values()) {
                si.doWithPrepStmt(conn, sql.format("fetch.indexes", schemaName, table.getTableName()), suppressErrorDialog, stmt -> si.forEachRow(stmt, table::addIndexes));
            }

            si.doWithPrepStmt(conn, sql.getProperty("fetch.foreign_keys"), suppressErrorDialog, stmt -> {
                stmt.setString(1, schemaName);
                si.forEachRow(stmt, rs -> new ForeignKey(list, rs));
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

    private static final class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        public static final Icon rootIcon     = UI.getIcon("icons/computer.png", JPAFromMySQL.class);
        public static final Icon schemaIcon   = UI.getIcon("icons/database.png", JPAFromMySQL.class);
        public static final Icon tableIcon    = UI.getIcon("icons/database_table.png", JPAFromMySQL.class);
        public static final Icon columnIcon   = UI.getIcon("icons/table_gear.png", JPAFromMySQL.class);
        public static final Icon pkColumnIcon = UI.getIcon("icons/table_key.png", JPAFromMySQL.class);
        public static final Icon folderIcon   = UI.getIcon("icons/folder.png", JPAFromMySQL.class);
        public static final Icon indexIcon    = UI.getIcon("icons/table_lightning.png", JPAFromMySQL.class);
        public static final Icon fkIcon       = UI.getIcon("icons/table_relationship.png", JPAFromMySQL.class);
        public static final Icon unknownIcon  = UI.getIcon("icons/cog.png", JPAFromMySQL.class);

        public MyTreeCellRenderer() { }

        public @Override Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component renderer = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if(renderer instanceof JLabel label && value instanceof DefaultMutableTreeNode node) {
                Object o = node.getUserObject();

                if(o instanceof String str) {
                    if(node.isRoot()) label.setIcon(rootIcon);
                    else if(node.getLevel() == 1) label.setIcon(schemaIcon);
                    else label.setIcon(switch(str) {
                            case "Columns", "Indexes", "Foreign Keys" -> folderIcon;
                            default -> unknownIcon;
                        });
                }
                else if(o instanceof Table) label.setIcon(tableIcon);
                else if(o instanceof Column column) label.setIcon(column.isPrimaryKey() ? pkColumnIcon : columnIcon);
                else if(o instanceof Index) setIcon(indexIcon);
                else if(o instanceof ForeignKey) setIcon(fkIcon);
                else setIcon(unknownIcon);
            }
            return renderer;
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