package com.projectgalen.app.jpafrommysql.components;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: OmittedForm.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 28, 2023
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

import com.projectgalen.app.jpafrommysql.settings.ColumnReference;
import com.projectgalen.app.jpafrommysql.settings.OmitInfo;
import com.projectgalen.app.jpafrommysql.settings.TableReference;
import com.projectgalen.lib.ui.components.table.PGJTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OmittedForm extends JPanel {
    protected PGJTable<TableReference>  omittedTablesTable;
    protected JButton                   addOmittedTableButton;
    protected JButton                   removeOmittedTableButton;
    protected PGJTable<ColumnReference> omittedFieldsTable;
    protected JButton                   addOmittedColumnButton;
    protected JButton                   removeOmittedColumnButton;
    protected JPanel                    rootComponent;

    public OmittedForm() { }

    public void getData(@NotNull OmitInfo omitInfo) {

    }

    public void setData(@NotNull OmitInfo omitInfo) {

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
