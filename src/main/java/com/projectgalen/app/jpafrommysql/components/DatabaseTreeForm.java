package com.projectgalen.app.jpafrommysql.components;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: DatabaseTreeForm.java
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

import com.projectgalen.app.jpafrommysql.dbinfo.DBServer;
import com.projectgalen.app.jpafrommysql.tree.DatabaseTreeCellRenderer;
import com.projectgalen.app.jpafrommysql.tree.models.DatabaseTreeModel;
import com.projectgalen.app.jpafrommysql.tree.nodes.DatabaseTreeNode;
import com.projectgalen.app.jpafrommysql.tree.nodes.ServerTreeNode;
import com.projectgalen.app.jpafrommysql.tree.nodes.StringTreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class DatabaseTreeForm {
    public static final StringTreeNode DEFAULT_TOP_NODE = new StringTreeNode("<-N/A->", DatabaseTreeNode.unknownIcon, false);

    protected JPanel                 rootComponent;
    protected JButton                reloadInfoButton;
    protected JTree                  tablesTree;
    protected DefaultMutableTreeNode topNode = DEFAULT_TOP_NODE;

    public DatabaseTreeForm() {
        tablesTree.setModel(new DefaultTreeModel(topNode));
        tablesTree.setCellRenderer(new DatabaseTreeCellRenderer());
    }

    public void addActionListener(@NotNull ActionListener l) {
        reloadInfoButton.addActionListener(l);
    }

    public void addMouseListener(@NotNull MouseListener l) {
        tablesTree.addMouseListener(l);
    }

    public void removeActionListener(@NotNull ActionListener l) {
        reloadInfoButton.removeActionListener(l);
    }

    public void removeMouseListener(@NotNull MouseListener l) {
        tablesTree.removeMouseListener(l);
    }

    public void setData(DBServer dbServer) {
        if(dbServer != null) {
            tablesTree.setModel(new DatabaseTreeModel(topNode = new ServerTreeNode(dbServer)));
            SwingUtilities.invokeLater(() -> tablesTree.expandRow(1));
        }
        else {
            tablesTree.setModel(new DefaultTreeModel(topNode = DEFAULT_TOP_NODE, false));
        }
        tablesTree.setCellRenderer(new DatabaseTreeCellRenderer());
    }
}
