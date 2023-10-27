package com.projectgalen.app.jpafrommysql.tree;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: DatabaseTreeNode.java
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

import com.projectgalen.app.jpafrommysql.JPAFromMySQL;
import com.projectgalen.lib.ui.UI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class DatabaseTreeNode<T> extends DefaultMutableTreeNode {

    public static final Icon tableIcon    = UI.getIcon("icons/database_table.png", JPAFromMySQL.class);
    public static final Icon pkColumnIcon = UI.getIcon("icons/table_key.png", JPAFromMySQL.class);
    public static final Icon indexIcon    = UI.getIcon("icons/table_lightning.png", JPAFromMySQL.class);
    public static final Icon fkIcon       = UI.getIcon("icons/table_relationship.png", JPAFromMySQL.class);
    public static final Icon columnIcon   = UI.getIcon("icons/table_gear.png", JPAFromMySQL.class);
    public static final Icon rootIcon     = UI.getIcon("icons/computer.png", JPAFromMySQL.class);
    public static final Icon schemaIcon   = UI.getIcon("icons/database.png", JPAFromMySQL.class);
    public static final Icon folderIcon   = UI.getIcon("icons/folder.png", JPAFromMySQL.class);
    public static final Icon unknownIcon  = UI.getIcon("icons/cog.png", JPAFromMySQL.class);

    public DatabaseTreeNode(@NotNull T userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    public @NotNull Icon getIcon() {
        return unknownIcon;
    }

    public @Override T getUserObject() {
        //noinspection unchecked
        return (T)super.getUserObject();
    }
}
