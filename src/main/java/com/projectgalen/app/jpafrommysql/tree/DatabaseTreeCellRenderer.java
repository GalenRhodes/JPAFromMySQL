package com.projectgalen.app.jpafrommysql.tree;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: DatabaseTreeCellRenderer.java
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

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public final class DatabaseTreeCellRenderer extends DefaultTreeCellRenderer {

    public DatabaseTreeCellRenderer() { }

    public @Override Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        return (((c instanceof JLabel r) && (value instanceof DefaultMutableTreeNode n)) ? getTreeCellRendererComponent(tree, r, n, sel, expanded, leaf, row, hasFocus) : c);
    }

    public @NotNull JLabel getTreeCellRendererComponent(@NotNull JTree tree,
                                                        @NotNull JLabel renderer,
                                                        @NotNull DefaultMutableTreeNode node,
                                                        boolean sel,
                                                        boolean expanded,
                                                        boolean leaf,
                                                        int row,
                                                        boolean hasFocus) {
        if(node instanceof DatabaseTreeNode<?> n) return getTreeCellRendererComponent(tree, renderer, n, sel, expanded, leaf, row, hasFocus);

        Object o = node.getUserObject();

        if(o instanceof String str) {
            if(node.isRoot()) renderer.setIcon(DatabaseTreeNode.rootIcon);
            else if(node.getLevel() == 1) renderer.setIcon(DatabaseTreeNode.schemaIcon);
            else renderer.setIcon(switch(str) {
                    case "Columns", "Indexes", "Foreign Keys" -> DatabaseTreeNode.folderIcon;
                    default -> DatabaseTreeNode.unknownIcon;
                });
        }
        else renderer.setIcon(DatabaseTreeNode.unknownIcon);

        return renderer;
    }

    public @NotNull JLabel getTreeCellRendererComponent(@NotNull JTree tree,
                                                        @NotNull JLabel renderer,
                                                        @NotNull DatabaseTreeNode<?> node,
                                                        boolean sel,
                                                        boolean expanded,
                                                        boolean leaf,
                                                        int row,
                                                        boolean hasFocus) {
        renderer.setIcon(node.getIcon());
        return renderer;
    }
}
