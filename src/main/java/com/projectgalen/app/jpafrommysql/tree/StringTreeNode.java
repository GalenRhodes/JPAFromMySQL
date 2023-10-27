package com.projectgalen.app.jpafrommysql.tree;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: StringTreeNode.java
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

public class StringTreeNode extends DatabaseTreeNode<String> {
    private @NotNull Icon icon;

    public StringTreeNode(@NotNull String text, @NotNull Icon icon, boolean allowsChildren) {
        super(text, allowsChildren);
        this.icon = icon;
    }

    public StringTreeNode(@NotNull String text, @NotNull Icon icon) {
        this(text, icon, true);
    }

    public @Override @NotNull Icon getIcon()  { return icon; }

    public @NotNull String getText()          { return getUserObject(); }

    public void setIcon(@NotNull Icon icon)   { this.icon = icon; }

    public void setText(@NotNull String text) { super.setUserObject(text); }

    public @Override void setUserObject(Object userObject) {
        if(userObject instanceof String text) setText(text);
        else throw new IllegalArgumentException("User Object must an instance of %s.".formatted(String.class.getName()));
    }
}
