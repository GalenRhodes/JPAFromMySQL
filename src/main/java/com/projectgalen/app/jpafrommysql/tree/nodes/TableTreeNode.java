package com.projectgalen.app.jpafrommysql.tree.nodes;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: TableTreeNode.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 27, 2023
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

import com.projectgalen.app.jpafrommysql.dbinfo.DBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TableTreeNode extends DatabaseTreeNode<DBTable> {
    public TableTreeNode(@NotNull DBTable table) {
        super(table, true);

        StringTreeNode columnsNode     = new StringTreeNode("Columns", folderIcon);
        StringTreeNode indexesNode     = new StringTreeNode("Indexes", folderIcon);
        StringTreeNode foreignKeysNode = new StringTreeNode("Foreign Keys", folderIcon);

        add(columnsNode);
        add(indexesNode);
        add(foreignKeysNode);

        table.getColumns().stream().map(ColumnTreeNode::new).forEach(columnsNode::add);
        table.getIndexes().stream().map(IndexTreeNode::new).forEach(indexesNode::add);
        table.getReferencedForeignKeys().stream().map(ForeignKeyTreeNode::new).forEach(foreignKeysNode::add);
    }

    public @Override @NotNull Icon getIcon() {
        return tableIcon;
    }

    public @Override void setUserObject(Object userObject) {
        if(userObject instanceof DBTable) super.setUserObject(userObject);
        else throw new IllegalArgumentException("User Object must an instance of %s.".formatted(DBTable.class.getName()));
    }
}
