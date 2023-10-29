package com.projectgalen.app.jpafrommysql.components;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: LeftHandColumn.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 29, 2023
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

import com.projectgalen.app.jpafrommysql.Utils;
import com.projectgalen.app.jpafrommysql.listeners.CheckBoxFocusListener;
import com.projectgalen.app.jpafrommysql.listeners.FocusListenerRecord;
import com.projectgalen.app.jpafrommysql.listeners.PortFocusListener;
import com.projectgalen.app.jpafrommysql.listeners.TextFieldFocusListener;
import com.projectgalen.app.jpafrommysql.settings.Settings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import static com.projectgalen.app.jpafrommysql.JPAFromMySQL.app;
import static com.projectgalen.app.jpafrommysql.JPAFromMySQL.msgs;
import static com.projectgalen.app.jpafrommysql.Utils.abbreviatePath;
import static com.projectgalen.app.jpafrommysql.Utils.getFromFileDialog;

public class LeftHandColumn {

    protected JButton                   savePathLookupButton;
    protected JButton                   outputPathLookupButton;
    protected JCheckBox                 saveValuesForLaterCheckBox;
    protected JCheckBox                 splitEntitiesIntoBaseCheckBox;
    protected JLabel                    basePackageLabel;
    protected JLabel                    subClassPackageLabel;
    protected JLabel                    subClassPrefixLabel;
    protected JLabel                    subClassSuffixLabel;
    protected JLabel                    baseClassPrefixLabel;
    protected JLabel                    baseClassSuffixLabel;
    protected JPanel                    rootComponent;
    protected JPasswordField            mySqlPasswordField;
    protected JTextField                mySqlHostnameField;
    protected JTextField                mySqlPortField;
    protected JTextField                mySqlSchemaField;
    protected JTextField                mySqlUsernameField;
    protected JTextField                projectField;
    protected JTextField                authorField;
    protected JTextField                organizationField;
    protected JTextField                basePackageField;
    protected JTextField                baseClassPrefixField;
    protected JTextField                baseClassSuffixField;
    protected JTextField                subClassPackageField;
    protected JTextField                subClassPrefixField;
    protected JTextField                subClassSuffixField;
    protected JTextField                savePathField;
    protected JTextField                outputPathField;
    protected JTextField                fkPrefixField;
    protected JTextField                fkSuffixField;
    protected JTextField                fkToOneFieldPatternField;
    protected JTextField                fkToManyFieldPatternField;
    protected List<FocusListenerRecord> listeners = new ArrayList<>();

    public LeftHandColumn() {
        savePathLookupButton.addActionListener(e -> savePathField.setText(getFromFileDialog(app(), msgs.getString("title.save_path_file_dialog"), savePathField.getText(), true)));
        outputPathLookupButton.addActionListener(e -> outputPathField.setText(getFromFileDialog(app(), msgs.getString("title.output_path_file_dialog"), outputPathField.getText(), false)));
    }

    @SuppressWarnings("Convert2MethodRef") public void addChangeListeners(@NotNull Settings settings, @NotNull Runnable runnable) {
        listeners.forEach(l -> l.component().removeFocusListener(l.listener()));
        listeners.clear();/*@f0*/
        addFocusListener(mySqlPortField,                new PortFocusListener());
        addFocusListener(saveValuesForLaterCheckBox,    new CheckBoxFocusListener( () -> settings.isSaveSettings(),                                    runnable));
        addFocusListener(splitEntitiesIntoBaseCheckBox, new CheckBoxFocusListener( () -> settings.getGenerationInfo().isSplitEntities(),               runnable));
        addFocusListener(authorField,                   new TextFieldFocusListener(() -> settings.getProjectInfo().getAuthor(),                        runnable));
        addFocusListener(baseClassPrefixField,          new TextFieldFocusListener(() -> settings.getGenerationInfo().getBaseClassPrefix(),            runnable));
        addFocusListener(baseClassSuffixField,          new TextFieldFocusListener(() -> settings.getGenerationInfo().getBaseClassSuffix(),            runnable));
        addFocusListener(basePackageField,              new TextFieldFocusListener(() -> settings.getGenerationInfo().getBasePackage(),                runnable));
        addFocusListener(fkPrefixField,                 new TextFieldFocusListener(() -> settings.getGenerationInfo().getFkPrefix(),                   runnable));
        addFocusListener(fkSuffixField,                 new TextFieldFocusListener(() -> settings.getGenerationInfo().getFkSuffix(),                   runnable));
        addFocusListener(fkToManyFieldPatternField,     new TextFieldFocusListener(() -> settings.getGenerationInfo().getFkToManyPattern(),            runnable));
        addFocusListener(fkToOneFieldPatternField,      new TextFieldFocusListener(() -> settings.getGenerationInfo().getFkToOnePattern(),             runnable));
        addFocusListener(mySqlHostnameField,            new TextFieldFocusListener(() -> settings.getServerInfo().getHostName(),                       runnable));
        addFocusListener(mySqlPasswordField,            new TextFieldFocusListener(() -> settings.getServerInfo().getPassword(),                       runnable));
        addFocusListener(mySqlPortField,                new TextFieldFocusListener(() -> String.valueOf(settings.getServerInfo().getPortNumber()),     runnable));
        addFocusListener(mySqlSchemaField,              new TextFieldFocusListener(() -> settings.getServerInfo().getSchemaName(),                     runnable));
        addFocusListener(mySqlUsernameField,            new TextFieldFocusListener(() -> settings.getServerInfo().getUsername(),                       runnable));
        addFocusListener(organizationField,             new TextFieldFocusListener(() -> settings.getProjectInfo().getOrganization(),                  runnable));
        addFocusListener(outputPathField,               new TextFieldFocusListener(() -> abbreviatePath(settings.getGenerationInfo().getOutputPath()), runnable));
        addFocusListener(projectField,                  new TextFieldFocusListener(() -> settings.getProjectInfo().getProjectName(),                   runnable));
        addFocusListener(savePathField,                 new TextFieldFocusListener(() -> abbreviatePath(settings.getSettingsSavePath()),               runnable));
        addFocusListener(subClassPackageField,          new TextFieldFocusListener(() -> settings.getGenerationInfo().getSubclassPackage(),            runnable));
        addFocusListener(subClassPrefixField,           new TextFieldFocusListener(() -> settings.getGenerationInfo().getSubclassPrefix(),             runnable));
        addFocusListener(subClassSuffixField,           new TextFieldFocusListener(() -> settings.getGenerationInfo().getSubclassSuffix(),             runnable));
    }/*@f1*/

    public void copyFieldsToSettings(@NotNull Settings settings) {
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
    }

    public void copySettingsToFields(@NotNull Settings settings, @NotNull String filename) {
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

    protected void addFocusListener(@NotNull JComponent component, @NotNull FocusListener listener) {
        component.addFocusListener(listener);
        listeners.add(new FocusListenerRecord(component, listener));
    }
}
