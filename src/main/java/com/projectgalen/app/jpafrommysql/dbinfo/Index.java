package com.projectgalen.app.jpafrommysql.dbinfo;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: Index.java
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

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@SuppressWarnings("unused")
public class Index {

    private final boolean      nonUnique;
    private final String       keyName;
    private final String       collation;
    private final BigInteger   cardinality;
    private final BigInteger   subPart;
    private final String       packed;
    private final boolean      isNull;
    private final String       indexType;
    private final String       comment;
    private final String       indexComment;
    private final boolean      visible;
    private final String       expression;
    private final List<Column> columns = new ArrayList<>();

    public Index(@NotNull Table table, @NotNull ResultSet rs) throws SQLException {
        nonUnique    = (rs.getLong("Non_unique") == 1);
        keyName      = rs.getString("Key_name");
        collation    = rs.getString("Collation");
        cardinality  = ofNullable(rs.getBigDecimal("Cardinality")).map(BigDecimal::toBigInteger).orElse(null);
        subPart      = ofNullable(rs.getBigDecimal("Sub_part")).map(BigDecimal::toBigInteger).orElse(null);
        packed       = rs.getString("Packed");
        isNull       = "YES".equals(rs.getString("Null"));
        indexType    = rs.getString("Index_type");
        comment      = rs.getString("Comment");
        indexComment = rs.getString("Index_comment");
        visible      = "YES".equals(rs.getString("Visible"));
        expression   = rs.getString("Expression");
    }

    public void addColumn(@NotNull Column column) {
        columns.add(column);
    }

    public BigInteger getCardinality() {
        return cardinality;
    }

    public String getCollation() {
        return collation;
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public String getComment() {
        return comment;
    }

    public String getExpression() {
        return expression;
    }

    public String getIndexComment() {
        return indexComment;
    }

    public String getIndexType() {
        return indexType;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getPacked() {
        return packed;
    }

    public BigInteger getSubPart() {
        return subPart;
    }

    public boolean isNonUnique() {
        return nonUnique;
    }

    public boolean isNull() {
        return isNull;
    }

    public boolean isVisible() {
        return visible;
    }

    public @Override String toString() {
        return "%s: (%s)".formatted(keyName, columns.stream().map(Column::getColumnName).collect(Collectors.joining("`, `", "`", "`")));
    }
}
