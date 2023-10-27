package com.projectgalen.app.jpafrommysql.tree;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: ColumnTreeNode.java
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

import com.projectgalen.app.jpafrommysql.dbinfo.Column;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ColumnTreeNode extends DatabaseTreeNode<Column> {
    public ColumnTreeNode(@NotNull Column userObject) {
        super(userObject, false);
    }

    public @Override @NotNull Icon getIcon() {
        return (getUserObject().isPrimaryKey() ? pkColumnIcon : columnIcon);
    }

    public @Override void setUserObject(Object userObject) {
        if(userObject instanceof Column) super.setUserObject(userObject);
        else throw new IllegalArgumentException("User Object must an instance of %s.".formatted(Column.class.getName()));
    }
}
